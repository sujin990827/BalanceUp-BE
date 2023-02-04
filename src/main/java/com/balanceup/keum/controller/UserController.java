package com.balanceup.keum.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.balanceup.keum.config.auth.PrincipalDetailService;
import com.balanceup.keum.config.util.JwtTokenUtil;
import com.balanceup.keum.controller.dto.request.user.ReIssueRequest;
import com.balanceup.keum.controller.dto.request.user.UserNicknameUpdateRequest;
import com.balanceup.keum.controller.dto.response.Response;
import com.balanceup.keum.domain.User;
import com.balanceup.keum.repository.UserRepository;
import com.balanceup.keum.service.RoutineService;
import com.balanceup.keum.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
public class UserController {
	private final UserRepository userRepository;

	private final UserService userService;
	private final RoutineService routineService;
	private final PrincipalDetailService principalDetailService;
	private final JwtTokenUtil jwtTokenUtil;

	@GetMapping("/nicknames")
	public ResponseEntity<?> duplicateNickname(@RequestParam String nickname) {
		return new ResponseEntity<>(getSuccessResponse("닉네임 중복 확인 성공", userService.duplicateNickname(nickname)),
			HttpStatus.OK);
	}

	@PutMapping("/user/nickname")
	public ResponseEntity<?> updateNickname(@RequestBody UserNicknameUpdateRequest request,
		HttpServletRequest servletRequest) {
		String username = jwtTokenUtil.getUserNameByToken(servletRequest.getHeader(HttpHeaders.AUTHORIZATION));

		return new ResponseEntity<>(getSuccessResponse("닉네임 업데이트 성공", userService.updateNickname(request, username)),
			HttpStatus.OK);
	}

	@PostMapping("/auth/refresh")
	public ResponseEntity<?> getRefreshToken(@RequestBody ReIssueRequest request) {
		UserDetails userDetails = principalDetailService.loadUserByUsername(request.getUsername());

		return new ResponseEntity<>(
			getSuccessResponse("AccessToken 재발급이 완료되었습니다.", userService.reIssue(request, userDetails)),
			HttpStatus.CREATED);
	}

	@DeleteMapping("/withdraw")
	public ResponseEntity<?> deleteUser(HttpServletRequest servletRequest) {
		String username = jwtTokenUtil.getUserNameByToken(servletRequest.getHeader(HttpHeaders.AUTHORIZATION));
		User user = userService.findUserByUsername(username);
		routineService.deleteRoutineByUser(user);

		return new ResponseEntity<>(
			getSuccessResponse("회원탈퇴가 완료되었습니다.", userService.delete(user)),
			HttpStatus.OK);
	}

	@GetMapping("/user")
	public ResponseEntity<?> inquireUser(HttpServletRequest servletRequest) {
		String username = jwtTokenUtil.getUserNameByToken(servletRequest.getHeader(HttpHeaders.AUTHORIZATION));
		User user = userService.findUserByUsername(username);

		return new ResponseEntity<>(
			getSuccessResponse("회원정보 조회가 완료되었습니다.", userService.getUserInfoByUsername(user)),
			HttpStatus.OK);
	}

	private static Response<Object> getSuccessResponse(String message, Object body) {
		return Response.of("success", message, body);
	}

}

