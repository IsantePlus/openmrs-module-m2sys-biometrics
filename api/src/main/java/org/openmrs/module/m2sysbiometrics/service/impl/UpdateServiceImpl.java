package org.openmrs.module.m2sysbiometrics.service.impl;

import org.openmrs.module.m2sysbiometrics.bioplugin.LocalBioServerClient;
import org.openmrs.module.m2sysbiometrics.bioplugin.NationalBioServerClient;
import org.openmrs.module.m2sysbiometrics.exception.M2SysBiometricsException;
import org.openmrs.module.m2sysbiometrics.model.M2SysCaptureResponse;
import org.openmrs.module.m2sysbiometrics.model.M2SysResults;
import org.openmrs.module.m2sysbiometrics.model.NationalSynchronizationFailure;
import org.openmrs.module.m2sysbiometrics.service.NationalSynchronizationFailureService;
import org.openmrs.module.m2sysbiometrics.service.UpdateService;
import org.openmrs.module.m2sysbiometrics.xml.XmlResultUtil;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricSubject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UpdateServiceImpl implements UpdateService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateServiceImpl.class);

    @Autowired
    private LocalBioServerClient localBioServerClient;

    @Autowired
    private NationalBioServerClient nationalBioServerClient;

    @Autowired
    private NationalSynchronizationFailureService nationalSynchronizationFailureService;
    
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
    public void updateNationally(BiometricSubject nationalSubject, M2SysCaptureResponse fingerScan) {
        String response = nationalBioServerClient.update(nationalSubject.getSubjectId(), fingerScan.getTemplateData());
        M2SysResults results = XmlResultUtil.parse(response);
        if (!results.isUpdateSuccess()) {
            LOGGER.error("Unable to update fingerprints nationally for: " + nationalSubject.getSubjectId()
                    + ", reason: " + results.firstValue());
            handleNationalUpdateError(nationalSubject, fingerScan);
        }
    }

    private void handleNationalUpdateError(BiometricSubject nationalSubject, M2SysCaptureResponse fingerScan) {
        NationalSynchronizationFailure nationalSynchronizationFailure =
                new NationalSynchronizationFailure(nationalSubject.getSubjectId(), fingerScan.getTemplateData(), true);
        nationalSynchronizationFailureService.save(nationalSynchronizationFailure);
    }
}
