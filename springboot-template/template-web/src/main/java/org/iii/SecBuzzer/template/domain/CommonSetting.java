package org.iii.SecBuzzer.template.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Entity
@Table(name = "commonSetting", uniqueConstraints = {@UniqueConstraint(columnNames = "Id")})
@Data
public class CommonSetting {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Id", nullable = false, insertable = false, updatable = false)
	private Long id;
	
	@Column(name = "FunctionCode", nullable = false, length = 10)
	private @NotNull String functionCode;
	
	@Column(name = "FunctionDescription", nullable = false, length = 50)
	private @NotNull String functionDescription;
	
	@Column(name = "SetupCode", nullable = false, length = 10)
	private @NotNull String setupCode;
	
	@Column(name = "SetupDescription", nullable = false, length = 50)
	private @NotNull String setupDescription;
	
	@Column(name = "Remark", nullable = true, length = 50)
	private String remark;
	
	@Column(name = "UseMark", nullable = false, length = 1)
	private @NotNull String useMark;
	
	@Column(name = "CreateId", nullable = false, length = 20)
	private @NotNull String createId;
	
	@Column(name = "UpdateId", nullable = false, length = 20)
	private @NotNull String updateId;

	@Column(name = "CreateTime", nullable = false, length = 50)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createTime;

	@Column(name = "UpdateTime", nullable = false, length = 50)
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateTime;
}
