package org.openmrs.module.m2sysbiometrics.service;

import org.openmrs.module.m2sysbiometrics.model.FingerScanStatus;
import org.openmrs.module.m2sysbiometrics.model.M2SysCaptureResponse;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricSubject;

public interface RegistrationService {

    void registerLocally(BiometricSubject subject, M2SysCaptureResponse fingerScan);

    void registerNationally(BiometricSubject subject, M2SysCaptureResponse fingerScan);

    void fetchFromMpiByNationalFpId(BiometricSubject nationalBiometricSubject, M2SysCaptureResponse fingerScan);

    void synchronizeFingerprints(M2SysCaptureResponse fingerScan, FingerScanStatus fingerScanStatus);
}
