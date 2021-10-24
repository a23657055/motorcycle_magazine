package org.iii.SecBuzzer.template.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Data;

@Entity
@Table(name="org",uniqueConstraints= {@UniqueConstraint(columnNames="Id")})
@Data
public class Org {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="Id", nullable=false, updatable=false)	
	private Long id;
	
	@Column(name="Name", nullable = false, length = 50)
	private String name;

	@Column(name="Code", nullable = false, length = 16)
	private String code;

	@Column(name="OrgType", nullable = false, length = 1)
	private String orgType;

	@Column(name="AuthType", nullable = false, length = 1)
	private String authType;

	@Column(name="Tel", length = 15)
	private String tel;

	@Column(name="Fax", length = 15)
	private String fax;

	@Column(name="City", length = 10)
	private String city;

	@Column(name="Town", length = 10)
	private String town;

	@Column(name="Address", length = 256)
	private String address;

	@Column(name="IsEnable", nullable = false)
	private Boolean isEnable;

	@Column(name="CreateId", nullable = false, updatable = false)
	private Long createId;

	@Column(name="CreateTime", nullable = false, updatable = false)
	private Date createTime;
	
	@Column(name="ModifyId", nullable = false)
	private Long modifyId;

	@Column(name="ModifyTime", nullable = false)
	private Date modifyTime;
}
