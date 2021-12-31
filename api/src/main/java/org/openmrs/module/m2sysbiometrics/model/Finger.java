package org.openmrs.module.m2sysbiometrics.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import org.openmrs.module.registrationcore.api.biometrics.model.Fingerprint;

@XmlRootElement(name = "Finger")
@XmlAccessorType(XmlAccessType.FIELD)
public class Finger {

	@XmlAttribute(name = "POS")
	private String pos;

	@XmlValue
	private String templateData;

	public String getTemplateData() {
		return templateData;
	}

	public void setTemplateData(String templateData) {
		this.templateData = templateData;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public Fingerprint toOpenMRSFingerprint() {
		return new Fingerprint("ISO", "ISO", templateData);
	}
}
