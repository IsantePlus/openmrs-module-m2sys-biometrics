package org.openmrs.module.m2sysbiometrics.dao.impl;

import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.m2sysbiometrics.dao.M2SysNationalRegistrationFailDao;
import org.openmrs.module.m2sysbiometrics.model.NationalRegistrationFail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class M2SysNationalRegistrationFailDaoImpl implements M2SysNationalRegistrationFailDao {

    @Autowired
    private DbSessionFactory sessionFactory;

    @Override
    public NationalRegistrationFail save(NationalRegistrationFail nationalRegistrationFail) {
        sessionFactory.getCurrentSession().save(nationalRegistrationFail);
        return nationalRegistrationFail;
    }
}
