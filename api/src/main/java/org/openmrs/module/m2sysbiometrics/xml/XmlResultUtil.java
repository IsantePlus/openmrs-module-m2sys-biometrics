package org.openmrs.module.m2sysbiometrics.xml;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.StringReader;

import org.openmrs.module.m2sysbiometrics.exception.M2SysBiometricsException;
import org.openmrs.module.m2sysbiometrics.model.M2SysResults;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public final class XmlResultUtil {

	public static M2SysResults parse(String resultsXml) {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();

			XMLReader xmlReader = saxParser.getXMLReader();

			xmlReader.setFeature(
					"http://apache.org/xml/features/continue-after-fatal-error",
					true);
			xmlReader.setErrorHandler(new M2SysXmlErrorHandler());

			final M2SysResults results = new M2SysResults();
			xmlReader.setContentHandler(new ResultContentHandler(results));

			InputSource inputSource = new InputSource(new StringReader(resultsXml));
			xmlReader.parse(inputSource);

			return results;
		}
		catch (SAXException | IOException | ParserConfigurationException e) {
			throw new M2SysBiometricsException("Matching result parse error", e);
		}
	}

	private XmlResultUtil() {
	}
}
