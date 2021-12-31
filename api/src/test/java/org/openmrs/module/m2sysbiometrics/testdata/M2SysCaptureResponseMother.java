package org.openmrs.module.m2sysbiometrics.testdata;

import org.openmrs.module.m2sysbiometrics.model.M2SysCaptureResponse;

public final class M2SysCaptureResponseMother {

	public static M2SysCaptureResponse validInstance() {
		return new M2SysCaptureResponse();
	}

	public static M2SysCaptureResponse withTemplateData(String templateData) {
		M2SysCaptureResponse m2SysCaptureResponse = validInstance();
		m2SysCaptureResponse.setTemplateData(templateData);
		return m2SysCaptureResponse;
	}

	private M2SysCaptureResponseMother() {
	}
}
