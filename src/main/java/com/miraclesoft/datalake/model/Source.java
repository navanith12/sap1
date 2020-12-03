package com.miraclesoft.datalake.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;

import javax.persistence.Id;

@Entity
@Table(name = "SOURCE")
public class Source {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(name = "description")
	private String description;

	@Column(name = "instances")
	private int instances;

	@Column(name = "systemid")
	private String systemId;

	@Column(name = "appserver", unique = true)
	private String applicationServer;

	@Column(name = "port", unique = true)
	private String port;

	@Column(name = "client")
	private int client;

	@Column(name = "login")
	private String login;

	@Column(name = "password")
	private String password;

	@Column(name = "sourcetype")
	private String sourcetype;

	@Column(name = "createdAt")
	private Date createdAt = new Date();

	public Source() {
	}

	public Source(String description, int instances, String systemId, String applicationServer, String port, int client,
			String login, String password, String sourcetype, Date createdAt) {
		super();
		this.description = description;
		this.instances = instances;
		this.systemId = systemId;
		this.applicationServer = applicationServer;
		this.port = port;
		this.client = client;
		this.login = login;
		this.password = password;
		this.sourcetype = sourcetype;
		this.createdAt = createdAt;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getInstances() {
		return instances;
	}

	public void setInstances(int instances) {
		this.instances = instances;
	}

	public String getSystemId() {
		return systemId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	public String getApplicationServer() {
		return applicationServer;
	}

	public void setApplicationServer(String applicationServer) {
		this.applicationServer = applicationServer;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public int getClient() {
		return client;
	}

	public void setClient(int client) {
		this.client = client;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public String getSourcetype() {
		return sourcetype;
	}

	public void setSourcetype(String sourcetype) {
		this.sourcetype = sourcetype;
	}

}