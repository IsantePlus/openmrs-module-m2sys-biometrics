package org.openmrs.module.m2sysbiometrics.service;

import java.util.List;
import org.openmrs.module.m2sysbiometrics.bioplugin.BioServerClient;
import org.openmrs.module.m2sysbiometrics.model.M2SysCaptureResponse;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricMatch;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricSubject;

public interface SearchService {

    List<BiometricMatch> search(M2SysCaptureResponse fingerScan, BioServerClient client);

    BiometricMatch findMostAdequate(M2SysCaptureResponse fingerScan, BioServerClient client);

    BiometricSubject findMostAdequateBiometricSubject(M2SysCaptureResponse fingerScan, BioServerClient client);
}
