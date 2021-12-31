package org.openmrs.module.m2sysbiometrics.dao;

import java.util.List;

import org.openmrs.module.m2sysbiometrics.model.NationalSynchronizationFailure;

public interface M2SysNationalSynchronizationFailureDao {

	NationalSynchronizationFailure save(NationalSynchronizationFailure nationalSynchronizationFailure);

	void delete(NationalSynchronizationFailure nationalSynchronizationFailure);

	List<NationalSynchronizationFailure> findAllRegistrationFailures();

	List<NationalSynchronizationFailure> findAllUpdateFailures();
}
