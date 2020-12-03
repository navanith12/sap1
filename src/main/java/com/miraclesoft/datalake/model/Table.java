package com.miraclesoft.datalake.model;

import java.sql.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.miraclesoft.datalake.mongo.model.StringPrefixedSequenceIdGenerator;

@Entity
@javax.persistence.Table(name = "table_job")
public class Table {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "table_seq")
	@GenericGenerator(name = "table_seq", strategy = "com.miraclesoft.datalake.mongo.model.StringPrefixedSequenceIdGenerator", parameters = {
			@Parameter(name = StringPrefixedSequenceIdGenerator.INCREMENT_PARAM, value = "10"),
			@Parameter(name = StringPrefixedSequenceIdGenerator.VALUE_PREFIX_PARAMETER, value = "T_"),
			@Parameter(name = StringPrefixedSequenceIdGenerator.NUMBER_FORMAT_PARAMETER, value = "%05d") })
	private String id;

	@Column(name = "name", unique = true)
	private String name;

	@Column(name = "sourceid")
	private long sourceId;

	@Column(name = "targetid")
	private String targetId;

	@Column(name = "tablename")
	private String table;

	@Column(name = "fields", columnDefinition = "MEDIUMTEXT")
	private String fields;

	@Column(name = "targetname")
	private String targetName;

	@Column(name = "filteroptions")
	@OneToMany(orphanRemoval = true)
	@JoinColumn(name = "id")
	private Set<table_filter> filteroptions;

	@Column(name = "tablerows")
	private int rows;

	@Column(name = "description")
	private String description;

	@Column(name = "INSTANCEID")
	private long instanceid;

	@Column(name = "category")
	private String category;

	@Column(name = "createdby")
	private String createdBy;

	@Column(name = "sourcetype")
	private String sourceType;

	@Column(name = "createdat")
	private Date createdAt = new Date(System.currentTimeMillis());

	@Column(name = "updateddate")
	private java.util.Date updatedDate = new java.util.Date();

	public Table() {
	}

	public Table(String name, long sourceId, String targetId, String table, String fields, String targetName,
			Set<table_filter> filteroptions, int rows, String description, long instanceid, String category,
			String createdBy, String sourceType, Date createdAt, java.util.Date updatedDate) {
		super();
		this.name = name;
		this.sourceId = sourceId;
		this.targetId = targetId;
		this.table = table;
		this.fields = fields;
		this.targetName = targetName;
		this.filteroptions = filteroptions;
		this.rows = rows;
		this.description = description;
		this.instanceid = instanceid;
		this.category = category;
		this.createdBy = createdBy;
		this.sourceType = sourceType;
		this.createdAt = createdAt;
		this.updatedDate = updatedDate;
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

	public long getSourceId() {
		return sourceId;
	}

	public void setSourceId(long sourceId) {
		this.sourceId = sourceId;
	}

	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getFields() {
		return fields;
	}

	public void setFields(String fields) {
		this.fields = fields;
	}

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public Set<table_filter> getFilteroptions() {
		return filteroptions;
	}

	public void setFilteroptions(Set<table_filter> filteroptions) {
		this.filteroptions = filteroptions;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
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

	public java.util.Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(java.util.Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

}
