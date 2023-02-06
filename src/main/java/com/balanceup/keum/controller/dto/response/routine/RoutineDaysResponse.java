package com.balanceup.keum.controller.dto.response.routine;

import java.util.Date;

import com.balanceup.keum.domain.RoutineDay;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RoutineDaysResponse {

	private Date day;
	private Boolean completed;

	public static RoutineDaysResponse of(RoutineDay routineDay) {
		return new RoutineDaysResponse(routineDay.getDay(), routineDay.isCompleted());
	}
}
