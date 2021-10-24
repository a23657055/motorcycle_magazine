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
@Table(name = "form", uniqueConstraints = {@UniqueConstraint(columnNames = "Id")})
@Data
public class Form {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Id", nullable = false, insertable = false, updatable = false)
	private Long id;

	@Column(name = "SubsystemId", nullable = false)
	private @NotNull Long subsystemId;
	
	@Column(name = "Code", nullable = false, length = 32)
	private @NotNull String code;
	
	@Lob
	@Column(name = "Name", nullable = false, columnDefinition = "LONGTEXT")
	private @NotNull String name;

	@Column(name = "ControllerName", nullable = false, length = 64)
	private @NotNull String controllerName;
	
	@Column(name = "ActionName", nullable = false, length = 64)
	private @NotNull String actionName;
	
	@Column(name = "IsExternalLink", nullable = false)
	private @NotNull Boolean isExternalLink;
	
	@Column(name = "IsEnable", nullable = false)
	private @NotNull Boolean isEnable;
	
	@Column(name = "IsShow", nullable = false)
	private @NotNull Boolean isShow;
	
	@Column(name = "Sort", nullable = false)
	private @NotNull Long sort;
	
	@Column(name = "CreateId", nullable = false, updatable = false)
	private @NotNull Long createId;
	
	@Column(name = "CreateTime", nullable = false, updatable = false)
	private @NotNull Date createTime;
	
	@Column(name = "ModifyId", nullable = false)
	private @NotNull Long modifyId;
	
	@Column(name = "ModifyTime", nullable = false)
	private @NotNull Date modifyTime;
}
