package com.balanceup.keum.controller.dto.request.routine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RoutineMakeRequest {

	private Long userId;
	private String routineTitle;
	private String routineCategory;
	private String days;
	private String alarmTime;

}
