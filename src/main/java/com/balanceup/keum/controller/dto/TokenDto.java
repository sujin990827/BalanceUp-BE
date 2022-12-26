package com.balanceup.keum.controller.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TokenDto {

	private String token;
	private String refreshToken;

	public TokenDto(Map<String, String> tokens) {
		this.token = tokens.get("accessToken");
		this.refreshToken = tokens.get("refreshToken");
	}

}
