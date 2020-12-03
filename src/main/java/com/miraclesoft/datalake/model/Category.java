package com.miraclesoft.datalake.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@javax.persistence.Table(name = "category")
public class Category {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Column(name = "categoryname")
	private String catergoryName;
	
	@Column(name = "categorydescription")
	private String categoryDescription;
	
	@Column(name  = "categorytype")
	private String categoryType;

	public Category() {
	}

	public Category(String catergoryName, String categoryDescription, String categoryType) {
		super();
		this.catergoryName = catergoryName;
		this.categoryDescription = categoryDescription;
		this.categoryType = categoryType;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCatergoryName() {
		return catergoryName;
	}

	public void setCatergoryName(String catergoryName) {
		this.catergoryName = catergoryName;
	}

	public String getCategoryDescription() {
		return categoryDescription;
	}

	public void setCategoryDescription(String categoryDescription) {
		this.categoryDescription = categoryDescription;
	}

	public String getCategoryType() {
		return categoryType;
	}

	public void setCategoryType(String categoryType) {
		this.categoryType = categoryType;
	}
}
