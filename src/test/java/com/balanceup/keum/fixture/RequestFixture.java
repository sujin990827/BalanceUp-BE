package com.balanceup.keum.fixture;

import com.balanceup.keum.controller.dto.request.routine.RoutineAllDoneRequest;
import com.balanceup.keum.controller.dto.request.routine.RoutineDeleteRequest;
import com.balanceup.keum.controller.dto.request.routine.RoutineInquireRequest;
import com.balanceup.keum.controller.dto.request.routine.RoutineMakeRequest;
import com.balanceup.keum.controller.dto.request.routine.RoutineProgressRequest;
import com.balanceup.keum.controller.dto.request.routine.RoutineUpdateRequest;
import com.balanceup.keum.controller.dto.request.user.ReIssueRequest;
import com.balanceup.keum.domain.RoutineCategory;

public class RequestFixture {

	public static RoutineUpdateRequest getRoutineUpdateRequestFixture() {
		RoutineUpdateRequest request = new RoutineUpdateRequest();
		request.setUserId(1L);
		request.setRoutineId(1L);
		request.setRoutineTitle("title");
		request.setDays("월화수");
		request.setAlarmTime("09:00");
		return request;
	}

	public static RoutineMakeRequest getRoutineMakeRequestFixture() {
		RoutineMakeRequest request = new RoutineMakeRequest();
		request.setUserId(1L);
		request.setRoutineTitle("title");
		request.setDays("월화수");
		request.setAlarmTime("09:00");
		request.setRoutineCategory(RoutineCategory.EXERCISE);
		return request;
	}

	public static RoutineInquireRequest getRoutineInquireRequestFixture() {
		RoutineInquireRequest request = new RoutineInquireRequest();
		request.setUserId(1L);
		request.setRoutineId(1L);
		return request;
	}

	public static RoutineDeleteRequest getRoutineDeleteRequestFixture() {
		RoutineDeleteRequest request = new RoutineDeleteRequest();
		request.setUserId(1L);
		request.setRoutineId(1L);
		return request;
	}

	public static RoutineProgressRequest getRoutineProgressRequestFixture() {
		RoutineProgressRequest request = new RoutineProgressRequest();
		request.setUserId(1L);
		request.setRoutineId(1L);
		return request;
	}

	public static RoutineAllDoneRequest getRoutineAllDoneRequestFixture() {
		RoutineAllDoneRequest request = new RoutineAllDoneRequest();
		request.setUserId(1L);
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
}
