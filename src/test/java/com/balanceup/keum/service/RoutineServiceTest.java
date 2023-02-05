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

import com.balanceup.keum.controller.dto.request.routine.RoutineAllDoneRequest;
import com.balanceup.keum.controller.dto.request.routine.RoutineCancelRequest;
import com.balanceup.keum.controller.dto.request.routine.RoutineDeleteRequest;
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
	private RoutineDayService routineDayService;

	@InjectMocks
	private RoutineService routineService;

	@DisplayName("루틴 생성 테스트")
	@Test
	void given_RoutineInfo_when_makeRoutine_then_DoesNotThrow() {
		//given
		RoutineMakeRequest request = RequestFixture.getRoutineMakeRequestFixture();
		User mockedUser = mock(User.class);
		Routine routine = Routine.ofRoutineInfo(request, mock(List.class), mockedUser);

		//when
		when(routineRepository.findAllByUser(any(User.class))).thenReturn(mock(List.class));
		when(routineDayService.makeRoutineDays("월화수")).thenReturn(mock(List.class));
		when(routineRepository.save(any())).thenReturn(routine);

		//then
		routineService.makeRoutine(request, mockedUser);
	}

	@DisplayName("루틴 생성 테스트 (루틴이 4개 이상일 때)")
	@Test
	void given_OverRoutineNumbers_when_makeRoutine_then_ThrowIllegalStateException() {
		//given
		RoutineMakeRequest request = RequestFixture.getRoutineMakeRequestFixture();
		Routine routine = Routine.ofRoutineInfo(request, List.of(), User.of("username", "1234", "asdf", "asd"));
		User mockedUser = mock(User.class);

		//when
		when(routineRepository.findAllByUser(any(User.class))).thenReturn(List.of(routine, routine, routine, routine));

		//then
		IllegalStateException e = assertThrows(IllegalStateException.class,
			() -> routineService.makeRoutine(request, mockedUser));
		assertEquals("루틴 갯수는 4개를 초과할 수 없습니다.",
			e.getMessage());
	}

	@DisplayName("루틴 생성 테스트 (루틴명이 입력되지 않았을 때)")
	@Test
	void given_InvalidRoutineTitle_when_makeRoutine_then_ThrowIllegalArgumentException() {
		//given
		RoutineMakeRequest request = RequestFixture.getRoutineMakeRequestFixture();
		User mockedUser = mock(User.class);
		request.setRoutineTitle(null);

		//when

		//then
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
			() -> routineService.makeRoutine(request, mockedUser));
		assertEquals("루틴명이 입력되지 않았습니다.", e.getMessage());
	}

	@DisplayName("루틴 생성 테스트 (루틴 카테고리가 입력되지 않았을 때)")
	@Test
	void given_InvalidRoutineCategory_when_makeRoutine_then_ThrowIllegalArgumentException() {
		//given
		RoutineMakeRequest request = RequestFixture.getRoutineMakeRequestFixture();
		User mockedUser = mock(User.class);
		request.setRoutineCategory(null);

		//when

		//then
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
			() -> routineService.makeRoutine(request, mockedUser));
		assertEquals("카테고리가 입력되지 않았습니다.", e.getMessage());
	}

	@DisplayName("루틴 생성 테스트 (진행 요일이 입력되지 않았을 때)")
	@Test
	void given_InvalidRoutineDays_when_makeRoutine_then_ThrowIllegalArgumentException() {
		//given
		RoutineMakeRequest request = RequestFixture.getRoutineMakeRequestFixture();
		User mockedUser = mock(User.class);
		request.setDays(null);

		//when

		//then
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
			() -> routineService.makeRoutine(request, mockedUser));
		assertEquals("진행 요일이 입력되지 않았습니다.", e.getMessage());
	}

	@DisplayName("루틴 수정 테스트")
	@Test
	void given_RoutineUpdateInfo_when_UpdateRoutine_then_DoesNotThrow() {
		//given
		RoutineUpdateRequest request = RequestFixture.getRoutineUpdateRequestFixture();
		User mockedUser = mock(User.class);

		//when
		when(routineRepository.findById(eq(request.getRoutineId()))).thenReturn(Optional.of(mock(Routine.class)));

		//then
		assertDoesNotThrow(() -> routineService.updateRoutine(request, mockedUser));
	}

	@DisplayName("루틴 수정 테스트 (루틴 id가 존재하지 않을때)")
	@Test
	void given_InvalidRoutineId_when_UpdateRoutine_then_ThrowIllegalArgumentException() {
		//given
		RoutineUpdateRequest request = RequestFixture.getRoutineUpdateRequestFixture();
		User mockedUser = mock(User.class);

		//when
		when(routineRepository.findById(eq(request.getRoutineId()))).thenReturn(Optional.empty());

		//then
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
			() -> routineService.updateRoutine(request, mockedUser));
		assertEquals("이미 삭제된 루틴 id 이거나, 잘못된 id 입니다.", e.getMessage());
	}

	@DisplayName("루틴 수정 테스트 (루틴명이 존재하지 않을때)")
	@Test
	void given_InvalidRoutineTitle_when_UpdateRoutine_then_ThrowIllegalArgumentException() {
		//given
		RoutineUpdateRequest request = RequestFixture.getRoutineUpdateRequestFixture();
		request.setRoutineTitle(null);
		User mockedUser = mock(User.class);

		//when
		when(routineRepository.findById(eq(request.getRoutineId()))).thenReturn(Optional.of(mock(Routine.class)));

		//then
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
			() -> routineService.updateRoutine(request, mockedUser));
		assertEquals("루틴명이 입력되지 않았습니다.", e.getMessage());
	}

	@DisplayName("루틴 상세 조회 테스트")
	@Test
	void given_RoutineInquireRequest_when_InquireRoutine_thenDoesNotThrow() {
		//given
		Long routineId = 1L;
		User mockedUser = mock(User.class);

		//when
		when(routineRepository.findById(eq(routineId))).thenReturn(Optional.of(mock(Routine.class)));

		//then
		assertDoesNotThrow(() -> routineService.inquireRoutine(routineId, mockedUser));
	}

	@DisplayName("루틴 상세 조회 테스트(루틴 id가 정확하지 않을 때)")
	@Test
	void given_InvalidRoutineId_when_InquireRoutine_ThrowsIllegalArgumentException() {
		//given
		Long routineId = 1L;
		User mockedUser = mock(User.class);

		//when
		when(routineRepository.findById(eq(routineId))).thenReturn(Optional.empty());

		//then
		assertThrows(IllegalArgumentException.class,
			() -> routineService.inquireRoutine(routineId, mockedUser));
	}

	@DisplayName("루틴 삭제 테스트")
	@Test
	void given_RoutineDeleteRequest_when_DeleteRoutine_then_DoesNotThrow() {
		//given
		RoutineDeleteRequest request = RequestFixture.getRoutineDeleteRequestFixture();

		//when
		Routine mockRoutine = mock(Routine.class);
		when(routineRepository.findById(eq(request.getRoutineId()))).thenReturn(Optional.of(mockRoutine));
		doNothing().when(mockRoutine).countCompletedDaysAndDecreaseRp();
		doNothing().when(routineRepository).delete(any(Routine.class));

		//then
		assertDoesNotThrow(() -> routineService.deleteRoutine(request));
	}

	@DisplayName("루틴 삭제 테스트(루틴 id가 정확하지 않을 때)")
	@Test
	void given_InvalidRoutineID_when_DeleteRoutine_then_ThrowIllegalArgumentException() {
		//given
		RoutineDeleteRequest request = RequestFixture.getRoutineDeleteRequestFixture();

		//when
		when(routineRepository.findById(eq(request.getRoutineId()))).thenReturn(Optional.empty());

		//then
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
			() -> routineService.deleteRoutine(request));
		assertEquals("이미 삭제된 루틴 id 이거나, 잘못된 id 입니다.", e.getMessage());
	}

	@DisplayName("루틴 진행 테스트 - 하루 루틴만 완료")
	@Test
	void given_RoutineProgressRequest_when_ProgressRoutine_then_DoesNotThrow() {
		//given
		RoutineProgressRequest request = RequestFixture.getRoutineProgressRequestFixture();
		User mockedUser = mock(User.class);

		//when
		when(routineRepository.findById(eq(request.getRoutineId()))).thenReturn(Optional.of(mock(Routine.class)));
		doNothing().when(routineDayService).progressDailyRoutine(any(Routine.class));
		doNothing().when(mockedUser).earnRp(1);

		//then
		assertDoesNotThrow(() -> routineService.progressRoutine(request, mockedUser));
	}

	@DisplayName("루틴 진행 테스트 - 루틴 전체 완료")
	@Test
	void given_RoutineAllDoneRequest_when_AllDoneRoutine_then_DoesNotThrow() {
		//given
		RoutineAllDoneRequest request = RequestFixture.getRoutineAllDoneRequestFixture();
		User mockedUser = mock(User.class);

		//when
		Routine mockRoutine = mock(Routine.class);
		when(routineRepository.findById(eq(request.getRoutineId()))).thenReturn(Optional.of(mockRoutine));
		doNothing().when(mockRoutine).isAllDone();
		doNothing().when(mockedUser).earnRp(20);

		//then
		assertDoesNotThrow(() -> routineService.allDoneRoutine(request, mockedUser));
	}

	@DisplayName("루틴 진행 테스트 - 루틴 실패 ")
	@Test
	void given_RoutineAllDoneRequest_when_AllDoneRoutine_then_ThrowIllegalStateException() {
		//given
		RoutineAllDoneRequest request = RequestFixture.getRoutineAllDoneRequestFixture();
		User mockedUser = mock(User.class);

		//when
		Routine mockRoutine = mock(Routine.class);

		when(routineRepository.findById(eq(request.getRoutineId()))).thenReturn(Optional.of(mockRoutine));
		doThrow(IllegalStateException.class).when(mockRoutine).isAllDone();

		//then
		assertThrows(IllegalStateException.class,
			() -> routineService.allDoneRoutine(request, mockedUser));
	}

	@DisplayName("루틴 전체 조회 테스트")
	@Test
	void given_RoutineTotalInquireRequest_when_TotalInquireRoutine_then_DoesNotThrow() {
		//given
		User mockedUser = mock(User.class);

		//when
		when(routineRepository.findAllByUser(mockedUser)).thenReturn(mock(List.class));

		//then
		assertDoesNotThrow(() -> routineService.totalInquireRoutine(mockedUser));
	}

	@DisplayName("루틴 취소 테스트")
	@Test
	void given_RoutineCancelRequest_when_CancelRoutine_then_DoesNotThrow() {
		//given
		RoutineCancelRequest request = RequestFixture.getRoutineCancelRequestFixture();

		//when
		Routine mockRoutine = mock(Routine.class);
		when(routineRepository.findById(request.getRoutineId())).thenReturn(Optional.of(mockRoutine));
		doNothing().when(mockRoutine).cancel(request.getDay());

		//then
		assertDoesNotThrow(() -> routineService.cancelRoutine(request));
	}

	@DisplayName("루틴 취소 테스트 - 완료되지 않은 루틴이거나, 해당 날짜에 진행된 루틴이 없는 경우")
	@Test
	void given_RoutineCancelRequest_when_CancelNotCompletedRoutine_then_DoesNotThrow() {
		//given
		RoutineCancelRequest request = RequestFixture.getRoutineCancelRequestFixture();

		//when
		Routine mockRoutine = mock(Routine.class);
		when(routineRepository.findById(request.getRoutineId())).thenReturn(Optional.of(mockRoutine));
		doThrow(new IllegalStateException()).when(mockRoutine).cancel(request.getDay());

		//then
		assertThrows(IllegalStateException.class,
			() -> routineService.cancelRoutine(request));
	}

}
