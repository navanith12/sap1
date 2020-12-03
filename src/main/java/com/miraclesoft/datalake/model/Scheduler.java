package com.miraclesoft.datalake.model;


import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "scheduler")
public class Scheduler {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	// jobname
	@Column(name = "jobname", unique = true)
	private String jobName;

	// jobtype
	@Column(name = "jobtype")
	private String jobtype;

	// job id of selected job
	@Column(name = "jobid", unique = true)
	private String jobId;
	
	//date of schedule
	@Column(name = "scheduleddate")
	private String scheduledDate;
	
	//number of times
	@Column(name = "frequency")
	private String frequency;
	
	//cronexpression
	@Column(name = "cronexp")
	private String cronDate;
	
	//scehdule status
	@Column(name = "status")
	private String status;

	@Column(name = "createdby")
	private String createdBy;
	
	@Column(name = "createdat")
	private Date createdAt = new Date(System.currentTimeMillis());


	public Scheduler() {
	}
	public Scheduler(String jobName, String jobtype, String jobId, String scheduledDate, String frequency,
			String cronDate, String status, String createdBy, Date createdAt) {
		this.jobName = jobName;
		this.jobtype = jobtype;
		this.jobId = jobId;
		this.scheduledDate = scheduledDate;
		this.frequency = frequency;
		this.cronDate = cronDate;
		this.status = status;
		this.createdBy = createdBy;
		this.createdAt = createdAt;
	}





	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getJobName() {
		return jobName;
	}


	public void setJobName(String jobName) {
		this.jobName = jobName;
	}


	public String getJobtype() {
		return jobtype;
	}

	public void setJobtype(String jobtype) {
		this.jobtype = jobtype;
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}


	public String getScheduledDate() {
		return scheduledDate;
	}

	public void setScheduledDate(String scheduledDate) {
		this.scheduledDate = scheduledDate;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public String getCronDate() {
		return cronDate;
	}

	public void setCronDate(String cronDate) {
		this.cronDate = cronDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	
	
	
}
