package com.balanceup.keum.controller.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
public class UpdateNicknameRequest {

	String username;
	String nickname;
	String token;

}
