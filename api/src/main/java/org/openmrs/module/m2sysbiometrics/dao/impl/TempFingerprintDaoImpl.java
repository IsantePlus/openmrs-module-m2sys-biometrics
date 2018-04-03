package org.openmrs.module.m2sysbiometrics.dao.impl;

import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.m2sysbiometrics.dao.TempFingerprintDao;
import org.openmrs.module.m2sysbiometrics.model.TempFingerprint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TempFingerprintDaoImpl implements TempFingerprintDao {

    private static final String TABLE_NAME = "temp_fingerprint";

    private static final String BIOMETRIC_ID_COLUMN = "biometric_id";

    private static final String BIOMETRIC_XML_COLUMN = "biometric_xml";

    @Autowired
    private DbSessionFactory sessionFactory;

    @Override
    public TempFingerprint save(TempFingerprint tempFingerprint) {
        sessionFactory.getCurrentSession().save(tempFingerprint);
        return tempFingerprint;
    }

    @Override
    public void delete(TempFingerprint tempFingerprint) {
        sessionFactory.getCurrentSession().delete(tempFingerprint);
    }

    @Override
    public TempFingerprint findOneByBiometricId(String biometricId) {
        TempFingerprint result = null;
        List<TempFingerprint> results = findListByQuery(
                "SELECT * FROM " + TABLE_NAME + " fp "
                        + "WHERE fp." + BIOMETRIC_ID_COLUMN + "=\'" + biometricId + '\'');
        if (results != null && results.size() > 0) {
            result = results.get(0);
        }
        return result;
    }


    @Override
    public List<TempFingerprint> findAllByBiometricXml(String biometricXml) {
        return findListByQuery(
                "SELECT * FROM " + TABLE_NAME + " fp "
                        + "WHERE fp." + BIOMETRIC_XML_COLUMN + "=\'" + biometricXml + '\'');
    }

    private List<TempFingerprint> findListByQuery(String query) {
        return sessionFactory.getCurrentSession().createSQLQuery(query)
                .addEntity(TempFingerprint.class).list();
    }
}
