package org.openmrs.module.m2sysbiometrics.util;

import java.util.List;
import org.apache.commons.collections.CollectionUtils;
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

    public static BiometricSubject searchMostFitBiometricSubject(M2SysCaptureResponse fingerScan, BioServerClient client) {
        List<BiometricMatch> biometricMatchList = search(fingerScan, client);
        if (CollectionUtils.isEmpty(biometricMatchList)) {
            return null;
        }
        String subjectId = biometricMatchList.stream().sorted().findFirst().get().getSubjectId();
        return new BiometricSubject(subjectId);
    }

    private M2SysClientUtil() {
    }
}
