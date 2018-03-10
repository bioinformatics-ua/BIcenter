package diSdk.trans;

import kettleExt.App;
import kettleExt.utils.JSONArray;
import kettleExt.utils.JSONObject;
import models.*;
import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.logging.*;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransAdapter;
import org.pentaho.di.trans.TransExecutionConfiguration;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.*;
import org.pentaho.di.www.SlaveServerTransStatus;
import repositories.*;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class responsible for transaction execution.
 *
 * @author leonardo
 */
public class TransExecutor implements Runnable, Serializable {
    private static Hashtable<Long, TransExecutor> executors = new Hashtable<>();
    /**
     * JPA repositories.
     */
    private static ExecutionRepository executionRepository;
    private static StepMetricRepository stepMetricRepository;
    private static StatusRepository statusRepository;
    private static DataRowRepository dataRowRepository;
    private static KeyValueRepository keyValueRepository;
    /**
     * Stores the output row structure of each step.
     */
    protected Map<StepMeta, RowMetaInterface> previewMetaMap = new HashMap<>();
    /**
     * Store the output data row of each step.
     */
    protected Map<StepMeta, List<Object[]>> previewDataMap = new HashMap<>();
    /**
     * Store the transaction logs of each step.
     */
    protected Map<StepMeta, StringBuffer> previewLogMap = new HashMap<>();
    /**
     * Identify the transaction.
     */
    private Long executionId;
    /**
     * A set of configurations that specifies how a transaction should be executed.
     */
    private TransExecutionConfiguration executionConfiguration;
    /**
     * Specification of the transaction structure.
     */
    private TransMeta transMeta = null;
    /**
     * The actual transaction.
     */
    private Trans trans = null;
    /**
     * Transaction logs.
     */
    private Map<StepMeta, String> stepLogMap = new HashMap<>();
    private String carteObjectId = null;
    private boolean finished = false;

    private TransExecutor(Long executionId, TransExecutionConfiguration transExecutionConfiguration, TransMeta transMeta) {
        this.executionId = executionId;
        this.executionConfiguration = transExecutionConfiguration;
        this.transMeta = transMeta;
    }

    /**
     * Create a TransExecutor.
     *
     * @param transExecutionConfiguration transaction execution details.
     * @param transMeta                   transaction structure.
     * @return
     */
    public static synchronized TransExecutor initExecutor(TransExecutionConfiguration transExecutionConfiguration, TransMeta transMeta, long taskId, TaskRepository taskRepository, ExecutionRepository executions, StepMetricRepository stepMetrics, StatusRepository status, DataRowRepository dataRows, KeyValueRepository keyValues) {
        executionRepository = executions;
        stepMetricRepository = stepMetrics;
        statusRepository = status;
        dataRowRepository = dataRows;
        keyValueRepository = keyValues;

        Execution execution = new Execution();
        Task task = taskRepository.get(taskId);
        execution.setTask(task);
        execution = executionRepository.add(execution);

        TransExecutor transExecutor = new TransExecutor(execution.getId(), transExecutionConfiguration, transMeta);
        executors.put(transExecutor.getExecutionId(), transExecutor);

        return transExecutor;
    }

    /**
     * Returns transaction execution id.
     */
    public Long getExecutionId() {
        return executionId;
    }

