package com.balanceup.keum.controller.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenDto {

	String token;
	String refreshToken;

	public TokenDto(Map<String, String> tokens) {
		this.token = tokens.get("accessToken");
		this.refreshToken = tokens.get("refreshToken");
	}

}
