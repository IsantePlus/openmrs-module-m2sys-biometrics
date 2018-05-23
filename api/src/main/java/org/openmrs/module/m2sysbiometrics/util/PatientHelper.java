package org.openmrs.module.m2sysbiometrics.util;

import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;

public interface PatientHelper {

    Patient findByLocalFpId(String subjectId);

    Patient findByNationalFpId(String nationalSubjectId);

    PatientIdentifierType getPatientIdentifierTypeByUuid(String patientIdentifierTypeUuid);
}
