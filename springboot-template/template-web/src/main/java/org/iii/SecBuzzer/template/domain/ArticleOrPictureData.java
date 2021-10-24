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
import javax.validation.constraints.NotNull;

import lombok.Data;

@Entity
@Table(name = "articleOrPictureData", uniqueConstraints = {@UniqueConstraint(columnNames = "Id")})
@Data
public class ArticleOrPictureData {
	@Id
	@Column(name = "Id", nullable = false, insertable = false, updatable = false, length = 13)
	private String id;
	
	@Column(name = "LocationCode", nullable = false, length = 10)
	private @NotNull String locationCode;
	
	@Column(name = "DataType", nullable = false, length = 1)
	private @NotNull String dataType;
	
	@Column(name = "Enable", nullable = false, length = 1)
	private @NotNull String enable;
	
	@Column(name = "Title", nullable = false, length = 20)
	private @NotNull String title;
	
	@Lob
	@Column(name = "Content", nullable = false, columnDefinition = "LONGTEXT")
	private String content;

	@Column(name = "ReleaseTime", nullable = false, updatable = false)
	@Temporal(TemporalType.DATE)
	private Date releaseTime;
	
	@Column(name = "CreateTime", nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createTime;
	
	@Column(name = "UpdateTime", nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateTime;
	
	@Column(name = "CreateId", nullable = false, length = 20)
	private @NotNull String createId;
	
	@Column(name = "UpdateId", nullable = false, length = 20)
	private @NotNull String updateId;
}
