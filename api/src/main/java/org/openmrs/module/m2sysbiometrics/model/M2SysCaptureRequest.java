package org.openmrs.module.m2sysbiometrics.model;

import org.codehaus.jackson.annotate.JsonProperty;

public class M2SysCaptureRequest extends AbstractM2SysRequest {

	private static final long serialVersionUID = 1996985652532947266L;

	@JsonProperty("CaptureType")
	private Integer captureType;

	@JsonProperty("CaptureMode")
	private Integer captureMode;

	@JsonProperty("SingleCaptureMode")
	private Integer singleCaptureMode;

	@JsonProperty("QuickScan")
	private Integer quickScan;

	@JsonProperty("TemplateFormat")
	private Integer templateFormat;

	@JsonProperty("BioMetricImageFormat")
	private Integer biometricImageFormat;

	@JsonProperty("CaptureOperationName")
	private Integer captureOperationName;

	@JsonProperty("FaceImage")
	private Integer faceImage;

	@JsonProperty("DeviceName")
	private String deviceName;

	public Integer getCaptureType() {
		return captureType;
	}

	public void setCaptureType(Integer captureType) {
		this.captureType = captureType;
	}

	public Integer getCaptureMode() {
		return captureMode;
	}

	public void setCaptureMode(Integer captureMode) {
		this.captureMode = captureMode;
	}

	public Integer getSingleCaptureMode() {
		return singleCaptureMode;
	}

	public void setSingleCaptureMode(Integer singleCaptureMode) {
		this.singleCaptureMode = singleCaptureMode;
	}

	public Integer getQuickScan() {
		return quickScan;
	}

	public void setQuickScan(Integer quickScan) {
		this.quickScan = quickScan;
	}

	public Integer getTemplateFormat() {
		return templateFormat;
	}

	public void setTemplateFormat(Integer templateFormat) {
		this.templateFormat = templateFormat;
	}

	public Integer getBiometricImageFormat() {
		return biometricImageFormat;
	}

	public void setBiometricImageFormat(Integer biometricImageFormat) {
		this.biometricImageFormat = biometricImageFormat;
	}

	public Integer getCaptureOperationName() {
		return captureOperationName;
	}

	public void setCaptureOperationName(Integer captureOperationName) {
		this.captureOperationName = captureOperationName;
	}

	public Integer getFaceImage() {
		return faceImage;
	}

	public void setFaceImage(Integer faceImage) {
		this.faceImage = faceImage;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
}
