package com.miraclesoft.datalake.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table
@Entity(name = "monitoring_instance")
public class MonitoringInstance {
	
	@Id
	@Column(name = "instanceid")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long instanceid;
	
	@Column(name = "jobname")
	private String jobName;

	public MonitoringInstance(String jobName) {
		this.jobName = jobName;
	}

	public Long getInstanceid() {
		return instanceid;
	}

	public void setInstanceid(Long instanceid) {
		this.instanceid = instanceid;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
}
