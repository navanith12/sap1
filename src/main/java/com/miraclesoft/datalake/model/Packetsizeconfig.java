package com.miraclesoft.datalake.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "packet_size")
public class Packetsizeconfig {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "extractorname", unique = true)
	private String extractorName;
	
	@Column(name = "type", unique = true)
	private String extractormodeType;
	
	@Column(name = "packetsize")
	private int packetsize;
	
	@Column(name = "numberofthreads")
	private int numberofthreads;

	public Packetsizeconfig() {
		
	}

	public Packetsizeconfig(String extractorName, String extractormodeType, int packetsize, int numberofthreads) {
		this.extractorName = extractorName;
		this.extractormodeType = extractormodeType;
		this.packetsize = packetsize;
		this.numberofthreads = numberofthreads;
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

	public String getExtractormodeType() {
		return extractormodeType;
	}

	public void setExtractormodeType(String extractormodeType) {
		this.extractormodeType = extractormodeType;
	}

	public int getPacketsize() {
		return packetsize;
	}

	public void setPacketsize(int packetsize) {
		this.packetsize = packetsize;
	}

	public int getNumberofthreads() {
		return numberofthreads;
	}

	public void setNumberofthreads(int numberofthreads) {
		this.numberofthreads = numberofthreads;
	}

	@Override
	public String toString() {
		return "Packetsizeconfig [id=" + id + ", extractorName=" + extractorName + ", extractormodeType="
				+ extractormodeType + ", packetsize=" + packetsize + ", numberofthreads=" + numberofthreads + "]";
	}
	
	
}
