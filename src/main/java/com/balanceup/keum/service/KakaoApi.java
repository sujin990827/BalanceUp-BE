package com.balanceup.keum.service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.balanceup.keum.config.util.JwtTokenUtil;
import com.balanceup.keum.controller.dto.TokenDto;
import com.balanceup.keum.domain.User;
import com.balanceup.keum.repository.RedisRepository;
import com.balanceup.keum.repository.UserRepository;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class KakaoApi {

	private final String PROVIDER_KAKAO = "kakao";
	private final String GRANT_TYPE = "authorization_code";

	@Value("${oauth.kakao.token-url}")
	private String TOKEN_URL;

	@Value("${oauth.kakao.userinfo}")
	private String USER_INFO_URI;

	@Value("${oauth.kakao.client-id}")
	private String CLIENT_ID;

	@Value("${oauth.kakao.redirect}")
	private String REDIRECT_URI;

	private final UserRepository userRepository;
	private final JwtTokenUtil jwtTokenUtil;
	private final RedisRepository redisRepository;
	private final BCryptPasswordEncoder encoder;

	public Map<String, String> getUserInfo(String accessToken) {
		ResponseEntity<String> response = getUserInfoResponse(setUserInfoHeaderByAccessToken(accessToken));

		JsonElement element = getElementByResponseBody(response);
		JsonElement kakaoAcount = element.getAsJsonObject().get("kakao_account").getAsJsonObject();

		String password = element.getAsJsonObject().get("id").getAsString();
		String username = kakaoAcount.getAsJsonObject().get("email").getAsString();

		redisRepository.setValues(username, password, Duration.ofMillis(60 * 1000));
		return getHeaderUserInfo(username);
	}

	public Map<String, String> join(String username, String nickname) {
		String password = encoder.encode(redisRepository.getValues(username));

		userRepository.save(User.of(username, password, nickname, PROVIDER_KAKAO));

		return makeTokens(username);
	}

	public Map<String, String> login(String username) {
		userRepository.findByUsername(username)
			.orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 username 입니다."));
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
		Map<String, String> state = new HashMap<>();
		state.put("username", username);
		return getHeaderLoginState(username, state);
	}

	private Map<String, String> getHeaderLoginState(String username, Map<String, String> state) {
		state.put("provider", PROVIDER_KAKAO);
		if (userRepository.findByUsername(username).isPresent()) {
			state.put("login", "sign-in");
			return state;
		}
		state.put("login", "sign-up");
		return state;
	}

	private Map<String, String> putTokensMap(String username) {
		Map<String, String> tokens = new HashMap<>();
		TokenDto token = jwtTokenUtil.generateToken(username);
		tokens.put("accessToken", token.getToken());
		tokens.put("refreshToken", token.getRefreshToken());

		return tokens;
	}

	private Map<String, String> makeTokens(String username) {
		return putTokensMap(username);
	}
}
