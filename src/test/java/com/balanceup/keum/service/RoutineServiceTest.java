package com.balanceup.keum.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.balanceup.keum.controller.dto.request.routine.RoutineInquireRequest;
import com.balanceup.keum.controller.dto.request.routine.RoutineMakeRequest;
import com.balanceup.keum.controller.dto.request.routine.RoutineUpdateRequest;
import com.balanceup.keum.domain.Routine;
import com.balanceup.keum.domain.RoutineCategory;
import com.balanceup.keum.domain.User;
import com.balanceup.keum.repository.RoutineRepository;

@ExtendWith(MockitoExtension.class)
public class RoutineServiceTest {

	@Mock
	private RoutineRepository routineRepository;

	@Mock
	private UserService userService;

	@Mock
	private RoutineDayService routineDayService;

	@InjectMocks
	private RoutineService routineService;

	@DisplayName("루틴 생성 테스트")
	@Test
	void given_RoutineInfo_when_makeRoutine_then_DoesNotThrow() {
		//given
		RoutineMakeRequest request = getRoutineMakeRequestFixture();

		//when
		when(userService.findUserByUsername(eq(request.getUsername()))).thenReturn(mock(User.class));
		when(routineDayService.makeRoutineDays()).thenReturn(mock(List.class));
		when(routineRepository.save(any())).thenReturn(mock(Routine.class));

		//then
		assertDoesNotThrow(() -> routineService.makeRoutine(request));
	}

	@DisplayName("루틴 생성 테스트 (로그인한 유저가 정확하지 않을때)")
	@Test
	void given_NonExistentUser_when_makeRoutine_then_ThrowUsernameNotFoundException() {
		//given
		RoutineMakeRequest request = getRoutineMakeRequestFixture();

		//when
		when(userService.findUserByUsername(eq(request.getUsername()))).thenThrow(UsernameNotFoundException.class);

		//then
		assertThrows(UsernameNotFoundException.class,
			() -> routineService.makeRoutine(request));
	}

	@DisplayName("루틴 생성 테스트 (Routine Day List 값이 저장이 안될때)")
	@Test
	void given_InvalidRoutineInfo_when_makeRoutine_then_ThrowIllegalArgumentException() {
		//given
		RoutineMakeRequest request = getRoutineMakeRequestFixture();

		//when
		when(userService.findUserByUsername(eq(request.getUsername()))).thenReturn(mock(User.class));
		doThrow(IllegalArgumentException.class).when(routineDayService).makeRoutineDays();

		//then
		assertThrows(IllegalArgumentException.class,
			() -> routineService.makeRoutine(request));
	}

	@DisplayName("루틴 생성 테스트 (Routine 값이 저장이 안될때)")
	@Test
	void given_InvalidRoutine_when_makeRoutine_then_ThrowIllegalArgumentException() {
		//given
		RoutineMakeRequest request = getRoutineMakeRequestFixture();

		//when
		when(userService.findUserByUsername(eq(request.getUsername()))).thenReturn(mock(User.class));
		doReturn(mock(List.class)).when(routineDayService).makeRoutineDays();
		doThrow(IllegalArgumentException.class).when(routineRepository).save(any(Routine.class));

		//then
		assertThrows(IllegalArgumentException.class,
			() -> routineService.makeRoutine(request));
	}

