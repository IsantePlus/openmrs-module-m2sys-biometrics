package org.openmrs.module.m2sysbiometrics.service;

import org.openmrs.module.m2sysbiometrics.model.M2SysCaptureResponse;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricSubject;

public interface RegistrationService {

    void registerLocally(BiometricSubject subject, M2SysCaptureResponse capture);

    void registerNationally(BiometricSubject subject, M2SysCaptureResponse capture);

    void fetchFromNational(BiometricSubject nationalBiometricSubject, M2SysCaptureResponse fingerScan);
}
