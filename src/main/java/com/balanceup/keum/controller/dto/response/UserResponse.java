package com.balanceup.keum.controller.dto.response;

import com.balanceup.keum.domain.User;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponse {

	String username;
	String nickname;
	String token;

	public static UserResponse from(User user, String token) {
		return new UserResponse(user.getUsername(), user.getNickname(), token);
	}

}
