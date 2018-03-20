package org.openmrs.module.m2sysbiometrics.service;

import org.openmrs.module.m2sysbiometrics.model.FingerScanStatus;
import org.openmrs.module.m2sysbiometrics.model.M2SysCaptureResponse;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricMatch;

import java.util.List;

public interface SearchService {

    List<BiometricMatch> searchLocally(M2SysCaptureResponse fingerScan);

    List<BiometricMatch> searchNationally(M2SysCaptureResponse fingerScan);

    BiometricMatch findMostAdequateLocally(M2SysCaptureResponse fingerScan);

    BiometricMatch findMostAdequateNationally(M2SysCaptureResponse fingerScan);

    FingerScanStatus checkIfFingerScanExists(M2SysCaptureResponse fingerScan);
}
