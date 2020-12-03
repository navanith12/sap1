package com.miraclesoft.datalake.mongo.model;

import java.util.List;

public class SocketResponse {
	
	private String jobName;
	private List<String> status;
	private String jobStatus;
	
	public SocketResponse() {
	}

	public SocketResponse(String jobName, List<String> status) {
		super();
		this.jobName = jobName;
		this.status = status;
	}

	public SocketResponse(String jobName, List<String> status, String jobStatus) {
		super();
		this.jobName = jobName;
		this.status = status;
		this.jobStatus = jobStatus;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public List<String> getStatus() {
		return status;
	}

	public void setStatus(List<String> status) {
		this.status = status;
	}

	public String getJobStatus() {
		return jobStatus;
	}

	public void setJobStatus(String jobStatus) {
		this.jobStatus = jobStatus;
	}
}
