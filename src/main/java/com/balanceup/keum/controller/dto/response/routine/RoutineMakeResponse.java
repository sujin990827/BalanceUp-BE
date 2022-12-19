package com.balanceup.keum.controller.dto.response.routine;

import com.balanceup.keum.domain.Routine;
import com.balanceup.keum.domain.RoutineCategory;

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
	private RoutineCategory routineCategory;
	private boolean alarm;
	private String days;

	public static RoutineMakeResponse from(String username, Routine routine) {
		return RoutineMakeResponse.builder()
			.username(username)
			.routineTitle(routine.getRoutineTitle())
			.routineCategory(routine.getRoutineCategory())
			.days(routine.getDays())
			.alarm(routine.isAlarm())
			.build();
	}

}
