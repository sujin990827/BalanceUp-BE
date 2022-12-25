package com.balanceup.keum.controller.dto.response.user;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
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
