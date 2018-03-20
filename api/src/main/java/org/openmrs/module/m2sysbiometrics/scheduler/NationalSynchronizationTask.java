package org.openmrs.module.m2sysbiometrics.scheduler;

import org.openmrs.api.context.Context;
import org.openmrs.module.m2sysbiometrics.model.FingerScanStatus;
import org.openmrs.module.m2sysbiometrics.model.M2SysCaptureResponse;
import org.openmrs.module.m2sysbiometrics.model.NationalSynchronizationFailure;
import org.openmrs.module.m2sysbiometrics.service.NationalSynchronizationFailureService;
import org.openmrs.module.m2sysbiometrics.service.RegistrationService;
import org.openmrs.module.m2sysbiometrics.service.SearchService;
import org.openmrs.module.m2sysbiometrics.util.PatientHelper;
import org.openmrs.scheduler.tasks.AbstractTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NationalSynchronizationTask extends AbstractTask {

    public static final String TASK_NAME = "M2Sys Biometrics National Synchronization Task";

    public static final String TASK_DESCRIPTION = "M2Sys Biometrics task for retry of the national synchronization "
            + "failures.";

    public static final long DEFAULT_INTERVAL_SECONDS = 3600;

    private static final Logger LOGGER = LoggerFactory.getLogger(NationalSynchronizationTask.class);

    private NationalSynchronizationFailureService nationalSynchronizationFailureService;

    private RegistrationService registrationService;

    private SearchService searchService;

    private PatientHelper patientHelper;

    @Override
    public void execute() {
        LOGGER.info("Executing " + TASK_NAME  + "...");
        initializeBeans();
        retryRegistrationFailures();
    }

    private void initializeBeans() {
        nationalSynchronizationFailureService = Context.getRegisteredComponent(
                "nationalSynchronizationFailureService", NationalSynchronizationFailureService.class);

        registrationService = Context.getRegisteredComponent(
                "registrationService", RegistrationService.class);

        searchService = Context.getRegisteredComponent(
                "searchService", SearchService.class);

        patientHelper = Context.getRegisteredComponent(
                "patientHelper", PatientHelper.class);
    }

    private void retryRegistrationFailures() {
        nationalSynchronizationFailureService.findAllRegistrationFailures().forEach(this::retryRegistrationFailure);
    }

    private void retryRegistrationFailure(NationalSynchronizationFailure failure) {
        M2SysCaptureResponse fingerScan = new M2SysCaptureResponse();
        fingerScan.setTemplateData(failure.getBiometricXml());
        FingerScanStatus fingerScanStatus = searchService.checkIfFingerScanExists(fingerScan);

        registrationService.synchronizeFingerprints(fingerScan, fingerScanStatus);

        fingerScanStatus = searchService.checkIfFingerScanExists(fingerScan);

        if (fingerScanStatus.isRegisteredNationally() && patientHelper.findByNationalFpId(
                fingerScanStatus.getNationalBiometricSubject().getSubjectId()) != null) {
            nationalSynchronizationFailureService.delete(failure);
        }
    }
}
