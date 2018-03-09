package org.openmrs.module.m2sysbiometrics.service.impl;

import java.util.List;
import org.openmrs.module.m2sysbiometrics.bioplugin.BioServerClient;
import org.openmrs.module.m2sysbiometrics.model.M2SysCaptureResponse;
import org.openmrs.module.m2sysbiometrics.model.M2SysResults;
import org.openmrs.module.m2sysbiometrics.service.SearchService;
import org.openmrs.module.m2sysbiometrics.xml.XmlResultUtil;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricMatch;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricSubject;
import org.springframework.stereotype.Component;

@Component
public class SearchServiceImpl implements SearchService {

    public List<BiometricMatch> search(M2SysCaptureResponse fingerScan, BioServerClient client) {
        String response = client.identify(fingerScan.getTemplateData());
        M2SysResults results = XmlResultUtil.parse(response);

        return results.toOpenMrsMatchList();
    }

    public BiometricMatch findMostAdequate(M2SysCaptureResponse fingerScan, BioServerClient client) {
        return search(fingerScan, client)
                .stream()
                .max(BiometricMatch::compareTo)
                .orElse(null);
    }

    public BiometricSubject findMostAdequateBiometricSubject(M2SysCaptureResponse fingerScan,
            BioServerClient client) {
        BiometricMatch biometricMatch = findMostAdequate(fingerScan, client);
        return biometricMatch == null ? null : new BiometricSubject(biometricMatch.getSubjectId());
    }
}
