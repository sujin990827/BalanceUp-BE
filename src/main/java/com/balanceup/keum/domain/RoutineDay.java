package com.balanceup.keum.domain;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
@Entity
public class RoutineDay {

	private static final Long DAY_MILLISECOND = 24 * 60 * 60 * 1000L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;

	@Temporal(TemporalType.DATE)
	private Date day;

	private boolean completed;

	private RoutineDay(Timestamp day) {
		this.day = day;
		this.completed = false;
	}

	public boolean isToday(String today) {
		String routineDay = new SimpleDateFormat("yyyy-MM-dd").format(this.day);
		return routineDay.equals(today);
	}

	public static RoutineDay makeRoutineDay(Date today, int routineDayOrder) {
		return new RoutineDay(new Timestamp(today.getTime() + (DAY_MILLISECOND * routineDayOrder)));
	}

	public void progress() {
		this.completed = true;
	}

	public void cancel() {
		this.completed = false;
	}
}
