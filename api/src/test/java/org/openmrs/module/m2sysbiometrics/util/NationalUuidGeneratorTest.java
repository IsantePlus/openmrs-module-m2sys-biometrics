package org.openmrs.module.m2sysbiometrics.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.openmrs.module.m2sysbiometrics.bioplugin.NationalBioServerClient;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class NationalUuidGeneratorTest {

    @InjectMocks
    private NationalUuidGenerator nationalUuidGenerator;

    @Mock
    private NationalBioServerClient nationalBioServerClient;

    @Test
    public void shouldGenerateUuid() {
        String response = "<Results><result value='-1'/></Results>";
        Mockito.when(nationalBioServerClient.isRegistered(anyString())).thenReturn(response);

        String uuid = nationalUuidGenerator.generate();

        assertNotNull(uuid);
        verify(nationalBioServerClient).isRegistered(uuid);
    }

    @Test
    public void shouldKeepGeneratingUuidIfAlreadyRegistered() {
        Mockito.when(nationalBioServerClient.isRegistered(anyString())).thenAnswer(new Answer<String>() {
            private int i = 0;

            @Override
            public String answer(InvocationOnMock invocationOnMock) throws Throwable {
                if (i == 0) {
                    i++;
                    return "<Results><result value='REGISTERED_ID'/></Results>";
                } else {
                    return "<Results><result value='-1'/></Results>";
                }
            }
        });

        String uuid = nationalUuidGenerator.generate();

        assertNotNull(uuid);
        verify(nationalBioServerClient, times(2)).isRegistered(anyString());
    }
}
