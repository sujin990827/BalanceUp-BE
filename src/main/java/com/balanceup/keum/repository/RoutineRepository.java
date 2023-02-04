package com.balanceup.keum.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.balanceup.keum.domain.Routine;
import com.balanceup.keum.domain.User;

public interface RoutineRepository extends JpaRepository<Routine, Long> {
	List<Routine> findAllByUser(User user);

	void deleteAllByUser(User user);
}
