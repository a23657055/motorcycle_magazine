package org.iii.SecBuzzer.template.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import lombok.Data;

@Entity
@Table(name = "resource_message", uniqueConstraints = {@UniqueConstraint(columnNames = "Id")})
@Data
public class ResourceMessage {
	private static final long serialVersionUID = 2759134468603014727L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Id", nullable = false, insertable = false, updatable = false)
	private Long id;
		
	@Column(name = "language", nullable = false, length = 16)
	private String language;
	
	@Column(name = "MessageKey", nullable = false, length = 128)
	private String messageKey;
	
	@Lob
	@Column(name = "MessageValue", nullable = false, columnDefinition = "LONGTEXT")
	private String messageValue;

	@Column(name = "CreateId", nullable = false, updatable = false)
	private Long createId;
	
	@Column(name = "CreateTime", nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createTime;

	@Column(name = "ModifyId", nullable = false)
	private Long modifyId;

	@Column(name = "ModifyTime", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date modifyTime;
}
