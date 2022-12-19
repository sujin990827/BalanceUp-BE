package com.balanceup.keum.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.balanceup.keum.controller.dto.request.routine.RoutineMakeRequest;
import com.balanceup.keum.controller.dto.response.routine.RoutineMakeResponse;
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
		Routine routine = routineRepository.save(Routine.from(request, routineDayService.makeRoutineDays(), user));
		return RoutineMakeResponse.from(user.getUsername(), routine);
	}
}
