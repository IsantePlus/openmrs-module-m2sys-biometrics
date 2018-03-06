package org.openmrs.module.m2sysbiometrics.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.Patient;
import org.openmrs.module.m2sysbiometrics.bioplugin.LocalBioServerClient;
import org.openmrs.module.m2sysbiometrics.bioplugin.NationalBioServerClient;
import org.openmrs.module.m2sysbiometrics.exception.M2SysBiometricsException;
import org.openmrs.module.m2sysbiometrics.model.M2SysCaptureResponse;
import org.openmrs.module.m2sysbiometrics.testdata.BiometricSubjectMother;
import org.openmrs.module.m2sysbiometrics.testdata.M2SysCaptureResponseMother;
import org.openmrs.module.m2sysbiometrics.testdata.PatientMother;
import org.openmrs.module.m2sysbiometrics.service.impl.RegistrationServiceImpl;
import org.openmrs.module.m2sysbiometrics.util.PatientHelper;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricSubject;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RegistrationServiceTest {

    private static final String EXISTING_RESULT_XML = "<Results><result score='1' value='SUCCESS'></Results>";

    private static final String EMPTY_RESULT_XML = "<Results><result score='0' value='empty'></Results>";

    @Mock
    private LocalBioServerClient localBioServerClient;

    @Mock
    private NationalBioServerClient nationalBioServerClient;

    @Mock
    private PatientHelper patientHelper;

    @InjectMocks
    private RegistrationService registrationService = new RegistrationServiceImpl();

    private BiometricSubject subject = BiometricSubjectMother.withSubjectId("subject");

    private M2SysCaptureResponse capture = M2SysCaptureResponseMother.withTemplateData("templateData");

    private Patient existingPatient = PatientMother.withName("Jose", "Arcadio", "Morales");

    @Test
    public void shouldRegisterLocally() throws Exception {
        //given
        when(localBioServerClient.enroll(subject.getSubjectId(), capture.getTemplateData())).thenReturn(EXISTING_RESULT_XML);

        //when
        registrationService.register(localBioServerClient, subject, capture);

        //then
        verify(patientHelper, times(0)).findByLocalFpId(any());
    }

    @Test
    public void shouldRegisterNationally() throws Exception {
        //given
        when(nationalBioServerClient.enroll(subject.getSubjectId(), capture.getTemplateData())).thenReturn(EXISTING_RESULT_XML);

        //when
        registrationService.register(nationalBioServerClient, subject, capture);

        //then
        verify(patientHelper, times(0)).findByNationalFpId(any());
    }

    @Test(expected = M2SysBiometricsException.class)
    public void shouldNotRegisterLocallyWithException() throws Exception {
        //given
        when(patientHelper.findByLocalFpId(any())).thenReturn(existingPatient);
        when(localBioServerClient.enroll(subject.getSubjectId(), capture.getTemplateData())).thenReturn(EMPTY_RESULT_XML);

        //when
        registrationService.register(localBioServerClient, subject, capture);

        //then
        verify(patientHelper, times(1)).findByNationalFpId(any());
    }

    @Test
    public void shouldNotRegisterNationallyWithoutException() throws Exception {
        //given
        when(patientHelper.findByNationalFpId(any())).thenReturn(existingPatient);
        when(nationalBioServerClient.enroll(subject.getSubjectId(), capture.getTemplateData())).thenReturn(EMPTY_RESULT_XML);

        //when
        registrationService.register(nationalBioServerClient, subject, capture);

        //then
        verify(patientHelper, times(1)).findByNationalFpId(any());
    }
}
