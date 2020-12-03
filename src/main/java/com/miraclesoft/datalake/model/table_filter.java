package com.miraclesoft.datalake.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.miraclesoft.datalake.mongo.model.StringPrefixedSequenceIdGenerator;

@Entity
@javax.persistence.Table(name = "table_filters")
public class table_filter {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tablefil_seq")
	@GenericGenerator(
	        name = "tablefil_seq", 
	        strategy = "com.miraclesoft.datalake.mongo.model.StringPrefixedSequenceIdGenerator", 
	        parameters = {
	            @Parameter(name = StringPrefixedSequenceIdGenerator.INCREMENT_PARAM, value = "10"),
	            @Parameter(name = StringPrefixedSequenceIdGenerator.VALUE_PREFIX_PARAMETER, value = "TF_"),
	            @Parameter(name = StringPrefixedSequenceIdGenerator.NUMBER_FORMAT_PARAMETER, value = "%05d") })
	private String id;
	
	@Column(name = "columname")
	private String columnName;
	
	@Column(name = "operator")
	private String operator;
	
	@Column(name = "columnvalue")
	private String columnValue;
	
	public table_filter() {
		
	}
	
	public table_filter(String columnName, String operator, String columnValue) {
		this.columnName = columnName;
		this.operator = operator;
		this.columnValue = columnValue;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getColumnValue() {
		return columnValue;
	}

	public void setColumnValue(String columnValue) {
		this.columnValue = columnValue;
	}
}
