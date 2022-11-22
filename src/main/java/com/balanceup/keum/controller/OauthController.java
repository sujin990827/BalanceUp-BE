package com.balanceup.keum.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.balanceup.keum.service.KakaoAPI;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class OauthController {

	private final KakaoAPI kakaoAPI;

	@GetMapping("/login")
	public String kakaoCallback(@RequestParam String code) {
		String accessToken = kakaoAPI.getAccessToken(code);
		System.out.println("accessToken : " + accessToken);
		return "index";
	}
}
