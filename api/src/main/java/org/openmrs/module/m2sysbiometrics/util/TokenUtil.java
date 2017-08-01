package org.openmrs.module.m2sysbiometrics.util;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TokenUtil implements Serializable {

	private static final long serialVersionUID = 2642478741110327810L;

	@SerializedName("access_token")
	private String accessToken;

	@SerializedName("token_type")
	private String tokenType;

	@SerializedName("expires_in")
	private String expiresIn;
	
	public String getAccessToken() {
		return accessToken;
	}
	
	public String getExpiresIn() {
		return expiresIn;
	}
	
	public String getTokenType() {
		return tokenType;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	public void setExpiresIn(String expiresIn) {
		this.expiresIn = expiresIn;
	}
}
