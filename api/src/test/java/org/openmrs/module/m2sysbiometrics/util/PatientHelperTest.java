package org.openmrs.module.m2sysbiometrics.util;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.m2sysbiometrics.util.impl.PatientHelperImpl;
import org.openmrs.module.registrationcore.api.RegistrationCoreService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collections;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openmrs.module.registrationcore.RegistrationCoreConstants.GP_BIOMETRICS_NATIONAL_PERSON_IDENTIFIER_TYPE_UUID;
import static org.openmrs.module.registrationcore.RegistrationCoreConstants.GP_BIOMETRICS_PERSON_IDENTIFIER_TYPE_UUID;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@Ignore("Skipping failing tests for now. See https://github.com/IsantePlus/openmrs-module-m2sys-biometrics/issues/56")
// @RunWith(PowerMockRunner.class)
@PrepareForTest({Context.class})
public class PatientHelperTest {

    private static final String LOCAL_ID_TYPE_UUID = UUID.randomUUID().toString();
    private static final String NATIONAL_ID_TYPE_UUID = UUID.randomUUID().toString();
    private static final String SUBJECT_ID = "xxxx-234";

    @InjectMocks
    private PatientHelper patientHelper = new PatientHelperImpl();

    @Mock
    private PatientService patientService;

    @Mock
    private RegistrationCoreService registrationCoreService;

    @Mock
    private M2SysProperties properties;

    @Mock
    private PatientIdentifierType idType;

    @Mock
    private Patient patient;

    @Test
    public void shouldReturnNullIfLocalFpIdNotDefined() {
        // given
        when(properties.getGlobalProperty(GP_BIOMETRICS_PERSON_IDENTIFIER_TYPE_UUID))
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
        when(properties.getGlobalProperty(GP_BIOMETRICS_PERSON_IDENTIFIER_TYPE_UUID))
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
        when(properties.isGlobalPropertySet(GP_BIOMETRICS_PERSON_IDENTIFIER_TYPE_UUID))
                .thenReturn(true);
        when(properties.getGlobalProperty(GP_BIOMETRICS_PERSON_IDENTIFIER_TYPE_UUID))
                .thenReturn(LOCAL_ID_TYPE_UUID);

        when(registrationCoreService.findByPatientIdentifier(SUBJECT_ID, LOCAL_ID_TYPE_UUID))
                .thenReturn(patient);
        when(patientService.getPatientIdentifierTypeByUuid(LOCAL_ID_TYPE_UUID)).thenReturn(idType);

        PatientIdentifier pi = new PatientIdentifier();
        pi.setIdentifierType(idType);
        when(patient.getIdentifiers()).thenReturn(Collections.singleton(pi));

        mockStatic(Context.class);

        // when
        Patient result = patientHelper.findByLocalFpId(SUBJECT_ID);

        // then
        assertEquals(patient, result);
    }

    @Test
    public void shouldReturnNullIfNationalFpIdNotDefined() {
        // given
        when(properties.getGlobalProperty(GP_BIOMETRICS_NATIONAL_PERSON_IDENTIFIER_TYPE_UUID))
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
        when(properties.getGlobalProperty(GP_BIOMETRICS_NATIONAL_PERSON_IDENTIFIER_TYPE_UUID))
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
        when(properties.isGlobalPropertySet(GP_BIOMETRICS_NATIONAL_PERSON_IDENTIFIER_TYPE_UUID))
                .thenReturn(true);
        when(properties.getGlobalProperty(GP_BIOMETRICS_NATIONAL_PERSON_IDENTIFIER_TYPE_UUID))
                .thenReturn(NATIONAL_ID_TYPE_UUID);

        when(patientService.getPatientIdentifierTypeByUuid(NATIONAL_ID_TYPE_UUID)).thenReturn(idType);
        when(registrationCoreService.findByPatientIdentifier(SUBJECT_ID, NATIONAL_ID_TYPE_UUID))
                .thenReturn(patient);

        PatientIdentifier pi = new PatientIdentifier();
        pi.setIdentifierType(idType);
        when(patient.getIdentifiers()).thenReturn(Collections.singleton(pi));

        mockStatic(Context.class);

        // when
        Patient result = patientHelper.findByNationalFpId(SUBJECT_ID);

        // then
        assertEquals(patient, result);
    }
}
