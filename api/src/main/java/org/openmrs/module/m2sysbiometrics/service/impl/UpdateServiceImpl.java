package org.openmrs.module.m2sysbiometrics.service.impl;

import org.openmrs.module.m2sysbiometrics.bioplugin.LocalBioServerClient;
import org.openmrs.module.m2sysbiometrics.bioplugin.NationalBioServerClient;
import org.openmrs.module.m2sysbiometrics.exception.M2SysBiometricsException;
import org.openmrs.module.m2sysbiometrics.model.M2SysCaptureResponse;
import org.openmrs.module.m2sysbiometrics.model.M2SysResults;
import org.openmrs.module.m2sysbiometrics.model.NationalSynchronizationFailure;
import org.openmrs.module.m2sysbiometrics.service.NationalSynchronizationFailureService;
import org.openmrs.module.m2sysbiometrics.service.RegistrationService;
import org.openmrs.module.m2sysbiometrics.service.SearchService;
import org.openmrs.module.m2sysbiometrics.service.UpdateService;
import org.openmrs.module.m2sysbiometrics.xml.XmlResultUtil;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricMatch;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricSubject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "updateService")
public class UpdateServiceImpl implements UpdateService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateServiceImpl.class);

    @Autowired
    private LocalBioServerClient localBioServerClient;

    @Autowired
    private NationalBioServerClient nationalBioServerClient;

    @Autowired
    private NationalSynchronizationFailureService nationalSynchronizationFailureService;

    @Autowired
    private SearchService searchService;

    @Autowired
    private RegistrationService registrationService;

    @Override
    public void updateLocally(BiometricSubject subject, M2SysCaptureResponse fingerScan) {
        String response = localBioServerClient.update(subject.getSubjectId(), fingerScan.getTemplateData());
        M2SysResults results = XmlResultUtil.parse(response);
        if (!results.isUpdateSuccess()) {
            throw new M2SysBiometricsException("Unable to update fingerprints locally for: " + subject.getSubjectId()
                    + ", reason: " + results.firstValue());
        }
    }

    @Override
    public void updateNationally(M2SysCaptureResponse fingerScan) {
        try {
            BiometricMatch nationalMatch = searchService.findMostAdequateNationally(fingerScan);
            if (nationalMatch != null) {
                BiometricSubject nationalSubject = new BiometricSubject(nationalMatch.getSubjectId());

                String response = nationalBioServerClient.update(nationalSubject.getSubjectId(),
                        fingerScan.getTemplateData());
                M2SysResults results = XmlResultUtil.parse(response);
                if (!results.isUpdateSuccess()) {
                    throw new M2SysBiometricsException("Unable to update fingerprints nationally for: "
                            + nationalSubject.getSubjectId()
                            + ", reason: " + results.firstValue());
                }
            } else {
                registrationService.registerNationally(fingerScan);
            }
        } catch (Exception e) {
            LOGGER.error("Update with the national fingerprint server failed.", e);
            String subjectId = searchService.findMostAdequateLocally(fingerScan).getSubjectId();
            BiometricSubject subject = new BiometricSubject(subjectId);
            handleNationalUpdateError(subject, fingerScan);
        }
    }

    private void handleNationalUpdateError(BiometricSubject subject, M2SysCaptureResponse fingerScan) {
        NationalSynchronizationFailure nationalSynchronizationFailure =
                new NationalSynchronizationFailure(subject.getSubjectId(), fingerScan.getTemplateData(), true);
        nationalSynchronizationFailureService.save(nationalSynchronizationFailure);
    }
}
