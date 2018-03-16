package org.openmrs.module.m2sysbiometrics.service.impl;

import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.m2sysbiometrics.dao.M2SysNationalRegistrationFailDao;
import org.openmrs.module.m2sysbiometrics.model.NationalRegistrationFail;
import org.openmrs.module.m2sysbiometrics.service.NationalRegistrationFailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NationalRegistrationFailServiceImpl extends BaseOpenmrsService implements NationalRegistrationFailService {

    @Autowired
    private M2SysNationalRegistrationFailDao dao;

    @Override
    public NationalRegistrationFail save(NationalRegistrationFail nationalRegistrationFail) {
        dao.save(nationalRegistrationFail);
        return nationalRegistrationFail;
    }
}
