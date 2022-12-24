package com.balanceup.keum.domain;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import com.balanceup.keum.controller.dto.request.routine.RoutineMakeRequest;
import com.balanceup.keum.controller.dto.request.routine.RoutineUpdateRequest;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "routine")
@Entity
public class Routine {

	public static final int ROUTINE_MAX_DAY = 14;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "routine_id")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@Column(length = 100, nullable = false, name = "routine_title")
	private String routineTitle;

	@JoinColumn(name = "routine_id")
	@OneToMany(fetch = FetchType.EAGER)
	List<RoutineDay> routineDays;

	private Boolean completed = false;

	@Column(name = "alarm_time")
	private String alarmTime;

	@Enumerated(EnumType.STRING)
	private RoutineCategory routineCategory;

	@Column
	private String days;

	@Column(name = "create_at")
	private Timestamp createAt;

	@Column(name = "modified_at")
	private Timestamp modifiedAt;

	@Builder
	private Routine(String routineTitle, RoutineCategory routineCategory, String days, String alarmTime,
		List<RoutineDay> routineDays, User user) {
		this.routineTitle = routineTitle;
		this.routineCategory = routineCategory;
		this.days = days;
		this.routineDays = routineDays;
		this.user = user;

		if (isValidTime(alarmTime)) {
			this.alarmTime = alarmTime;
		}
	}

	private boolean isValidTime(String alarmTime) {
		return alarmTime != null && alarmTime.length() >= 4;
	}

	public void update(RoutineUpdateRequest request) {
		this.routineTitle = request.getRoutineTitle();
		this.days = request.getDays();

		if (isValidTime(alarmTime)) {
			this.alarmTime = request.getAlarmTime();
			return;
		}
		this.alarmTime = null;
	}

	public static Routine of(String routineTitle, RoutineCategory routineCategory, String days, String alarmTime,
		List<RoutineDay> routineDays, User user) {
		return Routine.builder()
			.routineTitle(routineTitle)
			.routineCategory(routineCategory)
			.days(days)
			.alarmTime(alarmTime)
			.routineDays(routineDays)
			.user(user)
			.build();
	}

	public static Routine ofRoutineInfo(RoutineMakeRequest request, List<RoutineDay> routineDays, User user) {
		return of(
			request.getRoutineTitle(),
			request.getRoutineCategory(),
			request.getDays(),
			request.getAlarmTime(),
			routineDays,
			user);
	}

	public List<Day> getDayList() {
		return Day.of(this.days);
	}

	@PrePersist
	private void createdAt() {
		this.createAt = Timestamp.from(Instant.now());
	}

	@PreUpdate
	private void modifiedAt() {
		this.modifiedAt = Timestamp.from(Instant.now());
	}

}
