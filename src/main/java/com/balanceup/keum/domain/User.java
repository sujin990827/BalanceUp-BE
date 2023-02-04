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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "\"user\"")
@Entity
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;

	@Column(length = 50, nullable = false, unique = true)
	private String username;

	@Column(length = 100, nullable = false)
	private String password;

	@Column(length = 20, unique = true)
	private String nickname;

	private String provider;

	private Integer rp;

	@Column(name = "create_at")
	private Timestamp createAt;

	@Column(name = "modified_at")
	private Timestamp modifiedAt;

	public User updateUserNickname(String nickname) {
		this.nickname = nickname;
		return this;
	}

	public static User of(String username, String password, String nickname, String provider) {
		return new User(username, password, nickname, provider);
	}

	private User(String username, String password, String nickname, String provider) {
		this.username = username;
		this.password = password;
		this.nickname = nickname;
		this.provider = provider;
		this.rp = 0;
	}

	public void earnRp(int rp) {
		this.rp += rp;
	}

	@PrePersist
	private void createdAt() {
		this.createAt = Timestamp.from(Instant.now());
	}

	@PreUpdate
	private void modifiedAt() {
		this.modifiedAt = Timestamp.from(Instant.now());
	}

	public void decreaseRp(int rp) {
		if (rp > this.rp) {
			throw new IllegalStateException("rp가 음수가 됩니다. 잘못된 요청입니다.");
		}
		this.rp -= rp;
	}
}
