package org.openmrs.module.m2sysbiometrics.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public abstract class M2SysData implements Serializable {
    private static final long serialVersionUID = -7975830809846498434L;
}
