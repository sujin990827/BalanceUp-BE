package com.balanceup.keum.controller.dto.response.routine;

import com.balanceup.keum.domain.Routine;
import com.balanceup.keum.domain.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RoutineResponse {

	private String username;
	private Long routineId;
	private String routineTitle;
	private String days;
	private String alarmTime;

	public static RoutineResponse from(Routine routine, User user) {
		return RoutineResponse.builder()
			.username(user.getUsername())
			.routineId(routine.getId())
			.routineTitle(routine.getRoutineTitle())
			.days(routine.getDays())
			.alarmTime(routine.getAlarmTime())
			.build();
	}
}
