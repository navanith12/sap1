package com.miraclesoft.datalake.mongo.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;


@Entity
@Table(name = "MetadataStructure")
public class MetadataStructure implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1969146232515988097L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Column(name = "extractorname")
	private String extractorName;
	
	@Column(name = "metadata", columnDefinition = "LONGBLOB")
	private Metadata[] metadata;
	
	@Column(name = "sql_query", columnDefinition = "MEDIUMTEXT")
	private String sql;
	
	@Column(name = "sourceid")
	private String sourceid;
	
	@Column(name = "createdat")
	private Date createdAt = new Date();
	
	public MetadataStructure() {
		
	}
	
	public MetadataStructure(String extractorName, Metadata[] metadata,String sql, String sourceid, Date createdAt) {
		this.extractorName = extractorName;
		this.metadata = metadata;
		this.sql =sql;
		this.sourceid = sourceid;
		this.createdAt = createdAt;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getExtractorName() {
		return extractorName;
	}

	public void setExtractorName(String extractorName) {
		this.extractorName = extractorName;
	}

	public Metadata[] getMetadata() {
		return metadata;
	}

	public void setMetadata(Metadata[] metadata) {
		this.metadata = metadata;
	}

	public String getSourceid() {
		return sourceid;
	}

	public void setSourceid(String sourceid) {
		this.sourceid = sourceid;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	
	public String getSql() {
		return this.sql;
	}

	public void setString(String sql) {
		this.sql = sql;
	}
}
