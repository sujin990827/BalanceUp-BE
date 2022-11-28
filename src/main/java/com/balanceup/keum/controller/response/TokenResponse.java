package com.balanceup.keum.controller.response;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenResponse {

	String token;
	String refreshToken;

	public TokenResponse(Map<String, String> tokens) {
		this.token = tokens.get("accessToken");
		this.refreshToken = tokens.get("refreshToken");
	}

}
