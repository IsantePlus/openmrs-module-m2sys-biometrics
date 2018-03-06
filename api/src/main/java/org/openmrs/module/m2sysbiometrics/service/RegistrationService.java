package org.openmrs.module.m2sysbiometrics.service;

import org.openmrs.module.m2sysbiometrics.bioplugin.LocalBioServerClient;
import org.openmrs.module.m2sysbiometrics.bioplugin.NationalBioServerClient;
import org.openmrs.module.m2sysbiometrics.model.M2SysCaptureResponse;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricSubject;

public interface RegistrationService {

    void register(LocalBioServerClient client, BiometricSubject subject, M2SysCaptureResponse capture);

    void register(NationalBioServerClient client, BiometricSubject subject, M2SysCaptureResponse capture);
}