    @Override
    /**
     * Executes the defined transaction.
     */
    public void run() {
        try {
            if (executionConfiguration.isExecutingLocally()) {
                // Set the variables.
                transMeta.injectVariables(executionConfiguration.getVariables());

                // Set the named parameters.
                Map<String, String> paramMap = executionConfiguration.getParams();
                Set<String> keys = paramMap.keySet();
                for (String key : keys) {
                    transMeta.setParameterValue(key, Const.NVL(paramMap.get(key), ""));
                }
                transMeta.activateParameters();

                // Set the arguments.
                Map<String, String> arguments = executionConfiguration.getArguments();
                String[] argumentNames = arguments.keySet().toArray(new String[arguments.size()]);
                Arrays.sort(argumentNames);

                String[] args = new String[argumentNames.length];
                for (int i = 0; i < args.length; i++) {
                    String argumentName = argumentNames[i];
                    args[i] = arguments.get(argumentName);
                }

                // Transaction initialization.
                boolean initialized = false;
                trans = new Trans(transMeta);
                try {
                    trans.prepareExecution(args);
                    capturePreviewData(trans, transMeta.getSteps());
                    initialized = true;
                } catch (KettleException e) {
                    checkErrorVisuals();
                }

                // Transaction execution.
                if (trans.isReadyToStart() && initialized) {
                    trans.addTransListener(new TransAdapter() {
                        public void transFinished(Trans trans) {
                            checkErrorVisuals();
                        }
                    });
                    trans.startThreads();
                    while (!trans.isFinished()) Thread.sleep(500);
                } else {
                    checkErrorVisuals();
                }
            } else if (executionConfiguration.isExecutingRemotely()) {
                // Transaction initialization.
                carteObjectId = Trans.sendToSlaveServer(transMeta, executionConfiguration, App.getInstance().getRepository(), App.getInstance().getMetaStore());
                SlaveServer remoteSlaveServer = executionConfiguration.getRemoteServer();

                // Transaction execution.
                boolean running = true;
                while (running) {
                    SlaveServerTransStatus transStatus = remoteSlaveServer.getTransStatus(transMeta.getName(), carteObjectId, 0);
                    running = transStatus.isRunning();

                    Thread.sleep(500);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            App.getInstance().getLog().logError("Execution failed！", e);
        } finally {
            // Stores the execution results on the database
            this.getStepMeasure();
            this.getStepStatus();

            // Execution has finished.
            Execution execution = executionRepository.get(executionId);
            execution.setEndDate(new Date());
            execution.setFinished(true);
            execution.setErrors(trans.getErrors());
            executionRepository.add(execution);
        }
    }

    /**
     * Prepare transaction reporting structures, and actually fill them.
     *
     * @param trans     The transaction.
     * @param stepMetas Steps description.
     */
    public void capturePreviewData(Trans trans, List<StepMeta> stepMetas) {
        final StringBuffer loggingText = new StringBuffer();
        Execution execution = executionRepository.get(executionId);

        try {
            final TransMeta transMeta = trans.getTransMeta();

            // For each transaction's step
            for (final StepMeta stepMeta : stepMetas) {
                // To store the output row structure.
                final RowMetaInterface rowMeta = transMeta.getStepFields(stepMeta).clone();
                previewMetaMap.put(stepMeta, rowMeta);

                // To store the output data (according to the row structure).
                final List<Object[]> rowsData = new LinkedList<Object[]>();
                previewDataMap.put(stepMeta, rowsData);

                // To store the transaction logs.
                previewLogMap.put(stepMeta, loggingText);

                // Write step row output to rowsData.
                final StepInterface step = trans.findRunThread(stepMeta.getName());

                Task task = execution.getTask();
                List<Step> steps = task.getSteps();
                Step stepJPA = steps.stream()
                        .filter(s -> s.getLabel().equals(step.getStepname()))
                        .findFirst()
                        .get();

                if (step != null) {
                    AtomicInteger rowNr = new AtomicInteger(0);
                    step.addRowListener(new RowAdapter() {
                        @Override
                        public void rowWrittenEvent(RowMetaInterface rowMeta, Object[] row) throws KettleStepException {
                            try {
                                if (rowNr.get() < 20) {
                                    writeDataRow(rowNr.getAndIncrement(), execution, stepJPA, rowMeta, row);
                                }
                            } catch (Exception e) {
                                throw new KettleStepException("Unable to clone row for metadata : " + rowMeta, e);
                            }
                        }
                    });
                }

            }
        } catch (Exception e) {
            loggingText.append(Const.getStackTracker(e));
        }

        // Check when transaction is finished.
        trans.addTransListener(new TransAdapter() {
            @Override
            public void transFinished(Trans trans) throws KettleException {
                if (trans.getErrors() != 0) {
                    for (StepMetaDataCombi combi : trans.getSteps()) {
                        if (combi.copy == 0) {
                            StringBuffer logBuffer = KettleLogStore.getAppender().getBuffer(combi.step.getLogChannel().getLogChannelId(), false);
                            previewLogMap.put(combi.stepMeta, logBuffer);
                        }
                    }
                }
            }
        });

        StringBuilder sb = new StringBuilder();
        KettleLogStore.getAppender().addLoggingEventListener(
                new KettleLoggingEventListener() {
                    @Override
                    public void eventAdded(KettleLoggingEvent event) {
                        sb.append(event.getMessage().toString() + "\n");
                        execution.setLog(sb.toString());
                        executionRepository.add(execution);
                    }
                }
        );

    }

    /**
     * When errors happen, it writes them into the logging map.
     */
    private void checkErrorVisuals() {
        if (trans.getErrors() > 0) {
            stepLogMap.clear();

            for (StepMetaDataCombi combi : trans.getSteps()) {
                if (combi.step.getErrors() > 0) {
                    String channelId = combi.step.getLogChannel().getLogChannelId();
                    List<KettleLoggingEvent> eventList = KettleLogStore.getLogBufferFromTo(channelId, false, 0, KettleLogStore.getLastBufferLineNr());

                    StringBuilder logText = new StringBuilder();
                    for (KettleLoggingEvent event : eventList) {
                        Object message = event.getMessage();
                        if (message instanceof LogMessage) {
                            LogMessage logMessage = (LogMessage) message;
                            if (logMessage.isError()) {
                                logText.append(logMessage.getMessage()).append(Const.CR);
                            }
                        }
                    }
                    stepLogMap.put(combi.stepMeta, logText.toString());
                }
            }

        } else {
            stepLogMap.clear();
        }
    }

    /**
     * Checks if the transaction is finished.
     *
     * @return
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * Returns the performance measurements of each step.
     *
     * @return
     * @throws Exception
     */
    public void getStepMeasure() {
        if (executionConfiguration.isExecutingLocally()) {
            for (int i = 0; i < trans.nrSteps(); i++) {
                StepInterface baseStep = trans.getRunThread(i);
                StepStatus stepStatus = new StepStatus(baseStep);

                String[] fields = stepStatus.getTransLogFields();
                StepMetric stepMetric = new StepMetric(fields);
                Execution execution = executionRepository.get(executionId);
                stepMetric.setExecution(execution);

                Task task = execution.getTask();
                List<Step> steps = task.getSteps();
                Step step = steps.stream()
                        .filter(s -> s.getLabel().equals(fields[1]))
                        .findFirst()
                        .get();
                stepMetric.setStep(step);

                // Build Step Performance Metrics.
                stepMetricRepository.add(stepMetric);
            }
        } else {
            SlaveServer remoteSlaveServer = executionConfiguration.getRemoteServer();
            SlaveServerTransStatus transStatus = null;
            try {
                transStatus = remoteSlaveServer.getTransStatus(transMeta.getName(), carteObjectId, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            List<StepStatus> stepStatusList = transStatus.getStepStatusList();
            for (int i = 0; i < stepStatusList.size(); i++) {
                StepStatus stepStatus = stepStatusList.get(i);
                String[] fields = stepStatus.getTransLogFields();

                StepMetric stepMetric = new StepMetric(fields);
                Execution execution = executionRepository.get(executionId);
                stepMetric.setExecution(execution);

                Task task = execution.getTask();
                List<Step> steps = task.getSteps();
                Step step = steps.stream()
                        .filter(s -> s.getLabel().equals(fields[0]))
                        .findFirst()
                        .get();
                stepMetric.setStep(step);

                // Build Step Performance Metrics.
                stepMetricRepository.add(stepMetric);
            }
        }

    }

    public void getStepStatus() {
        JSONArray jsonArray = new JSONArray();

        HashMap<String, Integer> stepIndex = new HashMap<String, Integer>();
        if (executionConfiguration.isExecutingLocally()) {
            for (StepMetaDataCombi combi : trans.getSteps()) {
                String stepName = combi.stepMeta.getName();
                int errCount;
                StringBuilder logText = null;

                Status status = statusRepository.getByExecutionAndStepLabel(executionId, stepName);
                if (status == null) {
                    errCount = (int) combi.step.getErrors();

                    if (errCount > 0) {
                        logText = new StringBuilder();
                        String channelId = combi.step.getLogChannel().getLogChannelId();
                        List<KettleLoggingEvent> eventList = KettleLogStore.getLogBufferFromTo(channelId, false, -1, KettleLogStore.getLastBufferLineNr());
                        for (KettleLoggingEvent event : eventList) {
                            Object message = event.getMessage();
                            if (message instanceof LogMessage) {
                                LogMessage logMessage = (LogMessage) message;
                                if (logMessage.isError()) {
                                    logText.append(logMessage.getMessage()).append(Const.CR);
                                }
                            }
                        }
                    }
                } else {
                    errCount = (int) (combi.step.getErrors() + status.getStatus());
                }

                status = new Status(errCount, logText != null ? logText.toString() : "");
                Execution execution = executionRepository.get(executionId);
                status.setExecution(execution);

                Task task = execution.getTask();
                List<Step> steps = task.getSteps();
                Step step = steps.stream()
                        .filter(s -> s.getLabel().equals(stepName))
                        .findFirst()
                        .get();
                status.setStep(step);
                statusRepository.add(status);
            }
        } else {
            // TODO
            SlaveServer remoteSlaveServer = executionConfiguration.getRemoteServer();
            SlaveServerTransStatus transStatus = null;
            try {
                transStatus = remoteSlaveServer.getTransStatus(transMeta.getName(), carteObjectId, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            List<StepStatus> stepStatusList = transStatus.getStepStatusList();

            for (int i = 0; i < stepStatusList.size(); i++) {
                int errCount;

                StepStatus stepStatus = stepStatusList.get(i);
                Integer index = stepIndex.get(stepStatus.getStepname());
                if (index == null) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("stepName", stepStatus.getStepname());
                    jsonObject.put("stepStatus", stepStatus.getErrors());
                    errCount = (int) stepStatus.getErrors();

                    stepIndex.put(stepStatus.getStepname(), jsonArray.size());
                    jsonArray.add(jsonObject);
                } else {
                    JSONObject jsonObject = jsonArray.getJSONObject(index);
                    errCount = (int) (stepStatus.getErrors() + jsonObject.optInt("stepStatus"));
                    jsonObject.put("stepStatus", errCount);
                }

                Status status = new Status(errCount, "");
                Execution execution = executionRepository.get(executionId);
                status.setExecution(execution);
                statusRepository.add(status);
            }
        }
    }

    private void writeDataRow(int rowNr, Execution execution, Step step, RowMetaInterface rowMeta, Object[] row) throws KettleStepException {
        // Initialize Data Row.
        DataRow dataRow = new DataRow(rowNr);
        dataRow.setExecution(execution);
        dataRow.setStep(step);
        dataRow = dataRowRepository.add(dataRow);

        try {
            Object[] rowData = rowMeta.cloneRow(row);
            List<KeyValue> keyValues = new ArrayList<>();

            for (int colNr = 0; colNr < rowMeta.size(); colNr++) {
                String string;
                ValueMetaInterface valueMetaInterface = null;
                try {
                    valueMetaInterface = rowMeta.getValueMeta(colNr);
                    if (valueMetaInterface.isStorageBinaryString()) {
                        Object nativeType = valueMetaInterface.convertBinaryStringToNativeType((byte[]) rowData[colNr]);
                        string = valueMetaInterface.getStorageMetadata().getString(nativeType);
                    } else {
                        string = rowMeta.getString(rowData, colNr);
                    }
                } catch (Exception e) {
                    string = "Conversion error: " + e.getMessage();
                }

                // Initialize Key/Value and Assign it to the Data Row.
                String header = valueMetaInterface.getComments() == null ? valueMetaInterface.getName() : valueMetaInterface.getComments();
                KeyValue keyValue = new KeyValue(header, string);
                keyValue.setDataRow(dataRow);
                keyValues.add(keyValue);
            }

            keyValueRepository.addAll(keyValues);
        } catch (Exception e) {
            throw new KettleStepException("Unable to clone row for metadata : " + rowMeta, e);
        }
    }
}
