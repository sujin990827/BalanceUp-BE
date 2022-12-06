package com.balanceup.keum.domain;

import java.sql.Timestamp;
import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "routine")
@Entity
public class Routine {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "User_nickname")
	private User nickname;

	@Column(length = 100, nullable = false)
	private String content; //루틴명

	private boolean done;

	@Enumerated(EnumType.STRING)
	private Category category; // 운동,학습,일상,마음관리

	@Column
	private String days; // 루틴 지정 날짜

	@Column(name = "create_at")
	private Timestamp createAt;  // 루틴 등록 날짜

	@Column(name = "modified_at")
	private Timestamp modifiedAt;

	@Column(name = "deleted_at")
	private Timestamp deletedAt;

	public static Routine of(String content, boolean done, Category category){
		return new Routine(content, done, category);
	}

	public Routine(String content, boolean done, Category category) {
		this.content = content;
		this.done = done;
		this.category = category;
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
