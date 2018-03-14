package org.openmrs.module.m2sysbiometrics.scheduler;

import org.openmrs.scheduler.tasks.AbstractTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NationalRegistrationTask extends AbstractTask {

    public static final String TASK_NAME = "M2Sys Biometrics National Registrations Task";

    public static final String TASK_DESCRIPTION = "M2Sys Biometrics task for retry of the national registration failures.";

    public static final long DEFAULT_INTERVAL_SECONDS = 3600;

    private static final Logger LOGGER = LoggerFactory.getLogger(NationalRegistrationTask.class);

    @Override
    public void execute() {
        LOGGER.info("Executing " + TASK_NAME  + "...");
    }
}
