package org.openmrs.module.m2sysbiometrics.bioplugin;

import org.openmrs.module.m2sysbiometrics.client.M2SysClient;

public interface BioServerClient {

    String enroll(M2SysClient client, String subjectId, String biometricXml);

    String isRegistered(M2SysClient client, String subjectId);

    String changeId(M2SysClient client, String oldId, String newId);

    String update(M2SysClient client, String subjectId, String biometricXml);

    String identify(M2SysClient client, String biometricXml);

    String delete(M2SysClient client, String subjectId);

    String getServiceUrl(M2SysClient client);
}
