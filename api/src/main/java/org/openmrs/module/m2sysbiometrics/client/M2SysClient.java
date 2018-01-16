package org.openmrs.module.m2sysbiometrics.client;

import org.openmrs.module.registrationcore.api.biometrics.model.BiometricEngineStatus;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricMatch;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricSubject;

import java.util.List;

public interface M2SysClient {

    BiometricEngineStatus getStatus();

    BiometricSubject enroll(BiometricSubject subject);

    BiometricSubject update(BiometricSubject subject);

    BiometricSubject updateSubjectId(String oldId, String newId);

    List<BiometricMatch> search(BiometricSubject subject);

    BiometricSubject lookup(String subjectId);

    void delete(String subjectId);
}
