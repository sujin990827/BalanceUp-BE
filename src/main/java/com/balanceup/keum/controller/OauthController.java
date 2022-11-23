package com.balanceup.keum.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.balanceup.keum.controller.response.Response;
import com.balanceup.keum.service.KakaoAPI;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class OauthController {

	private final KakaoAPI kakaoAPI;

	@GetMapping("/login/kakao")
	public String kakaoCallback(@RequestParam String code) {
		System.out.println(code);
		String accessToken = kakaoAPI.getAccessToken(code);
		// Map<String, Object> userInfo = kakaoAPI.getUserInfo(accessToken);
		// System.out.println(userInfo);
		return "index";
	}

	// 여기서 회원가입 유무를 알 수 있다.
	@ResponseBody
	@GetMapping("/oauth/login/kakao")
	public void kakaoLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String access_token = request.getParameter("access_token");

		boolean isLogin = kakaoAPI.getUserInfo(access_token);
		if (isLogin) {
			response.sendRedirect("진행 페이지");
			return;
		}
		response.sendRedirect("닉네임 입력 페이지");
	}



}
