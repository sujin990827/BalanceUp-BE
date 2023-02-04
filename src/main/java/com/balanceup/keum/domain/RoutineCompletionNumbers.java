package com.balanceup.keum.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Getter;

@Getter
@Embeddable
public class RoutineCompletionNumbers {

	private Integer exercise;
	private Integer learning;
	private Integer daily;
	@Column(name = "mind_care")
	private Integer mindCare;

	public RoutineCompletionNumbers() {
		this.exercise = 0;
		this.learning = 0;
		this.daily = 0;
		this.mindCare = 0;
	}

	public void increaseRoutine(RoutineCategory routineCategory) {
		if (routineCategory.equals(RoutineCategory.EXERCISE)) {
			exercise += 1;
			return;
		}
		if (routineCategory.equals(RoutineCategory.LEARNING)) {
			learning += 1;
			return;
		}
		if (routineCategory.equals(RoutineCategory.DAILY)) {
			daily += 1;
			return;
		}
		if (routineCategory.equals(RoutineCategory.MIND_CARE)) {
			mindCare += 1;
		}
	}
}
