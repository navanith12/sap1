package com.miraclesoft.datalake.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.miraclesoft.datalake.mongo.model.StringPrefixedSequenceIdGenerator;

@Entity
@javax.persistence.Table(name = "AZUREDB")
public class FtpServer {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ftp_seq")
    @GenericGenerator(
        name = "ftp_seq", 
        strategy = "com.miraclesoft.datalake.mongo.model.StringPrefixedSequenceIdGenerator", 
        parameters = {
            @Parameter(name = StringPrefixedSequenceIdGenerator.INCREMENT_PARAM, value = "10"),
            @Parameter(name = StringPrefixedSequenceIdGenerator.VALUE_PREFIX_PARAMETER, value = "FTP_"),
            @Parameter(name = StringPrefixedSequenceIdGenerator.NUMBER_FORMAT_PARAMETER, value = "%05d") })
	private String id;
	
	@Column(name = "storagename")
	private String storageName;
	
	@Column(name  = "containername")
	private String containerName;
	
	@Column(name = "accesskey")
	private String accessKey;
	
	@Column(name= "createdAt")
	private Date createdAt = new Date();
	
	public FtpServer() {
		
	}

	public FtpServer(String storageName, String containerName, String accessKey, Date createdAt) {
		super();
		this.storageName = storageName;
		this.containerName = containerName;
		this.accessKey = accessKey;
		this.createdAt = createdAt;
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStorageName() {
		return storageName;
	}

	public void setStorageName(String storageName) {
		this.storageName = storageName;
	}

	public String getContainerName() {
		return containerName;
	}

	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
}
