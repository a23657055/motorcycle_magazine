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

@Entity
@Table(name = "v_form_subsystem", uniqueConstraints = {@UniqueConstraint(columnNames = "FormId")})
@Data
public class ViewFormSubsystem {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "FormId")
	private Long formId;

	@Lob
	@Column(name = "FormName", columnDefinition = "LONGTEXT")
	private String formName;

	@Column(name = "SubsystemId")
	private Long subsystemId;

	@Lob
	@Column(name = "SubsystemName", columnDefinition = "LONGTEXT")
	private String subsystemName;
}
