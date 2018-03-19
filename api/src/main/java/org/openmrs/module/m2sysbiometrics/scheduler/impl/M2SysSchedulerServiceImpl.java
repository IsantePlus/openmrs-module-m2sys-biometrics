package org.openmrs.module.m2sysbiometrics.scheduler.impl;

import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.m2sysbiometrics.exception.M2SysBiometricsException;
import org.openmrs.module.m2sysbiometrics.scheduler.M2SysSchedulerService;
import org.openmrs.module.m2sysbiometrics.scheduler.NationalSynchronizationTask;
import org.openmrs.module.m2sysbiometrics.util.M2SysProperties;
import org.openmrs.scheduler.SchedulerException;
import org.openmrs.scheduler.TaskDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

import static org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants.M2SYS_NATIONAL_REGISTRATIONS_TASK_INTERVAL;

@Service("m2sysbiometrics.M2SysSchedulerServiceImpl")
public class M2SysSchedulerServiceImpl extends BaseOpenmrsService implements M2SysSchedulerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(M2SysSchedulerServiceImpl.class);

    @Autowired
    private M2SysProperties properties;

    @Override
    public void runM2SysScheduler() {
        scheduleNationalRegistrationsTask();
    }

    private void scheduleNationalRegistrationsTask() {
        TaskDefinition nationalRegistrationsTask = createTask(NationalSynchronizationTask.TASK_NAME,
                NationalSynchronizationTask.TASK_DESCRIPTION, NationalSynchronizationTask.class.getName(),
                getNationalRegistrationInterval());

        scheduleTask(nationalRegistrationsTask);
    }

    private long getNationalRegistrationInterval() {
        if (properties.isGlobalPropertySet(M2SYS_NATIONAL_REGISTRATIONS_TASK_INTERVAL)) {
            return Long.parseLong(properties.getGlobalProperty(M2SYS_NATIONAL_REGISTRATIONS_TASK_INTERVAL));
        } else {
            return NationalSynchronizationTask.DEFAULT_INTERVAL_SECONDS;
        }
    }

    private void scheduleTask(TaskDefinition taskDefinition) {
        try {
            if (Context.getSchedulerService().getScheduledTasks().contains(taskDefinition)) {
                Context.getSchedulerService().rescheduleTask(taskDefinition);
            } else {
                Context.getSchedulerService().scheduleTask(taskDefinition);
            }
        } catch (SchedulerException e) {
            LOGGER.error("Error during starting " + taskDefinition.getName() + ":", e);
        }
    }

    private TaskDefinition createTask(String name, String description, String taskClass, Long interval) {
        TaskDefinition result = Context.getSchedulerService().getTaskByName(name);

        if (result == null) {
            result = new TaskDefinition();
        }

        result.setName(name);
        result.setDescription(description);
        result.setTaskClass(taskClass);
        result.setRepeatInterval(interval);
        result.setStartTime(new Timestamp(System.currentTimeMillis()));
        result.setStartOnStartup(true);

        try {
            Context.getSchedulerService().saveTaskDefinition(result);
        } catch (M2SysBiometricsException e) {
            LOGGER.error("Error during save " + name + " definition: ", e);
        }

        return Context.getSchedulerService().getTaskByName(name);
    }
}
