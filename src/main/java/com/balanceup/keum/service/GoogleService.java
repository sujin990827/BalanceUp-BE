package com.balanceup.keum.service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.balanceup.keum.config.util.JwtTokenUtil;
import com.balanceup.keum.controller.dto.TokenDto;
import com.balanceup.keum.domain.User;
import com.balanceup.keum.repository.RedisRepository;
import com.balanceup.keum.repository.UserRepository;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class GoogleService {

	private final String PROVIDER = "google";

	private final UserRepository userRepository;
	private final JwtTokenUtil jwtTokenUtil;
	private final RedisRepository redisRepository;
	private final BCryptPasswordEncoder encoder;

	@Value("${oauth.google.userinfo}")
	private String USER_INFO_URI;

	public Map<String, String> getUserInfo(String accessToken) {
		ResponseEntity<String> response = getUserInfoToResponseEntity(accessToken);

		JsonElement element = getElementByResponseBody(response);

		String password = element.getAsJsonObject().get("id").getAsString();
		String username = element.getAsJsonObject().get("email").getAsString();

		redisRepository.setValues(username, password, Duration.ofMillis(60 * 1000));

		return getHeaderUserInfo(username);
	}

	@Transactional
	public Map<String, String> join(String username, String nickname) {
		String encodePassword = encoder.encode(isExpireInRedis(username));

		userRepository.save(User.of(username, encodePassword, nickname, PROVIDER));

		return makeTokens(username);
	}

	@Transactional(readOnly = true)
	public Map<String, String> login(String username) {
		if (userRepository.findByUsername(username).isEmpty()) {
			throw new UsernameNotFoundException("존재하지 않는 username 입니다.");
		}

		return makeTokens(username);
	}

	private ResponseEntity<String> getUserInfoToResponseEntity(String jwtAccessToken) {
		return new RestTemplate().exchange(
			USER_INFO_URI,
			HttpMethod.GET,
			new HttpEntity<>(setHeaderByJwtAccessToken(jwtAccessToken)),
			String.class);
	}

	private static HttpHeaders setHeaderByJwtAccessToken(String jwtAccessToken) {
		HttpHeaders headers = new HttpHeaders();

		headers.add("Content-type", MediaType.APPLICATION_FORM_URLENCODED_VALUE);
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwtAccessToken);

		return headers;
	}

	private JsonElement getElementByResponseBody(ResponseEntity<String> response) {
		return new JsonParser()
			.parse(Objects.requireNonNull(response.getBody()));
	}

	private Map<String, String> getHeaderUserInfo(String username) {
		Map<String, String> state = new ConcurrentHashMap<>();

		state.put("username", username);

		return getHeaderLoginState(username, state);
	}

	private Map<String, String> getHeaderLoginState(String username, Map<String, String> state) {
		state.put("provider", PROVIDER);

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

		setTokens(tokens, token);

		return tokens;
	}

	private static void setTokens(Map<String, String> tokens, TokenDto token) {
		tokens.put("accessToken", token.getToken());
		tokens.put("refreshToken", token.getRefreshToken());
	}

	private String isExpireInRedis(String username) {
		String rawPassword = redisRepository.getValues(username);

		if (rawPassword == null) {
			throw new IllegalStateException("Password is expire in Redis");
		}

		return rawPassword;
	}

	private Map<String, String> makeTokens(String username) {
		return putTokensMap(username);
	}

}
