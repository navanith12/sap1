package com.miraclesoft.datalake.mongo.model;

public class ImportParameterModel {
	
	private String paramName;
	private String defaultValue;
	private String paramValue= "null";
	
	public ImportParameterModel(){
		
	}
	
	public ImportParameterModel(String paramName, String defaultValue) {
		this.paramName = paramName;
		this.defaultValue = defaultValue;
	}

	public ImportParameterModel(String paramName, String defaultValue, String paramValue) {
		this.paramName = paramName;
		this.defaultValue = defaultValue;
		this.paramValue = paramValue;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getParamValue() {
		return paramValue;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}
	
}
