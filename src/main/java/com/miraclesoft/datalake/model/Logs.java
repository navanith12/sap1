package com.miraclesoft.datalake.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "LOGS")
public class Logs implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name= "event_id")
    private BigInteger event_id;

    @Column(name="INSTANCE_ID")
    private String instance_id;

    @Column(name="MESSAGE")
    private String Message;

    public String getInstance_id() {
        return instance_id;
    }

    public void setInstance_id(String instance_id) {
        this.instance_id = instance_id;
    }

    @Column(name="JOB_NAME")
    private String Job_Name;

    @Column(name="JOB_ID")
    private String Job_Id;

    public void setEvent_id(BigInteger event_id) {
        this.event_id = event_id;
    }

    public String getJob_Id() {
        return Job_Id;
    }

    public void setJob_Id(String job_Id) {
        Job_Id = job_Id;
    }

    @Column(name="DATED")
    @JsonSerialize(using= CustomDateSerializer.class)
    private java.util.Date Date;



    public java.util.Date getDate() {
        return Date;
    }

    public void setDate(java.util.Date date) {
        Date = date;
    }

    public BigInteger getEvent_id() {
        return event_id;
    }


    public String getMessage() {
        return Message;
    }

    public String getJob_Name() {
        return Job_Name;
    }


    public void setMessage(String message) {
        Message = message;
    }

    public void setJob_Name(String job_Name) {
        Job_Name = job_Name;
    }

}
