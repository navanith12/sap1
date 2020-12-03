package com.miraclesoft.datalake.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.miraclesoft.datalake.model.ExtractorData;
import com.miraclesoft.datalake.service.ExtractorDataService;

@RestController
@RequestMapping("/fullload")
public class ExtractorDataController {
	
	@Autowired
	private ExtractorDataService extractorDataService;

	
	@GetMapping("/")
	public List<ExtractorData> fullloadList(){
		return extractorDataService.list();
	}
	
	

}
