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
@Table(name = "v_member_sign_apply", uniqueConstraints = {@UniqueConstraint(columnNames = "Id")})
@Data
public class ViewMemberSignApply {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Id", nullable = false, insertable = false, updatable = false)
	private Long id;

	@Column(name = "OrgId")
	private Long orgId;

	@Column(name = "Account", nullable = false, length = 64)
	private @NotNull String account;

	@Column(name = "Name", nullable = false, length = 128)
	private @NotNull String name;

	@Column(name = "Email", nullable = false, length = 128)
	private @NotNull String email;

	@Column(name = "MobilePhone", nullable = false, length = 16)
	private @NotNull String mobilePhone;

	@Column(name = "OrgName", length = 50)
	private String orgName;

	@Column(name = "OrgType", length = 1)
	private String orgType;

	@Column(name = "CreateTime")
	private @NotNull Date createTime;
}
