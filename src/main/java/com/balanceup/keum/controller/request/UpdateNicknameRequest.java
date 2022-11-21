package com.balanceup.keum.controller.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdateNicknameRequest {

	String username;
	String nickname;
	String token;

}