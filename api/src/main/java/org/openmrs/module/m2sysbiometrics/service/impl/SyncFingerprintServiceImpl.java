package org.openmrs.module.m2sysbiometrics.service.impl;

import java.util.List;

import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.m2sysbiometrics.dao.SyncFingerprintDao;
import org.openmrs.module.m2sysbiometrics.model.SyncFingerprint;
import org.openmrs.module.m2sysbiometrics.service.SyncFingerprintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "syncFingerprintService")
public class SyncFingerprintServiceImpl extends BaseOpenmrsService
		implements SyncFingerprintService {

	@Autowired
	private SyncFingerprintDao dao;

	@Override
	public SyncFingerprint saveOrUpdate(SyncFingerprint syncFingerprint) {
		return dao.saveOrUpdate(syncFingerprint);
	}

	@Override
	public void delete(SyncFingerprint syncFingerprint) {
		dao.delete(syncFingerprint);
	}

	@Override
	public SyncFingerprint findOneByBiometricId(String biometricId) {
		return dao.findOneByBiometricId(biometricId);
	}

	@Override
	public List<SyncFingerprint> findAllByBiometricXml(String biometricXml) {
		return dao.findAllByBiometricXml(biometricXml);
	}

	@Override
	public List<SyncFingerprint> findAll() {
		return dao.findAll();
	}
}
