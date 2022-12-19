package com.balanceup.keum.controller.dto.request.routine;

import com.balanceup.keum.domain.RoutineCategory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RoutineMakeRequest {

	private String username;
	private String routineTitle;
	private RoutineCategory routineCategory;
	private boolean alarm;
	private String days;
}
