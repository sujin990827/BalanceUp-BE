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
public class UserInfoResponse {

	private String nickname;
	private Integer rp;

	public static UserInfoResponse of(User user) {
		return new UserInfoResponse(user.getNickname(), user.getRp());
	}
}
