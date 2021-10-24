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
@Table(name = "member", uniqueConstraints = {@UniqueConstraint(columnNames = "Id")})
@Data
public class Member {
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

	@Column(name = "SpareEmail", length = 128)
	private String spareEmail;

	@Column(name = "MobilePhone", nullable = false, length = 16)
	private @NotNull String mobilePhone;

	@Column(name = "CityPhone", length = 16)
	private String cityPhone;

	@Column(name = "FaxPhone", length = 16)
	private String faxPhone;

	@Column(name = "Address", length = 256)
	private String address;

	@Column(name = "Department", length = 128)
	private String department;

	@Column(name = "Title", length = 128)
	private String title;

	@Column(name = "IsEnable", nullable = false)
	private Boolean isEnable;

	@Column(name = "EnableTime", nullable = false)
	private @NotNull Date enableTime;

	@Column(name = "CreateId", nullable = false, updatable = false)
	private Long createId;

	@Column(name = "CreateTime", nullable = false, updatable = false)
	private @NotNull Date createTime;

	@Column(name = "ModifyId", nullable = false)
	private Long modifyId;

	@Column(name = "ModifyTime", nullable = false)
	private @NotNull Date modifyTime;

}
