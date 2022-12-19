package com.balanceup.keum.controller.dto.request.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserJoinRequest {

	private String username;
	private String provider;
	private String nickname;
}
