package com.balanceup.keum.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.balanceup.keum.domain.User;
import com.balanceup.keum.repository.UserRepository;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class KakaoAPI {


	private final String TOKEN_URL = "https://kauth.kakao.com/oauth/token";
	private final String USER_INFO_URI = "https://kapi.kakao.com/v2/user/me";
	private final String CLIENT_ID = "66323ceffc6f43bec404a2f2addbd415";
	private final String REDIRECT_URI = "http://localhost:8080/login/kakao";
	private final String GRANT_TYPE = "authorization_code";

	private final UserRepository userRepository;

	public String getAccessToken(String authorize_code) {

		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", MediaType.APPLICATION_FORM_URLENCODED_VALUE);
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", GRANT_TYPE);
		params.add("client_id", CLIENT_ID);
		params.add("redirect_uri", REDIRECT_URI);
		params.add("code", authorize_code);
		HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

		ResponseEntity<String> response = restTemplate.postForEntity(TOKEN_URL, kakaoTokenRequest, String.class);

		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(response.getBody());
		String accessToken = element.getAsJsonObject().get("access_token").getAsString();
		String refreshToken = element.getAsJsonObject().get("refresh_token").getAsString();
		System.out.println(response);
		System.out.println("accessToken: " + accessToken);
		System.out.println("refreshToken: " + refreshToken);
		return accessToken;
	}

	public Boolean getUserInfo(String accessToken) {
		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", MediaType.APPLICATION_FORM_URLENCODED_VALUE);
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer "+accessToken);

		HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(null, headers);

		ResponseEntity<String> response = restTemplate.postForEntity(USER_INFO_URI, kakaoTokenRequest, String.class);

		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(response.getBody());
		JsonElement kakao_acount = element.getAsJsonObject().get("kakao_account").getAsJsonObject();

		String password = element.getAsJsonObject().get("id").getAsString();
		String username = kakao_acount.getAsJsonObject().get("email").getAsString();

		//userId로 username에서 있는지 찾기
		if (userRepository.findByUsername(username).isPresent()) {
			// throw new IllegalStateException("이미 회원가입을 한 email 입니다. 기존 연동된 Email 소셜 로그인을 이용해주세요.");
			return true;
		}
		//같은 email 로그인 처리 할껀지 말껀지

		return false;
	}
}
