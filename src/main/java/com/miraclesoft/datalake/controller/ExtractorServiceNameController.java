package com.miraclesoft.datalake.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.miraclesoft.datalake.model.ExtractorServiceName;
import com.miraclesoft.datalake.service.ExtractorServiceNameService;

@RestController
@RequestMapping("/extracservice")
public class ExtractorServiceNameController {
	
	@Autowired
	private ExtractorServiceNameService extractorServiceNameService;

	
	@PostMapping("/")
	public ResponseEntity<Object> add(@RequestBody ExtractorServiceName extractorServiceName) {
		return extractorServiceNameService.add(extractorServiceName);
	}
	
	@GetMapping("/")
	public ResponseEntity<Object> List(){
		return extractorServiceNameService.list();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Object> getExtractorServiceName(@PathVariable long id) {
		return extractorServiceNameService.findExtractorServiceName(id);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Object> update(@RequestBody ExtractorServiceName ExtractorServiceName, @PathVariable long id) {
		return extractorServiceNameService.update(ExtractorServiceName, id);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Object> delete(@PathVariable Long id) {
		return extractorServiceNameService.delete(id);
	}
	

}
