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
@Table(name="role",uniqueConstraints= {@UniqueConstraint(columnNames="Id")})
@Data
public class Role {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Id", nullable = false, insertable = false, updatable = false)
	private Long id;
	
	@Lob
	@Column(name="Name", nullable = false, columnDefinition = "LONGTEXT")
	private @NotNull String name;

	@Column(name="IsEnable", nullable = false)
	private @NotNull Boolean isEnable;

	@Column(name="Sort", nullable = false)
	private @NotNull Long sort;

	@Column(name="CreateId", nullable = false)
	private @NotNull Long createId;

	@Column(name="CreateTime", nullable = false)
	private @NotNull Date createTime;

	@Column(name="ModifyId", nullable = false)
	private @NotNull Long modifyId;

	@Column(name="ModifyTime", nullable = false)
	private @NotNull Date modifyTime;

}
