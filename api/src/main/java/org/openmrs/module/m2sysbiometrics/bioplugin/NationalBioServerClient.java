package org.openmrs.module.m2sysbiometrics.bioplugin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;

@Component
public class NationalBioServerClient extends AbstractBioServerClient {

	private final Log log = LogFactory.getLog(NationalBioServerClient.class);

	public NationalBioServerClient() {
		setMessageFactory(context.getBean("m2sysbiometrics.messageFactory", SaajSoapMessageFactory.class));
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
