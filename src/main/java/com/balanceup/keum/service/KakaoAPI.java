package com.balanceup.keum.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.balanceup.keum.config.util.JwtTokenUtil;
import com.balanceup.keum.domain.User;
import com.balanceup.keum.repository.UserRepository;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class KakaoAPI {

	@Value("${jwt.secret-key}")
	private String secretKey;

	@Value("${jwt.token.expired-time-ms}")
	private Long expiredTimeMS;

	private final String PROVIDER_KAKAO = "kakao";
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

		return element.getAsJsonObject().get("access_token").getAsString();
	}

	public Map<String, String> getUserInfo(String accessToken) {
		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", MediaType.APPLICATION_FORM_URLENCODED_VALUE);
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer "+accessToken);

		HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(null, headers);

		ResponseEntity<String> response = restTemplate.postForEntity(USER_INFO_URI, kakaoTokenRequest, String.class);

		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(response.getBody());
		JsonElement kakao_acount = element.getAsJsonObject().get("kakao_account").getAsJsonObject();

		Map<String, String> header = new HashMap<>();

		String password = element.getAsJsonObject().get("id").getAsString();
		String username = kakao_acount.getAsJsonObject().get("email").getAsString();

		//TODO : 임시 DB에 username + password 저장

		header.put("username", username);

		//userId로 username에서 있는지 찾기
		if (userRepository.findByUsername(username).isPresent()) {
			header.put("login", "sign-in");
			return header;
		}
		header.put("login", "sign-up");
		header.put("provider", PROVIDER_KAKAO);
		return header;
	}

	public Map<String, String> join(String username, String nickname) {

		//TODO : username을 통해 임시 DB에 저장된 OAuth ID 값 찾아오기 (encoder)
		User user = User.of(username, "Oauth ID", nickname, "kakao");
		userRepository.save(user);
		//TODO : 임시 DB 데이터 삭제
		//토큰 발급
		String token = JwtTokenUtil.generateToken(username, secretKey, expiredTimeMS);
		Map<String, String> tokens = new HashMap<>();
		tokens.put("token", token);
		tokens.put("refresh_token", "리프레쉬토큰");
		return tokens;
	}

	public Map<String, String> login(String username) {
		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 username 입니다."));
		//토큰 발급
		String token = JwtTokenUtil.generateToken(username, secretKey, expiredTimeMS);
		Map<String, String> tokens = new HashMap<>();
		tokens.put("token", token);
		tokens.put("refresh_token", "리프레쉬토큰");
		return tokens;
	}
}
