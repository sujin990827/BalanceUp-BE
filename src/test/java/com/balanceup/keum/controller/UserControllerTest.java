package com.balanceup.keum.controller;

import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.balanceup.keum.config.auth.PrincipalDetailService;
import com.balanceup.keum.config.util.JwtTokenUtil;
import com.balanceup.keum.controller.dto.TokenDto;
import com.balanceup.keum.controller.dto.request.user.ReIssueRequest;
import com.balanceup.keum.controller.dto.request.user.UserNicknameUpdateRequest;
import com.balanceup.keum.controller.dto.response.user.UserDeleteResponse;
import com.balanceup.keum.controller.dto.response.user.UserResponse;
import com.balanceup.keum.domain.User;
import com.balanceup.keum.service.RoutineService;
import com.balanceup.keum.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@AutoConfigureMockMvc
@SpringBootTest
public class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private UserService userService;

	@MockBean
	private RoutineService routineService;

	@MockBean
	private PrincipalDetailService principalDetailService;

	@MockBean
	private JwtTokenUtil jwtTokenUtil;

	@MockBean
	private MockHttpServletRequest servletRequest;

	@DisplayName("[API][POST] 닉네임 중복확인 테스트 - 성공")
	@Test
	@WithAnonymousUser
	void given_UserNicknameRequest_when_VerifyDuplicateNickname_then_ReturnOk() throws Exception {
		//given
		String nickname = "nickname";

		//mock
		when(userService.duplicateNickname(nickname)).thenReturn(nickname);

		//when & then
		mockMvc.perform(get("/nicknames")
				.with(csrf())
				.param("nickname", nickname)
			).andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.resultCode", containsString("success")))
			.andExpect(jsonPath("$.message", containsString("닉네임 중복 확인 성공")));
	}

	@DisplayName("[API][POST] 닉네임 중복확인 테스트(닉네임 중복) - 실패")
	@Test
	@WithAnonymousUser
	void given_DuplicateUserNicknameRequest_when_VerifyDuplicateNickname_then_ReturnBadRequest() throws Exception {
		//given
		String nickname = "nickname";

		//when
		when(userService.duplicateNickname(nickname)).thenThrow(
			IllegalStateException.class);

		//then
		mockMvc.perform(get("/nicknames")
				.with(csrf())
				.param("nickname", nickname)
			).andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.resultCode", containsString("error")));
	}

	@DisplayName("[API][POST] 닉네임 중복확인 테스트(잘못된 닉네임) - 실패")
	@Test
	@WithAnonymousUser
	void given_WrongNickname_when_VerifyDuplicateNickname_then_ReturnBadRequest() throws Exception {
		//given
		String nickname = "wrongNickname";

		//when
		when(userService.duplicateNickname(nickname)).thenThrow(
			IllegalArgumentException.class);

		//then
		mockMvc.perform(get("/nicknames")
				.with(csrf())
				.param("nickname", nickname)
			).andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.resultCode", containsString("error")));
	}

	@DisplayName("[API][PUT] 닉네임 업데이트 테스트 - 성공")
	@Test
	@WithMockUser
	void given_UpdateNicknameRequest_when_UpdateNickname_then_ReturnOk() throws Exception {
		//given
		String username = "username";
		String nickname = "nickname";
		String token = "token.token.token";
		UserNicknameUpdateRequest request = new UserNicknameUpdateRequest(nickname);

		//mock
		when(servletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(token);
		when(jwtTokenUtil.getUserNameByToken(token)).thenReturn(username);
		when(userService.updateNickname(request, username)).thenReturn(mock(UserResponse.class));

		//when & then
		mockMvc.perform(put("/user/nickname")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(request))
				.header(HttpHeaders.AUTHORIZATION, token)
			).andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.resultCode", containsString("success")))
			.andExpect(jsonPath("$.message", containsString("닉네임 업데이트 성공")));
	}

	@DisplayName("[API][PUT] 닉네임 업데이트 테스트(중복 닉네임) - 실패")
	@Test
	@WithMockUser
	void given_DuplicateNickname_when_UpdateNickname_then_ReturnBadRequest() throws Exception {
		//given
		String nickname = "nickname";
		String token = "token.token.token";
		String username = "username";
		UserNicknameUpdateRequest request = new UserNicknameUpdateRequest(nickname);

		//mock
		when(servletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(token);
		when(jwtTokenUtil.getUserNameByToken(token)).thenReturn(username);
		when(userService.updateNickname(Mockito.any(UserNicknameUpdateRequest.class), anyString())).thenThrow(
			IllegalArgumentException.class);

		//when & then
		mockMvc.perform(put("/user/nickname")
				.with(csrf())
				.with(request1 -> {
					request1.addHeader(HttpHeaders.AUTHORIZATION, token);
					return request1;
				})
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(request))
				.header(HttpHeaders.AUTHORIZATION, token)
			).andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.resultCode", containsString("error")));
	}

	@DisplayName("[API][PUT] 닉네임 업데이트 테스트(잘못된 닉네임) - 실패")
	@Test
	@WithMockUser
	void given_WrongNickname_when_UpdateNickname_then_ReturnBadRequest() throws Exception {
		//given
		String nickname = "wrongNickname";
		String username = "username";
		String token = "token.token.token";
		UserNicknameUpdateRequest request = new UserNicknameUpdateRequest(nickname);

		//mock
		when(servletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(token);
		when(jwtTokenUtil.getUserNameByToken(token)).thenReturn(username);
		when(userService.updateNickname(Mockito.any(UserNicknameUpdateRequest.class), anyString())).thenThrow(
			new IllegalStateException());

		//when & then
		mockMvc.perform(put("/user/nickname")
				.with(csrf())
				.with(request1 -> {
					request1.addHeader(HttpHeaders.AUTHORIZATION, token);
					return request1;
				})
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(request))
				.header(HttpHeaders.AUTHORIZATION, token)
			).andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.resultCode", containsString("error")));

	}

	@DisplayName("[API][GET] 토큰 재발급 테스트 ")
	@Test
	@WithMockUser
	void given_TokenDtoAndUserDetails_when_ReIssueToken_then_ReturnCreated() throws Exception {
		//given
		ReIssueRequest request = getReIssueRequestFixture();
		String token = "token.token.token";

		//mock
		when(principalDetailService.loadUserByUsername(request.getUsername())).thenReturn(mock(UserDetails.class));
		when(userService.reIssue(request, mock(UserDetails.class))).thenReturn(mock(TokenDto.class));

		//when & then
		mockMvc.perform(post("/auth/refresh")
				.with(csrf())
				.with(request1 -> {
					request1.addHeader(HttpHeaders.AUTHORIZATION, token);
					return request1;
				})
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(request))
			).andDo(print())
			.andExpect(status().isCreated())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.resultCode", containsString("success")))
			.andExpect(jsonPath("$.message", containsString("AccessToken 재발급이 완료되었습니다.")));
	}

	@DisplayName("[API][GET] 토큰 재발급 테스트(존재하지 않는 유저) - 실패 ")
	@Test
	@WithMockUser
	void given_TokenDtoAndUserDetails_when_ReIssueToken_then_ReturnBadRequest() throws Exception {
		//given
		ReIssueRequest request = getReIssueRequestFixture();
		String token = "token.token.token";

		//mock
		when(principalDetailService.loadUserByUsername(request.getUsername()))
			.thenThrow(UsernameNotFoundException.class);

		//when & then
		mockMvc.perform(post("/auth/refresh")
				.with(csrf())
				.with(request1 -> {
					request1.addHeader(HttpHeaders.AUTHORIZATION, token);
					return request1;
				})
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(request))
			).andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.resultCode", containsString("error")));
	}

	@DisplayName("[API][GET] 토큰 재발급 테스트(만료토큰) - 실패 ")
	@Test
	@WithMockUser
	void given_WrongToken_when_ReIssueToken_then_ReturnBadRequest() throws Exception {
		//given
		ReIssueRequest request = getReIssueRequestFixture();
		String token = "token.token.token";

		//mock
		when(principalDetailService.loadUserByUsername(request.getUsername())).thenReturn(mock(UserDetails.class));
		when(userService.reIssue(Mockito.any(ReIssueRequest.class), Mockito.any(UserDetails.class)))
			.thenThrow(IllegalStateException.class);

		//when & then
		mockMvc.perform(post("/auth/refresh")
				.with(csrf())
				.with(request1 -> {
					request1.addHeader(HttpHeaders.AUTHORIZATION, token);
					return request1;
				})
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(request))
			).andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.resultCode", containsString("error")));
	}

	@DisplayName("[API][DELETE] 회원 탈퇴 테스트 (로그인한 유저는 잘못된 유저) - 실패")
	@Test
	void given_NonExistentUser_when_DeleteUser_then_ReturnBadRequest() throws Exception {
		//given
		String username = "username";
		String token = "token.token.token";

		//mock
		when(servletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(token);
		when(jwtTokenUtil.getUserNameByToken(token)).thenReturn(username);
		when(routineService.deleteRoutineByUser(username)).thenThrow(new IllegalStateException());

		//when & then
		mockMvc.perform(delete("/withdraw")
				.with(csrf())
				.with(request1 -> {
					request1.addHeader(HttpHeaders.AUTHORIZATION, token);
					return request1;
				})
				.contentType(MediaType.APPLICATION_JSON)
			).andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.resultCode", containsString("error")));
	}

	@DisplayName("[API][DELETE] 회원 탈퇴 테스트 - 성공")
	@Test
	void given_Username_when_DeleteUser_then_ReturnOk() throws Exception {
		//given
		String username = "username";
		String token = "token.token.token";
		User mockUser = mock(User.class);

		//mock
		when(servletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(token);
		when(jwtTokenUtil.getUserNameByToken(token)).thenReturn(username);
		when(routineService.deleteRoutineByUser(username)).thenReturn(mockUser);
		when(userService.delete(mockUser)).thenReturn(ArgumentMatchers.any(UserDeleteResponse.class));

		//when & then
		mockMvc.perform(delete("/withdraw")
				.with(csrf())
				.with(request1 -> {
					request1.addHeader(HttpHeaders.AUTHORIZATION, token);
					return request1;
				})
				.contentType(MediaType.APPLICATION_JSON)
			).andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.resultCode", containsString("success")))
			.andExpect(jsonPath("$.message", containsString("회원탈퇴가 완료되었습니다.")));
	}

	@DisplayName("[API][GET] 유저 정보 조회 테스트 ")
	@Test
	@WithMockUser
	void given_Username_when_UserInfoRequire_then_ReturnOk() throws Exception {
		//given
		String username = "username";
		String token = "token";

		//mock
		when(servletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(token);
		when(jwtTokenUtil.getUserNameByToken(token)).thenReturn(username);
		when(userService.getUserInfoByUsername(anyString())).thenReturn(any());

		//when & then
		mockMvc.perform(get("/user")
				.with(csrf())
				.with(request1 -> {
					request1.addHeader(HttpHeaders.AUTHORIZATION, token);
					return request1;
				})
				.contentType(MediaType.APPLICATION_JSON)
			).andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.resultCode", containsString("success")));
	}

	@DisplayName("[API][GET] 유저 정보 조회 테스트 - 비즈니스 로직 에러")
	@Test
	@WithMockUser
	void given_NonexistentUsername_when_UserInfoRequire_then_ReturnBadRequest() throws Exception {
		//given
		String username = "username";
		String token = "token";

		//mock
		when(servletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(token);
		when(jwtTokenUtil.getUserNameByToken(token)).thenReturn(username);
		when(userService.getUserInfoByUsername(anyString())).thenThrow(new IllegalStateException());

		//when & then
		mockMvc.perform(get("/user")
				.with(csrf())
				.with(request1 -> {
					request1.addHeader(HttpHeaders.AUTHORIZATION, token);
					return request1;
				})
				.contentType(MediaType.APPLICATION_JSON)
			).andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.resultCode", containsString("error")));
	}

	private static ReIssueRequest getReIssueRequestFixture() {
		ReIssueRequest request = new ReIssueRequest();
		request.setUsername("username");
		request.setToken("accessToken");
		request.setRefreshToken("refreshToken");
		return request;
	}

}
