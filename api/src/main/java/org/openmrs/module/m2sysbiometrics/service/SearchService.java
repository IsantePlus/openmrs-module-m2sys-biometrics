package org.openmrs.module.m2sysbiometrics.service;

import java.util.List;

import org.openmrs.module.m2sysbiometrics.model.FingerScanStatus;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricMatch;

public interface SearchService {

	List<BiometricMatch> searchLocally(String biometricXml);

	List<BiometricMatch> searchNationally(String biometricXml);

	BiometricMatch findMostAdequateLocally(String biometricXml);

	BiometricMatch findMostAdequateNationally(String biometricXml);

	FingerScanStatus checkIfFingerScanExists(String biometricXml);
}
