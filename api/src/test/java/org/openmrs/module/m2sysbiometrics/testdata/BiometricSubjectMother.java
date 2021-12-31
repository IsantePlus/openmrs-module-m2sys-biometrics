package org.openmrs.module.m2sysbiometrics.testdata;

import org.openmrs.module.registrationcore.api.biometrics.model.BiometricSubject;

public final class BiometricSubjectMother {

	public static BiometricSubject validInstance() {
		return new BiometricSubject();
	}

	public static BiometricSubject withSubjectId(String subjectId) {
		BiometricSubject biometricSubject = validInstance();
		biometricSubject.setSubjectId(subjectId);
		return biometricSubject;
	}

	private BiometricSubjectMother() {
	}
}
