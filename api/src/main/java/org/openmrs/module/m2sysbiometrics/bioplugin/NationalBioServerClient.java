package org.openmrs.module.m2sysbiometrics.bioplugin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants;
import org.springframework.stereotype.Component;

@Component
public class NationalBioServerClient extends AbstractBioServerClient {

	private final Log log = LogFactory.getLog(NationalBioServerClient.class);

	public NationalBioServerClient() {
		setMessageFactory(messageFactory);
	}

	@Override
	protected String getServerUrlPropertyName() {
		return M2SysBiometricsConstants.M2SYS_NATIONAL_SERVICE_URL;
	}

	@Override
	protected Object getResponse(Object requestPayload) {
		return getWebServiceTemplate().marshalSendAndReceive(getServiceUrl(), requestPayload);
	}
}
