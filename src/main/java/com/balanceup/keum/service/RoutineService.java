package com.balanceup.keum.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.balanceup.keum.controller.dto.request.routine.RoutineAllDoneRequest;
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

	private final UserService userService;
	private final RoutineDayService routineDayService;
	private final RoutineRepository routineRepository;

	@Transactional
	public RoutineMakeResponse makeRoutine(RoutineMakeRequest request, String username) {
		User user = userService.findUserByUsername(username);

		isValidMakeRequest(request);
		isMaximumRoutine(user);

		Routine routine = routineRepository
			.save(Routine.ofRoutineInfo(request, routineDayService.makeRoutineDays(), user));

		return RoutineMakeResponse.from(user.getUsername(), routine);
	}

	@Transactional
	public RoutineResponse updateRoutine(RoutineUpdateRequest request, String username) {
		User user = userService.findUserByUsername(username);
		Routine routine = getRoutineByOptional(routineRepository.findById(request.getRoutineId()));

		isValidUpdateRequest(request);

		routine.update(request);

		return RoutineResponse.from(routine, user);
	}

	@Transactional(readOnly = true)
	public RoutineResponse inquireRoutine(Long routineId, String username) {
		User user = userService.findUserByUsername(username);
		Routine routine = getRoutineByOptional(routineRepository.findById(routineId));

		return RoutineResponse.from(routine, user);
	}

	@Transactional
	public void deleteRoutine(RoutineDeleteRequest request, String username) {
		userService.findUserByUsername(username);
		Routine routine = getRoutineByOptional(routineRepository.findById(request.getRoutineId()));

		routineRepository.delete(routine);
	}

	@Transactional
	public void progressRoutine(RoutineProgressRequest request, String username) {
		User user = userService.findUserByUsername(username);
		Routine routine = getRoutineByOptional(routineRepository.findById(request.getRoutineId()));

		routineDayService.progressDailyRoutine(routine);
		user.earnRp(1);
	}

	@Transactional
	public void allDoneRoutine(RoutineAllDoneRequest request, String username) {
		User user = userService.findUserByUsername(username);
		Routine routine = getRoutineByOptional(routineRepository.findById(request.getRoutineId()));

		routine.isAllDone();
		user.earnRp(20);
	}

	@Transactional(readOnly = true)
	public List<RoutineTotalResponse> totalInquireRoutine(String username) {
		User user = userService.findUserByUsername(username);
		List<Routine> routine = routineRepository.findAllByUser(user);

		return routine.stream()
			.map(RoutineTotalResponse::of)
			.collect(Collectors.toList());
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

}
