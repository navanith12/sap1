package com.miraclesoft.datalake.mongo.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;


@Entity
@Table(name = "Metadata")
public class Metadata implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@javax.persistence.Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Column(name="columnname")
	private String columName;
	
	@Column(name = "datatype")
	private String dataType;
	
	
	public Metadata() {
	}

	public Metadata(String columName, String dataType) {
		this.columName = columName;
		this.dataType = dataType;
	}

	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getColumName() {
		return columName;
	}

	public void setColumName(String columName) {
		this.columName = columName;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}	
}
