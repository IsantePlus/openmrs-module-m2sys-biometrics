package org.openmrs.module.m2sysbiometrics.service.impl;

import org.openmrs.module.m2sysbiometrics.bioplugin.LocalBioServerClient;
import org.openmrs.module.m2sysbiometrics.bioplugin.NationalBioServerClient;
import org.openmrs.module.m2sysbiometrics.exception.M2SysBiometricsException;
import org.openmrs.module.m2sysbiometrics.model.M2SysCaptureResponse;
import org.openmrs.module.m2sysbiometrics.service.UpdateService;
import org.openmrs.module.m2sysbiometrics.xml.XmlResultUtil;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricSubject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UpdateServiceImpl implements UpdateService {

    private static final Logger LOG = LoggerFactory.getLogger(UpdateServiceImpl.class);

    @Autowired
    private LocalBioServerClient localBioServerClient;

    @Autowired
    private NationalBioServerClient nationalBioServerClient;
    
    @Override
    public void updateLocally(BiometricSubject subject, M2SysCaptureResponse fingerScan) {
        String response = localBioServerClient.update(subject.getSubjectId(), fingerScan.getTemplateData());
        if (!XmlResultUtil.parse(response).isUpdateSuccess()) {
            throw new M2SysBiometricsException("Unable to update fingerprints locally for: " + subject.getSubjectId());
        }
    }

    @Override
    public void updateNationally(BiometricSubject subject, M2SysCaptureResponse fingerScan) {
        String response = nationalBioServerClient.update(subject.getSubjectId(), fingerScan.getTemplateData());
        if (!XmlResultUtil.parse(response).isUpdateSuccess()) {
            LOG.error("Unable to update fingerprints nationally for: " + subject.getSubjectId());
        }
    }
}
