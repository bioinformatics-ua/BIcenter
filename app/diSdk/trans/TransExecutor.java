package diSdk.trans;

import kettleExt.App;
import kettleExt.utils.JSONArray;
import kettleExt.utils.JSONObject;
import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Result;
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

import java.io.Serializable;
import java.util.*;

/**
 * Class responsible for transaction execution.
 *
 * @author leonardo
 */
public class TransExecutor implements Runnable, Serializable {
    private static Hashtable<String, TransExecutor> executors = new Hashtable<String, TransExecutor>();
    /**
     * Stores the output row structure of each step.
     */
    protected Map<StepMeta, RowMetaInterface> previewMetaMap = new HashMap<StepMeta, RowMetaInterface>();
    /**
     * Store the output data row of each step.
     */
    protected Map<StepMeta, List<Object[]>> previewDataMap = new HashMap<StepMeta, List<Object[]>>();
    /**
     * Store the transaction logs of each step.
     */
    protected Map<StepMeta, StringBuffer> previewLogMap = new HashMap<StepMeta, StringBuffer>();
    /**
     * Identify the transaction.
     */
    private String executionId;
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
    private Map<StepMeta, String> stepLogMap = new HashMap<StepMeta, String>();
    private String carteObjectId = null;
    private boolean finished = false;

