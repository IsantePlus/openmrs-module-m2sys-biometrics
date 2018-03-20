package org.openmrs.module.m2sysbiometrics.dao.impl;

import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.m2sysbiometrics.dao.M2SysNationalSynchronizationFailureDao;
import org.openmrs.module.m2sysbiometrics.model.NationalSynchronizationFailure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class M2SysNationalSynchronizationFailureDaoImpl implements M2SysNationalSynchronizationFailureDao {

    @Autowired
    private DbSessionFactory sessionFactory;

    @Override
    public NationalSynchronizationFailure save(NationalSynchronizationFailure nationalSynchronizationFailure) {
        sessionFactory.getCurrentSession().save(nationalSynchronizationFailure);
        return nationalSynchronizationFailure;
    }
}
