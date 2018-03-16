package org.openmrs.module.m2sysbiometrics.bioplugin;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.Patient;
import org.openmrs.module.m2sysbiometrics.exception.M2SysBiometricsException;
import org.openmrs.module.m2sysbiometrics.model.M2SysResult;
import org.openmrs.module.m2sysbiometrics.testdata.BiometricSubjectMother;
import org.openmrs.module.m2sysbiometrics.testdata.PatientMother;
import org.openmrs.module.m2sysbiometrics.util.PatientHelper;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricSubject;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class LocalBioServerClientTest {

    @Mock
    private PatientHelper patientHelper;

    @InjectMocks
    private LocalBioServerClient localBioServerClient = new LocalBioServerClient();

    private BiometricSubject subject = BiometricSubjectMother.withSubjectId("subject");

    private Patient existingPatient = PatientMother.withName("Jose", "Arcadio", "Morales");

    @Test(expected = M2SysBiometricsException.class)
    public void shouldThrowExceptionIfPatientIsExists() throws Exception {
        localBioServerClient.handleRegistrationError(subject, M2SysResult.FAILED, existingPatient);

        verify(patientHelper, times(1)).findByNationalFpId(any());
    }
}
