package com.balanceup.keum.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.balanceup.keum.domain.Routine;

public interface RoutineRepository extends JpaRepository<Routine, Long> {
}
