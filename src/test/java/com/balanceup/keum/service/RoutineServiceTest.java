package com.balanceup.keum.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.balanceup.keum.controller.dto.request.routine.RoutineMakeRequest;
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

	@DisplayName("루틴 생성 테스트 - 성공")
	@Test
	void given_RoutineInfo_when_makeRoutine_then_DoesNotThrow() {
		//given
		RoutineMakeRequest request = getRoutineMakeRequestFixture();

		//when
		when(userService.findUserByUsername(eq(request.getUsername()))).thenReturn(mock(User.class));
		doReturn(mock(List.class)).when(routineDayService).makeRoutineDays();
		doReturn(mock(Routine.class)).when(routineRepository).save(any());

		//then
		assertDoesNotThrow(() -> routineService.makeRoutine(request));
	}

	@DisplayName("루틴 생성 테스트 (로그인한 유저가 정확하지 않을때) - 실패")
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

	@DisplayName("루틴 생성 테스트 (Routine Day List 값이 저장이 안될때) - 실패")
	@Test
	void given_NotValidRoutineInfo_when_makeRoutine_then_ThrowIllegalArgumentException() {
		//given
		RoutineMakeRequest request = getRoutineMakeRequestFixture();

		//when
		when(userService.findUserByUsername(eq(request.getUsername()))).thenReturn(mock(User.class));
		doThrow(IllegalArgumentException.class).when(routineDayService).makeRoutineDays();

		//then
		assertThrows(IllegalArgumentException.class,
			() -> routineService.makeRoutine(request));
	}

	@DisplayName("루틴 생성 테스트 (Routine 값이 저장이 안될때) - 실패")
	@Test
	void given_NotValidRoutineDayList_when_makeRoutine_then_ThrowIllegalArgumentException() {
		//given
		RoutineMakeRequest request = getRoutineMakeRequestFixture();

		//when
		when(userService.findUserByUsername(eq(request.getUsername()))).thenReturn(mock(User.class));
		doReturn(mock(List.class)).when(routineDayService).makeRoutineDays();
		doThrow(IllegalArgumentException.class).when(routineRepository).save(any());

		//then
		assertThrows(IllegalArgumentException.class,
			() -> routineService.makeRoutine(request));
	}

	private static RoutineMakeRequest getRoutineMakeRequestFixture() {
		RoutineMakeRequest request = new RoutineMakeRequest();
		request.setUsername("username");
		request.setRoutineTitle("title");
		request.setDays("월화수");
		request.setAlarm(true);
		request.setRoutineCategory(RoutineCategory.EXERCISE);
		return request;
	}
}
