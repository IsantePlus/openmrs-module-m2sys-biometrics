package org.openmrs.module.m2sysbiometrics.dao;

import org.openmrs.module.m2sysbiometrics.model.SyncFingerprint;

import java.util.List;

public interface SyncFingerprintDao {

    SyncFingerprint saveOrUpdate(SyncFingerprint syncFingerprint);

    void delete(SyncFingerprint syncFingerprint);

    SyncFingerprint findOneByBiometricId(String biometricId);

    List<SyncFingerprint> findAllByBiometricXml(String biometricXml);

    List<SyncFingerprint> findAll();
}
