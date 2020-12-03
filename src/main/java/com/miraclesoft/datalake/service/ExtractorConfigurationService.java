package com.miraclesoft.datalake.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.miraclesoft.datalake.model.ExtractorConfiguration;
import com.miraclesoft.datalake.repository.ExtractorConfigurationRepository;

@Service
public class ExtractorConfigurationService {
	
	@Autowired
	private ExtractorConfigurationRepository extractorConfigurationRepository;
	
	public ExtractorConfiguration add(ExtractorConfiguration extractorConfiguration) {
		extractorConfigurationRepository.save(extractorConfiguration);
		return extractorConfiguration;
	}
	
	public ExtractorConfiguration findextractorConfiguration(Long id) {
		return extractorConfigurationRepository.findById(id).get();
	}

	public ExtractorConfiguration update(ExtractorConfiguration extractorConfiguration, Long id) {
		extractorConfiguration.setId(id);
		return extractorConfigurationRepository.save(extractorConfiguration);
	}

	public Long delete(Long id) {
		extractorConfigurationRepository.deleteById(id);
		return id;
	
	}

	public List<ExtractorConfiguration> list() {
		return extractorConfigurationRepository.findAll();
	}


}
