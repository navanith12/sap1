package com.miraclesoft.datalake.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.TypeDef;
import org.springframework.core.annotation.Order;

import com.miraclesoft.datalake.mongo.model.StringPrefixedSequenceIdGenerator;

@Entity
@Table(name = "BAPI")
public class Bapi {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bapi_seq")
	@GenericGenerator(
	        name = "bapi_seq", 
	        strategy = "com.miraclesoft.datalake.mongo.model.StringPrefixedSequenceIdGenerator", 
	        parameters = {
	            @Parameter(name = StringPrefixedSequenceIdGenerator.INCREMENT_PARAM, value = "10"),
	            @Parameter(name = StringPrefixedSequenceIdGenerator.VALUE_PREFIX_PARAMETER, value = "B_"),
	            @Parameter(name = StringPrefixedSequenceIdGenerator.NUMBER_FORMAT_PARAMETER, value = "%05d") })
	private String id;
	
	@Column(name = "name", unique = true)
	private String name;
	
	@Column(name = "funct")
	private String function;
	
	@Column(name = "sourceid")
	private long sourceId;
	
	@Column(name = "createdby")
	private String createdBy;

	@Column(name = "description")
	private String description;
	
	@Column(name = "createdat")
	private Date createdAt = new Date();
	
	@Column(name = "updateddate")
	private Date updatedDate = new Date();

	@Column(name = "INSTANCEID")
	private long instanceid;
	
	public Bapi() {
	}

	public Bapi(String name, String function, long sourceId, String createdBy, String description, Date createdAt,
			Date updatedDate, long instanceid) {
		super();
		this.name = name;
		this.function = function;
		this.sourceId = sourceId;
		this.createdBy = createdBy;
		this.description = description;
		this.createdAt = createdAt;
		this.updatedDate = updatedDate;
		this.instanceid = instanceid;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public long getSourceId() {
		return sourceId;
	}

	public void setSourceId(long sourceId) {
		this.sourceId = sourceId;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public long getInstanceid() {
		return instanceid;
	}

	public void setInstanceid(long instanceid) {
		this.instanceid = instanceid;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}
	
}
