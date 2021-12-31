package org.openmrs.module.m2sysbiometrics.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.openmrs.module.m2sysbiometrics.exception.M2SysBiometricsException;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricMatch;

public class M2SysResponseTest {

	@Test
	public void shouldParseMatchingResult() throws Exception {
		M2SysResponse response = new M2SysResponse();
		response.setMatchingResult(readFile("sampleMatchingResult.xml"));

		M2SysResults matchingResult = response.parseMatchingResult();
		M2SysResult result = matchingResult.getResults().get(0);

		assertNotNull(matchingResult);
		assertEquals(0, result.getScore());
		assertEquals("27", result.getValue());
		assertEquals(1, result.getInstance());
	}

	@Test
	public void shouldParseListOfMatches() throws IOException {
		M2SysResponse response = new M2SysResponse();
		response.setMatchingResult(readFile("resultList.xml"));

		List<BiometricMatch> matches = response.toMatchList();

		assertNotNull(matches);
		assertEquals(3, matches.size());
		assertEquals("10000X", matches.get(0).getSubjectId());
		assertEquals(22.0, matches.get(0).getMatchScore(), 0.1);
		assertEquals("10001X", matches.get(1).getSubjectId());
		assertEquals(1.0, matches.get(1).getMatchScore(), 0.1);
		assertEquals("10002X", matches.get(2).getSubjectId());
		assertEquals(102.0, matches.get(2).getMatchScore(), 0.1);
	}

	@Test
	public void shouldReturnEmptyListForNonMatchResult() throws IOException {
		M2SysResponse response = new M2SysResponse();
		response.setMatchingResult(readFile("noMatches.xml"));

		List<BiometricMatch> matches = response.toMatchList();

		assertNotNull(matches);
		assertTrue(matches.isEmpty());
	}

	@Test
	public void shouldReturnEmptyListForEmptyResult() throws IOException {
		M2SysResponse response = new M2SysResponse();
		response.setMatchingResult("");

		List<BiometricMatch> matches = response.toMatchList();

		assertNotNull(matches);
		assertTrue(matches.isEmpty());
	}

	@Test
	public void shouldReturnEmptyListForNullResult() throws IOException {
		M2SysResponse response = new M2SysResponse();
		response.setMatchingResult(null);

		List<BiometricMatch> matches = response.toMatchList();

		assertNotNull(matches);
		assertTrue(matches.isEmpty());
	}

	@Test
	public void shouldParseInvalidResultXmlWithEmpty() throws IOException {
		M2SysResponse response = new M2SysResponse();
		response.setMatchingResult(readFile("invalidResultXml.xml"));

		List<BiometricMatch> matches = response.toMatchList();

		assertNotNull(matches);
		assertEquals(1, matches.size());
		assertEquals(75.0, matches.get(0).getMatchScore(), 0.0);
		assertEquals("test", matches.get(0).getSubjectId());
	}

	@Test(expected = M2SysBiometricsException.class)
	public void shouldThrowExceptionForInvalidEngineResult() throws IOException {
		M2SysResponse response = new M2SysResponse();
		response.setMatchingResult(readFile("invalidEngine.xml"));

		response.toMatchList();
	}

	private String readFile(String file) throws IOException {
		try (InputStream in = getClass().getClassLoader().getResourceAsStream(file)) {
			return IOUtils.toString(in);
		}
	}
}
