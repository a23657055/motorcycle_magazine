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
@Table(name="role_form",uniqueConstraints= {@UniqueConstraint(columnNames="Id")})
@Data
public class RoleForm {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "Id", nullable=false, updatable=false)
	private Long id;
	
	@Column(name = "RoleId", nullable = false, updatable=false)
	private @NotNull Long roleId;
	
	@Column(name = "FormId", nullable = false, updatable=false)
	private @NotNull Long formId;
	
	@Column(name = "ActionCreate", nullable = false)
	private @NotNull Boolean actionCreate;
	
	@Column(name = "ActionUpdate", nullable = false)
	private @NotNull Boolean actionUpdate;
	
	@Column(name = "ActionDelete", nullable = false)
	private @NotNull Boolean actionDelete;
	
	@Column(name = "ActionRead", nullable = false)
	private @NotNull Boolean actionRead;

	@Column(name = "ActionSign", nullable = false)
	private @NotNull Boolean actionSign;
	
	@Column(name = "CreateId", nullable = false)
	private @NotNull Long createId;
	private @NotNull Date createTime;
	private @NotNull Long modifyId;
	private @NotNull Date modifyTime;

	public Long getCreateId() {
		return createId;
	}
	public void setCreateId(Long createId) {
		this.createId = createId;
	}

	@Column(name = "CreateTime", nullable = false)
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	@Column(name = "ModifyId", nullable = false)
	public Long getModifyId() {
		return modifyId;
	}
	public void setModifyId(Long modifyId) {
		this.modifyId = modifyId;
	}

	@Column(name = "ModifyTime", nullable = false)
	public Date getModifyTime() {
		return modifyTime;
	}
	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}
	
}
