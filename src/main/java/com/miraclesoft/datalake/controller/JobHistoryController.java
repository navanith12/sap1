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

import com.miraclesoft.datalake.model.ExtractorData;
import com.miraclesoft.datalake.model.Jobhistory;
import com.miraclesoft.datalake.model.LocalFile;
import com.miraclesoft.datalake.repository.JobHistoryRepository;
import com.miraclesoft.datalake.service.ExtractorDataService;
import com.miraclesoft.datalake.service.JobHistoryService;

@RestController
@RequestMapping("/jobhistory")
public class JobHistoryController {
	
	@Autowired
	private JobHistoryService jobHistoryService;

	
	@PostMapping("/")
	public Jobhistory add(@RequestBody Jobhistory jobhistory) {
		return jobHistoryService.add(jobhistory);
	}
	
	@GetMapping("/")
	public List<Jobhistory> jobHistoryList(){
		return jobHistoryService.list();
	}

	@GetMapping("/{id}")
	public Jobhistory getJobHistory(@PathVariable long id) {
		return jobHistoryService.findJobHistory(id);
	}

	@PutMapping("/{id}")
	public Jobhistory update(@RequestBody Jobhistory jobhistory, @PathVariable long id) {
		return jobHistoryService.update(jobhistory, id);
	}

	@DeleteMapping("/{id}")
	public Long delete(@PathVariable Long id) {
		return jobHistoryService.delete(id);
	}

}
