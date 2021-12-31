package org.openmrs.module.m2sysbiometrics.service;

import java.util.List;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.m2sysbiometrics.model.TempFingerprint;
import org.springframework.transaction.annotation.Transactional;

public interface TempFingerprintService extends OpenmrsService {

	@Transactional
	TempFingerprint saveOrUpdate(TempFingerprint tempFingerprint);

	@Transactional
	void delete(TempFingerprint tempFingerprint);

	@Transactional(readOnly = true)
	TempFingerprint findOneByBiometricId(String biometricId);

	@Transactional(readOnly = true)
	List<TempFingerprint> findAllByBiometricXml(String biometricXml);
}
