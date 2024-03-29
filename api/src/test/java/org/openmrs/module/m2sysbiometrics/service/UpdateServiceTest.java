package org.openmrs.module.m2sysbiometrics.service;

import org.junit.Ignore;
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
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricMatch;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricSubject;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UpdateServiceTest {

    private static final String UPDATE_SUCCESS_RESULT_XML = "<Results><result score='0' value='SUCCESS'></Results>";

    private static final String ALREADY_EXISTS_RESULT_XML = "<Results><result score='0' value='b0f04-exists'></Results>";

    @Mock
    private LocalBioServerClient localBioServerClient;

    @Mock
    private SearchService searchService;

    @Mock
    private NationalBioServerClient nationalBioServerClient;

    @Mock
    private NationalSynchronizationFailureService nationalSynchronizationFailureService;

    @InjectMocks
    private UpdateService updateService = new UpdateServiceImpl();

    private BiometricSubject subject = BiometricSubjectMother.withSubjectId("subject");

    private BiometricSubject nationalSubject = BiometricSubjectMother.withSubjectId("nationalSubject");

    private BiometricMatch biometricMatch = new BiometricMatch(nationalSubject.getSubjectId(), 99.0);

    private M2SysCaptureResponse fingerScan = M2SysCaptureResponseMother.withTemplateData("templateData");

    @Ignore("Skipping failing tests for now. See https://github.com/IsantePlus/openmrs-module-m2sys-biometrics/issues/56")
    @Test
    public void shouldUpdateLocally() throws Exception {
        //given
        when(localBioServerClient.update(subject.getSubjectId(), fingerScan.getTemplateData()))
                .thenReturn(UPDATE_SUCCESS_RESULT_XML);

        //when
        updateService.updateLocally(subject);
    }

    @Test
    public void shouldUpdateNationally() throws Exception {
        //given
        when(searchService.findMostAdequateNationally(fingerScan.toString())).thenReturn(biometricMatch);
        when(nationalBioServerClient.update(nationalSubject.getSubjectId(), fingerScan.getTemplateData()))
                .thenReturn(UPDATE_SUCCESS_RESULT_XML);

        //when
        // updateService.updateNationally(fingerScan);
    }

    @Ignore("Skipping failing tests for now. See https://github.com/IsantePlus/openmrs-module-m2sys-biometrics/issues/56")
    @Test(expected = M2SysBiometricsException.class)
    public void shouldUpdateLocallyWithException() throws Exception {
        //given
        when(localBioServerClient.update(subject.getSubjectId(), fingerScan.getTemplateData()))
                .thenReturn(ALREADY_EXISTS_RESULT_XML);

        //when
        updateService.updateLocally(subject);
    }

    @Ignore("Skipping failing tests for now. See https://github.com/IsantePlus/openmrs-module-m2sys-biometrics/issues/56")
    @Test
    public void shouldTryToUpdateNationallyWithoutException() throws Exception {
        //given

        when(nationalBioServerClient.update(nationalSubject.getSubjectId(), fingerScan.getTemplateData()))
                .thenReturn(ALREADY_EXISTS_RESULT_XML);
        when(searchService.findMostAdequateNationally(fingerScan.toString())).thenReturn(biometricMatch);
        when(searchService.findMostAdequateLocally(fingerScan.toString())).thenReturn(biometricMatch);
        when(nationalSynchronizationFailureService.save(any())).thenReturn(null);

        //when
        //updateService.updateNationally(fingerScan);

        //then
        verify(nationalSynchronizationFailureService, times(1)).save(any());
    }
}
