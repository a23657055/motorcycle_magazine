package org.iii.SecBuzzer.template.domain;

import lombok.Data;

//@Entity
//@Table(name="xp_member_role_name")
@Data
public class SpMemberRoleName {

//	@Column(name = "Id")	
	private Long id;

//	@Id
//	@Column(name="RoleId")
	private Long roleId;

//	@Column(name="Name")
	private String name;

//	@Column(name="Flag")
	private Long flag;
	
}
