package com.miraclesoft.datalake.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.miraclesoft.datalake.model.ExtractorData;
import com.miraclesoft.datalake.repository.ExtractorDataRepository;

@Service
public class ExtractorDataService {

	@Autowired
	private ExtractorDataRepository extractorDataRepository;

	public void addFile(ExtractorData extractorData) {
		extractorDataRepository.save(extractorData);
		
	}

	public List<ExtractorData> list() {
		// TODO Auto-generated method stub
		return extractorDataRepository.findAll();
	}

}
