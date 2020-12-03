package com.miraclesoft.datalake.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "EXtractorConfig")
public class ExtractorConfiguration {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(name = "extractorname", unique = true)
	private String extractorName;
	
	@Column(name = "type", unique = true)
	private String type;
	
	@Column(name = "packetsize")
	private int packetSize;

	public ExtractorConfiguration(String extractorName, String type, int packetSize) {
		super();
		this.extractorName = extractorName;
		this.type = type;
		this.packetSize = packetSize;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getExtractorName() {
		return extractorName;
	}

	public void setExtractorName(String extractorName) {
		this.extractorName = extractorName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getPacketSize() {
		return packetSize;
	}

	public void setPacketSize(int packetSize) {
		this.packetSize = packetSize;
	}
	
}
