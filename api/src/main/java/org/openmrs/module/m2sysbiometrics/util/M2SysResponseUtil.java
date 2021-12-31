package org.openmrs.module.m2sysbiometrics.util;

import org.apache.commons.lang.StringUtils;
import org.openmrs.module.m2sysbiometrics.exception.M2SysBiometricsException;
import org.openmrs.module.m2sysbiometrics.model.M2SysResponse;
import org.openmrs.module.m2sysbiometrics.model.M2SysResult;
import org.openmrs.module.m2sysbiometrics.model.M2SysResults;

public class M2SysResponseUtil {

	public static boolean checkLookupResponse(M2SysResponse response) {
		M2SysResults matchingResult = response.parseMatchingResult();
		checkIfMatchingResultIsNotEmptyList(matchingResult);

		M2SysResult result = matchingResult.getResults().get(0);
		result.checkCommonErrorValues();
		String value = result.getValue();
		return !(M2SysResult.FAILED.equals(value)
				|| (StringUtils.isBlank(value) || value.length() != 2 || !StringUtils.isNumeric(value)));
	}

	public static void checkUpdateSubjectIdResponse(M2SysResponse response) {
		M2SysResults matchingResult = (response.parseMatchingResult());
		checkIfMatchingResultIsNotEmptyList(matchingResult);

		M2SysResult result = matchingResult.getResults().get(0);
		result.checkCommonErrorValues();
		if (M2SysResult.UPDATE_SUBJECT_ID_FAILURE.equals(result.getValue())) {
			throw new IllegalArgumentException("Changing subject id failed. Probably the old id doesn't exist");
		} else if (!M2SysResult.UPDATE_SUBJECT_ID_SUCCESS.equals(result.getValue())) {
			throw new M2SysBiometricsException("Unknown updating subjectId result");
		}
	}

	public static void checkDeleteResponse(M2SysResponse response) {
		M2SysResults matchingResult = (response.parseMatchingResult());
		checkIfMatchingResultIsNotEmptyList(matchingResult);

		M2SysResult result = matchingResult.getResults().get(0);
		result.checkCommonErrorValues();
		if (M2SysResult.DELETE_FAILURE.equals(result.getValue())) {
			throw new IllegalArgumentException("Deleting subject failed. Probably the subject doesn't exist");
		} else if (!M2SysResult.DELETE_SUCCESS.equals(result.getValue())) {
			throw new M2SysBiometricsException("Unknown deleting result");
		}
	}

	private static void checkIfMatchingResultIsNotEmptyList(M2SysResults matchingResult) {
		if (matchingResult == null || matchingResult.getResults().isEmpty()) {
			throw new M2SysBiometricsException("Unknown m2Sys result");
		}
	}
}
