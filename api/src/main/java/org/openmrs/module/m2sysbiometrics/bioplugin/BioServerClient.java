package org.openmrs.module.m2sysbiometrics.bioplugin;

import org.openmrs.module.m2sysbiometrics.model.M2SysCaptureResponse;

public interface BioServerClient {

    String enroll(String subjectId, String biometricXml);

    String isRegistered(String subjectId);

    String changeId(String oldId, String newId);

    String update(String subjectId, String biometricXml);

    String identify(String biometricXml);

    String delete(String subjectId);

    boolean isFingerScanExists(M2SysCaptureResponse scannedFingers);
}
