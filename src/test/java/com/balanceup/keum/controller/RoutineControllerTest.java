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
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.balanceup.keum.config.util.JwtTokenUtil;
import com.balanceup.keum.controller.dto.request.routine.RoutineAllDoneRequest;
import com.balanceup.keum.controller.dto.request.routine.RoutineCancelRequest;
import com.balanceup.keum.controller.dto.request.routine.RoutineDeleteRequest;
import com.balanceup.keum.controller.dto.request.routine.RoutineMakeRequest;
import com.balanceup.keum.controller.dto.request.routine.RoutineProgressRequest;
import com.balanceup.keum.controller.dto.request.routine.RoutineUpdateRequest;
import com.balanceup.keum.controller.dto.response.routine.RoutineMakeResponse;
import com.balanceup.keum.controller.dto.response.routine.RoutineResponse;
import com.balanceup.keum.fixture.RequestFixture;
import com.balanceup.keum.service.RoutineService;
import com.fasterxml.jackson.databind.ObjectMapper;

@AutoConfigureMockMvc
@SpringBootTest
public class RoutineControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private RoutineService routineService;

	@MockBean
	private JwtTokenUtil jwtTokenUtil;

	@MockBean
	private MockHttpServletRequest servletRequest;

	@DisplayName("[API][POST] 루틴 생성 테스트")
	@Test
	@WithMockUser
	void given_RoutineMakeRequest_when_MakeRoutine_then_ReturnCreated() throws Exception {
		//given
		RoutineMakeRequest request = RequestFixture.getRoutineMakeRequestFixture();
		String username = "username";
		String token = "token.token.token";

		//mock
		when(servletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(token);
		when(jwtTokenUtil.getUserNameByToken(token)).thenReturn(username);
		when(routineService.makeRoutine(request, username)).thenReturn(Mockito.any(RoutineMakeResponse.class));

		//when & then
		mockMvc.perform(post("/routine")
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
			.andExpect(jsonPath("$.message", containsString("루틴 생성이 완료되었습니다.")));
	}

	@DisplayName("[API][POST] 루틴 생성 테스트 - 비즈니스 로직 오류")
	@Test
	@WithMockUser
	void given_InvalidRequest_when_MakeRoutine_then_ReturnBadRequest() throws Exception {
		//given
		RoutineMakeRequest request = RequestFixture.getRoutineMakeRequestFixture();
		String token = "token.token.token";

		//mock
		when(servletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(token);
		when(jwtTokenUtil.getUserNameByToken(token)).thenReturn("username");
		when(routineService.makeRoutine(request, "username")).thenThrow(IllegalStateException.class);

		//when & then
		mockMvc.perform(post("/routine")
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

	@DisplayName("[API][POST] 루틴 생성 테스트 - 비즈니스 로직 오류(루틴 갯수 초과)")
	@Test
	@WithMockUser
	void given_OverRoutineOfNumbers_when_MakeRoutine_then_ReturnBadRequest() throws Exception {
		//given
		RoutineMakeRequest request = RequestFixture.getRoutineMakeRequestFixture();
		request.setRoutineCategory(null);
		String username = "username";
		String token = "token.token.token";

		//mock
		when(servletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(token);
		when(jwtTokenUtil.getUserNameByToken(token)).thenReturn("username");
		when(routineService.makeRoutine(request, username)).thenThrow(IllegalStateException.class);

		//when & then
		mockMvc.perform(post("/routine")
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

	@DisplayName("[API][PUT] 루틴 수정 테스트")
	@Test
	@WithMockUser
	void given_RoutineUpdateRequest_when_UpdateRoutine_then_ReturnOk() throws Exception {
		//given
		RoutineUpdateRequest request = RequestFixture.getRoutineUpdateRequestFixture();
		String username = "username";
		String token = "token.token.token";

		//mock
		when(servletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(token);
		when(jwtTokenUtil.getUserNameByToken(token)).thenReturn(username);
		when(routineService.updateRoutine(request, username)).thenReturn(Mockito.any(RoutineResponse.class));

		//when & then
		mockMvc.perform(put("/routine")
				.with(csrf())
				.with(request1 -> {
					request1.addHeader(HttpHeaders.AUTHORIZATION, token);
					return request1;
				})
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(request))
			).andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.resultCode", containsString("success")))
			.andExpect(jsonPath("$.message", containsString("루틴 수정이 완료되었습니다.")));
	}

	@DisplayName("[API][PUT] 루틴 수정 테스트 - 비즈니스 로직 오류")
	@Test
	@WithMockUser
	void given_InvalidRequest_when_UpdateRoutine_then_ReturnBadRequest() throws Exception {
		//given
		RoutineUpdateRequest request = RequestFixture.getRoutineUpdateRequestFixture();
		String username = "username";
		String token = "token.token.token";

		//mock
		when(servletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(token);
		when(jwtTokenUtil.getUserNameByToken(token)).thenReturn(username);
		when(routineService.updateRoutine(request, username)).thenThrow(new IllegalStateException());

		//when & then
		mockMvc.perform(put("/routine")
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

	@DisplayName("[API][PUT] 루틴 하루 진행 테스트")
	@Test
	@WithMockUser
	void given_RoutineProgressRequest_when_ProgressRoutine_then_ReturnOk() throws Exception {
		//given
		RoutineProgressRequest request = RequestFixture.getRoutineProgressRequestFixture();
		String username = "username";
		String token = "token.token.token";

		//mock
		when(servletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(token);
		when(jwtTokenUtil.getUserNameByToken(token)).thenReturn(username);
		doNothing().when(routineService).progressRoutine(request, username);

		//when & then
		mockMvc.perform(put("/progress/routine")
				.with(csrf())
				.with(request1 -> {
					request1.addHeader(HttpHeaders.AUTHORIZATION, token);
					return request1;
				})
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(request))
			).andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.resultCode", containsString("success")))
			.andExpect(jsonPath("$.message", containsString("루틴 진행이 완료되었습니다. 1rp 상승")));
	}

	@DisplayName("[API][PUT] 루틴 하루 진행 테스트 - 비즈니스 로직 오류")
	@Test
	@WithMockUser
	void given_InvalidRequest_when_ProgressRoutine_then_ReturnBadRequest() throws Exception {
		//given
		RoutineProgressRequest request = RequestFixture.getRoutineProgressRequestFixture();
		String username = "username";
		String token = "token.token.token";

		//mock
		when(servletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(token);
		when(jwtTokenUtil.getUserNameByToken(token)).thenReturn(username);
		doThrow(new IllegalStateException()).when(routineService).progressRoutine(request, username);

		//when & then
		mockMvc.perform(put("/progress/routine")
				.with(request1 -> {
					request1.addHeader(HttpHeaders.AUTHORIZATION, token);
					return request1;
				})
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(request))
			).andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.resultCode", containsString("error")));
	}

	@DisplayName("[API][PUT] 루틴 전체 진행 테스트")
	@Test
	@WithMockUser
	void given_AllDoneRoutineRequest_when_AllDoneRoutine_then_ReturnOk() throws Exception {
		//given
		RoutineAllDoneRequest request = RequestFixture.getRoutineAllDoneRequestFixture();
		String username = "username";
		String token = "token.token.token";

		//mock
		when(servletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(token);
		when(jwtTokenUtil.getUserNameByToken(token)).thenReturn(username);
		doNothing().when(routineService).allDoneRoutine(request, username);

		//when & then
		mockMvc.perform(put("/progress/routines")
				.with(csrf())
				.with(request1 -> {
					request1.addHeader(HttpHeaders.AUTHORIZATION, token);
					return request1;
				})
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(request))
			).andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.resultCode", containsString("success")))
			.andExpect(jsonPath("$.message", containsString("루틴 전체 진행이 완료되었습니다. 20rp 상승")));
	}

	@DisplayName("[API][PUT] 루틴 전체 진행 테스트 - 비즈니스 로직 오류")
	@Test
	@WithMockUser
	void given_InvalidRequest_when_AllDoneRoutine_then_ReturnBadRequest() throws Exception {
		//given
		RoutineAllDoneRequest request = RequestFixture.getRoutineAllDoneRequestFixture();
		String username = "username";
		String token = "token.token.token";

		//mock
		when(servletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(token);
		when(jwtTokenUtil.getUserNameByToken(token)).thenReturn(username);
		doThrow(new IllegalStateException()).when(routineService).allDoneRoutine(request, username);

		//when & then
		mockMvc.perform(put("/progress/routines")
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

	@DisplayName("[API][GET] 루틴 상세 조회 테스트")
	@Test
	@WithMockUser
	void given_InquireRoutineRequest_when_InquireRoutine_then_ReturnOk() throws Exception {
		//given
		MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
		param.add("routineId", "1");
		String username = "username";
		String token = "token.token.token";

		//mock
		when(servletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(token);
		when(jwtTokenUtil.getUserNameByToken(token)).thenReturn(username);
		when(routineService.inquireRoutine(1L, username)).thenReturn(Mockito.any(RoutineResponse.class));

		//when & then
		mockMvc.perform(get("/routine")
				.with(csrf())
				.with(request1 -> {
					request1.addHeader(HttpHeaders.AUTHORIZATION, token);
					return request1;
				})
				.contentType(MediaType.APPLICATION_JSON)
				.params(param)
			).andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.resultCode", containsString("success")))
			.andExpect(jsonPath("$.message", containsString("루틴 조회가 완료되었습니다.")));
	}

	@DisplayName("[API][GET] 루틴 상세 조회 테스트 - 비즈니스 로직 오류")
	@Test
	@WithMockUser
	void given_InvalidRequest_when_InquireRoutine_then_ReturnBadRequest() throws Exception {
		//given
		MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
		param.add("routineId", "1");
		String username = "username";
		String token = "token.token.token";

		//mock
		when(jwtTokenUtil.getUserNameByToken(token)).thenReturn(username);
		when(routineService.inquireRoutine(1L, username)).thenThrow(new IllegalStateException());

		//when & then
		mockMvc.perform(get("/routine")
				.with(csrf())
				.with(request1 -> {
					request1.addHeader(HttpHeaders.AUTHORIZATION, token);
					return request1;
				})
				.contentType(MediaType.APPLICATION_JSON)
				.params(param)
			).andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.resultCode", containsString("error")));
	}

	@DisplayName("[API][GET] 루틴 전체 조회 테스트")
	@Test
	@WithMockUser
	void given_TotalInquireRoutineRequest_when_TotalInquireRoutine_then_ReturnOk() throws Exception {
		//given
		String username = "username";
		String token = "token.token.token";

		//mock
		when(jwtTokenUtil.getUserNameByToken(token)).thenReturn(username);
		when(routineService.totalInquireRoutine(username)).thenReturn(Mockito.anyList());

		//when & then
		mockMvc.perform(get("/routines")
				.with(csrf())
				.with(request1 -> {
					request1.addHeader(HttpHeaders.AUTHORIZATION, token);
					return request1;
				})
				.contentType(MediaType.APPLICATION_JSON)
			).andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.resultCode", containsString("success")))
			.andExpect(jsonPath("$.message", containsString("루틴 전체조회가 완료되었습니다.")));
	}

	@DisplayName("[API][GET] 루틴 전체 조회 테스트 - 비즈니스 로직 오류")
	@Test
	@WithMockUser
	void given_InvalidRequest_when_TotalInquireRoutine_then_ReturnBadRequest() throws Exception {
		//given
		String username = "username";
		String token = "token.token.token";

		//mock
		when(jwtTokenUtil.getUserNameByToken(token)).thenReturn(username);
		when(routineService.totalInquireRoutine(username)).thenThrow(new IllegalStateException());

		//when & then
		mockMvc.perform(get("/routines")
				.with(csrf())
				.with(request1 -> {
					request1.addHeader(HttpHeaders.AUTHORIZATION, token);
					return request1;
				})
				.contentType(MediaType.APPLICATION_JSON)
			).andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.resultCode", containsString("error")));
	}

	@DisplayName("[API][DELETE] 루틴 삭제 테스트")
	@Test
	@WithMockUser
	void given_DeleteRoutineRequest_when_DeleteRoutine_then_ReturnOk() throws Exception {
		//given
		RoutineDeleteRequest request = RequestFixture.getRoutineDeleteRequestFixture();
		String username = "username";
		String token = "token.token.token";

		//mock
		when(servletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(token);
		when(jwtTokenUtil.getUserNameByToken(token)).thenReturn(username);
		doNothing().when(routineService).deleteRoutine(request, username);

		//when & then
		mockMvc.perform(delete("/routine")
				.with(csrf())
				.with(request1 -> {
					request1.addHeader(HttpHeaders.AUTHORIZATION, token);
					return request1;
				})
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(request))
			).andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.resultCode", containsString("success")))
			.andExpect(jsonPath("$.message", containsString("루틴 삭제가 완료되었습니다.")));
	}

	@DisplayName("[API][DELETE] 루틴 삭제 테스트 - 비즈니스 로직 오류")
	@Test
	@WithMockUser
	void given_InvalidRequest_when_DeleteRoutine_then_ReturnBadRequest() throws Exception {
		//given
		RoutineDeleteRequest request = RequestFixture.getRoutineDeleteRequestFixture();
		String username = "username";
		String token = "token.token.token";

		//mock
		when(servletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(token);
		when(jwtTokenUtil.getUserNameByToken(token)).thenReturn(username);
		doThrow(new IllegalStateException()).when(routineService).deleteRoutine(request, username);

		//when & then
		mockMvc.perform(delete("/routine")
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

	@DisplayName("[API][PUT] 루틴 취소 테스트")
	@Test
	@WithMockUser
	void given_RoutineCancelRequest_when_CancelRoutine_then_ReturnOk() throws Exception {
		//given
		RoutineCancelRequest request = RequestFixture.getRoutineCancelRequestFixture();
		String username = "username";
		String token = "token.token.token";

		//mock
		when(servletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(token);
		when(jwtTokenUtil.getUserNameByToken(token)).thenReturn(username);
		doNothing().when(routineService).cancelRoutine(request, username);

		//when & then
		mockMvc.perform(put("/cancel")
				.with(csrf())
				.with(request1 -> {
					request1.addHeader(HttpHeaders.AUTHORIZATION, token);
					return request1;
				})
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(request))
			).andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.resultCode", containsString("success")));
	}

	@DisplayName("[API][PUT] 루틴 취소 테스트 - 비즈니스로직 에러")
	@Test
	@WithMockUser
	void given_InvalidCancelRequest_when_CancelRoutine_then_ReturnBadRequest() throws Exception {
		//given
		RoutineCancelRequest request = RequestFixture.getRoutineCancelRequestFixture();
		String username = "username";
		String token = "token.token.token";

		//mock
		when(servletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(token);
		when(jwtTokenUtil.getUserNameByToken(token)).thenReturn(username);
		doThrow(new IllegalStateException()).when(routineService).cancelRoutine(any(), anyString());

		//when & then
		mockMvc.perform(put("/cancel")
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

}
