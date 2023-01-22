package com.balanceup.keum.service;

import java.text.SimpleDateFormat;
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
		String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

		for (RoutineDay routineDay : routineDays) {
			if (routineDay.isToday(today)) {
				if (routineDay.isCompleted()) {
					throw new IllegalStateException("이미 진행된 루틴입니다.");
				}

				routineDay.progress();
				break;
			}
		}
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
