package org.openmrs.module.m2sysbiometrics.service.impl;

import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.m2sysbiometrics.dao.M2SysNationalSynchronizationFailureDao;
import org.openmrs.module.m2sysbiometrics.model.NationalSynchronizationFailure;
import org.openmrs.module.m2sysbiometrics.service.NationalSynchronizationFailureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service(value = "nationalSynchronizationFailureService")
public class NationalSynchronizationFailureServiceImpl extends BaseOpenmrsService
        implements NationalSynchronizationFailureService {

    @Autowired
    private M2SysNationalSynchronizationFailureDao dao;

    @Override
    public NationalSynchronizationFailure save(NationalSynchronizationFailure nationalSynchronizationFailure) {
        return dao.save(nationalSynchronizationFailure);
    }

    @Override
    public NationalSynchronizationFailure saveOrReplaceRegistrationFailure(
            NationalSynchronizationFailure nationalSynchronizationFailure) {
        if (nationalSynchronizationFailure.getBiometricXml() != null) {
            for (NationalSynchronizationFailure failureFromDB : dao.findAllRegistrationFailuresByBiometricXml(
                    nationalSynchronizationFailure.getBiometricXml())) {
                dao.delete(failureFromDB);
            }
        }
        return dao.save(nationalSynchronizationFailure);
    }

    @Override
    public void delete(NationalSynchronizationFailure nationalSynchronizationFailure) {
        dao.delete(nationalSynchronizationFailure);
    }

    @Override
    public List<NationalSynchronizationFailure> findAllRegistrationFailures() {
        return dao.findAllRegistrationFailures();
    }
}
