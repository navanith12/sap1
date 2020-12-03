package com.miraclesoft.datalake.model;

import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

public class Monitoringmodel {
	
	private List<String> status;
	private String role;
	private String createdby;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date startdate;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date enddate;
	
	public Monitoringmodel(List<String> status, String role, String username, String createdby, Date startdate,
			Date enddate) {
		this.status = status;
		this.role = role;
		this.createdby = createdby;
		this.startdate = startdate;
		this.enddate = enddate;
	}

	public List<String> getStatus() {
		return status;
	}

	public void setStatus(List<String> status) {
		this.status = status;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getCreatedby() {
		return createdby;
	}

	public void setCreatedby(String createdby) {
		this.createdby = createdby;
	}

	public Date getStartdate() {
		return startdate;
	}

	public void setStartdate(Date startdate) {
		this.startdate = startdate;
	}

	public Date getEnddate() {
		return enddate;
	}

	public void setEnddate(Date enddate) {
		this.enddate = enddate;
	}

}
