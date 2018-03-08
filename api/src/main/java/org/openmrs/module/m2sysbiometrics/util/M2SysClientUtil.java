package org.openmrs.module.m2sysbiometrics.util;

import java.util.List;
import org.openmrs.module.m2sysbiometrics.bioplugin.BioServerClient;
import org.openmrs.module.m2sysbiometrics.model.M2SysCaptureResponse;
import org.openmrs.module.m2sysbiometrics.model.M2SysResults;
import org.openmrs.module.m2sysbiometrics.xml.XmlResultUtil;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricMatch;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricSubject;

public final class M2SysClientUtil {

    public static List<BiometricMatch> search(M2SysCaptureResponse fingerScan, BioServerClient client) {
        String response = client.identify(fingerScan.getTemplateData());
        M2SysResults results = XmlResultUtil.parse(response);

        return results.toOpenMrsMatchList();
    }

    public static BiometricMatch searchMostAdequate(M2SysCaptureResponse fingerScan, BioServerClient client) {
        return M2SysClientUtil.search(fingerScan, client)
                .stream()
                .max(BiometricMatch::compareTo)
                .orElse(null);
    }

    public static BiometricSubject searchMostAdequateBiometricSubject(M2SysCaptureResponse fingerScan,
            BioServerClient client) {
        BiometricMatch biometricMatch = searchMostAdequate(fingerScan, client);
        return biometricMatch == null ? null : new BiometricSubject(biometricMatch.getSubjectId());
    }

    private M2SysClientUtil() {
    }
}
