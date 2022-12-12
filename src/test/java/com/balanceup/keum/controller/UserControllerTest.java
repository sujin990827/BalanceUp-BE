package com.balanceup.keum.controller;

import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.balanceup.keum.config.auth.PrincipalDetailService;
import com.balanceup.keum.controller.dto.TokenDto;
import com.balanceup.keum.controller.dto.request.DuplicateNicknameRequest;
import com.balanceup.keum.controller.dto.request.UpdateNicknameRequest;
import com.balanceup.keum.controller.dto.response.UserResponse;
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
	private PrincipalDetailService principalDetailService;

	@DisplayName("[API][POST] 닉네임 중복확인 테스트 - 성공")
	@Test
	@WithAnonymousUser
	void given_UserNicknameRequest_when_VerifyDuplicateNickname_then_ReturnOk() throws Exception {
		//given
		String nickname = "nickname";
		DuplicateNicknameRequest request = new DuplicateNicknameRequest(nickname);

		//mock
		when(userService.duplicateNickname(request)).thenReturn(nickname);

		//when & then
		mockMvc.perform(post("/user/nickname")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(request))
			).andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.resultCode", containsString("success")))
			.andExpect(jsonPath("$.message", containsString("닉네임 중복 확인 성공")))
			.andExpect(jsonPath("$.body", containsString(nickname)));
	}

	@DisplayName("[API][POST] 닉네임 중복확인 테스트(닉네임 중복) - 실패")
	@Test
	@WithAnonymousUser
	void given_DuplicateUserNicknameRequest_when_VerifyDuplicateNickname_then_ReturnBadRequest() throws Exception {
		//given
		String nickname = "nickname";
		DuplicateNicknameRequest request = new DuplicateNicknameRequest(nickname);

		//when
		when(userService.duplicateNickname(Mockito.any(DuplicateNicknameRequest.class))).thenThrow(
			IllegalStateException.class);

		//then
		mockMvc.perform(post("/user/nickname")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(request))
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
		DuplicateNicknameRequest request = new DuplicateNicknameRequest(nickname);

		//when
		when(userService.duplicateNickname(Mockito.any(DuplicateNicknameRequest.class))).thenThrow(
			IllegalArgumentException.class);

		//then
		mockMvc.perform(post("/user/nickname")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(request))
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
		String userName = "userName";
		String nickname = "nickname";
		String token = "jwtToken";
		UpdateNicknameRequest request = new UpdateNicknameRequest(nickname, token);

		//mock
		when(userService.updateNickname(request, userName)).thenReturn(mock(UserResponse.class));

		//when & then
		mockMvc.perform(put("/user/nickname")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(request))
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
		String token = "jwtToken";
		UpdateNicknameRequest request = new UpdateNicknameRequest(nickname, token);

		//mock
		when(userService.updateNickname(Mockito.any(UpdateNicknameRequest.class), anyString())).thenThrow(
			IllegalArgumentException.class);

		//when & then
		mockMvc.perform(put("/user/nickname")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(request))
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
		String nickname = "nickname";
		String token = "jwtToken";
		UpdateNicknameRequest request = new UpdateNicknameRequest(nickname, token);

		//mock
		when(userService.updateNickname(Mockito.any(UpdateNicknameRequest.class), anyString())).thenThrow(
			IllegalArgumentException.class);

		//when & then
		mockMvc.perform(put("/user/nickname")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(request))
			).andDo(print())

			.andExpect(status().isBadRequest())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.resultCode", containsString("error")));
	}

	@DisplayName("[API][GET] 토큰 재발급 테스트 - 성공 ")
	@Test
	@WithMockUser
	void given_TokenDtoAndUserDetails_when_ReIssueToken_then_ReturnCreated() throws Exception {
		//given
		TokenDto request = new TokenDto(Map.of("accessToken", "accessToken", "refreshToken", "refreshToken"));
		UserDetails details =
			new org.springframework.security.core.userdetails.User("username", "password",
				List.of(new SimpleGrantedAuthority("ROLE_USER")));

		//mock
		when(principalDetailService.loadUserByUsername(details.getUsername())).thenReturn(mock(UserDetails.class));
		when(userService.reIssue(request, details)).thenReturn(mock(TokenDto.class));

		//when & then
		mockMvc.perform(get("/auth/refresh")
				.with(csrf())
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
		TokenDto request = new TokenDto(Map.of("accessToken", "accessToken", "refreshToken", "refreshToken"));

		//mock
		when(principalDetailService.loadUserByUsername(anyString()))
			.thenThrow(UsernameNotFoundException.class);

		//when & then
		mockMvc.perform(get("/auth/refresh")
				.with(csrf())
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
		TokenDto request = new TokenDto(Map.of("accessToken", "accessToken", "refreshToken", "refreshToken"));

		//mock
		when(principalDetailService.loadUserByUsername(anyString())).thenReturn(mock(UserDetails.class));
		when(userService.reIssue(Mockito.any(TokenDto.class), Mockito.any(UserDetails.class)))
			.thenThrow(IllegalStateException.class);

		//when & then
		mockMvc.perform(get("/auth/refresh")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(request))
			).andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.resultCode", containsString("error")));
	}

}
