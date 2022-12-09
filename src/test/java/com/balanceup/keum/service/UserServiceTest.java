package com.balanceup.keum.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.balanceup.keum.config.util.JwtTokenUtil;
import com.balanceup.keum.controller.dto.TokenDto;
import com.balanceup.keum.controller.dto.request.DuplicateNicknameRequest;
import com.balanceup.keum.controller.dto.request.UpdateNicknameRequest;
import com.balanceup.keum.domain.User;
import com.balanceup.keum.repository.RedisRepository;
import com.balanceup.keum.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private JwtTokenUtil jwtTokenUtil;

	@Mock
	private RedisRepository redisRepository;

	@InjectMocks
	private UserService userService;

	@DisplayName("닉네임 중복 테스트 - 이미 존재하는 닉네임일 경우")
	@Test
	void given_DuplicateNickname_when_VerifyDuplicateNickname_then_ThrowException() {
		//given
		User user = User.of("username", "password", "dog", "google");
		String nickname1 = "dog";
		DuplicateNicknameRequest request = new DuplicateNicknameRequest(nickname1);

		//mock
		Mockito.when(userRepository.findByNickname(any())).thenReturn(Optional.of(user));

		//when & then
		IllegalStateException e = assertThrows(IllegalStateException.class,
			() -> userService.duplicateNickname(request));
		assertEquals("이미 존재하는 닉네임입니다.", e.getMessage());
	}

	@DisplayName("닉네임 중복 테스트 - 중복 닉네임이 아닌 경우")
	@Test
	void given_NotDuplicateNickname_when_VerifyDuplicateNickname_then_DoesNotThrow() {
		//given
		String nickname1 = "dog";
		DuplicateNicknameRequest request = new DuplicateNicknameRequest(nickname1);

		//mock
		Mockito.when(userRepository.findByNickname(any())).thenReturn(Optional.empty());

		//when & then
		assertDoesNotThrow(() -> userService.duplicateNickname(request));
	}

	@DisplayName("닉네임이 입력되지 않은 경우 ")
	@Test
	void given_NicknameNull_when_DuplicateNickname_then_ThrowException() {
		//given
		String nickname = null;
		DuplicateNicknameRequest request = new DuplicateNicknameRequest(nickname);

		//when & then
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
			() -> userService.duplicateNickname(request));
		assertEquals("닉네임이 비어있습니다.", e.getMessage());
	}

	@DisplayName("닉네임의 길이가 11자 이내")
	@Test
	void given_WrongNicknameLength_when_DuplicateNickname_then_ThrowException() {
		//given
		String nickname = "12345678910";
		DuplicateNicknameRequest request = new DuplicateNicknameRequest(nickname);

		//when & then
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
			() -> userService.duplicateNickname(request));
		assertEquals("닉네임의 길이는 11자 이내여야 합니다.", e.getMessage());
	}

	@DisplayName("닉네임에 숫자, 영어, 한글 외에 다른 문자가 들어가있는 경우")
	@Test
	void given_WrongNicknameCharacter_when_DuplicateNickname_then_ThrowException() {
		//given
		String nickname1 = "       ";
		String nickname2 = "!@#!@$!@";
		DuplicateNicknameRequest request1 = new DuplicateNicknameRequest(nickname1);
		DuplicateNicknameRequest request2 = new DuplicateNicknameRequest(nickname2);

		//when & then
		IllegalArgumentException e1 = assertThrows(IllegalArgumentException.class,
			() -> userService.duplicateNickname(request1));
		IllegalArgumentException e2 = assertThrows(IllegalArgumentException.class,
			() -> userService.duplicateNickname(request2));

		assertEquals("닉네임은 영어, 한글, 숫자만 가능합니다.", e1.getMessage());
		assertEquals("닉네임은 영어, 한글, 숫자만 가능합니다.", e2.getMessage());
	}

	@DisplayName("닉네임 업데이트 테스트 - 이미 존재하는 닉네임일 경우")
	@Test
	void given_DuplicateNickname_when_NicknameUpdate_then_ThrowException() {
		//given
		String nickname = "dog";
		UpdateNicknameRequest request = new UpdateNicknameRequest(nickname, "");
		User user = User.of("username", "password", nickname, "google");

		//mock
		when(userRepository.findByNickname(nickname)).thenReturn(Optional.of(user));

		//when & then
		IllegalStateException e = assertThrows(IllegalStateException.class,
			() -> userService.updateNickname(request, "username"));
		assertEquals("이미 존재하는 닉네임입니다.", e.getMessage());
	}

	@DisplayName("닉네임 업데이트 테스트 - 존재하지 않는 닉네임일 경우")
	@Test
	void given_Nickname_when_NicknameUpdate_then_DoesNotThrow() {
		//given
		String nickname = "dog";
		UpdateNicknameRequest request = new UpdateNicknameRequest(nickname, "");
		User user = User.of("username", "password", nickname, "google");

		//mock
		when(userRepository.findByNickname(nickname)).thenReturn(Optional.empty());
		when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

		//when & then
		assertDoesNotThrow(() -> userService.updateNickname(request, "username"));
	}

	@DisplayName("Refresh 토큰 테스트 - 토큰 만료시")
	@Test
	void given_WrongRefreshToken_when_ReIssue_then_ThrowException() {
		//given
		TokenDto dto = new TokenDto(Map.of("accessToken", "accessToken", "refreshToken", "refreshToken"));
		UserDetails details =
			new org.springframework.security.core.userdetails.User("username", "password",
				List.of(new SimpleGrantedAuthority("ROLE_USER")));

		//mock
		when(jwtTokenUtil.validateToken(dto.getRefreshToken(), details)).thenReturn(false);

		//when & then
		IllegalStateException e = assertThrows(IllegalStateException.class,
			() -> userService.reIssue(dto, details));
		assertEquals("Refresh 토큰이 만료되었습니다.", e.getMessage());
	}

	@DisplayName("Refresh 토큰 테스트 - Redis 에 없는 토큰일 때")
	@Test
	void given_NotNormalToken_when_ReIssue_then_ThrowException() {
		//given
		TokenDto dto = new TokenDto(Map.of("accessToken", "accessToken", "refreshToken", "refreshToken"));
		UserDetails details =
			new org.springframework.security.core.userdetails.User("username", "password",
				List.of(new SimpleGrantedAuthority("ROLE_USER")));

		//mock
		when(jwtTokenUtil.validateToken(dto.getRefreshToken(), details)).thenReturn(true);
		when(redisRepository.getValues(details.getUsername())).thenReturn("notNormalToken");

		//when & then
		IllegalStateException e = assertThrows(IllegalStateException.class,
			() -> userService.reIssue(dto, details));
		assertEquals("만료되거나 존재하지 않는 RefreshToken 입니다. 다시 로그인을 시도해주세요", e.getMessage());
	}

	@DisplayName("Refresh 토큰 테스트 - Redis에 존재하는 토큰일 때")
	@Test
	void given_NormalToken_when_ReIssue_then_DoesNotThrow() {
		//given
		TokenDto dto = new TokenDto(Map.of("accessToken", "accessToken", "refreshToken", "refreshToken"));
		UserDetails details =
			new org.springframework.security.core.userdetails.User("username", "password",
				List.of(new SimpleGrantedAuthority("ROLE_USER")));

		//mock
		when(jwtTokenUtil.validateToken(dto.getRefreshToken(), details)).thenReturn(true);
		when(redisRepository.getValues(details.getUsername())).thenReturn(dto.getRefreshToken());
		when(jwtTokenUtil.generateToken(details.getUsername())).thenReturn(any(TokenDto.class), eq(dto));

		//when & then
		assertDoesNotThrow(() -> userService.reIssue(dto, details));
	}

}
