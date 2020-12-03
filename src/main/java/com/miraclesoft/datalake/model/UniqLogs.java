package com.miraclesoft.datalake.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Time;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "LOGS")
public class UniqLogs {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name= "event_id")
    private BigInteger event_id;

    @Column(name="INSTANCE_ID")
    private String instance_id;

    @Column(name="JOB_NAME")
    private String Job_Name;

    @Column(name="JOB_ID")
    private String Job_Id;

    @Column(name="DATED")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "America/New_York")
     private Date dated;

    @Column(name="STATUS")
    private String status;

    @Column(name="STARTDATE")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "America/New_York")
    private Date startDate;

    @Column(name="STARTTIME")
    @JsonFormat(pattern = "HH:mm:ss", timezone = "America/New_York")
    private Date startTime;
    
    @Column(name="ENDDATE")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "America/New_York")
    private Date enddate;

    @Column(name="Duration")
    private Time duration;
    
    @Column(name = "TIMETAKEN", length = 8)
    private String timetaken;
    
    @Column(name = "CREATED_BY")
    private String createdby;

    public Time getDuration() {
        return duration;
    }

    public void setDuration(Time duration) {
        this.duration = duration;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigInteger getEvent_id() {	
        return event_id;
    }

    public void setEvent_id(BigInteger event_id) {
        this.event_id = event_id;
    }

    public String getInstance_id() {
        return instance_id;
    }

    public void setInstance_id(String instance_id) {
        this.instance_id = instance_id;
    }

    public String getJob_Name() {
        return Job_Name;
    }

    public void setJob_Name(String job_Name) {
        Job_Name = job_Name;
    }

    public String getJob_Id() {
        return Job_Id;
    }

    public void setJob_Id(String job_Id) {
        Job_Id = job_Id;
    }

    public Date getDated() {
        return dated;
    }

    public void setDated(Date dated) {
        this.dated = dated;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
    
    public String getTimetaken() {
		return timetaken;
	}

	public void setTimetaken(String timetaken) {
		this.timetaken = timetaken;
	}

	public String getCreatedby() {
		return createdby;
	}

	public void setCreatedby(String createdby) {
		this.createdby = createdby;
	}

	public Date getEnddate() {
		return enddate;
	}

	public void setEnddate(Date enddate) {
		this.enddate = enddate;
	}
	
	
	
	
}
