package com.miraclesoft.datalake.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

@Entity
@Table(name = "batchjob")
public class BatchJob {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name= "Id")
    private long Id;

    @Column(name="name")
    private String name;

    @Column(name="joblists")
    private String[] jobLists;
    
    @Column(name = "createdby")
    private String createdby;
    
    @Column(name = "createdat")
    private Date createdAt = new Date();
   
    public BatchJob() {
    	
    }
    
	public BatchJob(String name, String[] jobLists, String createdby, Date createdAt) {
		super();
		this.name = name;
		this.jobLists = jobLists;
		this.createdby = createdby;
		this.createdAt = createdAt;
	}

	public long getId() {
		return Id;
	}

	public void setId(long id) {
		Id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String[] getJobLists() {
		return jobLists;
	}

	public void setJobLists(String[] jobLists) {
		this.jobLists = jobLists;
	}

	public String getCreatedby() {
		return createdby;
	}

	public void setCreatedby(String createdby) {
		this.createdby = createdby;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
    
}
