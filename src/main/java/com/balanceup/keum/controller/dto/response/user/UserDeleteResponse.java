package com.balanceup.keum.controller.dto.response.user;

import com.balanceup.keum.domain.User;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDeleteResponse {

	String username;
	String nickname;

	public static UserDeleteResponse from(User user) {
		return new UserDeleteResponse(user.getUsername(), user.getNickname());
	}
}
