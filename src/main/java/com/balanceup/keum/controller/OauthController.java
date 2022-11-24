package com.balanceup.keum.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.balanceup.keum.controller.response.Response;
import com.balanceup.keum.controller.response.TokenResponse;
import com.balanceup.keum.service.KakaoAPI;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class OauthController {

	private final KakaoAPI kakaoAPI;

	@GetMapping("/login/kakao")
	public void getKakaoUserInfo(@RequestParam String code, HttpServletResponse response) throws IOException {
		String accessToken = kakaoAPI.getAccessToken(code);
		Map<String, String> userInfo = kakaoAPI.getUserInfo(accessToken);
		response.addHeader("username", userInfo.get("username"));
		response.addHeader("provider", userInfo.get("provider"));
		response.addHeader("login", userInfo.get("login"));
		if (userInfo.get("login").equals("sign-in")) {
			response.sendRedirect("로그인페이지이동");
			return;
		}
		response.sendRedirect("회원가입페이지 이동");
	}

	@PostMapping("/auth/sign-up/kakao")
	public ResponseEntity<?> kakaoJoin(HttpServletRequest request) {
		isKakaoLogin(request);
		String username = request.getHeader("username");
		String nickname = request.getHeader("nickname");

		return new ResponseEntity<>(
			Response.of("success", "회원가입 성공",
				new TokenResponse(kakaoAPI.join(username, nickname))), HttpStatus.CREATED);
	}

	@PostMapping("/auth/sign-in/kakao")
	public ResponseEntity<?> kakaoLogin(HttpServletRequest request) {
		isKakaoLogin(request);
		String username = request.getHeader("username");

		return new ResponseEntity<>(
			Response.of("success", "로그인 성공",
				new TokenResponse(kakaoAPI.login(username))), HttpStatus.OK);
	}

	private static void isKakaoLogin(HttpServletRequest request) {
		String provider = request.getHeader("provider");
		if (!provider.equals("kakao")) {
			throw new IllegalStateException("카카오 로그인이 아닙니다.");
		}
	}
}
