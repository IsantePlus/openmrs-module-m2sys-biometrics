package org.openmrs.module.m2sysbiometrics.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.m2sysbiometrics.bioplugin.LocalBioServerClient;
import org.openmrs.module.m2sysbiometrics.bioplugin.NationalBioServerClient;
import org.openmrs.module.m2sysbiometrics.capture.impl.CloudScanrCaptor;
import org.openmrs.module.m2sysbiometrics.capture.impl.TestEnvCaptor;
import org.openmrs.module.m2sysbiometrics.exception.M2SysBiometricsException;
import org.openmrs.module.m2sysbiometrics.model.*;
import org.openmrs.module.m2sysbiometrics.service.RegistrationService;
import org.openmrs.module.m2sysbiometrics.service.SearchService;
import org.openmrs.module.m2sysbiometrics.service.TempFingerprintService;
import org.openmrs.module.m2sysbiometrics.service.UpdateService;
import org.openmrs.module.m2sysbiometrics.xml.XmlResultUtil;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricMatch;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricSubject;
import org.openmrs.module.registrationcore.api.biometrics.model.EnrollmentResult;
import org.openmrs.module.registrationcore.api.biometrics.model.EnrollmentStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.util.ArrayList;
import java.util.List;

@Component("m2sysbiometrics.M2SysV1Client")
public class M2SysV105Client extends AbstractM2SysClient {

    private final Log log = LogFactory.getLog(M2SysV105Client.class);

    @Autowired
    private LocalBioServerClient localBioServerClient;

    @Autowired
    private NationalBioServerClient nationalBioServerClient;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private SearchService searchService;

    @Autowired
    private CloudScanrCaptor cloudScanrCaptor;

    @Autowired
    private TestEnvCaptor testEnvCaptor;

    @Autowired
    private UpdateService updateService;

    @Autowired
    private TempFingerprintService tempFingerprintService;

    private JAXBContext jaxbContext;

    @PostConstruct
    public void init() throws JAXBException {
        jaxbContext = JAXBContext.newInstance(Fingers.class, Finger.class, M2SysResults.class,
                M2SysResult.class);
    }

    @Override
    public EnrollmentResult enroll(BiometricSubject subjectId) {
        M2SysCaptureResponse capture = scanDoubleFingers();
        log.info("M2SysV105Client.enroll======================================>  : "+capture.getTemplateData());
        return enroll(subjectId, capture.getTemplateData());
    }

