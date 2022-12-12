package com.balanceup.keum.domain;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
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

import lombok.AccessLevel;
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

	private boolean completed = false;

	private boolean alarm;

	@Enumerated(EnumType.STRING)
	private RoutineCategory routineCategory;

	@Column
	private String days;

	@Column(name = "create_at")
	private Timestamp createAt;

	@Column(name = "modified_at")
	private Timestamp modifiedAt;

	public static Routine of(String content, RoutineCategory routineCategory, boolean alarm) {
		return new Routine(content, routineCategory, alarm);
	}

	public Routine(String routineTitle, RoutineCategory routineCategory, boolean alarm) {
		this.routineTitle = routineTitle;
		this.routineCategory = routineCategory;
		this.alarm = alarm;
		this.routineDays = new ArrayList<>();
		for (int day = 0; day < ROUTINE_MAX_DAY; day++) {
			this.routineDays.add(RoutineDay.makeRoutineDay(new Date(), day));
		}
	}

	public List<Day> getDayList() {
		return Day.getDayListByDaysColumn(this.days);
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
