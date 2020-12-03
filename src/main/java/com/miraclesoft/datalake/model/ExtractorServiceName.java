package com.miraclesoft.datalake.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "servicename")
public class ExtractorServiceName {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(name = "extractorname", unique = true)
	private String extractorName;

	@Column(name = "servicename")
	private String serviceName;
	
	@Column(name="sourcesystem")
	private String sourceSystem;
	
	@Column(name = "sourcetype")
	private String sourcetype;
	
	@Column(name = "sourcesubtype")
	private String sourceSubType;

	@Column(name = "createdby")
	private String createdBy;

	@Column(name = "createdat")
	private Date createdAt = new Date();

	public ExtractorServiceName() {

	}

	public ExtractorServiceName(String extractorName, String serviceName, String sourceSystem, String sourcetype,
			String sourceSubType, String createdBy, Date createdAt) {
		super();
		this.extractorName = extractorName;
		this.serviceName = serviceName;
		this.sourceSystem = sourceSystem;
		this.sourcetype = sourcetype;
		this.sourceSubType = sourceSubType;
		this.createdBy = createdBy;
		this.createdAt = createdAt;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getExtractorName() {
		return extractorName;
	}

	public void setExtractorName(String extractorName) {
		this.extractorName = extractorName;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public String getSourcetype() {
		return sourcetype;
	}

	public void setSourcetype(String sourcetype) {
		this.sourcetype = sourcetype;
	}

	public String getSourceSubType() {
		return sourceSubType;
	}

	public void setSourceSubType(String sourceSubType) {
		this.sourceSubType = sourceSubType;
	}

	public String getSourceSystem() {
		return sourceSystem;
	}

	public void setSourceSystem(String sourceSystem) {
		this.sourceSystem = sourceSystem;
	}
	
	
}
