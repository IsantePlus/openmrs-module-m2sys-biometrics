package org.openmrs.module.m2sysbiometrics.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class M2SysData implements Serializable {
    private static final long serialVersionUID = -7975830809846498434L;
}
