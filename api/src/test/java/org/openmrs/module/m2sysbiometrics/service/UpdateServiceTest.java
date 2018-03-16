package org.openmrs.module.m2sysbiometrics.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.module.m2sysbiometrics.bioplugin.LocalBioServerClient;
import org.openmrs.module.m2sysbiometrics.bioplugin.NationalBioServerClient;
import org.openmrs.module.m2sysbiometrics.exception.M2SysBiometricsException;
import org.openmrs.module.m2sysbiometrics.model.M2SysCaptureResponse;
import org.openmrs.module.m2sysbiometrics.service.impl.UpdateServiceImpl;
import org.openmrs.module.m2sysbiometrics.testdata.BiometricSubjectMother;
import org.openmrs.module.m2sysbiometrics.testdata.M2SysCaptureResponseMother;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricSubject;

import java.util.UUID;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UpdateServiceTest {

    private static final String UPDATE_SUCCESS_RESULT_XML = "<Results><result score='0' value='SUCCESS'></Results>";

    private static final String ALREADY_EXISTS_RESULT_XML = "<Results><result score='0' value='b0f04-exists'></Results>";

    @Mock
    private LocalBioServerClient localBioServerClient;

    @Mock
    private NationalBioServerClient nationalBioServerClient;

    @InjectMocks
    private UpdateService updateService = new UpdateServiceImpl();

    private BiometricSubject subject = BiometricSubjectMother.withSubjectId("subject");

    private M2SysCaptureResponse capture = M2SysCaptureResponseMother.withTemplateData("templateData");

    @Test
    public void shouldUpdateLocally() throws Exception {
        //given
        when(localBioServerClient.update(subject.getSubjectId(), capture.getTemplateData()))
                .thenReturn(UPDATE_SUCCESS_RESULT_XML);

        //when
        updateService.updateLocally(subject, capture);
    }

    @Test
    public void shouldUpdateNationally() throws Exception {
        //given
        String nationalId = UUID.randomUUID().toString();
        when(nationalBioServerClient.update(nationalId, capture.getTemplateData()))
                .thenReturn(UPDATE_SUCCESS_RESULT_XML);

        //when
        updateService.updateNationally(nationalId, capture);
    }

    @Test(expected = M2SysBiometricsException.class)
    public void shouldUpdateLocallyWithException() throws Exception {
        //given
        when(localBioServerClient.update(subject.getSubjectId(), capture.getTemplateData()))
                .thenReturn(ALREADY_EXISTS_RESULT_XML);

        //when
        updateService.updateLocally(subject, capture);
    }

    @Test
    public void shouldTryToUpdateNationallyWithoutException() throws Exception {
        //given
        String nationalId = UUID.randomUUID().toString();
        when(nationalBioServerClient.update(nationalId, capture.getTemplateData()))
                .thenReturn(ALREADY_EXISTS_RESULT_XML);

        //when
        updateService.updateNationally(nationalId, capture);
    }
}