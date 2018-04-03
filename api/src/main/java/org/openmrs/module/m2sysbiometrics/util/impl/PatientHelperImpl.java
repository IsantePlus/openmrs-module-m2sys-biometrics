package org.openmrs.module.m2sysbiometrics.util.impl;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.LocationService;
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

    @Autowired
    private LocationService locationService;

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

    @Override
    public void changeLocalFpId(Patient patient, String newLocalSubjectId) {
        String identifierUuid = properties.getGlobalProperty(
                RegistrationCoreConstants.GP_BIOMETRICS_PERSON_IDENTIFIER_TYPE_UUID);
        PatientIdentifierType pit = patientService.getPatientIdentifierTypeByUuid(identifierUuid);

        PatientIdentifier localFpPatientIdentifier = patient.getPatientIdentifier(pit);
        localFpPatientIdentifier.setIdentifier(newLocalSubjectId);
        patientService.savePatientIdentifier(localFpPatientIdentifier);
    }

    @Override
    public void attachNationalIdToThePatient(Patient patient, String nationalId) {
        attachIdToThePatient(patient, nationalId, properties.getNationalPatientIdentifierTypeUuid());
    }

    @Override
    public void attachLocalIdToThePatient(Patient patient, String localId) {
        attachIdToThePatient(patient, localId, properties.getLocalPatientIdentifierTypeUuid());
    }

    private void attachIdToThePatient(Patient patient, String id, String identifierTypeUuid) {
        PatientIdentifierType patientIdentifierType = patientService.getPatientIdentifierTypeByUuid(identifierTypeUuid);
        Location location = locationService.getDefaultLocation();
        PatientIdentifier identifier = new PatientIdentifier(id, patientIdentifierType, location);
        patient.addIdentifier(identifier);
        patientService.savePatient(patient);
    }

    private Patient findByIdType(String subjectId, String idTypeProp) {
        Patient patient = null;
        if (properties.isGlobalPropertySet(idTypeProp)) {
            String identifierUuid = properties.getGlobalProperty(idTypeProp);
            if (patientIdentifierTypeExists(identifierUuid)) {
                patient = registrationCoreService.findByPatientIdentifier(subjectId, identifierUuid);
            } else {
                LOGGER.warn("Identifier type defined by prop {} is missing: {}", idTypeProp, identifierUuid);
            }
        }
        return patient;
    }

    private boolean patientIdentifierTypeExists(String patientIdentifierTypeUuid) {
        return patientService.getPatientIdentifierTypeByUuid(patientIdentifierTypeUuid) != null;
    }
}
