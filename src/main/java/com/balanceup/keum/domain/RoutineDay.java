package com.balanceup.keum.domain;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class RoutineDay {

	private static final Long DAY_MILLISECOND = 24 * 60 * 60 * 1000L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;

	private Date day;

	private boolean completed = false;

	private RoutineDay(Timestamp day) {
		this.day = day;
	}

	public boolean isToday(String today) {
		String routineDay = new SimpleDateFormat("dd-MM-yyyy").format(this.day);
		return routineDay.equals(today);
	}

	public static RoutineDay makeRoutineDay(Date today, int routineDayOrder) {
		return new RoutineDay(new Timestamp(today.getTime() + (DAY_MILLISECOND * routineDayOrder)));
	}

	public void progress() {
		this.completed = true;
	}

}
