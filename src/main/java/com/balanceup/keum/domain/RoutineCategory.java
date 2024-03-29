package com.balanceup.keum.domain;

import lombok.Getter;

@Getter
public enum RoutineCategory {
	EXERCISE("운동"),
	LEARNING("학습"),
	DAILY("일상"),
	MIND_CARE("마음관리");

	private final String value;

	RoutineCategory(String value) {
		this.value = value;
	}

	public static RoutineCategory find(String value) {
		for (RoutineCategory routine : RoutineCategory.values()) {
			if (routine.value.equals(value)) {
				return routine;
			}
		}
		throw new IllegalArgumentException("정확한 루틴카테고리가 아닙니다.");
	}
}
