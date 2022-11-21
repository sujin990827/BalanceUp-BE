package com.balanceup.keum.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.balanceup.keum.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByUsername(String username);
	Optional<User> findByNickname(String nickname);

}
