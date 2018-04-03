package org.openmrs.module.m2sysbiometrics.service.impl;

import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.m2sysbiometrics.dao.TempFingerprintDao;
import org.openmrs.module.m2sysbiometrics.model.TempFingerprint;
import org.openmrs.module.m2sysbiometrics.service.TempFingerprintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service(value = "tempFingerprintService")
public class TempFingerprintServiceImpl extends BaseOpenmrsService
        implements TempFingerprintService {
    @Autowired
    private TempFingerprintDao dao;

    @Override
    public TempFingerprint save(TempFingerprint tempFingerprint) {
        return dao.save(tempFingerprint);
    }

    @Override
    public void delete(TempFingerprint tempFingerprint) {
        dao.delete(tempFingerprint);
    }

    @Override
    public TempFingerprint findOneByBiometricId(String biometricId) {
        return dao.findOneByBiometricId(biometricId);
    }

    @Override
    public List<TempFingerprint> findAllByBiometricXml(String biometricXml) {
        return dao.findAllByBiometricXml(biometricXml);
    }
}
