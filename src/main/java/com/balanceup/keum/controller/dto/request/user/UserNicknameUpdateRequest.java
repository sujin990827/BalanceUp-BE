package com.balanceup.keum.controller.dto.request.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserNicknameUpdateRequest {

	private String nickname;
	private String token;
}
