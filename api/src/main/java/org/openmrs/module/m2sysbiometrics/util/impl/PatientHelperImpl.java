package org.openmrs.module.m2sysbiometrics.util.impl;

import java.util.Collections;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.PatientService;
import org.openmrs.module.m2sysbiometrics.util.M2SysProperties;
import org.openmrs.module.m2sysbiometrics.util.PatientHelper;
import org.openmrs.module.registrationcore.RegistrationCoreConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component(value = "patientHelper")
public class PatientHelperImpl implements PatientHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(PatientHelperImpl.class);

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
        if (properties.isGlobalPropertySet(idTypeProp)) {
            String identifierUuid = properties.getGlobalProperty(idTypeProp);
            PatientIdentifierType idType = patientService.getPatientIdentifierTypeByUuid(identifierUuid);
            if (idType == null) {
                LOGGER.warn("Identifier type defined by prop {} is missing: {}", idTypeProp, identifierUuid);
            } else {
                //Currently method getPatients() doesn't take into consideration the identifierTypes,
                //so it needs to be filtered anyway
                List<Patient> patients = patientService.getPatients(null, subjectId,
                        Collections.singletonList(idType), true).stream()
                        .filter(p -> p.getIdentifiers().stream().anyMatch(pi -> pi.getIdentifierType().equals(idType)))
                        .collect(Collectors.toList());

                return CollectionUtils.isEmpty(patients) ? null : patients.get(0);
            }
        }
        return null;
    }
}
