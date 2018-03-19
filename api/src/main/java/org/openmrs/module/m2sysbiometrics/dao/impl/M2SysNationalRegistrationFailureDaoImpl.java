package org.openmrs.module.m2sysbiometrics.dao.impl;

import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.m2sysbiometrics.dao.M2SysNationalRegistrationFailureDao;
import org.openmrs.module.m2sysbiometrics.model.NationalRegistrationFailure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class M2SysNationalRegistrationFailureDaoImpl implements M2SysNationalRegistrationFailureDao {

    @Autowired
    private DbSessionFactory sessionFactory;

    @Override
    public NationalRegistrationFailure save(NationalRegistrationFailure nationalRegistrationFailure) {
        sessionFactory.getCurrentSession().save(nationalRegistrationFailure);
        return nationalRegistrationFailure;
    }
}
