package com.balanceup.keum.controller.dto.response.user;

import com.balanceup.keum.domain.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserResponse {

	private String username;
	private String nickname;

	public static UserResponse from(User user) {
		return new UserResponse(user.getUsername(), user.getNickname());
	}

}
