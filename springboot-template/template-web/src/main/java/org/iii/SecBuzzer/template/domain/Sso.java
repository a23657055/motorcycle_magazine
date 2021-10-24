package org.iii.SecBuzzer.template.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Entity
@Table(name = "sso", uniqueConstraints = {@UniqueConstraint(columnNames = "Code")})
@Data
public class Sso {
	@Id
	@Column(name = "Code", nullable = false, length = 128)
	private @NotNull String code;

	@Column(name = "MemberId", nullable = false)
	private @NotNull Long memberId;
	
	@Column(name = "ExpireTime", nullable = false)
	private @NotNull Date expireTime;
}
