package org.openmrs.module.m2sysbiometrics.bioplugin;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.m2sysbiometrics.model.M2SysCaptureResponse;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NationalBioServerClientTest {

    private static final String EMPTY_RESULT_XML = "<Results><result score='0' value='empty'></Results>";
    private static final String EXISTING_RESULT_XML = "<Results><result score='1' value='72'></Results>";
    private static String NATIONAL_TEMPLATE_DATA = "national";

    NationalBioServerClient nationalBioServerClient = mock(NationalBioServerClient.class);

    private M2SysCaptureResponse scannedFingers;

    @Before
    public void setUp() {
        scannedFingers = new M2SysCaptureResponse();
        scannedFingers.setTemplateData(NATIONAL_TEMPLATE_DATA);
    }

    @Test
    public void shouldReturnScannedFingersNotExist() {
        when(nationalBioServerClient.identify(NATIONAL_TEMPLATE_DATA)).thenReturn(EMPTY_RESULT_XML);
        when(nationalBioServerClient.isFingerScanExists(scannedFingers)).thenCallRealMethod();

        Assert.assertEquals(false, nationalBioServerClient.isFingerScanExists(scannedFingers));
    }

    @Test
    public void shouldReturnScannedFingersExists() {
        when(nationalBioServerClient.identify(NATIONAL_TEMPLATE_DATA)).thenReturn(EXISTING_RESULT_XML);
        when(nationalBioServerClient.isFingerScanExists(scannedFingers)).thenCallRealMethod();

        Assert.assertEquals(true, nationalBioServerClient.isFingerScanExists(scannedFingers));
    }
}
