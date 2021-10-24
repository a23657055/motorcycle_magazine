package org.iii.SecBuzzer.template.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Entity
@Table(name = "member_history", uniqueConstraints = {@UniqueConstraint(columnNames = "Id")})
@Data
public class MemberHistory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Id", nullable = false, insertable = false, updatable = false)
	private Long id;

	@Column(name = "MemberId", nullable = false)
	private Long memberId;

	@Column(name = "Password", nullable = false, length = 128)
	private @NotNull String password;

	@Column(name = "Salt", nullable = false, updatable = false, length = 32)
	private @NotNull String salt;

	@Column(name = "ErrorCount", nullable = false)
	private @NotNull Short errorCount;
	
	@Column(name = "CreateId", nullable = false, updatable = false)
	private Long createId;

	@Column(name = "CreateTime", nullable = false, updatable = false)
	private @NotNull Date createTime;

	@Column(name = "ModifyId", nullable = false)
	private Long modifyId;
	
	@Column(name = "ModifyTime", nullable = false)
	private @NotNull Date modifyTime;
	
}
