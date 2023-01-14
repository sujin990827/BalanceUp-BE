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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.balanceup.keum.controller.dto.request.routine.RoutineAllDoneRequest;
import com.balanceup.keum.controller.dto.request.routine.RoutineInquireRequest;
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

	@DisplayName("[API][POST] 루틴 생성 테스트 - 비즈니스 로직 오류")
	@Test
	@WithMockUser
	void given_InvalidRequest_when_MakeRoutine_then_ReturnBadRequest() throws Exception {
		//given
		RoutineMakeRequest request = RequestFixture.getRoutineMakeRequestFixture();

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

	@DisplayName("[API][POST] 루틴 생성 테스트 - 비즈니스 로직 오류(루틴 갯수 초과)")
	@Test
	@WithMockUser
	void given_OverRoutineOfNumbers_when_MakeRoutine_then_ReturnBadRequest() throws Exception {
		//given
		RoutineMakeRequest request = RequestFixture.getRoutineMakeRequestFixture();
		request.setRoutineCategory(null);

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

	@DisplayName("[API][PUT] 루틴 수정 테스트")
	@Test
	@WithMockUser
	void given_RoutineUpdateRequest_when_UpdateRoutine_then_ReturnOk() throws Exception {
		//given
		RoutineUpdateRequest request = RequestFixture.getRoutineUpdateRequestFixture();

		//mock
		when(routineService.updateRoutine(request)).thenReturn(Mockito.any(RoutineResponse.class));

		//when & then
		mockMvc.perform(put("/routine")
				.with(csrf())
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

		//mock
		when(routineService.updateRoutine(request)).thenThrow(new IllegalStateException());

		//when & then
		mockMvc.perform(put("/routine")
				.with(csrf())
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

		//mock
		doNothing().when(routineService).progressRoutine(request);

		//when & then
		mockMvc.perform(put("/progress/routine")
				.with(csrf())
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

		//mock
		doThrow(new IllegalStateException()).when(routineService).progressRoutine(request);

		//when & then
		mockMvc.perform(put("/progress/routine")
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

		//mock
		doNothing().when(routineService).allDoneRoutine(request);

		//when & then
		mockMvc.perform(put("/progress/routines")
				.with(csrf())
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

		//mock
		doThrow(new IllegalStateException()).when(routineService).allDoneRoutine(request);

		//when & then
		mockMvc.perform(put("/progress/routines")
				.with(csrf())
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
		RoutineInquireRequest request = RequestFixture.getRoutineInquireRequestFixture();
		MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
		param.add("userId", "1");
		param.add("routineId", "1");

		//mock
		when(routineService.inquireRoutine(request)).thenReturn(Mockito.any(RoutineResponse.class));

		//when & then
		mockMvc.perform(get("/routine")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(request))
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
		RoutineInquireRequest request = RequestFixture.getRoutineInquireRequestFixture();
		MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
		param.add("userId", "1");
		param.add("routineId", "1");

		//mock
		when(routineService.inquireRoutine(request)).thenThrow(new IllegalStateException());

		//when & then
		mockMvc.perform(get("/routine")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(request))
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
		RoutineInquireRequest request = RequestFixture.getRoutineInquireRequestFixture();
		MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
		param.add("userId", "1");

		//mock
		when(routineService.totalInquireRoutine(1L)).thenReturn(Mockito.anyList());

		//when & then
		mockMvc.perform(get("/routines")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(request))
				.params(param)
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
		RoutineInquireRequest request = RequestFixture.getRoutineInquireRequestFixture();
		MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
		param.add("userId", "1");

		//mock
		when(routineService.totalInquireRoutine(1L)).thenThrow(new IllegalStateException());

		//when & then
		mockMvc.perform(get("/routines")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(request))
				.params(param)
			).andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.resultCode", containsString("error")));
	}

}
