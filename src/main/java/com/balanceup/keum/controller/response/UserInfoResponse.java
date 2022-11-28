package com.balanceup.keum.controller.response;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserInfoResponse {

	private String username;
	private String provider;
	private String login;

	public UserInfoResponse(Map<String, String> info) {
		this.username = info.get("username");
		this.provider = info.get("provider");
		this.login = info.get("login");
	}
}
