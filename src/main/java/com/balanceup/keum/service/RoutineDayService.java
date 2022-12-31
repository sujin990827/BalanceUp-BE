package com.balanceup.keum.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.balanceup.keum.domain.Routine;
import com.balanceup.keum.domain.RoutineDay;
import com.balanceup.keum.repository.RoutineDayRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RoutineDayService {

	private final RoutineDayRepository routineDayRepository;

	@Transactional
	public List<RoutineDay> makeRoutineDays() {
		List<RoutineDay> routineDays = new ArrayList<>();
		saveRoutineDay(routineDays);
		return routineDays;
	}

	public void progressDailyRoutine(Routine routine) {
		List<RoutineDay> routineDays = routine.getRoutineDays();
		Date today = new Date();

		for (RoutineDay routineDay : routineDays) {
			if (routineDay.isToday(today)) {
				routineDay.progress();
				break;
			}
		}
	}

	public boolean isComplete(Routine routine) {
		List<RoutineDay> routineDays = routine.getRoutineDays();
		int completeCount = 0;
		int routineLength = routine.getDays().length();

		for (int i = 0; i < 14; i++) {
			RoutineDay routineDay = routineDays.get(i);
			if (routineDay.isCompleted()) {
				completeCount++;
			}
		}

		if (completeCount == routineLength * 2) {
			return true;
		}
		return false;
	}

	private void saveRoutineDay(List<RoutineDay> routineDays) {
		Date today = new Date();

		for (int day = 0; day < 14; day++) {
			RoutineDay routineDay = RoutineDay.makeRoutineDay(today, day);
			routineDayRepository.save(routineDay);
			routineDays.add(routineDay);
		}

	}

}
