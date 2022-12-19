package com.balanceup.keum.controller.dto.response;

import com.balanceup.keum.domain.User;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeleteUserResponse {

	String username;
	String nickname;

	public static DeleteUserResponse from(User user) {
		return new DeleteUserResponse(user.getUsername(), user.getNickname());
	}
}
