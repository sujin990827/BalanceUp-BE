package com.balanceup.keum.domain;

import java.sql.Timestamp;
import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "\"user\"")
@SQLDelete(sql = "UPDATE \"user\" SET deleted_at = NOW() where id =?")
@Where(clause = "deleted_at is NULL")
@Entity
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;

	@Column(length = 50, nullable = false, unique = true)
	private String username;

	@Column(length = 50, nullable = false)
	private String password;

	@Column(length = 20, unique = true)
	private String nickname;

	private String provider;

	@Column(name = "create_at")
	private Timestamp createAt;

	@Column(name = "modified_at")
	private Timestamp modifiedAt;

	@Column(name = "deleted_at")
	private Timestamp deletedAt;

	public User updateUserNickname(String nickname) {
		this.nickname = nickname;
		return this;
	}

	public static User of(String username, String password, String provider) {
		return new User(username, password, provider);
	}

	private User(String username, String password, String provider) {
		this.username = username;
		this.password = password;
		this.provider = provider;
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
