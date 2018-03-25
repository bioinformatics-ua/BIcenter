package scheduler;

import org.quartz.*;
import repositories.*;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

public class ExecutionScheduler implements ILatch {
    private CountDownLatch latch = new CountDownLatch(1);

    public void fireJob(long scheduleId, boolean schedule, int hour,int minutes, int dayOfMonth, int month, int year, boolean periodic, String interval, long taskId, long serverId, TaskRepository taskRepository, ServerRepository serverRepository, ExecutionRepository executionRepository, StepMetricRepository stepMetricRepository, StatusRepository statusRepository, DataRowRepository dataRowRepository, KeyValueRepository keyValueRepository) throws SchedulerException, InterruptedException {
        SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();
        Scheduler scheduler = schedFact.getScheduler();
        scheduler.start();

        // Define the job and tie it to our ExecutionJob class
        JobBuilder jobBuilder = JobBuilder.newJob(ExecutionJob.class);
        JobDataMap data = new JobDataMap();
        data.put("latch", this);
        data.put("task", taskId);
        data.put("server", serverId);
        data.put("periodic", periodic);
        data.put("taskRepository", taskRepository);
        data.put("serverRepository", serverRepository);
        data.put("executionRepository", executionRepository);
        data.put("stepMetricRepository", stepMetricRepository);
        data.put("statusRepository", statusRepository);
        data.put("dataRowRepository", dataRowRepository);
        data.put("keyValueRepository", keyValueRepository);

        JobDetail jobDetail = jobBuilder
                .usingJobData(data)
                .build();

        Trigger trigger = null;
        if(!schedule && !periodic){
            trigger = TriggerBuilder.newTrigger()
                    .withIdentity(String.valueOf(scheduleId),String.valueOf(taskId))
                    .startNow()
                    .build();
        }
        else{
            if(!periodic){
                trigger = TriggerBuilder.newTrigger()
                        .withIdentity(String.valueOf(scheduleId),String.valueOf(taskId))
                        .startAt(DateBuilder.dateOf(hour,minutes,0,dayOfMonth,month,year))
                        .build();
            }
            else {
                switch(interval){
                    case "Daily":
                        trigger = TriggerBuilder.newTrigger()
                                .withIdentity(String.valueOf(scheduleId),String.valueOf(taskId))
                                .startAt(DateBuilder.dateOf(hour, minutes, 0, dayOfMonth, month, year))
                                .withSchedule(CronScheduleBuilder.cronSchedule("0 "+minutes+" "+hour+" * * ?"))
                                .build();
                        break;

                    case "Weekly":
                        // Day of the Week to schedule
                        Date date = DateBuilder.dateOf(hour, minutes, 0, dayOfMonth, month, year);
                        java.util.Calendar c = java.util.Calendar.getInstance();
                        c.setTime(date);
                        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

                        trigger = TriggerBuilder.newTrigger()
                                .withIdentity(String.valueOf(scheduleId),String.valueOf(taskId))
                                .startAt(date)
                                .withSchedule(CronScheduleBuilder.cronSchedule("0 "+minutes+" "+hour+" ? * "+dayOfWeek))
                                .build();
                        break;

                    case "Yearly":
                        trigger = TriggerBuilder.newTrigger()
                                .withIdentity(String.valueOf(scheduleId),String.valueOf(taskId))
                                .startAt(DateBuilder.dateOf(hour, minutes, 0, dayOfMonth, month, year))
                                .withSchedule(CronScheduleBuilder.cronSchedule(minutes+" "+hour+" "+dayOfMonth+" "+month+" *"))
                                .build();
                        break;
                }
            }
        }

        // Tell quartz to schedule the job using our trigger
        scheduler.scheduleJob(jobDetail, trigger);
        latch.await();
        System.out.println("All triggers executed. Shutdown scheduler");
        scheduler.shutdown();
    }

    @Override
    public void countDown() {
        latch.countDown();
    }
}
