package com.miraclesoft.datalake.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.miraclesoft.datalake.mongo.model.StringPrefixedSequenceIdGenerator;

@Entity
@Table(name = "sourcedatabase")
public class SourceDatabase {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sourcedb_seq")
    @GenericGenerator(
        name = "dbinsert_seq", 
        strategy = "com.miraclesoft.datalake.mongo.model.StringPrefixedSequenceIdGenerator", 
        parameters = {
            @Parameter(name = StringPrefixedSequenceIdGenerator.INCREMENT_PARAM, value = "10"),
            @Parameter(name = StringPrefixedSequenceIdGenerator.VALUE_PREFIX_PARAMETER, value = "sdb"),
            @Parameter(name = StringPrefixedSequenceIdGenerator.NUMBER_FORMAT_PARAMETER, value = "%05d") })
	private String id;

	@Column(name = "connectionName")
	private String connectionName;

	@Column(name = "dbtype")
	private String dbtype;
	
	@Column(name = "hostadd")
	private String hostAddress;

	@Column(name = "portnumber")
	private String portnumber;

	@Column(name = "dbname")
	private String dbname;

	@Column(name = "username")
	private String username;

	@Column(name = "password")
	private String password;

	@Column(name = "createdAt")
	private Date createdAt = new Date();

	public SourceDatabase() {
	}

	public SourceDatabase(String connectionName, String dbtype, String hostAddress, String portnumber, String dbname,
			String username, String password, Date createdAt) {
		super();
		this.connectionName = connectionName;
		this.dbtype = dbtype;
		this.hostAddress = hostAddress;
		this.portnumber = portnumber;
		this.dbname = dbname;
		this.username = username;
		this.password = password;
		this.createdAt = createdAt;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getConnectionName() {
		return connectionName;
	}

	public void setConnectionName(String connectionName) {
		this.connectionName = connectionName;
	}

	public String getHostAddress() {
		return hostAddress;
	}

	public void setHostAddress(String hostAddress) {
		this.hostAddress = hostAddress;
	}

	public String getPortnumber() {
		return portnumber;
	}

	public void setPortnumber(String portnumber) {
		this.portnumber = portnumber;
	}

	public String getDbname() {
		return dbname;
	}

	public void setDbname(String dbname) {
		this.dbname = dbname;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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

	public String getDbtype() {
		return dbtype;
	}

	public void setDbtype(String dbtype) {
		this.dbtype = dbtype;
	}

	@Override
	public String toString() {
		return "DbInsert [id=" + id + ", connectionName=" + connectionName + ", hostAddress=" + hostAddress
				+ ", portnumber=" + portnumber + ", dbname=" + dbname + ", username=" + username + ", password="
				+ password + ", createdAt=" + createdAt + "]";
	}

}
