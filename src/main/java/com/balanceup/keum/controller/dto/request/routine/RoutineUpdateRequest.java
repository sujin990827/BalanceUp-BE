package com.balanceup.keum.controller.dto.request.routine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RoutineUpdateRequest {

	private String username;
	private Long routineId;
	private String routineTitle;
	private String days;
	private String alarmTime;

}
