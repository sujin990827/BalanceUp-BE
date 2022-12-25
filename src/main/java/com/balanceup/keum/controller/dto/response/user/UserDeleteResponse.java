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
public class UserDeleteResponse {

	private String username;
	private String nickname;

	public static UserDeleteResponse from(User user) {
		return new UserDeleteResponse(user.getUsername(), user.getNickname());
	}
}
