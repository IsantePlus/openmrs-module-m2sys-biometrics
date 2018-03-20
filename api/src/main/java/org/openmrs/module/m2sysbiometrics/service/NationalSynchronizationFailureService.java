package org.openmrs.module.m2sysbiometrics.service;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.m2sysbiometrics.model.NationalSynchronizationFailure;

import javax.transaction.Transactional;
import java.util.List;

public interface NationalSynchronizationFailureService extends OpenmrsService {

    @Transactional
    NationalSynchronizationFailure saveOrUpdate(NationalSynchronizationFailure nationalSynchronizationFailure);

    @Transactional
    void delete(NationalSynchronizationFailure nationalSynchronizationFailure);

    @Transactional
    List<NationalSynchronizationFailure> findAllRegistrationFailures();
}
