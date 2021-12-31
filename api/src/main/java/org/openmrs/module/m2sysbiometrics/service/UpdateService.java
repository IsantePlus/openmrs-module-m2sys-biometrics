package org.openmrs.module.m2sysbiometrics.service;

//import org.openmrs.module.m2sysbiometrics.model.M2SysCaptureResponse;

import org.openmrs.module.registrationcore.api.biometrics.model.BiometricSubject;

public interface UpdateService {

	void updateLocally(BiometricSubject subject);

	//  void updateNationally(M2SysCaptureResponse fingerScan);
}
