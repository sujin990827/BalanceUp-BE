package com.balanceup.keum.config.oauth;

import java.util.Optional;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.balanceup.keum.config.auth.PrincipalDetails;
import com.balanceup.keum.config.oauth.provider.GoogleUserInfo;
import com.balanceup.keum.config.oauth.provider.KakaoUserInfo;
import com.balanceup.keum.config.oauth.provider.OAuth2UserInfo;
import com.balanceup.keum.domain.User;
import com.balanceup.keum.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

	private final UserRepository userRepository;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		log.info("OAuth2User.class 실행 : {}", userRequest);
		OAuth2User oAuth2User = super.loadUser(userRequest);
		String registrationId = userRequest.getClientRegistration().getRegistrationId();

		OAuth2UserInfo oAuth2UserInfo;
		if (registrationId.equals("google")) {
			oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
		} else if (registrationId.equals("kakao")) {
			oAuth2UserInfo = new KakaoUserInfo(oAuth2User.getAttributes());
		} else {
			log.error("We supported google and kakao");
			throw new OAuth2AuthenticationException("지원하지 않는 OAuth");
		}

		String provider = oAuth2UserInfo.getProvider();
		String password = oAuth2UserInfo.getProviderId();
		String username = oAuth2UserInfo.getEmail();

		Optional<User> userEntity = userRepository.findByUsername(username);
		User user;
		if (userEntity.isEmpty()) {
			log.info("OAuth 로그인이 최초입니다.");
			user = userRepository.save(User.of(username, password, provider));
			log.info("Oauth Join {}", user);
		} else {
			user = userEntity.get();
			log.info("이미 OAuth 로그인을 한적이 있습니다. 자동회원가입 대상입니다.");
		}

		return new PrincipalDetails(user, oAuth2User.getAttributes());
	}

}
