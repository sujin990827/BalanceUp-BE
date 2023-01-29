package com.balanceup.keum.fixture;

import java.util.Date;

import com.balanceup.keum.controller.dto.request.routine.RoutineAllDoneRequest;
import com.balanceup.keum.controller.dto.request.routine.RoutineCancelRequest;
import com.balanceup.keum.controller.dto.request.routine.RoutineDeleteRequest;
import com.balanceup.keum.controller.dto.request.routine.RoutineMakeRequest;
import com.balanceup.keum.controller.dto.request.routine.RoutineProgressRequest;
import com.balanceup.keum.controller.dto.request.routine.RoutineUpdateRequest;
import com.balanceup.keum.controller.dto.request.user.ReIssueRequest;

public class RequestFixture {

	public static RoutineUpdateRequest getRoutineUpdateRequestFixture() {
		RoutineUpdateRequest request = new RoutineUpdateRequest();
		request.setRoutineId(1L);
		request.setRoutineTitle("title");
		request.setAlarmTime("09:00");
		return request;
	}

	public static RoutineMakeRequest getRoutineMakeRequestFixture() {
		RoutineMakeRequest request = new RoutineMakeRequest();
		request.setRoutineTitle("title");
		request.setDays("월화수");
		request.setAlarmTime("09:00");
		request.setRoutineCategory("운동");
		return request;
	}

	public static RoutineDeleteRequest getRoutineDeleteRequestFixture() {
		RoutineDeleteRequest request = new RoutineDeleteRequest();
		request.setRoutineId(1L);
		return request;
	}

	public static RoutineProgressRequest getRoutineProgressRequestFixture() {
		RoutineProgressRequest request = new RoutineProgressRequest();
		request.setRoutineId(1L);
		return request;
	}

	public static RoutineAllDoneRequest getRoutineAllDoneRequestFixture() {
		RoutineAllDoneRequest request = new RoutineAllDoneRequest();
		request.setRoutineId(1L);
		return request;
	}

	public static ReIssueRequest getReIssueRequestFixture() {
		ReIssueRequest request = new ReIssueRequest();
		request.setUsername("username");
		request.setToken("accessToken");
		request.setRefreshToken("refreshToken");
		return request;
	}

	public static RoutineCancelRequest getRoutineCancelRequestFixture() {
		RoutineCancelRequest request = new RoutineCancelRequest();
		request.setRoutineId(1L);
		request.setDay(new Date());
		return request;
	}
}