    private TransExecutor(TransExecutionConfiguration transExecutionConfiguration, TransMeta transMeta) {
        this.executionId = UUID.randomUUID().toString().replaceAll("-", "");
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
    public static synchronized TransExecutor initExecutor(TransExecutionConfiguration transExecutionConfiguration, TransMeta transMeta) {
        TransExecutor transExecutor = new TransExecutor(transExecutionConfiguration, transMeta);
        executors.put(transExecutor.getExecutionId(), transExecutor);
        return transExecutor;
    }

    /**
     * Returns transaction execution id.
     */
    public String getExecutionId() {
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
            finished = true;
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
                StepInterface step = trans.findRunThread(stepMeta.getName());
                if (step != null) {

                    step.addRowListener(new RowAdapter() {
                        @Override
                        public void rowWrittenEvent(RowMetaInterface rowMeta, Object[] row) throws KettleStepException {
                            try {
                                rowsData.add(rowMeta.cloneRow(row));
                                if (rowsData.size() > 100) {
                                    rowsData.remove(0);
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
    public JSONArray getStepMeasure() throws Exception {
        JSONArray jsonArray = new JSONArray();

        if (executionConfiguration.isExecutingLocally()) {
            for (int i = 0; i < trans.nrSteps(); i++) {
                StepInterface baseStep = trans.getRunThread(i);
                StepStatus stepStatus = new StepStatus(baseStep);

                String[] fields = stepStatus.getTransLogFields();

                JSONArray childArray = new JSONArray();
                for (int f = 1; f < fields.length; f++) {
                    childArray.add(fields[f]);
                }
                jsonArray.add(childArray);
            }
        } else {
            SlaveServer remoteSlaveServer = executionConfiguration.getRemoteServer();
            SlaveServerTransStatus transStatus = remoteSlaveServer.getTransStatus(transMeta.getName(), carteObjectId, 0);
            List<StepStatus> stepStatusList = transStatus.getStepStatusList();
            for (int i = 0; i < stepStatusList.size(); i++) {
                StepStatus stepStatus = stepStatusList.get(i);
                String[] fields = stepStatus.getTransLogFields();

                JSONArray childArray = new JSONArray();
                for (int f = 1; f < fields.length; f++) {
                    childArray.add(fields[f]);
                }
                jsonArray.add(childArray);
            }
        }

        return jsonArray;
    }

    /**
     * Returns the execution logging of the transformation.
     *
     * @return
     * @throws Exception
     */
    public String getExecutionLog() throws Exception {

        if (executionConfiguration.isExecutingLocally()) {
            StringBuffer sb = new StringBuffer();
            KettleLogLayout logLayout = new KettleLogLayout(true);
            List<String> childIds = LoggingRegistry.getInstance().getLogChannelChildren(trans.getLogChannelId());
            List<KettleLoggingEvent> logLines = KettleLogStore.getLogBufferFromTo(childIds, true, -1, KettleLogStore.getLastBufferLineNr());
            for (int i = 0; i < logLines.size(); i++) {
                KettleLoggingEvent event = logLines.get(i);
                String line = logLayout.format(event).trim();
                sb.append(line).append("\n");
            }
            return sb.toString();
        } else {
            SlaveServer remoteSlaveServer = executionConfiguration.getRemoteServer();
            SlaveServerTransStatus transStatus = remoteSlaveServer.getTransStatus(transMeta.getName(), carteObjectId, 0);
            return transStatus.getLoggingString();
        }
    }

    public JSONArray getStepStatus() throws Exception {
        JSONArray jsonArray = new JSONArray();

        HashMap<String, Integer> stepIndex = new HashMap<String, Integer>();
        if (executionConfiguration.isExecutingLocally()) {
            for (StepMetaDataCombi combi : trans.getSteps()) {
                Integer index = stepIndex.get(combi.stepMeta.getName());
                if (index == null) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("stepName", combi.stepMeta.getName());
                    int errCount = (int) combi.step.getErrors();
                    jsonObject.put("stepStatus", errCount);

                    if (errCount > 0) {
                        StringBuilder logText = new StringBuilder();
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

                        jsonObject.put("logText", logText.toString());
                    }

                    stepIndex.put(combi.stepMeta.getName(), jsonArray.size());
                    jsonArray.add(jsonObject);
                } else {
                    JSONObject jsonObject = jsonArray.getJSONObject(index);
                    int errCount = (int) (combi.step.getErrors() + jsonObject.optInt("stepStatus"));
                    jsonObject.put("stepStatus", errCount);
                }
            }
        } else {
            SlaveServer remoteSlaveServer = executionConfiguration.getRemoteServer();
            SlaveServerTransStatus transStatus = remoteSlaveServer.getTransStatus(transMeta.getName(), carteObjectId, 0);
            List<StepStatus> stepStatusList = transStatus.getStepStatusList();
            for (int i = 0; i < stepStatusList.size(); i++) {
                StepStatus stepStatus = stepStatusList.get(i);
                Integer index = stepIndex.get(stepStatus.getStepname());
                if (index == null) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("stepName", stepStatus.getStepname());
                    jsonObject.put("stepStatus", stepStatus.getErrors());

                    stepIndex.put(stepStatus.getStepname(), jsonArray.size());
                    jsonArray.add(jsonObject);
                } else {
                    JSONObject jsonObject = jsonArray.getJSONObject(index);
                    int errCount = (int) (stepStatus.getErrors() + jsonObject.optInt("stepStatus"));
                    jsonObject.put("stepStatus", errCount);
                }

            }
        }

        return jsonArray;
    }

    /**
     * Returns the result of the transformation.
     *
     * @return
     */
    public JSONObject getPreviewData() throws Exception {
        JSONObject jsonObject = new JSONObject();

        if (executionConfiguration.isExecutingLocally()) {
            for (StepMetaDataCombi combi : trans.getSteps()) {
                RowMetaInterface rowMeta = previewMetaMap.get(combi.stepMeta);

                if (rowMeta != null) {
                    JSONObject stepJson = new JSONObject();
                    List<ValueMetaInterface> valueMetas = rowMeta.getValueMetaList();

                    JSONArray columns = new JSONArray();
                    JSONObject metaData = new JSONObject();
                    JSONArray fields = new JSONArray();
                    for (int i = 0; i < valueMetas.size(); i++) {
                        ValueMetaInterface valueMeta = rowMeta.getValueMeta(i);
                        fields.add(valueMeta.getName());

                        JSONObject column = new JSONObject();
                        column.put("dataIndex", valueMeta.getName());
                        column.put("width", 100);
                        column.put("header", valueMeta.getComments() == null ? valueMeta.getName() : valueMeta.getComments());
                        column.put("width", valueMeta.getLength() > 0 ? valueMeta.getLength() : 150);
                        columns.add(column);
                    }
                    metaData.put("fields", fields);
                    metaData.put("root", "firstRecords");
                    stepJson.put("metaData", metaData);
                    stepJson.put("columns", columns);

                    List<Object[]> rowsData = previewDataMap.get(combi.stepMeta);
                    JSONArray firstRecords = new JSONArray();
                    JSONArray lastRecords = new JSONArray();
                    for (int rowNr = 0; rowNr < rowsData.size(); rowNr++) {
                        Object[] rowData = rowsData.get(rowNr);
                        JSONObject row = new JSONObject();
                        for (int colNr = 0; colNr < rowMeta.size(); colNr++) {
                            String string;
                            ValueMetaInterface valueMetaInterface;
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

                            ValueMetaInterface valueMeta = rowMeta.getValueMeta(colNr);
                            row.put(valueMeta.getName(), string);
                        }
                        if (firstRecords.size() <= 50) {
                            firstRecords.add(row);
                        }
                        lastRecords.add(row);
                        if (lastRecords.size() > 50)
                            lastRecords.remove(0);
                    }
                    stepJson.put("firstRecords", firstRecords);
                    jsonObject.put(combi.stepname, stepJson);
                }
            }
        } else {
            SlaveServer remoteSlaveServer = executionConfiguration.getRemoteServer();
            SlaveServerTransStatus transStatus = remoteSlaveServer.getTransStatus(transMeta.getName(), carteObjectId, 0);
            Result result = transStatus.getResult();
        }

        return jsonObject;
    }
}
