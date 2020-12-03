package com.miraclesoft.datalake.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "Jobhistory")
public class Jobhistory {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;	
	
	@Column(name = "instanceid")
	private Long instanceId;
	
	@Column(name = "extractorId")
	private String extractorId;
	
	@Column(name = "jobName")
	private String jobName;
	
	@Column(name = "jobType")
	private String jobType;
	
	@Column(name = "jobId")
	private String jobId;
	
	@Column(name = "lastScheduledDate")
	private String lastScheduledDate;
	
	@Column(name = "executionTime")
	private String executionTime;
	
	@Column(name = "status")
	private String status;
	
	public Jobhistory() {
		
	}
		
	public Jobhistory(Long instanceid,String extractorId, String jobName,String jobType, String jobId, String lastScheduledDate,String executionTime, String status) {	
		this.instanceId = instanceid;
		this.extractorId = extractorId;
		this.jobName = jobName;
		this.jobType = jobType;
		this.jobId = jobId;
		this.lastScheduledDate = lastScheduledDate;
		this.executionTime = executionTime;
		this.status = status;
	}

	public String getExtractorId() {
		return extractorId;
	}


	public void setExtractorId(String extractorId) {
		this.extractorId = extractorId;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public Long getInstanceId() {
		return instanceId;
	}


	public void setInstanceId(Long instanceId) {
		this.instanceId = instanceId;
	}


	public String getJobType() {
		return jobType;
	}


	public void setJobType(String jobType) {
		this.jobType = jobType;
	}


	public String getJobId() {
		return jobId;
	}


	public void setJobId(String jobId) {
		this.jobId = jobId;
	}


	public String getLastScheduledDate() {
		return lastScheduledDate;
	}


	public void setLastScheduledDate(String lastScheduledDate) {
		this.lastScheduledDate = lastScheduledDate;
	}


	public String getExecutionTime() {
		return executionTime;
	}


	public void setExecutionTime(String executionTime) {
		this.executionTime = executionTime;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}	
}
