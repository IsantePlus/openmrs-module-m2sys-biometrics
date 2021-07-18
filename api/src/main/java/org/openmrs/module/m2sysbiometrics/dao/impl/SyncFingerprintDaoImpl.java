package org.openmrs.module.m2sysbiometrics.dao.impl;

import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.m2sysbiometrics.dao.SyncFingerprintDao;
import org.openmrs.module.m2sysbiometrics.model.SyncFingerprint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SyncFingerprintDaoImpl implements SyncFingerprintDao {

    private static final String TABLE_NAME = "sync_fingerprint";

    private static final String BIOMETRIC_ID_COLUMN = "biometric_id";

    private static final String BIOMETRIC_XML_COLUMN = "biometric_xml";

    @Autowired
    private DbSessionFactory sessionFactory;

    @Override
    public SyncFingerprint saveOrUpdate(SyncFingerprint syncFingerprint) {
        SyncFingerprint existing = findOneByBiometricId(syncFingerprint.getBiometricId());

        if (existing == null) {
            sessionFactory.getCurrentSession().save(syncFingerprint);
            return syncFingerprint;
        } else {
            existing.setBiometricXml(syncFingerprint.getBiometricXml());
            sessionFactory.getCurrentSession().update(existing);
            return existing;
        }

    }

    @Override
    public void delete(SyncFingerprint syncFingerprint) {
        sessionFactory.getCurrentSession().delete(syncFingerprint);
    }

    @Override
    public SyncFingerprint findOneByBiometricId(String biometricId) {
        SyncFingerprint result = null;
        List<SyncFingerprint> results = findListByQuery(
                "SELECT * FROM " + TABLE_NAME + " WHERE " + BIOMETRIC_ID_COLUMN + "=\'" + biometricId + '\'');
        if (results != null && results.size() > 0) {
            result = results.get(0);
        }
        return result;
    }


    @Override
    public List<SyncFingerprint> findAllByBiometricXml(String biometricXml) {
        return findListByQuery(
                "SELECT * FROM " + TABLE_NAME + "WHERE " + BIOMETRIC_XML_COLUMN + "=\'" + biometricXml + '\'');
    }

    @Override
    public List<SyncFingerprint> findAll() {
        return findListByQuery(
                "SELECT * FROM " + TABLE_NAME );
    }

    private List<SyncFingerprint> findListByQuery(String query) {
        return sessionFactory.getCurrentSession().createSQLQuery(query)
                .addEntity(SyncFingerprint.class).list();
    }
}
