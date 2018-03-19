package org.openmrs.module.m2sysbiometrics.service.impl;

import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.m2sysbiometrics.dao.M2SysNationalRegistrationFailureDao;
import org.openmrs.module.m2sysbiometrics.model.NationalRegistrationFailure;
import org.openmrs.module.m2sysbiometrics.service.NationalRegistrationFailureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NationalRegistrationFailureServiceImpl extends BaseOpenmrsService
        implements NationalRegistrationFailureService {

    @Autowired
    private M2SysNationalRegistrationFailureDao dao;

    @Override
    public NationalRegistrationFailure save(NationalRegistrationFailure nationalRegistrationFailure) {
        dao.save(nationalRegistrationFailure);
        return nationalRegistrationFailure;
    }
}
