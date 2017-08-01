package org.openmrs.module.m2sysbiometrics;

import java.util.ResourceBundle;

public class M2SysBiometricsConstants {
	
	public static final String M2SYS_BIOMETRICS_READER_ID = "m2sys-biometrics.readerId";
	
	public static final String M2SYS_SERVER_URL = "m2sys-biometrics.server.url";
	
	public static final String M2SYS_USER = "m2sys-biometrics.server.user";
	
	public static final String M2SYS_PASSWORD = "m2sys-biometrics.server.password";
	
	public static final String M2SYS_LOCATION_ID = "m2sys-biometrics.locationID";
	
	public static final String M2SYS_CUSTOM_KEY = "m2sys-biometrics.customKey";
	
	public static final String M2SYS_ACCESS_POINT_ID = "m2sys-biometrics.accessPointID";
	
	public static final String M2SYS_CAPTURE_TIMEOUT = "m2sys-biometrics.captureTimeout";
	
	public static final String M2SYS_LOOKUP_ENDPOINT = "/api/Biometric/Identify";
	
	public static final String M2SYS_REGISTER_ENDPOINT = "/api/Biometric/Register";
	
	public static final String M2SYS_UPDATE_ENDPOINT = "/api/Biometric/Update";

	public static final String M2SYS_DELETE_ID_ENDPOINT = "api/Biometric/DeleteID";

	public static final String M2SYS_CHANGE_ID_ENDPOINT = "api/Biometric/ChangeID";
	
	//Names
	public static final String CUSTOMER_KEY = "CustomerKey";
	
	public static final String REGISTRATION_ID = "RegistrationID";
	
	public static final String NEW_REGISTRATION_ID = "NewRegistrationID";
	
	public static final String LOCATION_ID = "LocationID";
	
	public static final String ACCESSPOINT_ID = "AccessPointID";
	
	public static final String CAPTURE_TIMEOUT = "CaptureTimeOut";
	
	public static final String BIOMETRIC_WITH = "BioMetricWith";
	
	public static final String ERROR_CODE_OF_SUBJECT_NOT_EXIST = "CS004";
	
	public static String getErrorMessage(String errorCode) {
		return ResourceBundle.getBundle("MessageBundle").getString("m2sys.response.error." + errorCode);
	}
}
