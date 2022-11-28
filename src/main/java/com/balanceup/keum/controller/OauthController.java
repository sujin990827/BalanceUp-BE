package com.balanceup.keum.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.balanceup.keum.controller.response.Response;
import com.balanceup.keum.controller.response.TokenResponse;
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
	public void getKakaoUserInfo(@RequestParam String code, HttpServletResponse response) throws IOException {
		Map<String, String> userInfo = kakaoAPI.getUserInfo(kakaoAPI.getAccessToken(code));
		setCookie(response, userInfo);

		if (userInfo.get("login").equals("sign-in")) {
			response.sendRedirect("/auth/sign-in/kakao");
			return;
		}
		response.sendRedirect("/auth/sign-up/kakao");
	}

	@GetMapping("/auth/sign-up/kakao")
	public ResponseEntity<?> kakaoJoin(HttpServletRequest request) {
		Userinfo userinfo = getUserInfoByCookies(request);
		isKakaoLogin(userinfo.provider);

		String nickname = "sjk"; //TODO : 클라이언트에서 받아와야 되는 값 (cookie or header)
		return new ResponseEntity<>(
			Response.of("success", "회원가입 성공",
				new TokenResponse(kakaoAPI.join(userinfo.username, nickname))), HttpStatus.CREATED);
	}

	@GetMapping("/auth/sign-in/kakao")
	public ResponseEntity<?> kakaoLogin(HttpServletRequest request) {
		Userinfo userinfo = getUserInfoByCookies(request);
		isKakaoLogin(userinfo.provider);

		return new ResponseEntity<>(
			Response.of("success", "로그인 성공",
				new TokenResponse(kakaoAPI.login(userinfo.username))), HttpStatus.OK);
	}

	@GetMapping("/login/google")
	public void getGoogleUserInfo(@RequestParam String code, HttpServletResponse response) throws IOException {
		Map<String, String> userInfo = googleAPI.getUserInfo(googleAPI.getAccessToken(code));
		log.info("getGoogleUserInfo");
		System.out.println(userInfo.toString());
		setCookie(response, userInfo);

		if (userInfo.get("login").equals("sign-in")) {
			response.sendRedirect("/auth/sign-in/google");
			return;
		}
		response.sendRedirect("/auth/sign-up/google");
	}

	@GetMapping("/auth/sign-up/google")
	public ResponseEntity<?> googleJoin(HttpServletRequest request) {
		Userinfo userinfo = getUserInfoByCookies(request);
		isGoogleLogin(userinfo.provider);

		String nickname = "sjkggo"; //TODO : 클라이언트에서 받아와야 되는 값 (cookie or header)
		return new ResponseEntity<>(
			Response.of("success", "회원가입 성공", new TokenResponse(googleAPI.join(userinfo.username, nickname))),
			HttpStatus.CREATED);
	}

	@GetMapping("/auth/sign-in/google")
	public ResponseEntity<?> googleLogin(HttpServletRequest request) {
		Userinfo userinfo = getUserInfoByCookies(request);
		isGoogleLogin(userinfo.provider);

		return new ResponseEntity<>(
			Response.of("success", "로그인 성공", new TokenResponse(googleAPI.login(userinfo.username))), HttpStatus.OK);
	}

	private static Userinfo getUserInfoByCookies(HttpServletRequest request) {
		Userinfo userinfo = new Userinfo();
		Cookie[] cookies = request.getCookies();

		for (Cookie cookie : cookies) {
			if (cookie.getName().equals("provider")) {
				userinfo.provider = cookie.getValue();
			}
			if (cookie.getName().equals("username")) {
				userinfo.username = cookie.getValue();
			}
		}
		return userinfo;
	}

	private static void setCookie(HttpServletResponse response, Map<String, String> userInfo) {
		Cookie username = new Cookie("username", userInfo.get("username"));
		Cookie provider = new Cookie("provider", userInfo.get("provider"));
		Cookie login = new Cookie("login", userInfo.get("login"));

		setCookieOption(username, provider, login);
		for (Cookie cookie : Arrays.asList(username, provider, login)) {
			response.addCookie(cookie);
		}
	}

	private static void setCookieOption(Cookie username, Cookie provider, Cookie login) {
		setMaxAge(username, provider, login);
		setPath(username, provider, login);
	}

	private static void setPath(Cookie username, Cookie provider, Cookie login) {
		username.setPath("/auth/");
		provider.setPath("/auth/");
		login.setPath("/auth/");
	}

	private static void setMaxAge(Cookie username, Cookie provider, Cookie login) {
		username.setMaxAge(5);
		provider.setMaxAge(5);
		login.setMaxAge(5);
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

	private static class Userinfo {
		String username;
		String provider;
	}
}
