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
	//TODO : CLIENT_ID yml 파일에 넣기
	private final String CLIENT_ID = "66323ceffc6f43bec404a2f2addbd415";
	private final String REDIRECT_URI = "http://localhost:8080/login/kakao";
	private final String GRANT_TYPE = "authorization_code";

	private final UserRepository userRepository;

	public String getAccessToken(String authorize_code) {
		ResponseEntity<String> response = getKakaoTokenResponse(authorize_code);
		return getElementByResponseBody(response).getAsJsonObject().get("access_token").getAsString();
	}

	public Map<String, String> getUserInfo(String accessToken) {
		ResponseEntity<String> response = getUserInfoResponse(setUserInfoHeaderByAccessToken(accessToken));

		JsonElement element = getElementByResponseBody(response);
		JsonElement kakaoAcount = element.getAsJsonObject().get("kakao_account").getAsJsonObject();

		String password = element.getAsJsonObject().get("id").getAsString();
		String username = kakaoAcount.getAsJsonObject().get("email").getAsString();
		//TODO : 임시 DB에 username + password 저장

		return getHeaderUserInfo(username);
	}

	public Map<String, String> join(String username, String nickname) {

		//TODO : username을 통해 임시 DB에 저장된 OAuth ID 값 찾아오기 (encoder)
		userRepository.save(User.of(username, "Oauth ID", nickname, PROVIDER_KAKAO));
		//TODO : 임시 DB 데이터 삭제
		//토큰 발급
		return makeTokens(username);
	}

	public Map<String, String> login(String username) {
		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 username 입니다."));
		//토큰 발급
		return makeTokens(username);
	}

	private MultiValueMap<String, String> addParamByAuthorizeCode(String authorize_code) {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", GRANT_TYPE);
		params.add("client_id", CLIENT_ID);
		params.add("redirect_uri", REDIRECT_URI);
		params.add("code", authorize_code);
		return params;
	}

	private static HttpHeaders setHeaderContentTypeApplicationFormUrlencoded() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", MediaType.APPLICATION_FORM_URLENCODED_VALUE);
		return headers;
	}

	private HttpEntity<MultiValueMap<String, String>> getKakaoTokenRequest(String authorize_code) {
		return new HttpEntity<>(addParamByAuthorizeCode(authorize_code),
			setHeaderContentTypeApplicationFormUrlencoded());
	}

	private ResponseEntity<String> getKakaoTokenResponse(String authorize_code) {
		return new RestTemplate().postForEntity(TOKEN_URL, getKakaoTokenRequest(authorize_code), String.class);
	}

	private JsonElement getElementByResponseBody(ResponseEntity<String> response) {
		return new JsonParser().parse(response.getBody());
	}

	private ResponseEntity<String> getUserInfoResponse(HttpHeaders headers) {
		return new RestTemplate().postForEntity(USER_INFO_URI, new HttpEntity<>(null, headers), String.class);
	}

	private static HttpHeaders setUserInfoHeaderByAccessToken(String accessToken) {
		HttpHeaders headers = setHeaderContentTypeApplicationFormUrlencoded();
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
		return headers;
	}

	private Map<String, String> getHeaderUserInfo(String username) {
		Map<String, String> header = new HashMap<>();
		header.put("username", username);
		return getHeaderLoginState(username, header);
	}

	private Map<String, String> getHeaderLoginState(String username, Map<String, String> header) {
		if (userRepository.findByUsername(username).isPresent()) {
			header.put("login", "sign-in");
			return header;
		}
		header.put("login", "sign-up");
		header.put("provider", PROVIDER_KAKAO);
		return header;
	}

	private Map<String, String> makeTokens(String username) {
		Map<String, String> tokens = new HashMap<>();
		tokens.put("token", JwtTokenUtil.generateToken(username, secretKey, expiredTimeMS));
		tokens.put("refresh_token", "리프레쉬토큰");
		return tokens;
	}

}
