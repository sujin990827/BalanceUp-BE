package com.balanceup.keum.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.balanceup.keum.controller.dto.request.routine.RoutineDeleteRequest;
import com.balanceup.keum.controller.dto.request.routine.RoutineInquireRequest;
import com.balanceup.keum.controller.dto.request.routine.RoutineMakeRequest;
import com.balanceup.keum.controller.dto.request.routine.RoutineUpdateRequest;
import com.balanceup.keum.controller.dto.response.routine.RoutineMakeResponse;
import com.balanceup.keum.controller.dto.response.routine.RoutineResponse;
import com.balanceup.keum.domain.Routine;
import com.balanceup.keum.domain.User;
import com.balanceup.keum.repository.RoutineRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RoutineService {

	private final UserService userService;
	private final RoutineDayService routineDayService;
	private final RoutineRepository routineRepository;

	@Transactional
	public RoutineMakeResponse makeRoutine(RoutineMakeRequest request) {
		User user = userService.findUserByUsername(request.getUsername());

		isValidMakeRequest(request);

		Routine routine = routineRepository
			.save(Routine.ofRoutineInfo(request, routineDayService.makeRoutineDays(), user));

		return RoutineMakeResponse.from(user.getUsername(), routine);
	}

	@Transactional
	public RoutineResponse updateRoutine(RoutineUpdateRequest request) {
		User user = userService.findUserByUsername(request.getUsername());
		Routine routine = routineRepository.findById(request.getRoutineId())
			.orElseThrow(() -> new IllegalArgumentException("이미 삭제된 루틴 id 이거나, 잘못된 id 입니다."));

		isValidUpdateRequest(request);

		routine.update(request);

		return RoutineResponse.from(routine, user);
	}

	@Transactional(readOnly = true)
	public RoutineResponse inquireRoutine(RoutineInquireRequest request) {
		User user = userService.findUserByUsername(request.getUsername());
		Routine routine = routineRepository.findById(request.getRoutineId())
			.orElseThrow(() -> new IllegalArgumentException("이미 삭제된 루틴 id 이거나, 잘못된 id 입니다."));

		return RoutineResponse.from(routine, user);
	}

	@Transactional
	public void deleteRoutine(RoutineDeleteRequest request) {
		userService.findUserByUsername(request.getUsername());
		Routine routine = routineRepository.findById(request.getRoutineId())
			.orElseThrow(() -> new IllegalArgumentException("이미 삭제된 루틴 id 이거나, 잘못된 id 입니다."));

		routineRepository.delete(routine);
	}

	private static void isValidUpdateRequest(RoutineUpdateRequest request) {
		if (isNull(request.getRoutineTitle())) {
			throw new IllegalArgumentException("루틴명이 입력되지 않았습니다.");
		}

		if (isNull(request.getDays())) {
			throw new IllegalArgumentException("진행 요일이 입력되지 않았습니다.");
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

}
