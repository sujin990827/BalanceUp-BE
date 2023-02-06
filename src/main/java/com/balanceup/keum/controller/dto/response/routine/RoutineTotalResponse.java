package com.balanceup.keum.controller.dto.response.routine;

import java.util.List;
import java.util.stream.Collectors;

import com.balanceup.keum.domain.Routine;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RoutineTotalResponse {

	private Long routineId;
	private String routineTitle;
	private String routineCategory;
	private String days;
	private String alarmTime;
	private boolean completed;
	private List<RoutineDaysResponse> routineDays;

	public static RoutineTotalResponse of(Routine routine) {

		List<RoutineDaysResponse> routineDays = routine.getRoutineDays().stream()
			.map(RoutineDaysResponse::of)
			.collect(Collectors.toList());

		return RoutineTotalResponse.builder()
			.routineId(routine.getId())
			.routineTitle(routine.getRoutineTitle())
			.routineCategory(routine.getRoutineCategory().getValue())
			.days(routine.getDays())
			.alarmTime(routine.getAlarmTime())
			.completed(routine.getCompleted())
			.routineDays(routineDays)
			.build();
	}

}
