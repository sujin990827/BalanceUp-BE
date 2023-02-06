package com.balanceup.keum.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.balanceup.keum.controller.dto.request.routine.RoutineAllDoneRequest;
import com.balanceup.keum.controller.dto.request.routine.RoutineCancelRequest;
import com.balanceup.keum.controller.dto.request.routine.RoutineDeleteRequest;
import com.balanceup.keum.controller.dto.request.routine.RoutineMakeRequest;
import com.balanceup.keum.controller.dto.request.routine.RoutineProgressRequest;
import com.balanceup.keum.controller.dto.request.routine.RoutineUpdateRequest;
import com.balanceup.keum.controller.dto.response.routine.RoutineMakeResponse;
import com.balanceup.keum.controller.dto.response.routine.RoutineResponse;
import com.balanceup.keum.controller.dto.response.routine.RoutineTotalResponse;
import com.balanceup.keum.domain.Routine;
import com.balanceup.keum.domain.User;
import com.balanceup.keum.repository.RoutineRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RoutineService {

	public static final String INVALID_ROUTINE_ID_MESSAGE = "이미 삭제된 루틴 id 이거나, 잘못된 id 입니다.";
	private final int ROUTINE_MAXIMUM = 4;

	private final RoutineDayService routineDayService;
	private final RoutineRepository routineRepository;

	@Transactional
	public RoutineMakeResponse makeRoutine(RoutineMakeRequest request, User user) {
		isValidMakeRequest(request);
		isMaximumRoutine(user);

		Routine routine = routineRepository
			.save(Routine.ofRoutineInfo(request, routineDayService.makeRoutineDays(request.getDays()), user));

		return RoutineMakeResponse.from(user.getUsername(), routine);
	}

	@Transactional
	public RoutineResponse updateRoutine(RoutineUpdateRequest request, User user) {
		Routine routine = getRoutineByOptional(routineRepository.findById(request.getRoutineId()));

		isValidUpdateRequest(request);

		routine.update(request);

		return RoutineResponse.from(routine, user);
	}

	@Transactional(readOnly = true)
	public RoutineResponse inquireRoutine(Long routineId, User user) {
		Routine routine = getRoutineByOptional(routineRepository.findById(routineId));

		return RoutineResponse.from(routine, user);
	}

	@Transactional
	public void deleteRoutine(RoutineDeleteRequest request) {
		Routine routine = getRoutineByOptional(routineRepository.findById(request.getRoutineId()));
		routine.countCompletedDaysAndDecreaseRp();
		routineRepository.delete(routine);
	}

	@Transactional
	public void progressRoutine(RoutineProgressRequest request) {
		Routine routine = getRoutineByOptional(routineRepository.findById(request.getRoutineId()));
		routineDayService.progressDailyRoutine(routine);
	}

	@Transactional
	public Routine allDoneRoutine(RoutineAllDoneRequest request) {
		Routine routine = getRoutineByOptional(routineRepository.findById(request.getRoutineId()));
		routine.isAllDone();

		return routine;
	}

	@Transactional(readOnly = true)
	public List<RoutineTotalResponse> totalInquireRoutine(User user) {
		List<Routine> routine = routineRepository.findAllByUser(user);

		return routine.stream()
			.map(RoutineTotalResponse::of)
			.collect(Collectors.toList());
	}

	@Transactional
	public void cancelRoutine(RoutineCancelRequest request) {
		Routine routine = getRoutineByOptional(routineRepository.findById(request.getRoutineId()));
		routine.cancel(request.getDay());
	}

	private static void isValidUpdateRequest(RoutineUpdateRequest request) {
		if (isNull(request.getRoutineTitle())) {
			throw new IllegalArgumentException("루틴명이 입력되지 않았습니다.");
		}
	}

	private void isValidMakeRequest(RoutineMakeRequest request) {
		if (isNull(request.getRoutineTitle())) {
			throw new IllegalArgumentException("루틴명이 입력되지 않았습니다.");
		}

		if (request.getRoutineCategory() == null) {
			throw new IllegalArgumentException("카테고리가 입력되지 않았습니다.");
		}

		if (isNull(request.getDays())) {
			throw new IllegalArgumentException("진행 요일이 입력되지 않았습니다.");
		}
	}

	private static boolean isNull(String s) {
		return s == null || s.length() == 0;
	}

	private static Routine getRoutineByOptional(Optional<Routine> routineOptional) {
		if (routineOptional.isEmpty()) {
			throw new IllegalArgumentException(INVALID_ROUTINE_ID_MESSAGE);
		}
		return routineOptional.get();
	}

	private void isMaximumRoutine(User user) {
		List<Routine> routineList = routineRepository.findAllByUser(user);
		if (routineList.size() >= ROUTINE_MAXIMUM) {
			throw new IllegalStateException("루틴 갯수는 " + ROUTINE_MAXIMUM + "개를 초과할 수 없습니다.");
		}
	}

	@Transactional
	public void deleteRoutineByUser(User user) {
		routineRepository.deleteAllByUser(user);
	}

	@Transactional
	public int deleteExpiryRoutine(User user) {
		List<Routine> routines = routineRepository.findAllByUser(user);
		int deleteCount = 0;
		for (Routine routine : routines) {
			if (routine.isExpiry()) {
				deleteCount++;
				routineRepository.delete(routine);
			}
		}

		return deleteCount;
	}
}
