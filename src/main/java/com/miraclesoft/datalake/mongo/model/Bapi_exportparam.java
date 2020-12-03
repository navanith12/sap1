package com.miraclesoft.datalake.mongo.model;

import java.util.List;

public class Bapi_exportparam {
	
	private String exportparamName;
	private List<String> exportparamValues;
	
	public Bapi_exportparam(String exportparamName, List<String> exportparamValues) {
		this.exportparamName = exportparamName;
		this.exportparamValues = exportparamValues;
	}

	public String getExportparamName() {
		return exportparamName;
	}

	public void setExportparamName(String exportparamName) {
		this.exportparamName = exportparamName;
	}

	public List<String> getExportparamValues() {
		return exportparamValues;
	}

	public void setExportparamValues(List<String> exportparamValues) {
		this.exportparamValues = exportparamValues;
	}
	
}
