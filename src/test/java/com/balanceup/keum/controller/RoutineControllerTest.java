package com.balanceup.keum.controller;

import static org.hamcrest.CoreMatchers.*;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.balanceup.keum.controller.dto.request.routine.RoutineMakeRequest;
import com.balanceup.keum.controller.dto.response.routine.RoutineMakeResponse;
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

	@DisplayName("[API][POST] 루틴 생성 테스트")
	@Test
	@WithMockUser
	void given_RoutineMakeRequest_when_MakeRoutine_then_ReturnCreated() throws Exception {
		//given
		RoutineMakeRequest request = RequestFixture.getRoutineMakeRequestFixture();

		//mock
		when(routineService.makeRoutine(request)).thenReturn(Mockito.any(RoutineMakeResponse.class));

		//when & then
		mockMvc.perform(post("/routine")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(request))
			).andDo(print())
			.andExpect(status().isCreated())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.resultCode", containsString("success")))
			.andExpect(jsonPath("$.message", containsString("루틴 생성이 완료되었습니다.")));
	}

	@DisplayName("[API][POST] 루틴 생성 테스트 - 루틴명이 입력되지 않은 경우")
	@Test
	@WithMockUser
	void given_NonexistentRoutineTitle_when_MakeRoutine_then_ReturnBadRequest() throws Exception {
		//given
		RoutineMakeRequest request = RequestFixture.getRoutineMakeRequestFixture();
		request.setRoutineTitle(null);

		//mock
		when(routineService.makeRoutine(request)).thenThrow(IllegalArgumentException.class);

		//when & then
		mockMvc.perform(post("/routine")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(request))
			).andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.resultCode", containsString("error")));
	}

	@DisplayName("[API][POST] 루틴 생성 테스트 - 루틴카테고리가 입력되지 않은 경우")
	@Test
	@WithMockUser
	void given_NonexistentRoutineCategory_when_MakeRoutine_then_ReturnBadRequest() throws Exception {
		//given
		RoutineMakeRequest request = RequestFixture.getRoutineMakeRequestFixture();
		request.setRoutineCategory(null);

		//mock
		when(routineService.makeRoutine(request)).thenThrow(IllegalArgumentException.class);

		//when & then
		mockMvc.perform(post("/routine")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(request))
			).andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.resultCode", containsString("error")));
	}

	@DisplayName("[API][POST] 루틴 생성 테스트 - 진행 요일이 입력되지 않은 경우")
	@Test
	@WithMockUser
	void given_NonexistentRoutineDays_when_MakeRoutine_then_ReturnBadRequest() throws Exception {
		//given
		RoutineMakeRequest request = RequestFixture.getRoutineMakeRequestFixture();
		request.setDays(null);

		//mock
		when(routineService.makeRoutine(request)).thenThrow(IllegalArgumentException.class);

		//when & then
		mockMvc.perform(post("/routine")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(request))
			).andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.resultCode", containsString("error")));
	}

	@DisplayName("[API][POST] 루틴 생성 테스트 - 루틴 개수가 4개를 초과한 경우")
	@Test
	@WithMockUser
	void given_OverRoutineOfNumbers_when_MakeRoutine_then_ReturnBadRequest() throws Exception {
		//given
		RoutineMakeRequest request = RequestFixture.getRoutineMakeRequestFixture();
		request.setDays(null);

		//mock
		when(routineService.makeRoutine(request)).thenThrow(IllegalStateException.class);

		//when & then
		mockMvc.perform(post("/routine")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(request))
			).andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.resultCode", containsString("error")));
	}

}
