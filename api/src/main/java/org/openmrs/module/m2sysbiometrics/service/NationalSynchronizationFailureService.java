package org.openmrs.module.m2sysbiometrics.service;

import javax.transaction.Transactional;
import java.util.List;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.m2sysbiometrics.model.NationalSynchronizationFailure;

public interface NationalSynchronizationFailureService extends OpenmrsService {

	@Transactional
	NationalSynchronizationFailure save(NationalSynchronizationFailure nationalSynchronizationFailure);

	@Transactional
	void delete(NationalSynchronizationFailure nationalSynchronizationFailure);

	@Transactional
	List<NationalSynchronizationFailure> findAllRegistrationFailures();

	@Transactional
	List<NationalSynchronizationFailure> findAllUpdateFailures();
}
