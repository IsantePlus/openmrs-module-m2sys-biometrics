package org.openmrs.module.m2sysbiometrics.dao;

import org.openmrs.module.m2sysbiometrics.model.TempFingerprint;

import java.util.List;

public interface TempFingerprintDao {

    TempFingerprint saveOrUpdate(TempFingerprint tempFingerprint);

    void delete(TempFingerprint tempFingerprint);

    TempFingerprint findOneByBiometricId(String biometricId);

    List<TempFingerprint> findAllByBiometricXml(String biometricXml);

}
