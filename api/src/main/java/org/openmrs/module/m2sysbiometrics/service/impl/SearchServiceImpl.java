package org.openmrs.module.m2sysbiometrics.service.impl;

import org.openmrs.module.m2sysbiometrics.bioplugin.LocalBioServerClient;
import org.openmrs.module.m2sysbiometrics.bioplugin.NationalBioServerClient;
import org.openmrs.module.m2sysbiometrics.exception.M2SysBiometricsException;
import org.openmrs.module.m2sysbiometrics.model.M2SysCaptureResponse;
import org.openmrs.module.m2sysbiometrics.model.M2SysResults;
import org.openmrs.module.m2sysbiometrics.service.SearchService;
import org.openmrs.module.m2sysbiometrics.xml.XmlResultUtil;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricMatch;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricSubject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SearchServiceImpl implements SearchService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchServiceImpl.class);

    @Autowired
    private LocalBioServerClient localBioServerClient;

    @Autowired
    private NationalBioServerClient nationalBioServerClient;

    @Override
    public List<BiometricMatch> searchLocally(M2SysCaptureResponse fingerScan) {
        String response = localBioServerClient.identify(fingerScan.getTemplateData());
        M2SysResults results = XmlResultUtil.parse(response);

        if (results.isSearchError()) {
            throw new M2SysBiometricsException("Error occurred during local fingerprint search: " + results.firstValue());
        }

        return results.toOpenMrsMatchList();
    }

    @Override
    public List<BiometricMatch> searchNationally(M2SysCaptureResponse fingerScan) {
        List<BiometricMatch> biometricMatches = new ArrayList<>();
        String response = nationalBioServerClient.identify(fingerScan.getTemplateData());
        M2SysResults results = XmlResultUtil.parse(response);

        if (results.isSearchError()) {
            LOGGER.error("Error occurred during national fingerprint search: " + results.firstValue());
        } else {
            biometricMatches = results.toOpenMrsMatchList();
        }

        return biometricMatches;
    }

    @Override
    public BiometricMatch findMostAdequateLocally(M2SysCaptureResponse fingerScan) {
        return searchLocally(fingerScan)
                .stream()
                .max(BiometricMatch::compareTo)
                .orElse(null);
    }

    @Override
    public BiometricMatch findMostAdequateNationally(M2SysCaptureResponse fingerScan) {
        return searchNationally(fingerScan)
                .stream()
                .max(BiometricMatch::compareTo)
                .orElse(null);
    }

    @Override
    public BiometricSubject findMostAdequateSubjectLocally(M2SysCaptureResponse fingerScan) {
        BiometricMatch biometricMatch = findMostAdequateLocally(fingerScan);
        return biometricMatch == null ? null : new BiometricSubject(biometricMatch.getSubjectId());
    }

    @Override
    public BiometricSubject findMostAdequateSubjectNationally(M2SysCaptureResponse fingerScan) {
        BiometricMatch biometricMatch = findMostAdequateNationally(fingerScan);
        return biometricMatch == null ? null : new BiometricSubject(biometricMatch.getSubjectId());
    }
}
