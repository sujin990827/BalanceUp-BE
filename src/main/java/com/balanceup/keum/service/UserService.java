package com.balanceup.keum.service;

import static java.util.Objects.*;

import org.springframework.stereotype.Service;

import com.balanceup.keum.controller.request.UpdateNicknameRequest;
import com.balanceup.keum.controller.response.UserResponse;
import com.balanceup.keum.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {

	private final UserRepository userRepository;

	public UserResponse updateNickname(UpdateNicknameRequest dto) {

		String username = dto.getUsername();
		String nickname = dto.getNickname();

		if (userRepository.findByNickname(nickname).isPresent()) {
			throw new IllegalStateException("이미 회원가입한 유저입니다.");
		}

		isValidNickname(nickname);

		return UserResponse.from(userRepository.findByUsername(username)
			.orElseThrow(() -> new IllegalStateException("존재하지 않는 회원입니다."))
			.updateUserNickname(nickname), dto.getToken());
	}

	public String duplicateNickname(String nickname) {
		isValidNickname(nickname);
		return userRepository.findByNickname(nickname)
			.orElseThrow(() -> new IllegalStateException("이미 존재하는 닉네임입니다."))
			.getNickname();
	}

	private static void isValidNickname(String nickname) {
		if (isNull(nickname)) {
			throw new IllegalArgumentException("닉네임이 비어있습니다.");
		}

		if (nickname.length() < 1 || nickname.length() > 11) {
			throw new IllegalArgumentException("닉네임의 길이는 11자 이내여야 합니다.");
		}

		if (!nickname.matches("^[a-zA-Z0-9가-힣]*$")) {
			throw new IllegalArgumentException("닉네임은 영어, 한글, 숫자만 가능합니다.");
		}
	}

}

