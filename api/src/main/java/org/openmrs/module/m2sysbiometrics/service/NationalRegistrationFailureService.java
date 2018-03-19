package org.openmrs.module.m2sysbiometrics.service;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.m2sysbiometrics.model.NationalRegistrationFailure;

import javax.transaction.Transactional;

public interface NationalRegistrationFailureService extends OpenmrsService {

    @Transactional
    NationalRegistrationFailure save(NationalRegistrationFailure nationalRegistrationFailure);
}
