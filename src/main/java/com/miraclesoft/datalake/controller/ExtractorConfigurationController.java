package com.miraclesoft.datalake.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.miraclesoft.datalake.model.ExtractorConfiguration;
import com.miraclesoft.datalake.service.ExtractorConfigurationService;

@RestController
@RequestMapping("/configextrac")
public class ExtractorConfigurationController {
	
	@Autowired
	private ExtractorConfigurationService extractorConfigurationService;

	
	@PostMapping("/")
	public ExtractorConfiguration add(@RequestBody ExtractorConfiguration extractorConfiguration) {
		return extractorConfigurationService.add(extractorConfiguration);
	}
	
	@GetMapping("/")
	public List<ExtractorConfiguration> jobHistoryList(){
		return extractorConfigurationService.list();
	}

	@GetMapping("/{id}")
	public ExtractorConfiguration getJobHistory(@PathVariable long id) {
		return extractorConfigurationService.findextractorConfiguration(id);
	}

	@PutMapping("/{id}")
	public ExtractorConfiguration update(@RequestBody ExtractorConfiguration extractorConfiguration, @PathVariable long id) {
		return extractorConfigurationService.update(extractorConfiguration, id);
	}

	@DeleteMapping("/{id}")
	public Long delete(@PathVariable Long id) {
		return extractorConfigurationService.delete(id);
	}

}
