package com.balanceup.keum.domain;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.persistence.CascadeType;
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

import org.hibernate.annotations.BatchSize;

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

	@BatchSize(size = 20)
	@JoinColumn(name = "routine_id")
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	List<RoutineDay> routineDays;

	private Boolean completed;

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
		this.completed = false;

		if (isValidTime(alarmTime)) {
			this.alarmTime = alarmTime;
		}
	}

	private boolean isValidTime(String alarmTime) {
		return alarmTime != null && alarmTime.length() >= 4;
	}

	public void update(RoutineUpdateRequest request) {
		this.routineTitle = request.getRoutineTitle();

		if (isValidTime(request.getAlarmTime())) {
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
			RoutineCategory.find(request.getRoutineCategory()),
			request.getDays(),
			request.getAlarmTime(),
			routineDays,
			user);
	}

	public List<Day> getDayList() {
		return Day.of(this.days);
	}

	public void isAllDone() {
		if (!completed) {
			throw new IllegalStateException("이미 진행된 루틴입니다.");
		}

		List<Day> daysList = Day.of(days);
		int count = getCompleteCount(daysList);

		if (count != daysList.size() * 2) {
			throw new IllegalStateException("루틴이 완료되지 않았습니다.");
		}

		this.completed = true;
	}

	private int getCompleteCount(List<Day> daysList) {
		int count = 0;

		for (RoutineDay routineDay : routineDays) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(routineDay.getDay());
			int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

			for (Day day : daysList) {
				if (dayOfWeek == day.getDayOfTheWeek() && routineDay.isCompleted()) {
					count++;
					break;
				}
			}

		}
		return count;
	}

	@PrePersist
	private void createdAt() {
		this.createAt = Timestamp.from(Instant.now());
	}

	@PreUpdate
	private void modifiedAt() {
		this.modifiedAt = Timestamp.from(Instant.now());
	}

	public List<RoutineDay> getRoutineDaysWithFiltering() {
		List<RoutineDay> copyRoutineDays = new CopyOnWriteArrayList<>(routineDays);
		List<RoutineDay> filteringRoutineDays = new ArrayList<>();

		for (RoutineDay routineDay : copyRoutineDays) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(routineDay.getDay());
			int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

			for (Day day : getDayList()) {
				if (day.getDayOfTheWeek() == dayOfWeek) {
					filteringRoutineDays.add(routineDay);
					break;
				}
			}
		}
		return filteringRoutineDays;
	}

	public void cancel(Date day) {
		String cancelDay = new SimpleDateFormat("yyyy-MM-dd").format(day);
		for (RoutineDay routineDay : routineDays) {
			if (routineDay.isToday(cancelDay)) {
				if (!routineDay.isCompleted()) {
					throw new IllegalStateException("선택한 날짜는 루틴이 완료되지 않았습니다.");
				}

				if (this.completed) {
					routineDay.cancel();
					this.completed = false;
					user.decreaseRp(21);
					return;
				}

				routineDay.cancel();
				user.decreaseRp(1);
				return;
			}
		}
		throw new IllegalStateException("선택한 날짜는 루틴 진행 날짜가 아닙니다.");
	}

	public void countCompletedDaysAndDecreaseRp() {
		int completedCount = 0;
		for (RoutineDay routineDay : routineDays) {
			if (routineDay.isCompleted()) {
				completedCount++;
			}
		}

		if (this.completed) {
			user.decreaseRp(20 + completedCount);
			return;
		}

		user.decreaseRp(completedCount);
	}
}