	@DisplayName("루틴 생성 테스트 (루틴명이 입력되지 않았을 때)")
	@Test
	void given_InvalidRoutineTitle_when_makeRoutine_then_ThrowIllegalArgumentException() {
		//given
		RoutineMakeRequest request = getRoutineMakeRequestFixture();
		request.setRoutineTitle(null);

		//when
		when(userService.findUserByUsername(eq(request.getUsername()))).thenReturn(mock(User.class));

		//then
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
			() -> routineService.makeRoutine(request));
		assertEquals("루틴명이 입력되지 않았습니다.", e.getMessage());
	}

	@DisplayName("루틴 생성 테스트 (루틴 카테고리가 입력되지 않았을 때)")
	@Test
	void given_InvalidRoutineCategory_when_makeRoutine_then_ThrowIllegalArgumentException() {
		//given
		RoutineMakeRequest request = getRoutineMakeRequestFixture();
		request.setRoutineCategory(null);

		//when
		when(userService.findUserByUsername(eq(request.getUsername()))).thenReturn(mock(User.class));

		//then
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
			() -> routineService.makeRoutine(request));
		assertEquals("카테고리가 입력되지 않았습니다.", e.getMessage());
	}

	@DisplayName("루틴 생성 테스트 (진행 요일이 입력되지 않았을 때)")
	@Test
	void given_InvalidRoutineDays_when_makeRoutine_then_ThrowIllegalArgumentException() {
		//given
		RoutineMakeRequest request = getRoutineMakeRequestFixture();
		request.setDays(null);

		//when
		when(userService.findUserByUsername(eq(request.getUsername()))).thenReturn(mock(User.class));

		//then
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
			() -> routineService.makeRoutine(request));
		assertEquals("진행 요일이 입력되지 않았습니다.", e.getMessage());
	}

	@DisplayName("루틴 수정 테스트")
	@Test
	void given_RoutineUpdateInfo_when_UpdateRoutine_then_DoesNotThrow() {
		//given
		RoutineUpdateRequest request = getRoutineUpdateRequestFixture();

		//when
		when(userService.findUserByUsername(eq(request.getUsername()))).thenReturn(mock(User.class));
		when(routineRepository.findById(eq(request.getRoutineId()))).thenReturn(Optional.of(mock(Routine.class)));

		//then
		assertDoesNotThrow(() -> routineService.updateRoutine(request));
	}

	@DisplayName("루틴 수정 테스트 (username 이 정확하지 않을때)")
	@Test
	void given_InvalidUsername_when_UpdateRoutine_then_ThrowUsernameNotFoundException() {
		//given
		RoutineUpdateRequest request = getRoutineUpdateRequestFixture();

		//when
		when(userService.findUserByUsername(eq(request.getUsername()))).thenThrow(UsernameNotFoundException.class);

		//then
		assertThrows(UsernameNotFoundException.class,
			() -> routineService.updateRoutine(request));
	}

	@DisplayName("루틴 수정 테스트 (루틴 id가 존재하지 않을때)")
	@Test
	void given_InvalidRoutineId_when_UpdateRoutine_then_ThrowIllegalArgumentException() {
		//given
		RoutineUpdateRequest request = getRoutineUpdateRequestFixture();

		//when
		when(userService.findUserByUsername(eq(request.getUsername()))).thenReturn(mock(User.class));
		when(routineRepository.findById(eq(request.getRoutineId()))).thenReturn(Optional.empty());

		//then
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
			() -> routineService.updateRoutine(request));
		assertEquals("이미 삭제된 루틴 id 이거나, 잘못된 id 입니다.", e.getMessage());
	}

	@DisplayName("루틴 수정 테스트 (루틴명이 존재하지 않을때)")
	@Test
	void given_InvalidRoutineTitle_when_UpdateRoutine_then_ThrowIllegalArgumentException() {
		//given
		RoutineUpdateRequest request = getRoutineUpdateRequestFixture();
		request.setRoutineTitle(null);

		//when
		when(userService.findUserByUsername(eq(request.getUsername()))).thenReturn(mock(User.class));
		when(routineRepository.findById(eq(request.getRoutineId()))).thenReturn(Optional.of(mock(Routine.class)));

		//then
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
			() -> routineService.updateRoutine(request));
		assertEquals("루틴명이 입력되지 않았습니다.", e.getMessage());
	}

	@DisplayName("루틴 수정 테스트 (진행요일이 입력되지 않았을 때)")
	@Test
	void given_InvalidRoutineDays_when_UpdateRoutine_then_ThrowIllegalArgumentException() {
		//given
		RoutineUpdateRequest request = getRoutineUpdateRequestFixture();
		request.setDays(null);

		//when
		when(userService.findUserByUsername(eq(request.getUsername()))).thenReturn(mock(User.class));
		when(routineRepository.findById(eq(request.getRoutineId()))).thenReturn(Optional.of(mock(Routine.class)));

		//then
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
			() -> routineService.updateRoutine(request));
		assertEquals("진행 요일이 입력되지 않았습니다.", e.getMessage());
	}

	@DisplayName("루틴 조회 테스트")
	@Test
	void given_RoutineInquireRequest_when_InquireRoutine_thenDoesNotThrow() {
		//given
		RoutineInquireRequest request = getRoutineInquireRequestFixture();

		//when
		when(userService.findUserByUsername(eq(request.getUsername()))).thenReturn(mock(User.class));
		when(routineRepository.findById(eq(request.getRoutineId()))).thenReturn(Optional.of(mock(Routine.class)));

		//then
		assertDoesNotThrow(() -> routineService.inquireRoutine(request));
	}

	@DisplayName("루틴 조회 테스트(username 이 정확하지 않을 때)")
	@Test
	void given_InvalidUsername_when_InquireRoutine_ThrowsUsernameNotFoundException() {
		//given
		RoutineInquireRequest request = getRoutineInquireRequestFixture();

		//when
		when(userService.findUserByUsername(eq(request.getUsername()))).thenThrow(UsernameNotFoundException.class);

		//then
		assertThrows(UsernameNotFoundException.class,
			() -> routineService.inquireRoutine(request));
	}

	@DisplayName("루틴 조회 테스트(루틴 id가 정확하지 않을 때)")
	@Test
	void given_InvalidRoutineId_when_InquireRoutine_ThrowsIllegalArgumentException() {
		//given
		RoutineInquireRequest request = getRoutineInquireRequestFixture();

		//when
		when(userService.findUserByUsername(eq(request.getUsername()))).thenReturn(mock(User.class));
		when(routineRepository.findById(eq(request.getRoutineId()))).thenReturn(Optional.empty());

		//then
		assertThrows(IllegalArgumentException.class,
			() -> routineService.inquireRoutine(request));
	}

	private RoutineUpdateRequest getRoutineUpdateRequestFixture() {
		RoutineUpdateRequest request = new RoutineUpdateRequest();
		request.setUsername("username");
		request.setRoutineId(1L);
		request.setRoutineTitle("title");
		request.setDays("월화수");
		request.setAlarmTime("09:00");
		return request;
	}

	private static RoutineMakeRequest getRoutineMakeRequestFixture() {
		RoutineMakeRequest request = new RoutineMakeRequest();
		request.setUsername("username");
		request.setRoutineTitle("title");
		request.setDays("월화수");
		request.setAlarmTime("09:00");
		request.setRoutineCategory(RoutineCategory.EXERCISE);
		return request;
	}

	private RoutineInquireRequest getRoutineInquireRequestFixture() {
		RoutineInquireRequest request = new RoutineInquireRequest();
		request.setUsername("username");
		request.setRoutineId(1L);
		return request;
	}
}
