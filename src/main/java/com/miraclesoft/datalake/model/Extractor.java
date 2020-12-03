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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;
import com.miraclesoft.datalake.mongo.model.StringPrefixedSequenceIdGenerator;

@Entity
@Table(name = "extractor")
public class Extractor {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "extractor_seq")
	@GenericGenerator(name = "extractor_seq", strategy = "com.miraclesoft.datalake.mongo.model.StringPrefixedSequenceIdGenerator", parameters = {
			@Parameter(name = StringPrefixedSequenceIdGenerator.INCREMENT_PARAM, value = "10"),
			@Parameter(name = StringPrefixedSequenceIdGenerator.VALUE_PREFIX_PARAMETER, value = "E_"),
			@Parameter(name = StringPrefixedSequenceIdGenerator.NUMBER_FORMAT_PARAMETER, value = "%05d") })
	private String id;

	@Column(name = "NAME", unique = true)
	private String name;

	@Column(name = "FUNCT")
	private String function;

	@Column(name = "SOURCEID")
	private long sourceId;

	@Column(name = "TARGETTYPE")
	private String targetType;

	@Column(name = "TARGETNAME")
	private String targetName;

	@Column(name = "TARGETID")
	private String targetId;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "INSTANCEID")
	private long instanceid;

	@Column(name = "CREATEDBY")
	private String createdBy;

	@Column(name = "SOURCETYPE")
	private String sourceType;

	@Column(name = "CATEGORY")
	private String category;

	@Column(name = "CREATEDAT")
	private String createdAt = new Date().toString();

	@Column(name = "updateddate")
//	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm a z")
	private String updatedDate = new Date().toString();

	public Extractor() {

	}

	public Extractor(String name, String function, long sourceId, String targetType, String targetName, String targetId,
			String description, long instanceid, String createdBy, String sourceType, String category, String createdAt,
			String updatedDate) {
		this.name = name;
		this.function = function;
		this.sourceId = sourceId;
		this.targetType = targetType;
		this.targetName = targetName;
		this.targetId = targetId;
		this.description = description;
		this.instanceid = instanceid;
		this.createdBy = createdBy;
		this.sourceType = sourceType;
		this.category = category;
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

	public String getTargetType() {
		return targetType;
	}

	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getInstanceid() {
		return instanceid;
	}

	public void setInstanceid(long instanceid) {
		this.instanceid = instanceid;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public String getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(String updatedDate) {
		this.updatedDate = updatedDate;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

}
