package com.balanceup.keum.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.balanceup.keum.controller.dto.request.user.UserJoinRequest;
import com.balanceup.keum.controller.dto.request.user.UserLoginRequest;
import com.balanceup.keum.controller.dto.response.Response;
import com.balanceup.keum.controller.dto.TokenDto;
import com.balanceup.keum.controller.dto.response.user.UserInfoResponse;
import com.balanceup.keum.service.GoogleApi;
import com.balanceup.keum.service.KakaoApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
public class OauthController {

	private final KakaoApi kakaoAPI;
	private final GoogleApi googleAPI;

	@GetMapping("/login/kakao")
	public ResponseEntity<?> getKakaoUserInfo(@RequestParam String accessToken) {
		return new ResponseEntity<>(
			Response.of("success", "유저 정보 받아오기 성공",
				new UserInfoResponse(kakaoAPI.getUserInfo(accessToken))), HttpStatus.OK
		);
	}

	@GetMapping("/auth/sign-up/kakao")
	public ResponseEntity<?> kakaoJoin(@RequestBody UserJoinRequest request) {
		isKakaoLogin(request.getProvider());
		return new ResponseEntity<>(
			Response.of("success", "회원가입 성공",
				new TokenDto(kakaoAPI.join(request.getUsername(), request.getNickname()))), HttpStatus.CREATED);
	}

	@GetMapping("/auth/sign-in/kakao")
	public ResponseEntity<?> kakaoLogin(@RequestBody UserLoginRequest request) {
		isKakaoLogin(request.getProvider());

		return new ResponseEntity<>(
			Response.of("success", "로그인 성공",
				new TokenDto(kakaoAPI.login(request.getUsername()))), HttpStatus.OK);
	}

	@GetMapping("/login/google")
	public ResponseEntity<?> getGoogleUserInfo(@RequestParam String accessToken) {
		return new ResponseEntity<>(
			Response.of("success", "유저 정보 받아오기 성공",
				new UserInfoResponse(googleAPI.getUserInfo(accessToken))), HttpStatus.OK
		);
	}

	@GetMapping("/auth/sign-up/google")
	public ResponseEntity<?> googleJoin(@RequestBody UserJoinRequest request) {
		isGoogleLogin(request.getProvider());
		return new ResponseEntity<>(
			Response.of("success", "회원가입 성공",
				new TokenDto(googleAPI.join(request.getUsername(), request.getNickname()))), HttpStatus.CREATED);
	}

	@GetMapping("/auth/sign-in/google")
	public ResponseEntity<?> googleLogin(@RequestBody UserLoginRequest request) {
		isGoogleLogin(request.getProvider());

		return new ResponseEntity<>(
			Response.of("success", "로그인 성공",
				new TokenDto(googleAPI.login(request.getUsername()))), HttpStatus.OK);
	}

	private static void isKakaoLogin(String provider) {
		if (!provider.equals("kakao")) {
			throw new IllegalStateException("카카오 로그인이 아닙니다.");
		}
	}

	private static void isGoogleLogin(String provider) {
		if (!provider.equals("google")) {
			throw new IllegalStateException("구글 로그인이 아닙니다.");
		}
	}

}
