package org.openmrs.module.m2sysbiometrics.client;

import org.apache.commons.collections.CollectionUtils;
import org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants;
import org.openmrs.module.m2sysbiometrics.bioplugin.AbstractBioServerClient;
import org.openmrs.module.m2sysbiometrics.bioplugin.LocalBioServerClient;
import org.openmrs.module.m2sysbiometrics.bioplugin.NationalBioServerClient;
import org.openmrs.module.m2sysbiometrics.exception.M2SysBiometricsException;
import org.openmrs.module.m2sysbiometrics.model.Finger;
import org.openmrs.module.m2sysbiometrics.model.FingerScanStatus;
import org.openmrs.module.m2sysbiometrics.model.Fingers;
import org.openmrs.module.m2sysbiometrics.model.M2SysCaptureRequest;
import org.openmrs.module.m2sysbiometrics.model.M2SysCaptureResponse;
import org.openmrs.module.m2sysbiometrics.model.M2SysResult;
import org.openmrs.module.m2sysbiometrics.model.M2SysResults;
import org.openmrs.module.m2sysbiometrics.model.Token;
import org.openmrs.module.m2sysbiometrics.service.RegistrationService;
import org.openmrs.module.m2sysbiometrics.xml.XmlResultUtil;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricMatch;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricSubject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.util.List;

@Component("m2sysbiometrics.M2SysV1Client")
public class M2SysV105Client extends AbstractM2SysClient {

    private static final Logger LOG = LoggerFactory.getLogger(M2SysV105Client.class);

    @Autowired
    private LocalBioServerClient localBioServerClient;

    @Autowired
    private NationalBioServerClient nationalBioServerClient;

    @Autowired
    private RegistrationService registrationService;

    private JAXBContext jaxbContext;

    @PostConstruct
    public void init() throws JAXBException {
        jaxbContext = JAXBContext.newInstance(Fingers.class, Finger.class, M2SysResults.class,
                M2SysResult.class);
    }

    @Override
    public BiometricSubject enroll(BiometricSubject subject) {
        M2SysCaptureResponse capture = scanDoubleFingers();

        FingerScanStatus fingerScanStatus = checkIfFingerScanExists(capture);

        registrationService.register(localBioServerClient, subject, capture);

        if (nationalBioServerClient.isServerUrlConfigured() && !fingerScanStatus.isRegisteredNationally()) {
            registrationService.register(nationalBioServerClient, subject, capture);
        }

        Fingers fingers = capture.getFingerData(jaxbContext);

        subject.setFingerprints(fingers.toTwoOpenMrsFingerprints());

        return subject;
    }

    @Override
    public BiometricSubject update(BiometricSubject subject) {
        M2SysCaptureResponse capture = scanDoubleFingers();

        String response = localBioServerClient.update(subject.getSubjectId(), capture.getTemplateData());
        M2SysResults results = XmlResultUtil.parse(response);

        if (!results.isUpdateSuccess()) {
            throw new M2SysBiometricsException("Unable to update fingerprints for: "
                    + subject.getSubjectId());
        }

        Fingers fingers = capture.getFingerData(jaxbContext);
        subject.setFingerprints(fingers.toTwoOpenMrsFingerprints());

        return subject;
    }

    @Override
    public BiometricSubject updateSubjectId(String oldId, String newId) {
        String response = localBioServerClient.changeId(oldId, newId);
        M2SysResults results = XmlResultUtil.parse(response);

        if (!results.isChangeIdSuccess()) {
            throw new M2SysBiometricsException("Unable to change ID from " + oldId
                + " to " + newId);
        }

        return new BiometricSubject(newId);
    }

    @Override
    public List<BiometricMatch> search(BiometricSubject subject) {
        M2SysCaptureResponse capture = scanDoubleFingers();

        String response = localBioServerClient.identify(capture.getTemplateData());
        M2SysResults results = XmlResultUtil.parse(response);

        return results.toOpenMrsMatchList();
    }

    @Override
    public BiometricSubject lookup(String subjectId) {
        String response = localBioServerClient.isRegistered(subjectId);
        M2SysResults results = XmlResultUtil.parse(response);

        return results.isLookupNotFound() ? null : new BiometricSubject(subjectId);
    }

    @Override
    public void delete(String subjectId) {
        String response = localBioServerClient.delete(subjectId);
        M2SysResults results = XmlResultUtil.parse(response);

        if (!results.isDeleteSuccess()) {
            throw new M2SysBiometricsException("Unable to delete fingerprints for: " + subjectId);
        }
    }

    private M2SysCaptureResponse scanDoubleFingers() {
        M2SysCaptureRequest request = new M2SysCaptureRequest();
        addRequiredValues(request);
        request.setCaptureType(1);

        Token token = getToken();

        return getHttpClient().postRequest(
                getCloudScanrUrl() + M2SysBiometricsConstants.M2SYS_CAPTURE_ENDPOINT,
                request, token, M2SysCaptureResponse.class);
    }

    private FingerScanStatus checkIfFingerScanExists(M2SysCaptureResponse fingerScan) {
        boolean registeredLocally = isFingerScanRegistered(fingerScan, localBioServerClient);
        boolean registeredNationally = false;

        if (nationalBioServerClient.isServerUrlConfigured()) {
            try {
                registeredNationally = isFingerScanRegistered(fingerScan, nationalBioServerClient);
            } catch (RuntimeException exception) {
                LOG.error("Connection failure to national server.", exception);
            }
        }

        return new FingerScanStatus(registeredLocally, registeredNationally);
    }

    private boolean isFingerScanRegistered(M2SysCaptureResponse fingerScan, AbstractBioServerClient client) {
        String response = client.identify(fingerScan.getTemplateData());
        M2SysResults results = XmlResultUtil.parse(response);
        return CollectionUtils.isNotEmpty(results.toOpenMrsMatchList());
    }
}
