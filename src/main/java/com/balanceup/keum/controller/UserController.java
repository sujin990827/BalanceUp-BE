package com.balanceup.keum.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.balanceup.keum.config.auth.PrincipalDetailService;
import com.balanceup.keum.controller.dto.TokenDto;
import com.balanceup.keum.controller.dto.request.DuplicateNicknameRequest;
import com.balanceup.keum.controller.dto.request.UpdateNicknameRequest;
import com.balanceup.keum.controller.dto.response.Response;
import com.balanceup.keum.service.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class UserController {

	private final UserService userService;
	private final PrincipalDetailService principalDetailService;

	@PostMapping("/user/nickname")
	public ResponseEntity<?> duplicateNickname(@RequestBody DuplicateNicknameRequest dto) {
		return new ResponseEntity<>(getSuccessResponse("닉네임 중복 확인 성공", userService.duplicateNickname(dto)),
			HttpStatus.OK);
	}

	@PutMapping("/user/nickname")
	public ResponseEntity<?> updateNickname(@RequestBody UpdateNicknameRequest dto) {
		return new ResponseEntity<>(getSuccessResponse("닉네임 업데이트 성공", userService.updateNickname(dto)), HttpStatus.OK);
	}

	private static Response<Object> getSuccessResponse(String message, Object body) {
		return Response.of("success", message, body);
	}

	@GetMapping("/auth/refresh")
	public ResponseEntity<?> getRefreshToken(TokenDto tokenDto) {
		String username = getUserNameBySecurityContextHolder();
		UserDetails userDetails = principalDetailService.loadUserByUsername(username);

		return new ResponseEntity<>(
			getSuccessResponse("AccessToken 재발급이 완료되었습니다.", userService.reIssue(tokenDto, userDetails)), HttpStatus.OK);
	}

	private static String getUserNameBySecurityContextHolder() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

}

