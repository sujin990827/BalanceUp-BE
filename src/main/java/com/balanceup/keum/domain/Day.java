package com.balanceup.keum.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Day {
	SUNDAY("일"),
	MONDAY("월"),
	TUESDAY("화"),
	WEDNESDAY("수"),
	THURSDAY("목"),
	FRIDAY("금"),
	SATURDAY("토");

	private final String value;

	public static List<Day> of(String days) {
		return getDayList(days);
	}

	private static List<Day> getDayList(String days) {
		List<Day> dayList = new ArrayList<>();

		for (int i = 0; i < days.length(); i++) {
			dayList.add(getDay(String.valueOf(days.charAt(i))));
		}

		return dayList;
	}

	private static Day getDay(String daysCharAt) {
		for (Day day : Day.values()) {
			if (day.value.equals(daysCharAt)) {
				return day;
			}
		}
		throw new IllegalArgumentException("잘못된 요일입니다.");
	}

	private static String from(List<Day> days) {
		StringBuilder sb = new StringBuilder();
		for (Day day : days) {
			sb.append(day.value);
		}
		return sb.toString();
	}

}
