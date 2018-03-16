package org.openmrs.module.m2sysbiometrics.service;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.m2sysbiometrics.model.NationalRegistrationFail;

import javax.transaction.Transactional;

public interface NationalRegistrationFailService extends OpenmrsService {

    @Transactional
    NationalRegistrationFail save(NationalRegistrationFail nationalRegistrationFail);
}
