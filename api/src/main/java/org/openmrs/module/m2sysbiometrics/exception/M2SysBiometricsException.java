package org.openmrs.module.m2sysbiometrics.exception;

public class M2SysBiometricsException extends RuntimeException {

	public M2SysBiometricsException(Throwable e) {
		super(e);
	}

	public M2SysBiometricsException(String message) {
		super(message);
	}

	public M2SysBiometricsException(String message, Throwable e) {
		super(message, e);
	}
}
