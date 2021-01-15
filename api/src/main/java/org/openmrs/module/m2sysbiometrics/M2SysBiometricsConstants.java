package org.openmrs.module.m2sysbiometrics;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class M2SysBiometricsConstants {

    public static final String M2SYS_CLOUD_SCANR_URL = "m2sys-biometrics.server.url";

    public static final String M2SYS_CAPTURE_DEVICE_NAME = "m2sys-biometrics.device.name";

   // public static final String M2SYS_CLOUD_SCANR_USERNAME = "m2sys-biometrics.server.user";

   // public static final String M2SYS_CLOUD_SCANR_PASSWORD = "m2sys-biometrics.server.password";

    public static final String M2SYS_LOCAL_SERVICE_URL = "m2sys-biometrics.local-service.url";

    public static final String M2SYS_NATIONAL_SERVICE_URL = "m2sys-biometrics.national-service.url";

    public static final String M2SYS_NATIONAL_USERNAME = "m2sys-biometrics.server.national.user";

    public static final String M2SYS_NATIONAL_PASSWORD = "m2sys-biometrics.server.national.password";

    public static final String M2SYS_LOCATION_ID = "m2sys-biometrics.locationID";

   // public static final String M2SYS_CUSTOMER_KEY = "m2sys-biometrics.customKey";

  //  public static final String M2SYS_ACCESS_POINT_ID = "m2sys-biometrics.accessPointID";

 //   public static final String M2SYS_ACCESS_POINT_MAP = "m2sys-biometrics.accessPointMap";

    public static final String M2SYS_CAPTURE_TIMEOUT = "m2sys-biometrics.captureTimeout";

    public static final String M2SYS_NATIONAL_REGISTRATIONS_TASK_INTERVAL = "m2sys-biometrics"
            + ".nationalRegistrationTaskInterval";

    public static final String M2SYS_LOOKUP_ENDPOINT = "/api/Biometric/IsRegistered";

    public static final String M2SYS_SEARCH_ENDPOINT = "/api/Biometric/Identify";

    public static final String M2SYS_REGISTER_ENDPOINT = "/api/Biometric/Register";

    public static final String M2SYS_UPDATE_ENDPOINT = "/api/Biometric/Update";

    public static final String M2SYS_DELETE_ID_ENDPOINT = "/api/Biometric/DeleteID";

    public static final String M2SYS_CHANGE_ID_ENDPOINT = "/api/Biometric/ChangeID";

    public static final String M2SYS_CAPTURE_ENDPOINT = "/api/CloudScanr/FPCapture";

    public static final String ERROR_CODE_OF_SUBJECT_NOT_EXIST = "CS004";

    // if this system var is set, always returns the value as the template, skipping scanning
    public static final String CONST_TEST_TEMPLATE = "m2sys-biometrics.server.constTestTemplate";

    public static final String M2SYS_CLOUDABIS_APP_KEY = "m2sys-biometrics.cloudabis.app.key";
    public static final String M2SYS_CLOUDABIS_SECRET_KEY = "m2sys-biometrics.cloudabis.secret.key";
    public static final String M2SYS_CLOUDABIS_GRANT_TYPE = "m2sys-biometrics.cloudabis.grant.type";
    public static final String M2SYS_CLOUDABIS_API_URL = "m2sys-biometrics.cloudabis.api.url";
    public static final String M2SYS_CLOUDABIS_CUSTOMER_KEY = "m2sys-biometrics.cloudabis.customer.key";
    public static final String M2SYS_CLOUDABIS_ENGINE_NAME = "m2sys-biometrics.cloudabis.engine.name";
    public static final String M2SYS_CLOUDABIS_TEMPLATE_FORMAT = "m2sys-biometrics.cloudabis.template.format";

    public static String getErrorMessage(String errorCode) {
        return ResourceBundle.getBundle("MessageBundle").getString("m2sys.response.error." + errorCode);
    }

    public static String getServerStatusDescription(int responseCode) {
        try {
            return ResourceBundle.getBundle("MessageBundle").getString("m2sys.getStatus.decription." + responseCode);
        } catch (MissingResourceException ex) {
            return ResourceBundle.getBundle("MessageBundle").getString("m2sys.getStatus.decription.default");
        }
    }
}
