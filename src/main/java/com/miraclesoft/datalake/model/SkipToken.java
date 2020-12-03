package com.miraclesoft.datalake.model;

import lombok.AllArgsConstructor;
import javax.persistence.Table;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.net.URL;

@Entity
@Table(name = "skiptokens")
public class SkipToken implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private Long id;

    @Column(name = "extractorname")
    private String extractorName;
    
    @Column(name = "currskiptoken")
    private URL currentSkiptoken;
    
    @Column(name = "prevskiptoken")
    private URL previousSkiptoken;

    public SkipToken() {
    	
    }
	public SkipToken(String extractorName, URL currentSkiptoken, URL previousSkiptoken) {
		this.extractorName = extractorName;
		this.currentSkiptoken = currentSkiptoken;
		this.previousSkiptoken = previousSkiptoken;
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

	public URL getCurrentSkiptoken() {
		return currentSkiptoken;
	}

	public void setCurrentSkiptoken(URL currentSkiptoken) {
		this.currentSkiptoken = currentSkiptoken;
	}

	public URL getPreviousSkiptoken() {
		return previousSkiptoken;
	}

	public void setPreviousSkiptoken(URL previousSkiptoken) {
		this.previousSkiptoken = previousSkiptoken;
	}

}
