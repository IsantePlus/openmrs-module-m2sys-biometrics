package org.openmrs.module.m2sysbiometrics.scheduler;

import org.openmrs.api.context.Context;
//import org.openmrs.module.m2sysbiometrics.model.FingerScanStatus;
//import org.openmrs.module.m2sysbiometrics.model.M2SysCaptureResponse;
//import org.openmrs.module.m2sysbiometrics.model.NationalSynchronizationFailure;
import org.openmrs.module.m2sysbiometrics.model.FingerScanStatus;
import org.openmrs.module.m2sysbiometrics.model.M2SysCaptureResponse;
import org.openmrs.module.m2sysbiometrics.model.SyncFingerprint;
import org.openmrs.module.m2sysbiometrics.service.*;
import org.openmrs.scheduler.tasks.AbstractTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class NationalSynchronizationTask extends AbstractTask {

    public static final String TASK_NAME = "M2Sys Biometrics National Synchronization Task";

    public static final String TASK_DESCRIPTION = "M2Sys Biometrics task for retry of the national synchronization "
            + "failures.";

    public static final long DEFAULT_INTERVAL_SECONDS = 7200;

    private static final Logger LOGGER = LoggerFactory.getLogger(NationalSynchronizationTask.class);

    @Autowired
    private SyncFingerprintService syncFingerprintService;

    @Autowired
    private SearchService searchService;

    @Autowired
    private RegistrationService registrationService;

    @Override
    public void execute() {
        LOGGER.info("Executing " + TASK_NAME + "...");
        initializeBeans();
        syncRegistrationFailures();
        //    retryUpdateFailures();
    }

    private void initializeBeans() {
        Context.getRegisteredComponent(
                "nationalSynchronizationFailureService", NationalSynchronizationFailureService.class);

        Context.getRegisteredComponent(
                "registrationService", RegistrationService.class);

        Context.getRegisteredComponent(
                "searchService", SearchService.class);

        Context.getRegisteredComponent(
                "updateService", UpdateService.class);
    }
    /*
    private void retryRegistrationFailures() {
        for (NationalSynchronizationFailure failure : nationalSynchronizationFailureService.findAllRegistrationFailures()) {
            retryRegistrationFailure(failure);
        }
    }

    private void retryUpdateFailures() {
        for (NationalSynchronizationFailure failure : nationalSynchronizationFailureService.findAllUpdateFailures()) {
            retryUpdateFailure(failure);
        }
    }

    private void retryRegistrationFailure(NationalSynchronizationFailure failure) {
        try {
            M2SysCaptureResponse fingerScan = new M2SysCaptureResponse();
            fingerScan.setTemplateData(failure.getBiometricXml());
            FingerScanStatus fingerScanStatus = searchService.checkIfFingerScanExists(fingerScan);

            registrationService.synchronizeFingerprints(fingerScan, fingerScanStatus);
            nationalSynchronizationFailureService.delete(failure);
        } catch (Exception e) {
            LOGGER.error("Scheduled retry of registration failed", e);
        }
    }

  
    private void retryUpdateFailure(NationalSynchronizationFailure failure) {
        try {
            M2SysCaptureResponse fingerScan = new M2SysCaptureResponse();
            fingerScan.setTemplateData(failure.getBiometricXml());

            updateService.updateNationally(fingerScan);
            nationalSynchronizationFailureService.delete(failure);
        } catch (Exception e) {
            LOGGER.error("Scheduled retry of update failed", e);
        }
    }
 */

    private void syncRegistrationFailures() {

        List<SyncFingerprint> syncList = syncFingerprintService.findAll();
        for (SyncFingerprint fingerprint : syncList) {
            try {
                M2SysCaptureResponse fingerScan = new M2SysCaptureResponse();
                fingerScan.setTemplateData(fingerprint.getBiometricXml());
                registrationService.registerNationally(fingerScan);
                FingerScanStatus fingerScanStatus = searchService.checkIfFingerScanExists(fingerScan.getTemplateData());
                if (fingerScanStatus.isRegisteredNationally()) {
                    syncFingerprintService.delete(fingerprint);
                }
            } catch (Exception e) {
                LOGGER.error("Scheduled retry of Fingerprint Sync failed", e);
            }
        }

    }
}
