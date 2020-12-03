package com.miraclesoft.datalake.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ExtractorData")
public class ExtractorData {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	
	@Column(name = "XML_DATA", columnDefinition = "MEDIUMTEXT")
	private String file;

	@Column(name = "EXTRACTOR")
	private String extractor;

	@Column(name = "FLAG")
	private String flag;
	
	@Column(name="SYS_ID")
	private String sysid; 
	
	
	public ExtractorData(String extractor, String file, String flag, String sysid) {
		this.file = file;
		this.extractor = extractor;
		this.flag = flag;
		this.sysid = sysid;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getExtractor() {
		return extractor;
	}

	public void setExtractor(String extractor) {
		this.extractor = extractor;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getSysid() {
		return sysid;
	}

	public void setSysid(String sysid) {
		this.sysid = sysid;
	}

}
