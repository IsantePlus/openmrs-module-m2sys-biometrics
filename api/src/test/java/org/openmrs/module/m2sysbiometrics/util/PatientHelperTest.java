package org.openmrs.module.m2sysbiometrics.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PatientService;
import org.openmrs.module.m2sysbiometrics.util.impl.PatientHelperImpl;

import java.util.UUID;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.openmrs.module.registrationcore.RegistrationCoreConstants.GP_BIOMETRICS_NATIONAL_PERSON_IDENTIFIER_TYPE_UUID;
import static org.openmrs.module.registrationcore.RegistrationCoreConstants.GP_BIOMETRICS_PERSON_IDENTIFIER_TYPE_UUID;

@RunWith(MockitoJUnitRunner.class)
public class PatientHelperTest {

    private static final String LOCAL_ID_TYPE_UUID = UUID.randomUUID().toString();
    private static final String NATIONAL_ID_TYPE_UUID = UUID.randomUUID().toString();
    private static final String SUBJECT_ID = "xxxx-234";

    @InjectMocks
    private PatientHelper patientHelper = new PatientHelperImpl();

    @Mock
    private PatientService patientService;

    @Mock
    private AdministrationService adminService;

    @Mock
    private PatientIdentifierType idType;

    @Mock
    private Patient patient;

    @Test
    public void shouldReturnNullIfLocalFpIdNotDefined() {
        // given
        when(adminService.getGlobalProperty(GP_BIOMETRICS_PERSON_IDENTIFIER_TYPE_UUID, null))
                .thenReturn(null);

        // when
        Patient result = patientHelper.findByLocalFpId(SUBJECT_ID);

        // then
        assertNull(result);
        verify(patientService, never()).getPatients(anyString(), anyString(), any(), anyBoolean());
    }

    @Test
    public void shouldReturnNullIfLocalIdTypeMissing() {
        // given
        when(adminService.getGlobalProperty(GP_BIOMETRICS_PERSON_IDENTIFIER_TYPE_UUID, null))
                .thenReturn(LOCAL_ID_TYPE_UUID);
        when(patientService.getPatientIdentifierTypeByUuid(LOCAL_ID_TYPE_UUID)).thenReturn(null);

        // when
        Patient result = patientHelper.findByLocalFpId(SUBJECT_ID);

        // then
        assertNull(result);
        verify(patientService, never()).getPatients(anyString(), anyString(), any(), anyBoolean());
    }

    @Test
    public void shouldFindPatientByLocalId() {
        // given
        when(adminService.getGlobalProperty(GP_BIOMETRICS_PERSON_IDENTIFIER_TYPE_UUID, null))
                .thenReturn(LOCAL_ID_TYPE_UUID);
        when(patientService.getPatientIdentifierTypeByUuid(LOCAL_ID_TYPE_UUID)).thenReturn(idType);
        when(patientService.getPatients(null, SUBJECT_ID, singletonList(idType), true))
                .thenReturn(singletonList(patient));

        // when
        Patient result = patientHelper.findByLocalFpId(SUBJECT_ID);

        // then
        assertEquals(patient, result);
    }

    @Test
    public void shouldReturnNullIfNationalFpIdNotDefined() {
        // given
        when(adminService.getGlobalProperty(GP_BIOMETRICS_NATIONAL_PERSON_IDENTIFIER_TYPE_UUID, null))
                .thenReturn(null);

        // when
        Patient result = patientHelper.findByNationalFpId(SUBJECT_ID);

        // then
        assertNull(result);
        verify(patientService, never()).getPatients(anyString(), anyString(), any(), anyBoolean());
    }

    @Test
    public void shouldReturnNullIfNationalIdTypeMissing() {
        // given
        when(adminService.getGlobalProperty(GP_BIOMETRICS_NATIONAL_PERSON_IDENTIFIER_TYPE_UUID, null))
                .thenReturn(NATIONAL_ID_TYPE_UUID);
        when(patientService.getPatientIdentifierTypeByUuid(NATIONAL_ID_TYPE_UUID)).thenReturn(null);

        // when
        Patient result = patientHelper.findByNationalFpId(SUBJECT_ID);

        // then
        assertNull(result);
        verify(patientService, never()).getPatients(anyString(), anyString(), any(), anyBoolean());
    }

    @Test
    public void shouldFindPatientByNationalId() {
        // given
        when(adminService.getGlobalProperty(GP_BIOMETRICS_NATIONAL_PERSON_IDENTIFIER_TYPE_UUID, null))
                .thenReturn(NATIONAL_ID_TYPE_UUID);
        when(patientService.getPatientIdentifierTypeByUuid(NATIONAL_ID_TYPE_UUID)).thenReturn(idType);
        when(patientService.getPatients(null, SUBJECT_ID, singletonList(idType), true))
                .thenReturn(singletonList(patient));

        // when
        Patient result = patientHelper.findByNationalFpId(SUBJECT_ID);

        // then
        assertEquals(patient, result);
    }
}
