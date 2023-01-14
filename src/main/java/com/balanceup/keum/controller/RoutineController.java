package com.balanceup.keum.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.balanceup.keum.controller.dto.request.routine.RoutineMakeRequest;
import com.balanceup.keum.controller.dto.request.routine.RoutineUpdateRequest;
import com.balanceup.keum.controller.dto.response.Response;
import com.balanceup.keum.service.RoutineService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class RoutineController {

	private final RoutineService routineService;

	@PostMapping("/routine")
	public ResponseEntity<?> makeRoutine(@RequestBody RoutineMakeRequest request) {
		return new ResponseEntity<>(
			Response.of("success",
				"루틴 생성이 완료되었습니다.",
				routineService.makeRoutine(request)
			), HttpStatus.CREATED);
	}

	@PutMapping("/routine")
	public ResponseEntity<?> updateRoutine(@RequestBody RoutineUpdateRequest request) {
		return new ResponseEntity<>(
			Response.of("success",
				"루틴 수정이 완료되었습니다.",
				routineService.updateRoutine(request)
			), HttpStatus.OK);
	}

}
