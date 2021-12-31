package org.openmrs.module.m2sysbiometrics.client;

import java.util.List;

import org.openmrs.module.registrationcore.api.biometrics.model.BiometricEngineStatus;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricMatch;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricSubject;
import org.openmrs.module.registrationcore.api.biometrics.model.EnrollmentResult;

public interface M2SysClient {

	BiometricEngineStatus getStatus();

	EnrollmentResult enroll(BiometricSubject subjectId);

	EnrollmentResult enroll(BiometricSubject subjectId, String fingerprintsXmlTemplate);

	BiometricSubject update(BiometricSubject subject);

	BiometricSubject updateSubjectId(String oldId, String newId);

	List<BiometricMatch> search();

	BiometricSubject lookup(String subjectId);

	void delete(String subjectId);
}
