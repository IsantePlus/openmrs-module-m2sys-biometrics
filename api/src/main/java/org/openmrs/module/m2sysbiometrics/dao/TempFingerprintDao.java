package org.openmrs.module.m2sysbiometrics.dao;

import java.util.List;

import org.openmrs.module.m2sysbiometrics.model.TempFingerprint;

public interface TempFingerprintDao {

	TempFingerprint saveOrUpdate(TempFingerprint tempFingerprint);

	void delete(TempFingerprint tempFingerprint);

	TempFingerprint findOneByBiometricId(String biometricId);

	List<TempFingerprint> findAllByBiometricXml(String biometricXml);

}
