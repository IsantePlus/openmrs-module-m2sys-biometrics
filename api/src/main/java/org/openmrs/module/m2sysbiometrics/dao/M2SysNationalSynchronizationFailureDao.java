package org.openmrs.module.m2sysbiometrics.dao;

import org.openmrs.module.m2sysbiometrics.model.NationalSynchronizationFailure;

import java.util.List;

public interface M2SysNationalSynchronizationFailureDao {

    NationalSynchronizationFailure save(NationalSynchronizationFailure nationalSynchronizationFailure);

    void delete(NationalSynchronizationFailure nationalSynchronizationFailure);

    List<NationalSynchronizationFailure> findAllRegistrationFailures();
}
