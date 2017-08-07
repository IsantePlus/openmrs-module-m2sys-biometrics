package org.openmrs.module.m2sysbiometrics;

import java.util.ResourceBundle;

public final class M2SysBiometricsConstants {

    public static final String M2SYS_BIOMETRICS_READER_ID = "m2sys-biometrics.readerId";

    public static final String M2SYS_SERVER_URL = "m2sys-biometrics.server.url";

    public static final String M2SYS_USER = "m2sys-biometrics.server.user";

    public static final String M2SYS_PASSWORD = "m2sys-biometrics.server.password";

    public static final String M2SYS_LOCATION_ID = "m2sys-biometrics.locationID";

    public static final String M2SYS_CUSTOMER_KEY = "m2sys-biometrics.customKey";

    public static final String M2SYS_ACCESS_POINT_ID = "m2sys-biometrics.accessPointID";

    public static final String M2SYS_CAPTURE_TIMEOUT = "m2sys-biometrics.captureTimeout";

    public static final String M2SYS_LOOKUP_ENDPOINT = "/api/Biometric/Identify";

    public static final String M2SYS_REGISTER_ENDPOINT = "/api/Biometric/Register";

    public static final String M2SYS_UPDATE_ENDPOINT = "/api/Biometric/Update";

    public static final String M2SYS_DELETE_ID_ENDPOINT = "api/Biometric/DeleteID";

    public static final String M2SYS_CHANGE_ID_ENDPOINT = "api/Biometric/ChangeID";

    public static final String ERROR_CODE_OF_SUBJECT_NOT_EXIST = "CS004";

    public static String getErrorMessage(String errorCode) {
        return ResourceBundle.getBundle("MessageBundle").getString("m2sys.response.error." + errorCode);
    }
}
