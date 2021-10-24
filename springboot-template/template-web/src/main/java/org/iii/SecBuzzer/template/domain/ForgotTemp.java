package org.iii.SecBuzzer.template.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Data;

@Entity
@Table(name = "forgot_temp", uniqueConstraints = {@UniqueConstraint(columnNames = "Code")})
@Data
public class ForgotTemp {
	@Id
	@Column(name = "Code", nullable = false)
	private String code;

	@Column(name = "MemberId", nullable = false)
	private Long memberId;
	
	@Column(name = "ExpireTime", nullable = false)
	private Date expireTime;
}
