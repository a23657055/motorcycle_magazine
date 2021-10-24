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
@Table(name="member_role",uniqueConstraints= {@UniqueConstraint(columnNames="Id")})
@Data
public class MemberRole {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="Id", nullable=false, updatable=false)
	private Long id;

	@Column(name="MemberId")
	private Long memberId;
	
	@Column(name="RoleId")
	private Long roleId;
	
	@Column(name = "IsEnable", nullable = false)
	private Boolean isEnable;

	@Column(name="CreateId")
	private Long createId;

	@Column(name="CreateTime")
	private @NotNull Date createTime;

	@Column(name="ModifyId")
	private Long modifyId;

	@Column(name="ModifyTime")
	private @NotNull Date modifyTime;
}
