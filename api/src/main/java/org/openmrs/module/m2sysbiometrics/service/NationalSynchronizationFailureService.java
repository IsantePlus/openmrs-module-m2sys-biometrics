package org.openmrs.module.m2sysbiometrics.service;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.m2sysbiometrics.model.NationalSynchronizationFailure;

import javax.transaction.Transactional;

public interface NationalSynchronizationFailureService extends OpenmrsService {

    @Transactional
    NationalSynchronizationFailure save(NationalSynchronizationFailure nationalSynchronizationFailure);
}
