package org.openmrs.module.m2sysbiometrics.bioplugin;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.m2sysbiometrics.model.M2SysCaptureResponse;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LocalBioServerClientTest {

    private static final String EMPTY_RESULT_XML = "<Results><result score='0' value='empty'></Results>";
    private static final String EXISTING_RESULT_XML = "<Results><result score='1' value='16'></Results>";
    private static final String LOCAL_TEMPLATE_DATA = "local";

    private LocalBioServerClient localBioServerClient = mock(LocalBioServerClient.class);

    private M2SysCaptureResponse scannedFingers;

    @Before
    public void setUp() {
        scannedFingers = new M2SysCaptureResponse();
        scannedFingers.setTemplateData(LOCAL_TEMPLATE_DATA);
    }

    @Test
    public void shouldReturnScannedFingersNotExist() {
        when(localBioServerClient.identify(LOCAL_TEMPLATE_DATA)).thenReturn(EMPTY_RESULT_XML);
        when(localBioServerClient.isFingerScanExists(scannedFingers)).thenCallRealMethod();

        Assert.assertEquals(false, localBioServerClient.isFingerScanExists(scannedFingers));
    }

    @Test
    public void shouldReturnScannedFingersExists() {
        when(localBioServerClient.identify(LOCAL_TEMPLATE_DATA)).thenReturn(EXISTING_RESULT_XML);
        when(localBioServerClient.isFingerScanExists(scannedFingers)).thenCallRealMethod();

        Assert.assertEquals(true, localBioServerClient.isFingerScanExists(scannedFingers));
    }
}
