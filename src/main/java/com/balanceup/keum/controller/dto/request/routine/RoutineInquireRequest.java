package com.balanceup.keum.controller.dto.request.routine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RoutineInquireRequest {

	private Long userId;
	private Long routineId;
}
