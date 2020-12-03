package com.miraclesoft.datalake.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.mongodb.core.mapping.Document;

@Entity
@Table(name = "DELTA_TOKENS")
public class Delta {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Column(name = "extractor", unique = true)
	private String extractor;
	
	@Column(name = "currenttoken")
	private String current_token;
	
	@Column(name = "previoustoken")
	private String previous_token;
	
	@Column(name = "createdby")
	private String createdBy;
	
	@UpdateTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;

	public Delta() {
	}

	public Delta(String extractor, String current_token, String previous_token, Date createdAt) {
		this.extractor = extractor;
		this.current_token = current_token;
		this.previous_token = previous_token;
		this.createdAt = createdAt;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getExtractor() {
		return extractor;
	}

	public void setExtractor(String extractor) {
		this.extractor = extractor;
	}

	public String getCurrent_token() {
		return current_token;
	}

	public void setCurrent_token(String current_token) {
		this.current_token = current_token;
	}

	public String getPrevious_token() {
		return previous_token;
	}

	public void setPrevious_token(String previous_token) {
		this.previous_token = previous_token;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

}
