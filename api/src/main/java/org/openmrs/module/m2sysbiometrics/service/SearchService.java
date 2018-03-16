package org.openmrs.module.m2sysbiometrics.service;

import org.openmrs.module.m2sysbiometrics.model.M2SysCaptureResponse;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricMatch;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricSubject;

import java.util.List;

public interface SearchService {

    List<BiometricMatch> searchLocally(M2SysCaptureResponse fingerScan);

    List<BiometricMatch> searchNationally(M2SysCaptureResponse fingerScan);

    BiometricMatch findMostAdequateLocally(M2SysCaptureResponse fingerScan);

    BiometricMatch findMostAdequateNationally(M2SysCaptureResponse fingerScan);

    BiometricSubject findMostAdequateSubjectLocally(M2SysCaptureResponse fingerScan);

    BiometricSubject findMostAdequateSubjectNationally(M2SysCaptureResponse fingerScan);
}
