package com.balanceup.keum.domain;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Table(name = "'user'")
@SQLDelete(sql="update 'user' set deleted_at = now() where id=?")
@Where(clause = "deleted_at is NULL")
@Entity
public class User {

	@Id
	@GeneratedValue
	private Long id;

	@Column(length = 50, nullable = false, unique = true)
	private String username;

	@Column(length = 50, nullable = false)
	private String password;

	@Column(length = 20, nullable = false, unique = true)
	private String nickname;

	@Column(name = "create_at")
	private Timestamp createAt;

	@Column(name = "modified_at")
	private Timestamp modifiedAt;

	@Column(name = "deleted_at")
	private Timestamp deletedAt;

	@PrePersist
	private void createdAt(){
		this.createAt = Timestamp.from(Instant.now());
	}

	@PreUpdate
	private void modifiedAt(){
		this.modifiedAt = Timestamp.from(Instant.now());
	}


}
