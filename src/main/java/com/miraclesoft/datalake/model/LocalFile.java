package com.miraclesoft.datalake.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Document("LOCAL")
@JsonIgnoreProperties(value = { "createdAt" }, allowGetters = true)
public class LocalFile {

	@Id
	private String id;
	private String description;
	private String applicationServer;
	private Date createdAt = new Date();

	public LocalFile() {
	}

	public LocalFile(String description, String applicationServer, Date createdAt) {
		this.description = description;
		this.applicationServer = applicationServer;
		this.createdAt = createdAt;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getApplicationServer() {
		return applicationServer;
	}

	public void setApplicationServer(String applicationServer) {
		this.applicationServer = applicationServer;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

}
