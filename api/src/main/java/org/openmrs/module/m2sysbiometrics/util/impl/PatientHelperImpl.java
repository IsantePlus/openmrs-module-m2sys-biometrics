package org.openmrs.module.m2sysbiometrics.util.impl;

import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.module.m2sysbiometrics.util.M2SysProperties;
import org.openmrs.module.m2sysbiometrics.util.PatientHelper;
import org.openmrs.module.registrationcore.RegistrationCoreConstants;
import org.openmrs.module.registrationcore.api.RegistrationCoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PatientHelperImpl implements PatientHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(PatientHelperImpl.class);

    @Autowired
    private RegistrationCoreService registrationCoreService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private M2SysProperties properties;

    @Override
    public Patient findByLocalFpId(String subjectId) {
        return findByIdType(subjectId,
                RegistrationCoreConstants.GP_BIOMETRICS_PERSON_IDENTIFIER_TYPE_UUID);
    }

    @Override
    public Patient findByNationalFpId(String nationalSubjectId) {
        return findByIdType(nationalSubjectId,
                RegistrationCoreConstants.GP_BIOMETRICS_NATIONAL_PERSON_IDENTIFIER_TYPE_UUID);
    }

    private Patient findByIdType(String subjectId, String idTypeProp) {
        Patient patient = null;
        if (properties.isGlobalPropertySet(idTypeProp)) {
            String identifierUuid = properties.getGlobalProperty(idTypeProp);
            if (isPatientIdentifierTypeExists(identifierUuid)) {
                patient = registrationCoreService.findByPatientIdentifier(subjectId, identifierUuid);
            } else {
                LOGGER.warn("Identifier type defined by prop {} is missing: {}", idTypeProp, identifierUuid);
            }
        }
        return patient;
    }

    private boolean isPatientIdentifierTypeExists(String patientIdentifierTypeUuid) {
        return patientService.getPatientIdentifierTypeByUuid(patientIdentifierTypeUuid) != null;
    }
}
