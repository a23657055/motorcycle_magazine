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
@Table(name = "system_counter", uniqueConstraints = {@UniqueConstraint(columnNames = "Id")})
@Data
public class SystemCounter {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Id", nullable = false, insertable = false, updatable = false)
	private Long id;

	@Column(name = "Ip", nullable = false, updatable = false, length = 1024)
	private @NotNull String ip;

	@Column(name = "CreateTime", nullable = false, updatable = false)
	private @NotNull Date createTime;
}
