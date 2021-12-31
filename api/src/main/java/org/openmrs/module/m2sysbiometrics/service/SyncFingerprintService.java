package org.openmrs.module.m2sysbiometrics.service;

import java.util.List;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.m2sysbiometrics.model.SyncFingerprint;
import org.springframework.transaction.annotation.Transactional;

public interface SyncFingerprintService extends OpenmrsService {

	@Transactional
	SyncFingerprint saveOrUpdate(SyncFingerprint syncFingerprint);

	@Transactional
	void delete(SyncFingerprint syncFingerprint);

	@Transactional(readOnly = true)
	SyncFingerprint findOneByBiometricId(String biometricId);

	@Transactional(readOnly = true)
	List<SyncFingerprint> findAllByBiometricXml(String biometricXml);

	@Transactional(readOnly = true)
	List<SyncFingerprint> findAll();
}
