package org.openmrs.module.m2sysbiometrics.xml;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.module.m2sysbiometrics.model.M2SysResult;
import org.openmrs.module.m2sysbiometrics.model.M2SysResults;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ResultContentHandler extends DefaultHandler {

	private final M2SysResults results;

	public ResultContentHandler(M2SysResults results) {
		this.results = results;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes)
			throws SAXException {
		if (qName.equalsIgnoreCase("result")) {
			M2SysResult result = new M2SysResult();

			result.setValue(attributes.getValue("value"));

			String score = attributes.getValue("score");
			if (StringUtils.isNotBlank(score)) {
				result.setScore(Integer.parseInt(score));
			}

			String instance = attributes.getValue("Instance");
			if (StringUtils.isNotBlank(instance)) {
				result.setInstance(Integer.parseInt(instance));
			}

			if (!result.isEmpty()) {
				results.addResult(result);
			}
		} else {
			super.startElement(uri, localName, qName, attributes);
		}
	}
}
