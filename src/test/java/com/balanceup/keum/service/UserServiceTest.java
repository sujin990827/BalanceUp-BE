package com.balanceup.keum.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.balanceup.keum.controller.dto.request.DuplicateNicknameRequest;
import com.balanceup.keum.domain.User;
import com.balanceup.keum.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserService userService;

	@DisplayName("닉네임 중복 테스트 - 이미 존재하는 닉네임일 경우")
	@Test
	void given_DuplicateNickname_when_VerifyDuplicateNickname_then_ThrowException() {
		//given
		User user = User.of("username", "password", "dog", "google");
		String nickname1 = "dog";
		DuplicateNicknameRequest request = new DuplicateNicknameRequest(nickname1);

		//when
		Mockito.when(userRepository.findByNickname(any())).thenReturn(Optional.of(user));

		//then
		IllegalStateException e = assertThrows(IllegalStateException.class,
			() -> userService.duplicateNickname(request));
		assertEquals("이미 존재하는 닉네임입니다.", e.getMessage());
	}

	@DisplayName("닉네임 중복 테스트 - 중복 닉네임이 아닌 경우")
	@Test
	void given_NotDuplicateNickname_when_VerifyDuplicateNickname_then_DoesNotThrow() {
		//given
		User user = User.of("username", "password", "dog", "google");
		String nickname1 = "dog";
		DuplicateNicknameRequest request = new DuplicateNicknameRequest(nickname1);

		//when
		Mockito.when(userRepository.findByNickname(any())).thenReturn(Optional.empty());

		//then
		assertDoesNotThrow(() -> userService.duplicateNickname(request));
	}

	@DisplayName("닉네임이 입력되지 않은 경우 ")
	@Test
	void given_NicknameNull_when_DuplicateNickname_then_ThrowException() {
		//given
		String nickname = null;
		DuplicateNicknameRequest request = new DuplicateNicknameRequest(nickname);

		//when
		//then
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

		//when
		//then
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

		//when
		//then
		IllegalArgumentException e1 = assertThrows(IllegalArgumentException.class,
			() -> userService.duplicateNickname(request1));
		IllegalArgumentException e2 = assertThrows(IllegalArgumentException.class,
			() -> userService.duplicateNickname(request2));

		assertEquals("닉네임은 영어, 한글, 숫자만 가능합니다.", e1.getMessage());
		assertEquals("닉네임은 영어, 한글, 숫자만 가능합니다.", e2.getMessage());
	}

}
