package com.balanceup.keum.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.balanceup.keum.controller.request.UserJoinRequest;
import com.balanceup.keum.controller.request.UserLoginRequest;
import com.balanceup.keum.controller.response.Response;
import com.balanceup.keum.controller.response.TokenResponse;
import com.balanceup.keum.controller.response.UserInfoResponse;
import com.balanceup.keum.service.GoogleAPI;
import com.balanceup.keum.service.KakaoAPI;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
public class OauthController {

	private final KakaoAPI kakaoAPI;
	private final GoogleAPI googleAPI;

	@GetMapping("/login/kakao")
	public ResponseEntity<?> getKakaoUserInfo(@RequestParam String code) {
		return new ResponseEntity<>(
			Response.of("success", "유저 정보 받아오기 성공",
				new UserInfoResponse(kakaoAPI.getUserInfo(kakaoAPI.getAccessToken(code)))), HttpStatus.OK
		);
	}

	@GetMapping("/auth/sign-up/kakao")
	public ResponseEntity<?> kakaoJoin(@RequestBody UserJoinRequest request) {
		isKakaoLogin(request.getProvider());
		return new ResponseEntity<>(
			Response.of("success", "회원가입 성공",
				new TokenResponse(kakaoAPI.join(request.getUsername(), request.getNickname()))), HttpStatus.CREATED);
	}

	@GetMapping("/auth/sign-in/kakao")
	public ResponseEntity<?> kakaoLogin(@RequestBody UserLoginRequest request) {
		isKakaoLogin(request.getProvider());

		return new ResponseEntity<>(
			Response.of("success", "로그인 성공",
				new TokenResponse(kakaoAPI.login(request.getUsername()))), HttpStatus.OK);
	}

	@GetMapping("/login/google")
	public ResponseEntity<?> getGoogleUserInfo(@RequestParam String code) {
		return new ResponseEntity<>(
			Response.of("success", "유저 정보 받아오기 성공",
				new UserInfoResponse(googleAPI.getUserInfo(googleAPI.getAccessToken(code)))), HttpStatus.OK
		);
	}

	@GetMapping("/auth/sign-up/google")
	public ResponseEntity<?> googleJoin(@RequestBody UserJoinRequest request) {
		isGoogleLogin(request.getProvider());
		return new ResponseEntity<>(
			Response.of("success", "회원가입 성공",
				new TokenResponse(googleAPI.join(request.getUsername(), request.getNickname()))), HttpStatus.CREATED);
	}

	@GetMapping("/auth/sign-in/google")
	public ResponseEntity<?> googleLogin(@RequestBody UserLoginRequest request) {
		isGoogleLogin(request.getProvider());

		return new ResponseEntity<>(
			Response.of("success", "로그인 성공",
				new TokenResponse(googleAPI.login(request.getUsername()))), HttpStatus.OK);
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
