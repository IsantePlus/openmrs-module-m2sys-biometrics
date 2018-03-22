package org.openmrs.module.m2sysbiometrics.dao.impl;

import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.m2sysbiometrics.dao.M2SysNationalSynchronizationFailureDao;
import org.openmrs.module.m2sysbiometrics.model.NationalSynchronizationFailure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class M2SysNationalSynchronizationFailureDaoImpl implements M2SysNationalSynchronizationFailureDao {

    private static final String TABLE_NAME = "national_synchronization_failure";

    private static final String UPDATE = "update_failure";

    private static final String CREATED_DATE = "date_created";

    @Autowired
    private DbSessionFactory sessionFactory;

    @Override
    public NationalSynchronizationFailure save(NationalSynchronizationFailure nationalSynchronizationFailure) {
        sessionFactory.getCurrentSession().save(nationalSynchronizationFailure);
        return nationalSynchronizationFailure;
    }

    @Override
    public void delete(NationalSynchronizationFailure nationalSynchronizationFailure) {
        sessionFactory.getCurrentSession().delete(nationalSynchronizationFailure);
    }

    @Override
    public List<NationalSynchronizationFailure> findAllRegistrationFailures() {
        return findListByQuery(
                "SELECT * FROM " + TABLE_NAME + " failure "
                        + "WHERE failure." + UPDATE + "=0 "
                        + "ORDER BY " + CREATED_DATE);
    }

    @Override
    public List<NationalSynchronizationFailure> findAllUpdateFailures() {
        return findListByQuery(
                "SELECT * FROM " + TABLE_NAME + " failure "
                        + "WHERE failure." + UPDATE + "=1 "
                        + "ORDER BY " + CREATED_DATE);
    }

    private List<NationalSynchronizationFailure> findListByQuery(String query) {
        return sessionFactory.getCurrentSession().createSQLQuery(query)
                .addEntity(NationalSynchronizationFailure.class).list();
    }
}
