package org.openmrs.module.m2sysbiometrics.service.impl;

import groovy.json.internal.JsonParserCharArray;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.openmrs.module.m2sysbiometrics.bioplugin.LocalBioServerClient;
import org.openmrs.module.m2sysbiometrics.bioplugin.NationalBioServerClient;
import org.openmrs.module.m2sysbiometrics.exception.M2SysBiometricsException;
import org.openmrs.module.m2sysbiometrics.model.*;
//import org.openmrs.module.m2sysbiometrics.model.M2SysCaptureResponse;
import org.openmrs.module.m2sysbiometrics.service.SearchService;
import org.openmrs.module.m2sysbiometrics.util.PatientHelper;
import org.openmrs.module.m2sysbiometrics.xml.XmlResultUtil;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricMatch;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricSubject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service(value = "searchService")
public class SearchServiceImpl implements SearchService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchServiceImpl.class);

    @Autowired
    private LocalBioServerClient localBioServerClient;

    @Autowired
    private NationalBioServerClient nationalBioServerClient;

    @Autowired
    private PatientHelper patientHelper;

    @Override
    public List<BiometricMatch> searchLocally(String biometricXml) {
        String response = localBioServerClient.identify(biometricXml);
        M2SysResults results = XmlResultUtil.parse(response);

        if (results.isSearchError()) {
            throw new M2SysBiometricsException("Error occurred during local fingerprint search: " + results.firstValue());
        }

        return results.toOpenMrsMatchList();
    }

    @Override
    public List<BiometricMatch> searchNationally(String biometricXml) {
        List<BiometricMatch> biometricMatches;
        CloudAbisResult response = nationalBioServerClient.identifyAbis(biometricXml);
        biometricMatches = toOpenMrsMatchList(response);
        return biometricMatches;
    }

    public List<BiometricMatch> toOpenMrsMatchList(CloudAbisResult result) {
        List<BiometricMatch> matches = new ArrayList<>();
        Integer matchCount = result.getMatchCount();
        if (matchCount > 0) {
            List<ScoreResult> detailResult = result.getDetailResult();
            Iterator<ScoreResult> iterator = detailResult.iterator();
            while (iterator.hasNext()) {
                ScoreResult next = iterator.next();
                String id = next.getMatchId();
                Integer score = next.getScore();
                BiometricMatch match = new BiometricMatch(id, score.doubleValue());
                matches.add(match);
            }
        }
        return matches;
    }

    @Override
    public BiometricMatch findMostAdequateLocally(String biometricXml) {
        return searchLocally(biometricXml)
                .stream()
                .max(BiometricMatch::compareTo)
                .orElse(null);
    }

    @Override
    public BiometricMatch findMostAdequateNationally(String biometricXml) {
        return searchNationally(biometricXml)
                .stream()
                .max(BiometricMatch::compareTo)
                .orElse(null);
    }

    @Override
    public FingerScanStatus checkIfFingerScanExists(String biometricXml) {
        BiometricSubject nationalBiometricSubject = null;
        BiometricSubject localBiometricSubject = null;

        if (localBioServerClient.isServerUrlConfigured()) {
            try {

//                TODO - Ping the local fingerprint server to see if there is a connection over and above the configurations
                localBiometricSubject = findMostAdequateSubjectLocally(biometricXml);
                localBiometricSubject = validateLocalSubjectExistence(localBiometricSubject);
            } catch (RuntimeException exception) {
                LOGGER.error("Connection failure to local server.", exception);
            }

        }
        if (nationalBioServerClient.isServerUrlConfigured()) {
            try {
                nationalBiometricSubject = findMostAdequateSubjectNationally(biometricXml);
            } catch (RuntimeException exception) {
                LOGGER.error(ExceptionUtils.getFullStackTrace(exception));
                LOGGER.error("Connection failure to national server.", exception);
            }
        }

        return new FingerScanStatus(localBiometricSubject, nationalBiometricSubject);
    }

    private BiometricSubject validateLocalSubjectExistence(BiometricSubject localBiometricSubject) {
        return localBiometricSubject == null || patientHelper.findByLocalFpId(localBiometricSubject.getSubjectId()) == null
                ? null
                : localBiometricSubject;
    }

    private BiometricSubject findMostAdequateSubjectLocally(String biometricXml) {
        BiometricMatch biometricMatch = findMostAdequateLocally(biometricXml);
        return biometricMatch == null ? null : new BiometricSubject(biometricMatch.getSubjectId());
    }

    private BiometricSubject findMostAdequateSubjectNationally(String biometricXml) {
        BiometricMatch biometricMatch = findMostAdequateNationally(biometricXml);
        return biometricMatch == null ? null : new BiometricSubject(biometricMatch.getSubjectId());
    }
}
