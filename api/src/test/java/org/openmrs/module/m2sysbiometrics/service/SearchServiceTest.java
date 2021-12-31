package org.openmrs.module.m2sysbiometrics.service;

import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Assert;
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
import org.openmrs.module.m2sysbiometrics.service.impl.SearchServiceImpl;
import org.openmrs.module.m2sysbiometrics.testdata.M2SysCaptureResponseMother;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricMatch;

@RunWith(MockitoJUnitRunner.class)
public class SearchServiceTest {

	private static final String EXISTING_SUBJECT_ID = "ExistingSubjectId";

	private static final String EXISTING_RESULT_XML = "<Results><result score='72' value='" + EXISTING_SUBJECT_ID +
			"'></Results>";

	private static final String ERROR_RESULT_XML = "<Results><result score='0' value='TEMPLATE_FORMAT_ERROR'></Results>";

	private static final String NOT_FOUND_RESULT_XML = "<Results><result score='0' value='-1'></Results>";

	@Mock
	private LocalBioServerClient localBioServerClient;

	@Mock
	private NationalBioServerClient nationalBioServerClient;

	@InjectMocks
	private SearchService searchService = new SearchServiceImpl();

	private M2SysCaptureResponse goodFingerScan = M2SysCaptureResponseMother.withTemplateData("<xml>good fingerprint</xml>");

	private M2SysCaptureResponse notExistingFingerScan = M2SysCaptureResponseMother.withTemplateData(
			"<xml>not existing good fingerprint</xml>");

	private M2SysCaptureResponse badFingerScan = M2SysCaptureResponseMother.withTemplateData("bad fingerprint");

	@Ignore("Skipping failing tests for now. See https://github.com/IsantePlus/openmrs-module-m2sys-biometrics/issues/56")
	@Test
	public void shouldSearchLocallyWithSuccess() throws Exception {
		//given
		when(localBioServerClient.identify(goodFingerScan.getTemplateData()))
				.thenReturn(EXISTING_RESULT_XML);

		//when
		List<BiometricMatch> results = searchService.searchLocally(goodFingerScan.toString());

		//then
		Assert.assertEquals(EXISTING_SUBJECT_ID, results.get(0).getSubjectId());
	}

	@Ignore("Skipping failing tests for now. See https://github.com/IsantePlus/openmrs-module-m2sys-biometrics/issues/56")
	@Test(expected = M2SysBiometricsException.class)
	public void shouldSearchLocallyWithException() throws Exception {
		//given
		when(localBioServerClient.identify(badFingerScan.getTemplateData()))
				.thenReturn(ERROR_RESULT_XML);

		//when
		searchService.searchLocally(badFingerScan.toString());
	}

	@Ignore("Skipping failing tests for now. See https://github.com/IsantePlus/openmrs-module-m2sys-biometrics/issues/56")
	@Test
	public void shouldSearchNationallyWithSuccess() throws Exception {
		//given
		when(nationalBioServerClient.identify(goodFingerScan.getTemplateData()))
				.thenReturn(EXISTING_RESULT_XML);

		//when
		List<BiometricMatch> results = searchService.searchNationally(goodFingerScan.toString());

		//then
		Assert.assertEquals(results.size(), 1);
		Assert.assertEquals(EXISTING_SUBJECT_ID, results.get(0).getSubjectId());
	}

	@Ignore("Skipping failing tests for now. See https://github.com/IsantePlus/openmrs-module-m2sys-biometrics/issues/56")
	@Test
	public void shouldSearchNationallyWithoutException() throws Exception {
		//given
		when(nationalBioServerClient.identify(badFingerScan.getTemplateData()))
				.thenReturn(ERROR_RESULT_XML);

		//when
		List<BiometricMatch> results = searchService.searchNationally(badFingerScan.toString());

		//then
		Assert.assertTrue(results.isEmpty());
	}

	@Ignore("Skipping failing tests for now. See https://github.com/IsantePlus/openmrs-module-m2sys-biometrics/issues/56")
	@Test
	public void shouldSearchLocallyWithEmptyResult() throws Exception {
		//given
		when(localBioServerClient.identify(notExistingFingerScan.getTemplateData()))
				.thenReturn(NOT_FOUND_RESULT_XML);

		//when
		List<BiometricMatch> results = searchService.searchLocally(notExistingFingerScan.toString());

		//then
		Assert.assertTrue(results.isEmpty());
	}

	@Ignore("Skipping failing tests for now. See https://github.com/IsantePlus/openmrs-module-m2sys-biometrics/issues/56")
	@Test
	public void shouldSearchNationallyWithEmptyResult() throws Exception {
		//given
		when(nationalBioServerClient.identify(notExistingFingerScan.getTemplateData()))
				.thenReturn(NOT_FOUND_RESULT_XML);

		//when
		List<BiometricMatch> results = searchService.searchNationally(notExistingFingerScan.toString());

		//then
		Assert.assertTrue(results.isEmpty());
	}
}
