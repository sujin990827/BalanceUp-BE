package com.balanceup.keum.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.balanceup.keum.controller.request.DuplicateNicknameRequest;
import com.balanceup.keum.controller.request.UpdateNicknameRequest;
import com.balanceup.keum.controller.response.Response;
import com.balanceup.keum.service.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class UserController {

	private final UserService userService;

	@PostMapping("/user/nickname")
	public ResponseEntity<?> duplicateNickname(@RequestBody DuplicateNicknameRequest dto) {
		return new ResponseEntity<>(getSuccessResponse("닉네임 중복 확인 성공", userService.duplicateNickname(dto)), HttpStatus.OK);
	}

	@PutMapping("/user/nickname")
	public ResponseEntity<?> updateNickname(@RequestBody UpdateNicknameRequest dto) {
		return new ResponseEntity<>(getSuccessResponse("닉네임 업데이트 성공", userService.updateNickname(dto)), HttpStatus.OK);
	}

	private static Response<Object> getSuccessResponse(String message, Object body) {
		return Response.of("success", message, body);
	}

}
