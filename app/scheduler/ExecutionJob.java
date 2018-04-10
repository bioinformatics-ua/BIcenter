package scheduler;

import com.google.inject.Inject;
import diSdk.task.TaskDecoder;
import diSdk.trans.TransExecutor;
import models.*;
import org.hibernate.Hibernate;
import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.trans.TransExecutionConfiguration;
import org.pentaho.di.trans.TransMeta;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import repositories.*;
import repositories.user.UserRepository;

public class ExecutionJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDetail jobDetail = context.getJobDetail();
        long taskId = (long) jobDetail.getJobDataMap().get("task");
        long serverId = (long) jobDetail.getJobDataMap().get("server");
        long userId = (long) jobDetail.getJobDataMap().get("user");
        boolean periodic = (boolean) jobDetail.getJobDataMap().get("periodic");
        TaskRepository taskRepository = (TaskRepository) jobDetail.getJobDataMap().get("taskRepository");
        ServerRepository serverRepository = (ServerRepository) jobDetail.getJobDataMap().get("serverRepository");
        ExecutionRepository executionRepository = (ExecutionRepository) jobDetail.getJobDataMap().get("executionRepository");
        StepMetricRepository stepMetricRepository = (StepMetricRepository) jobDetail.getJobDataMap().get("stepMetricRepository");
        StatusRepository statusRepository = (StatusRepository) jobDetail.getJobDataMap().get("statusRepository");
        DataRowRepository dataRowRepository = (DataRowRepository) jobDetail.getJobDataMap().get("dataRowRepository");
        KeyValueRepository keyValueRepository = (KeyValueRepository) jobDetail.getJobDataMap().get("keyValueRepository");
        UserRepository userRepository = (UserRepository) jobDetail.getJobDataMap().get("userRepository");

        // Prepare Transformation based on the JPA Task.
        Task task = taskRepository.get(taskId);
        initializeTask(task);
        TransMeta transMeta = null;
        try {
            transMeta = TaskDecoder.decode(task);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Setting Execution Configurations.
        TransExecutionConfiguration executionConfiguration = new TransExecutionConfiguration();
        executionConfiguration.setExecutingLocally( false );
        executionConfiguration.setExecutingRemotely( true );
        executionConfiguration.setExecutingClustered( false );

        // Building the Carte Server.
        Server server = serverRepository.get(serverId);
        SlaveServer carteServer = new SlaveServer(
                server.getName(),
                server.getHostName(),
                Long.toString(server.getPortNumber()),
                server.getUsername(),
                server.getPassword()
        );
        executionConfiguration.setRemoteServer( carteServer );

        // To kill the schedule.
        if(!periodic) {
            ILatch latch = (ILatch) jobDetail.getJobDataMap().get("latch");
            latch.countDown();
        }

        // Execute Transformation.
        TransExecutor transExecutor = TransExecutor.initExecutor(executionConfiguration, transMeta, taskId, taskRepository, serverId, serverRepository, userId, userRepository, executionRepository, stepMetricRepository, statusRepository, dataRowRepository, keyValueRepository);
        new Thread(transExecutor).start();
    }

    private void initializeTask(Task task){
        if(!task.getSteps().isEmpty()) {
            Hibernate.initialize(task.getSteps());
            for (Step step : task.getSteps()) {
                Hibernate.initialize(step.getComponent());
                Component component = step.getComponent();
                if(!component.getComponentProperties().isEmpty())
                    Hibernate.initialize(component.getComponentProperties());
                for (ComponentProperty componentProperty : step.getComponent().getComponentProperties()) {
                    if(!componentProperty.getComponentMetadatas().isEmpty())
                        Hibernate.initialize(componentProperty.getComponentMetadatas());
                }
                Hibernate.initialize(step.getCell());
                if(!step.getStepProperties().isEmpty()) Hibernate.initialize(step.getStepProperties());
            }
        }
        if(!task.getHops().isEmpty()) Hibernate.initialize(task.getHops());
    }
}
