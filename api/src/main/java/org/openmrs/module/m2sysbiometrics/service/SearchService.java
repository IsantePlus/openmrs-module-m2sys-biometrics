package org.openmrs.module.m2sysbiometrics.service;

import org.openmrs.module.m2sysbiometrics.model.FingerScanStatus;
//import org.openmrs.module.m2sysbiometrics.model.M2SysCaptureResponse;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricMatch;

import java.util.List;

public interface SearchService {

    List<BiometricMatch> searchLocally(String biometricXml);

    List<BiometricMatch> searchNationally(String biometricXml);

    BiometricMatch findMostAdequateLocally(String biometricXml);

    BiometricMatch findMostAdequateNationally(String biometricXml);

    FingerScanStatus checkIfFingerScanExists(String biometricXml);
}