    @Override
    public EnrollmentResult enroll(BiometricSubject subjectId, String fingerprintXmlTemplate) {
        BiometricSubject nationalSubject = new BiometricSubject("");
        M2SysCaptureResponse capture = convertXmlTemplateToCapture(fingerprintXmlTemplate);
        FingerScanStatus fingerScanStatus = searchService.checkIfFingerScanExists(capture.getTemplateData());
        log.info("Fingerscan response =>>>>>>>>>>>>>: Local ==> "+ fingerScanStatus.isRegisteredLocally() + ", National ==> " + fingerScanStatus.isRegisteredNationally());
        EnrollmentStatus enrollmentStatus = EnrollmentStatus.SUCCESS;
        Fingers fingers = capture.getFingerData(jaxbContext);
        subjectId.setFingerprints(fingers.toTwoOpenMrsFingerprints());
        nationalSubject.setFingerprints(fingers.toTwoOpenMrsFingerprints());

//        1. If registered both locally and nationally, return the IDs
        if(fingerScanStatus.isRegisteredLocally() && fingerScanStatus.isRegisteredNationally()){
            subjectId = fingerScanStatus.getLocalBiometricSubject();
            nationalSubject = fingerScanStatus.getNationalBiometricSubject();
        }
//        2. If registered locally but not nationally, try register nationally. If successful, return both IDs, if not, return local ID and queue for later national registration
//        3. If registered nationally but not locally, try register locally. If successful, return both IDs, if not, return national ID and queue for later local registration - should be very rare
        else if (fingerScanStatus.isRegisteredLocally() || fingerScanStatus.isRegisteredNationally()) {
            if (fingerScanStatus.isRegisteredLocally()) {
                subjectId = fingerScanStatus.getLocalBiometricSubject();
                enrollmentStatus = EnrollmentStatus.ALREADY_REGISTERED;
//                Enroll nationally
                if (nationalBioServerClient.isServerUrlConfigured()) {
                    registrationService.registerNationally(capture);
                    fingerScanStatus = searchService.checkIfFingerScanExists(capture.getTemplateData());
                    if (fingerScanStatus.isRegisteredNationally()) {
                        nationalSubject = fingerScanStatus.getNationalBiometricSubject();
                    } else {
//                    National registration did not happen - queue for later sync
                        SyncFingerprint syncFingerprint = new SyncFingerprint(subjectId.getSubjectId(), fingerprintXmlTemplate);
//                    syncFingerprintService.saveOrUpdate(syncFingerprint);
                    }
                }

            } else {
                nationalSubject = fingerScanStatus.getNationalBiometricSubject();
                enrollmentStatus = EnrollmentStatus.ALREADY_REGISTERED;
//                Enroll Locally
                if (localBioServerClient.isServerUrlConfigured()) {
                    registrationService.registerLocally(subjectId,capture);
                    fingerScanStatus = searchService.checkIfFingerScanExists(capture.getTemplateData());
                    if (fingerScanStatus.isRegisteredLocally()) {
                        subjectId = fingerScanStatus.getLocalBiometricSubject();
                    } else {
//                    Local registration did not happen - queue for later sync
//                        SyncFingerprint syncFingerprint = new SyncFingerprint(nationalSubject.getSubjectId(), fingerprintXmlTemplate);
//                    syncFingerprintService.saveOrUpdate(syncFingerprint);
                        subjectId = new BiometricSubject("");
                    }
                }
            }
        }
//        4. Not registered locally and nationally - register
        else {
            //                register locally and nationally
            if (localBioServerClient.isServerUrlConfigured()) {
                registrationService.registerLocally(subjectId,capture);
                log.info("Local EnrollmentStatus : " + EnrollmentStatus.SUCCESS);
            }
            if (nationalBioServerClient.isServerUrlConfigured()) {
                registrationService.registerNationally(capture);
                log.info("National EnrollmentStatus : " + EnrollmentStatus.SUCCESS);
            }
//                retrieve the registered biometrics
            fingerScanStatus = searchService.checkIfFingerScanExists(capture.getTemplateData());
            if (fingerScanStatus.isRegisteredNationally() || fingerScanStatus.isRegisteredLocally()) {
                enrollmentStatus = EnrollmentStatus.SUCCESS;
            }

//            if (!fingerScanStatus.isRegisteredNationally() || !fingerScanStatus.isRegisteredLocally()) {
//                   Registration did not happen - queue for later sync
//                SyncFingerprint syncFingerprint = new SyncFingerprint(subjectId.getSubjectId(), fingerprintXmlTemplate);
//                    syncFingerprintService.saveOrUpdate(syncFingerprint);
//            }
            subjectId = fingerScanStatus.getLocalBiometricSubject();
            nationalSubject = fingerScanStatus.getNationalBiometricSubject();
        }

//        if (!fingerScanStatus.isRegisteredLocally()) {
//            if (fingerScanStatus.isRegisteredNationally()) {
//                nationalSubject = fingerScanStatus.getNationalBiometricSubject();
//                enrollmentStatus = EnrollmentStatus.ALREADY_REGISTERED;
//                if(localBioServerClient.isServerUrlConfigured()){
//                    registrationService.registerLocally(subjectId);
//                    log.error("Local EnrollmentStatus : "+EnrollmentStatus.SUCCESS);
//                }else{
//                    log.error("Local Enrollment Aborted: Status - Local Server not configured!");
//                    subjectId = new BiometricSubject("");
//                }
//            } else {
//
//
//            }
//        } else {
//            subjectId = fingerScanStatus.getLocalBiometricSubject();
//            enrollmentStatus = EnrollmentStatus.ALREADY_REGISTERED;
//            if (!fingerScanStatus.isRegisteredNationally()) {
////                Enroll nationally
//                registrationService.registerNationally(capture);
//                fingerScanStatus = searchService.checkIfFingerScanExists(capture.getTemplateData());
//                if (fingerScanStatus.isRegisteredNationally()) {
//                    nationalSubject = fingerScanStatus.getNationalBiometricSubject();
//                }else{
////                    National registration did not happen - queue for later sync
//                    SyncFingerprint syncFingerprint = new SyncFingerprint(subjectId.getSubjectId(), fingerprintXmlTemplate);
////                    syncFingerprintService.saveOrUpdate(syncFingerprint);
//                }
//
//            }else{
//                nationalSubject = fingerScanStatus.getNationalBiometricSubject();
//            }
//        }
        return new EnrollmentResult(subjectId, nationalSubject, enrollmentStatus);
    }

    private M2SysCaptureResponse convertXmlTemplateToCapture(String fingerprintXmlTemplate) {
        M2SysCaptureResponse capture = new M2SysCaptureResponse();
        capture.setTemplateData(fingerprintXmlTemplate);
        return capture;
    }

    @Override
    public BiometricSubject update(BiometricSubject subject) {
        M2SysCaptureResponse fingerScan = scanDoubleFingers();
        updateService.updateLocally(subject);

     /*   if (nationalBioServerClient.isServerUrlConfigured()) {
            updateService.updateNationally(fingerScan);
        }
*/
        Fingers fingers = fingerScan.getFingerData(jaxbContext);
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
    public List<BiometricMatch> search() {
        M2SysCaptureResponse fingerScan = scanDoubleFingers();
        FingerScanStatus fingerScanStatus = searchService.checkIfFingerScanExists(fingerScan.getTemplateData());
        List<BiometricMatch> results = new ArrayList<>();

        if (fingerScanStatus.isRegisteredLocally()) {
            results = searchService.searchLocally(fingerScan.getTemplateData());
        }

        if (nationalBioServerClient.isServerUrlConfigured()) {
            try {
                if (fingerScanStatus.isRegisteredNationally()) {
                    List<BiometricMatch> nationalResults = searchService.searchNationally(fingerScan.getTemplateData());
                    String biometricXml = fingerScan.getTemplateData();
                    for (BiometricMatch nationalResult : nationalResults) {
                        TempFingerprint fingerprint = new TempFingerprint(nationalResult.getSubjectId(), biometricXml);
                        tempFingerprintService.saveOrUpdate(fingerprint);
                    }
                    results.addAll(nationalResults);
                }
                if (fingerScanStatus.isRegisteredLocally()) {
                    registrationService.synchronizeFingerprints(fingerScan, fingerScanStatus);
                }
            } catch (RuntimeException exception) {
                getLogger().error("Connection failure to national server.", exception);
            }
        }


        return results;
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
        M2SysCaptureResponse response = testEnvCaptor.scanDoubleFingers();

        if (response == null) {
            response = cloudScanrCaptor.scanDoubleFingers();
        } else {
            getLogger().warn("Using test template from the environment, skipping capture");
        }

        return response;
    }
}
