package com.balanceup.keum.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.balanceup.keum.config.util.JwtTokenUtil;
import com.balanceup.keum.controller.dto.request.routine.RoutineAllDoneRequest;
import com.balanceup.keum.controller.dto.request.routine.RoutineCancelRequest;
import com.balanceup.keum.controller.dto.request.routine.RoutineDeleteRequest;
import com.balanceup.keum.controller.dto.request.routine.RoutineMakeRequest;
import com.balanceup.keum.controller.dto.request.routine.RoutineProgressRequest;
import com.balanceup.keum.controller.dto.request.routine.RoutineUpdateRequest;
import com.balanceup.keum.controller.dto.response.Response;
import com.balanceup.keum.service.RoutineService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class RoutineController {

	private final RoutineService routineService;
	private final JwtTokenUtil jwtTokenUtil;

	@PostMapping("/routine")
	public ResponseEntity<?> makeRoutine(
		@RequestBody RoutineMakeRequest request,
		@RequestHeader(name = HttpHeaders.AUTHORIZATION) String jwtToken) {

		String username = jwtTokenUtil.getUserNameByToken(jwtToken);

		return new ResponseEntity<>(
			Response.of("success",
				"루틴 생성이 완료되었습니다.",
				routineService.makeRoutine(request, username)
			), HttpStatus.CREATED);
	}

	@PutMapping("/routine")
	public ResponseEntity<?> updateRoutine(
		@RequestBody RoutineUpdateRequest request,
		@RequestHeader(name = HttpHeaders.AUTHORIZATION) String jwtToken) {

		String username = jwtTokenUtil.getUserNameByToken(jwtToken);

		return new ResponseEntity<>(
			Response.of("success",
				"루틴 수정이 완료되었습니다.",
				routineService.updateRoutine(request, username)
			), HttpStatus.OK);
	}

	@PutMapping("/progress/routine")
	public ResponseEntity<?> progressRoutine(
		@RequestBody RoutineProgressRequest request,
		@RequestHeader(name = HttpHeaders.AUTHORIZATION) String jwtToken) {

		String username = jwtTokenUtil.getUserNameByToken(jwtToken);

		routineService.progressRoutine(request, username);
		return new ResponseEntity<>(
			Response.of("success",
				"루틴 진행이 완료되었습니다. 1rp 상승",
				null
			), HttpStatus.OK);
	}

	@PutMapping("/progress/routines")
	public ResponseEntity<?> allDoneRoutine(
		@RequestBody RoutineAllDoneRequest request,
		@RequestHeader(name = HttpHeaders.AUTHORIZATION) String jwtToken) {

		String username = jwtTokenUtil.getUserNameByToken(jwtToken);

		routineService.allDoneRoutine(request, username);
		return new ResponseEntity<>(
			Response.of("success",
				"루틴 전체 진행이 완료되었습니다. 20rp 상승",
				null
			), HttpStatus.OK);
	}

	@DeleteMapping("routine")
	public ResponseEntity<?> deleteRoutine(
		@RequestBody RoutineDeleteRequest request,
		@RequestHeader(name = HttpHeaders.AUTHORIZATION) String jwtToken) {

		String username = jwtTokenUtil.getUserNameByToken(jwtToken);

		routineService.deleteRoutine(request, username);
		return new ResponseEntity<>(
			Response.of("success",
				"루틴 삭제가 완료되었습니다.",
				null
			), HttpStatus.OK);
	}

	@GetMapping("/routine")
	public ResponseEntity<?> inquireRoutine(
		@RequestParam(name = "routineId") Long routineId,
		@RequestHeader(name = HttpHeaders.AUTHORIZATION) String jwtToken) {

		String username = jwtTokenUtil.getUserNameByToken(jwtToken);

		return new ResponseEntity<>(
			Response.of("success",
				"루틴 조회가 완료되었습니다.",
				routineService.inquireRoutine(routineId, username)
			), HttpStatus.OK);
	}

	@GetMapping("/routines")
	public ResponseEntity<?> totalInquireRoutine(
		@RequestHeader(name = HttpHeaders.AUTHORIZATION) String jwtToken) {
		String username = jwtTokenUtil.getUserNameByToken(jwtToken);

		return new ResponseEntity<>(
			Response.of("success",
				"루틴 전체조회가 완료되었습니다.",
				routineService.totalInquireRoutine(username)
			), HttpStatus.OK);
	}

	@PutMapping("/cancel")
	public ResponseEntity<?> cancelRoutine(
		@RequestBody RoutineCancelRequest request,
		@RequestHeader(name = HttpHeaders.AUTHORIZATION) String jwtToken) {

		String username = jwtTokenUtil.getUserNameByToken(jwtToken);
		routineService.cancelRoutine(request, username);

		return new ResponseEntity<>(
			Response.of("success",
				"루틴 취소가 완료되었습니다.",
				null
			), HttpStatus.OK);
	}

}
