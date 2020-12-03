package com.miraclesoft.datalake.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "recordscount")
public class FetchedRecordsCount {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Column(name = "jobname")
	private String jobName;
	
	@Column(name = "instanceid")
	private long instanceid;
	
	@Column(name = "recordsfromurl")
	private int recordsFromURL;
	
	@Column(name = "recordsfromdb")
	private long recordsFromDb;
	
	public FetchedRecordsCount() {
	}

	public FetchedRecordsCount(String jobName, long instanceid, int recordsFromURL,	long recordsFromDb) {
		this.jobName = jobName;
		this.instanceid = instanceid;
		this.recordsFromURL = recordsFromURL;
		this.recordsFromDb = recordsFromDb;
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

	public long getInstanceid() {
		return instanceid;
	}

	public void setInstanceid(long instanceid) {
		this.instanceid = instanceid;
	}

	public int getRecordsFromURL() {
		return recordsFromURL;
	}

	public void setRecordsFromURL(int recordsFromURL) {
		this.recordsFromURL = recordsFromURL;
	}

	public long getRecordsFromDb() {
		return recordsFromDb;
	}

	public void setRecordsFromDb(long recordsFromDb) {
		this.recordsFromDb = recordsFromDb;
	}
	
}
