package org.openmrs.module.m2sysbiometrics.xml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class M2SysXmlErrorHandler extends DefaultHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(M2SysXmlErrorHandler.class);

	@Override
	public void fatalError(SAXParseException ex) throws SAXException {
		LOGGER.warn("Invalid XML coming from M2Sys, still processing. Error: {}", ex.getMessage());
	}
}
