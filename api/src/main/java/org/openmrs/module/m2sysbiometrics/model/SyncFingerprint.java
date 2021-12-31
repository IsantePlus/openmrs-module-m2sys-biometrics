package org.openmrs.module.m2sysbiometrics.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.openmrs.BaseOpenmrsData;

@Entity
@Table(name = "sync_fingerprint")
public class SyncFingerprint extends BaseOpenmrsData {

	private static final long serialVersionUID = -6509686390029003588L;

	@Id
	@Column(name = "sync_fingerprint_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "biometric_id", unique = true)
	private String biometricId;

	@Lob
	@Column(name = "biometric_xml")
	private String biometricXml;

	public SyncFingerprint() {
	}

	public SyncFingerprint(String biometricId, String biometricXml) {
		this.biometricId = biometricId;
		this.biometricXml = biometricXml;
	}

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	public String getBiometricId() {
		return biometricId;
	}

	public void setBiometricId(String biometricId) {
		this.biometricId = biometricId;
	}

	public String getBiometricXml() {
		return biometricXml;
	}

	public void setBiometricXml(String biometricXml) {
		this.biometricXml = biometricXml;
	}
}
