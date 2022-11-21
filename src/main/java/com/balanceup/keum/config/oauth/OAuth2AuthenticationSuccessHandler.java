package com.balanceup.keum.config.oauth;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.balanceup.keum.config.util.JwtTokenUtil;
import com.balanceup.keum.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	@Value("${jwt.secret-key}")
	private String secretKey;

	@Value("${jwt.token.expired-time-ms}")
	private Long expiredTimeMS;

	private final UserRepository userRepository;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {
		log.info("OAuth2AuthenticationSuccessHandler 실행");
		if (response.isCommitted()) {
			log.info("이미 응답이 제출되었습니다");
			throw new IllegalStateException("이미 응답 제출");
		}
		clearAuthenticationAttributes(request);
		if (authentication.getPrincipal() == null) {
			log.info("null");
			return;
		}
		String username = authentication.getName();
		String jwtToken = JwtTokenUtil.generateToken(username, secretKey, expiredTimeMS);

		response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
		response.addHeader(HttpHeaders.AUTHORIZATION, jwtToken);

		log.info("jwtToken 생성 {}", jwtToken);

		if (userRepository.findByUsername(username).isEmpty()) {
			//TODO : 닉네임 입력 URI
			getRedirectStrategy().sendRedirect(request, response, "/loginSuccess?token=" + jwtToken);
		}
		//TODO : 이미 회원가입 후 다음 URI
		getRedirectStrategy().sendRedirect(request, response, "/loginSuccess?token=" + jwtToken);

		//TODO :
		// 3. 회원정보 입력 페이지에서 닉네임을 입력 후 Access Token과 함께 제출
		// 4. Access Token을 이용해 이미 가입된 소셜 유저 DB 조회
		// 5. 추가정보들 업데이트 및  추가
	}

}
