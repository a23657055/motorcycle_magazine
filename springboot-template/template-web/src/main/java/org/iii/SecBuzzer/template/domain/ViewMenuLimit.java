package org.iii.SecBuzzer.template.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "v_menu", uniqueConstraints = {@UniqueConstraint(columnNames = "Id")})
@Data
public class ViewMenuLimit {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Id", nullable = false, insertable = false, updatable = false)
	private Double id;

	@Column(name = "SubsytemId")
	private Long subsytemId;

	@Column(name = "IconStyle", length = 32)
	private String iconStyle;

	@Lob
	@Column(name = "SubsystemName", columnDefinition = "LONGTEXT")
	private String subsystemName;

	@Column(name = "FormId")
	private Long formId;

	@Column(name = "FormCode")
	private String formCode;

	@Lob
	@Column(name = "FormName", columnDefinition = "LONGTEXT")
	private String formName;
	
	@Column(name = "ControllerName", length = 64)
	private String controllerName;

	@Column(name = "ActionName", length = 64)
	private String actionName;

	@Column(name = "IsExternalLink")
	private Boolean isExternalLink;
	
	@Column(name = "MemberId")
	private Long memberId;
	
	@Getter
	@Setter
	@Column(name = "ActionCreate")
	private Boolean actionCreate;

	@Column(name = "ActionUpdate")
	private Boolean actionUpdate;

	@Column(name = "ActionDelete")
	private Boolean actionDelete;

	@Column(name = "ActionRead")
	private Boolean actionRead;

	@Column(name = "ActionSign")
	private Boolean actionSign;

	@Column(name = "SubsystemSort")
	private Long subsystemSort;
	
	@Column(name = "FormSort")
	private Long formSort;

	@Column(name = "SubsystemCode", length = 32)
	private String subsystemCode;

	@Column(name = "SubsystemIsShow")
	private Boolean subsystemIsShow;

	@Column(name = "FormIsShow")
	private Boolean formIsShow;

}
