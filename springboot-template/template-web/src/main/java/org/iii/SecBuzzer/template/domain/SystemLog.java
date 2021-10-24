package org.iii.SecBuzzer.template.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Entity
@Table(name = "system_log", uniqueConstraints = {@UniqueConstraint(columnNames = "Id")})
@Data
public class SystemLog {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Id", nullable = false, insertable = false, updatable = false)
	private Long id;

	@Column(name = "AppName", nullable = false, updatable = false, length = 64)
	private @NotNull String appName;

	@Column(name = "FuncName", nullable = false, updatable = false, length = 64)
	private @NotNull String funcName;

	@Lob
	@Column(name = "InputValue", nullable = false, updatable = false, columnDefinition = "LONGTEXT")
	private @NotNull String inputValue;

	@Column(name = "ActionName", nullable = false, updatable = false, length = 16)
	private @NotNull String actionName;

	@Column(name = "Status", nullable = false, updatable = false, length = 16)
	private @NotNull String status;

	@Column(name = "Ip", nullable = false, updatable = false, length = 1024)
	private @NotNull String ip;

	@Column(name = "HashCode", nullable = false, updatable = false, length = 128)
	private @NotNull String hashCode;

	@Column(name = "CreateAccount", nullable = false, updatable = false, length = 64)
	private @NotNull String createAccount;

	@Column(name = "CreateTime", nullable = false, updatable = false)
	private @NotNull Date createTime;
}
