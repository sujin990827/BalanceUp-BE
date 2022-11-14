package com.balanceup.keum.repository;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import com.balanceup.keum.domain.User;

@SpringBootTest
public class UserRepositoryTest {

	@Autowired
	private UserRepository userRepository;

	@BeforeEach
	public void setUser() {
		userRepository.save(User.of("username", "password", "nickname"));
	}

	@DisplayName("Save 테스트")
	@Sql("classpath:db/tableInit.sql")
	@Test
	void givenUserInfo_whenSaveUser_thenReturnPersistentUser() {
		//given
		User user = User.of("username1", "password", "nickname1");

		//when
		User savedUser = userRepository.save(user);

		//then
		Assertions.assertThat(savedUser)
			.isEqualTo(user);
	}

	@DisplayName("findAll 테스트")
	@Sql("classpath:db/tableInit.sql")
	@Test
	void givenNone_whenFindAllUser_thenPersistentUserList() {
		//given

		//when
		List<User> userList = userRepository.findAll();

		//then
		Assertions.assertThat(userList.size())
			.isEqualTo(1);
		Assertions.assertThat(userList.get(0).getUsername())
			.isEqualTo("username");
	}

	@DisplayName("Delete 테스트")
	@Sql("classpath:db/tableInit.sql")
	@Test
	void givenUserInfo_whenDelete_thenNotThrows() {
		//given
		User user = userRepository.findById(1L).get();

		//when

		//then
		org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> userRepository.deleteById(user.getId()));

	}

	@DisplayName("Update 테스트")
	@Sql("classpath:db/tableInit.sql")
	@Test
	void givenUserUpdateInfo_whenUserUpdate_thenReturnUpdatedUser() {
		//given

		//when

		//then

	}

}
