package com.balanceup.keum.controller.dto.response.routine;

import com.balanceup.keum.domain.Routine;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RoutineMakeResponse {

	private String username;
	private String routineTitle;
	private String routineCategory;
	private String alarmTime;
	private String days;

	public static RoutineMakeResponse from(String username, Routine routine) {
		return RoutineMakeResponse.builder()
			.username(username)
			.routineTitle(routine.getRoutineTitle())
			.routineCategory(routine.getRoutineCategory().getValue())
			.days(routine.getDays())
			.alarmTime(routine.getAlarmTime())
			.build();
	}

}
