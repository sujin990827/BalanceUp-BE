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

import com.balanceup.keum.controller.dto.request.routine.RoutineAllDoneRequest;
import com.balanceup.keum.controller.dto.request.routine.RoutineDeleteRequest;
import com.balanceup.keum.controller.dto.request.routine.RoutineInquireRequest;
import com.balanceup.keum.controller.dto.request.routine.RoutineMakeRequest;
import com.balanceup.keum.controller.dto.request.routine.RoutineProgressRequest;
import com.balanceup.keum.controller.dto.request.routine.RoutineUpdateRequest;
import com.balanceup.keum.domain.Routine;
import com.balanceup.keum.domain.User;
import com.balanceup.keum.fixture.RequestFixture;
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
		RoutineMakeRequest request = RequestFixture.getRoutineMakeRequestFixture();
		String username = "username";
		User mockedUser = mock(User.class);
		Routine routine = Routine.ofRoutineInfo(request, mock(List.class), mockedUser);

		//when
		when(userService.findUserByUsername(username)).thenReturn(mockedUser);
		when(routineRepository.findAllByUser(any(User.class))).thenReturn(mock(List.class));
		when(routineDayService.makeRoutineDays()).thenReturn(mock(List.class));
		when(routineRepository.save(any())).thenReturn(routine);

		//then
		routineService.makeRoutine(request, username);
	}

	@DisplayName("루틴 생성 테스트 (로그인한 유저가 정확하지 않을때)")
	@Test
	void given_NonExistentUser_when_makeRoutine_then_ThrowUsernameNotFoundException() {
		//given
		RoutineMakeRequest request = RequestFixture.getRoutineMakeRequestFixture();
		String username = "username";

		//when
		when(userService.findUserByUsername(username)).thenThrow(UsernameNotFoundException.class);

		//then
		assertThrows(UsernameNotFoundException.class,
			() -> routineService.makeRoutine(request, username));
	}

	@DisplayName("루틴 생성 테스트 (루틴이 4개 이상일 때)")
	@Test
	void given_OverRoutineNumbers_when_makeRoutine_then_ThrowIllegalStateException() {
		//given
		RoutineMakeRequest request = RequestFixture.getRoutineMakeRequestFixture();
		Routine routine = Routine.ofRoutineInfo(request, List.of(), User.of("username", "1234", "asdf", "asd"));
		String username = "username";

		//when
		when(userService.findUserByUsername(username)).thenReturn(mock(User.class));
		when(routineRepository.findAllByUser(any(User.class))).thenReturn(List.of(routine, routine, routine, routine));

		//then
		IllegalStateException e = assertThrows(IllegalStateException.class,
			() -> routineService.makeRoutine(request, username));
		assertEquals("루틴 갯수는 4개를 초과할 수 없습니다.",
			e.getMessage());
	}

	@DisplayName("루틴 생성 테스트 (Routine Day List 값이 저장이 안될때)")
	@Test
	void given_InvalidRoutineInfo_when_makeRoutine_then_ThrowIllegalArgumentException() {
		//given
		RoutineMakeRequest request = RequestFixture.getRoutineMakeRequestFixture();
		String username = "username";

		//when
		when(userService.findUserByUsername(username)).thenReturn(mock(User.class));
		when(routineRepository.findAllByUser(any(User.class))).thenReturn(mock(List.class));
		doThrow(IllegalArgumentException.class).when(routineDayService).makeRoutineDays();

		//then
		assertThrows(IllegalArgumentException.class,
			() -> routineService.makeRoutine(request, username));
	}

	@DisplayName("루틴 생성 테스트 (Routine 값이 저장이 안될때)")
	@Test
	void given_InvalidRoutine_when_makeRoutine_then_ThrowIllegalArgumentException() {
		//given
		RoutineMakeRequest request = RequestFixture.getRoutineMakeRequestFixture();
		String username = "username";

		//when
		when(userService.findUserByUsername(username)).thenReturn(mock(User.class));
		when(routineRepository.findAllByUser(any(User.class))).thenReturn(mock(List.class));
		doReturn(mock(List.class)).when(routineDayService).makeRoutineDays();
		doThrow(IllegalArgumentException.class).when(routineRepository).save(any(Routine.class));

		//then
		assertThrows(IllegalArgumentException.class,
			() -> routineService.makeRoutine(request, username));
	}

	@DisplayName("루틴 생성 테스트 (루틴명이 입력되지 않았을 때)")
	@Test
	void given_InvalidRoutineTitle_when_makeRoutine_then_ThrowIllegalArgumentException() {
		//given
		RoutineMakeRequest request = RequestFixture.getRoutineMakeRequestFixture();
		String username = "username";
		request.setRoutineTitle(null);

		//when
		when(userService.findUserByUsername(username)).thenReturn(mock(User.class));

		//then
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
			() -> routineService.makeRoutine(request, username));
		assertEquals("루틴명이 입력되지 않았습니다.", e.getMessage());
	}

	@DisplayName("루틴 생성 테스트 (루틴 카테고리가 입력되지 않았을 때)")
	@Test
	void given_InvalidRoutineCategory_when_makeRoutine_then_ThrowIllegalArgumentException() {
		//given
		RoutineMakeRequest request = RequestFixture.getRoutineMakeRequestFixture();
		String username = "username";
		request.setRoutineCategory(null);

		//when
		when(userService.findUserByUsername(username)).thenReturn(mock(User.class));

		//then
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
			() -> routineService.makeRoutine(request, username));
		assertEquals("카테고리가 입력되지 않았습니다.", e.getMessage());
	}

	@DisplayName("루틴 생성 테스트 (진행 요일이 입력되지 않았을 때)")
	@Test
	void given_InvalidRoutineDays_when_makeRoutine_then_ThrowIllegalArgumentException() {
		//given
		RoutineMakeRequest request = RequestFixture.getRoutineMakeRequestFixture();
		String username = "username";
		request.setDays(null);

		//when
		when(userService.findUserByUsername(username)).thenReturn(mock(User.class));

		//then
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
			() -> routineService.makeRoutine(request, username));
		assertEquals("진행 요일이 입력되지 않았습니다.", e.getMessage());
	}

	@DisplayName("루틴 수정 테스트")
	@Test
	void given_RoutineUpdateInfo_when_UpdateRoutine_then_DoesNotThrow() {
		//given
		RoutineUpdateRequest request = RequestFixture.getRoutineUpdateRequestFixture();
		String username = "username";

		//when
		when(userService.findUserByUsername(username)).thenReturn(mock(User.class));
		when(routineRepository.findById(eq(request.getRoutineId()))).thenReturn(Optional.of(mock(Routine.class)));

		//then
		assertDoesNotThrow(() -> routineService.updateRoutine(request, username));
	}

	@DisplayName("루틴 수정 테스트 (username 이 정확하지 않을때)")
	@Test
	void given_InvalidUsername_when_UpdateRoutine_then_ThrowUsernameNotFoundException() {
		//given
		RoutineUpdateRequest request = RequestFixture.getRoutineUpdateRequestFixture();
		String username = "username";

		//when
		when(userService.findUserByUsername(username)).thenThrow(IllegalStateException.class);

		//then
		assertThrows(IllegalStateException.class,
			() -> routineService.updateRoutine(request, username));
	}

	@DisplayName("루틴 수정 테스트 (루틴 id가 존재하지 않을때)")
	@Test
	void given_InvalidRoutineId_when_UpdateRoutine_then_ThrowIllegalArgumentException() {
		//given
		RoutineUpdateRequest request = RequestFixture.getRoutineUpdateRequestFixture();
		String username = "username";

		//when
		when(userService.findUserByUsername(username)).thenReturn(mock(User.class));
		when(routineRepository.findById(eq(request.getRoutineId()))).thenReturn(Optional.empty());

		//then
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
			() -> routineService.updateRoutine(request, username));
		assertEquals("이미 삭제된 루틴 id 이거나, 잘못된 id 입니다.", e.getMessage());
	}

	@DisplayName("루틴 수정 테스트 (루틴명이 존재하지 않을때)")
	@Test
	void given_InvalidRoutineTitle_when_UpdateRoutine_then_ThrowIllegalArgumentException() {
		//given
		RoutineUpdateRequest request = RequestFixture.getRoutineUpdateRequestFixture();
		request.setRoutineTitle(null);
		String username = "username";

		//when
		when(userService.findUserByUsername(username)).thenReturn(mock(User.class));
		when(routineRepository.findById(eq(request.getRoutineId()))).thenReturn(Optional.of(mock(Routine.class)));

		//then
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
			() -> routineService.updateRoutine(request, username));
		assertEquals("루틴명이 입력되지 않았습니다.", e.getMessage());
	}

	@DisplayName("루틴 수정 테스트 (진행요일이 입력되지 않았을 때)")
	@Test
	void given_InvalidRoutineDays_when_UpdateRoutine_then_ThrowIllegalArgumentException() {
		//given
		RoutineUpdateRequest request = RequestFixture.getRoutineUpdateRequestFixture();
		request.setDays(null);
		String username = "username";

		//when
		when(userService.findUserByUsername(username)).thenReturn(mock(User.class));
		when(routineRepository.findById(eq(request.getRoutineId()))).thenReturn(Optional.of(mock(Routine.class)));

		//then
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
			() -> routineService.updateRoutine(request, username));
		assertEquals("진행 요일이 입력되지 않았습니다.", e.getMessage());
	}

	@DisplayName("루틴 상세 조회 테스트")
	@Test
	void given_RoutineInquireRequest_when_InquireRoutine_thenDoesNotThrow() {
		//given
		RoutineInquireRequest request = RequestFixture.getRoutineInquireRequestFixture();
		String username = "username";

		//when
		when(userService.findUserByUsername(username)).thenReturn(mock(User.class));
		when(routineRepository.findById(eq(request.getRoutineId()))).thenReturn(Optional.of(mock(Routine.class)));

		//then
		assertDoesNotThrow(() -> routineService.inquireRoutine(request, username));
	}

	@DisplayName("루틴 상세 조회 테스트(username 이 정확하지 않을 때)")
	@Test
	void given_InvalidUsername_when_InquireRoutine_ThrowsUsernameNotFoundException() {
		//given
		RoutineInquireRequest request = RequestFixture.getRoutineInquireRequestFixture();
		String username = "username";

		//when
		when(userService.findUserByUsername(username)).thenThrow(UsernameNotFoundException.class);

		//then
		assertThrows(UsernameNotFoundException.class,
			() -> routineService.inquireRoutine(request, username));
	}

	@DisplayName("루틴 상세 조회 테스트(루틴 id가 정확하지 않을 때)")
	@Test
	void given_InvalidRoutineId_when_InquireRoutine_ThrowsIllegalArgumentException() {
		//given
		RoutineInquireRequest request = RequestFixture.getRoutineInquireRequestFixture();
		String username = "username";

		//when
		when(userService.findUserByUsername(username)).thenReturn(mock(User.class));
		when(routineRepository.findById(eq(request.getRoutineId()))).thenReturn(Optional.empty());

		//then
		assertThrows(IllegalArgumentException.class,
			() -> routineService.inquireRoutine(request, username));
	}

	@DisplayName("루틴 삭제 테스트")
	@Test
	void given_RoutineDeleteRequest_when_DeleteRoutine_then_DoesNotThrow() {
		//given
		RoutineDeleteRequest request = RequestFixture.getRoutineDeleteRequestFixture();
		String username = "username";

		//when
		when(userService.findUserByUsername(username)).thenReturn(mock(User.class));
		when(routineRepository.findById(eq(request.getRoutineId()))).thenReturn(Optional.of(mock(Routine.class)));
		doNothing().when(routineRepository).delete(any(Routine.class));

		//then
		assertDoesNotThrow(() -> routineService.deleteRoutine(request, username));
	}

	@DisplayName("루틴 삭제 테스트(username 이 정확하지 않을 때)")
	@Test
	void given_InvalidUsername_when_DeleteRoutine_then_ThrowUsernameNotFoundException() {
		//given
		RoutineDeleteRequest request = RequestFixture.getRoutineDeleteRequestFixture();
		String username = "username";

		//when
		when(userService.findUserByUsername(username)).thenThrow(IllegalStateException.class);

		//then
		assertThrows(IllegalStateException.class,
			() -> routineService.deleteRoutine(request, username));
	}

	@DisplayName("루틴 삭제 테스트(루틴 id가 정확하지 않을 때)")
	@Test
	void given_InvalidRoutineID_when_DeleteRoutine_then_ThrowIllegalArgumentException() {
		//given
		RoutineDeleteRequest request = RequestFixture.getRoutineDeleteRequestFixture();
		String username = "username";

		//when
		when(userService.findUserByUsername(username)).thenReturn(mock(User.class));
		when(routineRepository.findById(eq(request.getRoutineId()))).thenReturn(Optional.empty());

		//then
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
			() -> routineService.deleteRoutine(request, username));
		assertEquals("이미 삭제된 루틴 id 이거나, 잘못된 id 입니다.", e.getMessage());
	}

	@DisplayName("루틴 진행 테스트 - 하루 루틴만 완료")
	@Test
	void given_RoutineProgressRequest_when_ProgressRoutine_then_DoesNotThrow() {
		//given
		RoutineProgressRequest request = RequestFixture.getRoutineProgressRequestFixture();
		String username = "username";

		//when
		User mockUser = mock(User.class);
		when(userService.findUserByUsername(username)).thenReturn(mockUser);
		when(routineRepository.findById(eq(request.getRoutineId()))).thenReturn(Optional.of(mock(Routine.class)));
		doNothing().when(routineDayService).progressDailyRoutine(any(Routine.class));
		doNothing().when(mockUser).earnRp(1);

		//then
		assertDoesNotThrow(() -> routineService.progressRoutine(request, username));
	}

	@DisplayName("루틴 진행 테스트 - 루틴 전체 완료")
	@Test
	void given_RoutineAllDoneRequest_when_AllDoneRoutine_then_DoesNotThrow() {
		//given
		RoutineAllDoneRequest request = RequestFixture.getRoutineAllDoneRequestFixture();
		String username = "username";

		//when
		User mockUser = mock(User.class);
		Routine mockRoutine = mock(Routine.class);

		when(userService.findUserByUsername(username)).thenReturn(mockUser);
		when(routineRepository.findById(eq(request.getRoutineId()))).thenReturn(Optional.of(mockRoutine));
		doNothing().when(mockRoutine).isAllDone();
		doNothing().when(mockUser).earnRp(20);

		//then
		assertDoesNotThrow(() -> routineService.allDoneRoutine(request, username));
	}

	@DisplayName("루틴 진행 테스트 - 루틴 실패 ")
	@Test
	void given_RoutineAllDoneRequest_when_AllDoneRoutine_then_ThrowIllegalStateException() {
		//given
		RoutineAllDoneRequest request = RequestFixture.getRoutineAllDoneRequestFixture();
		String username = "username";

		//when
		Routine mockRoutine = mock(Routine.class);

		when(userService.findUserByUsername(username)).thenReturn(mock(User.class));
		when(routineRepository.findById(eq(request.getRoutineId()))).thenReturn(Optional.of(mockRoutine));
		doThrow(IllegalStateException.class).when(mockRoutine).isAllDone();

		//then
		assertThrows(IllegalStateException.class,
			() -> routineService.allDoneRoutine(request, username));
	}

	@DisplayName("루틴 전체 조회 테스트")
	@Test
	void given_RoutineTotalInquireRequest_when_TotalInquireRoutine_thenDoesNotThrow() {
		//given
		String username = "username";

		//when
		User mockedUser = mock(User.class);
		when(userService.findUserByUsername(username)).thenReturn(mockedUser);
		when(routineRepository.findAllByUser(mockedUser)).thenReturn(mock(List.class));

		//then
		assertDoesNotThrow(() -> routineService.totalInquireRoutine(username));
	}

	@DisplayName("루틴 전체 조회 테스트 (유저 id가 정확하지 않을 때)")
	@Test
	void given_RoutineTotalInquireRequest_when_TotalInquireRoutine_Throw() {
		//given
		String username = "username";

		//when
		when(userService.findUserByUsername(username)).thenThrow(new IllegalStateException());

		//then
		assertThrows(IllegalStateException.class,
			() -> routineService.totalInquireRoutine(username));
	}

}
