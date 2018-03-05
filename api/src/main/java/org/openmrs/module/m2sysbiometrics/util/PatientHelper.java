package org.openmrs.module.m2sysbiometrics.util;

import org.openmrs.Patient;

public interface PatientHelper {

    Patient findByLocalFpId(String subjectId);

    Patient findByNationalFpId(String nationalSubjectId);
}
