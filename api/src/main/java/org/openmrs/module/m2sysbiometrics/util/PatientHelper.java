package org.openmrs.module.m2sysbiometrics.util;

import org.openmrs.Patient;

public interface PatientHelper {

    Patient findByLocalFpId(String subjectId);

    Patient findByNationalFpId(String nationalSubjectId);

    void changeLocalFpId(Patient patient, String newLocalSubjectId);

    void attachNationalIdToThePatient(Patient patient, String nationalId);

    void attachLocalIdToThePatient(Patient patient, String localId);
}
