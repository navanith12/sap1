package com.miraclesoft.datalake.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.miraclesoft.datalake.model.BatchJob;
import com.miraclesoft.datalake.model.ExtractorData;
import com.miraclesoft.datalake.model.LocalFile;
import com.miraclesoft.datalake.repository.JobHistoryRepository;
import com.miraclesoft.datalake.service.BatchJobService;
import com.miraclesoft.datalake.service.ExtractorDataService;
import com.miraclesoft.datalake.service.JobHistoryService;

@RestController
@RequestMapping("/batchjob")
public class BatchJobController {
	
	@Autowired
	private BatchJobService batchJobService;

	
	@PostMapping("/")
	public BatchJob add(@RequestBody BatchJob batchJob) {
		System.out.println(batchJob.getJobLists());
		return batchJobService.add(batchJob);
	}
	
	@GetMapping("/")
	public List<BatchJob> BatchJobList(){
		return batchJobService.list();
	}

	@GetMapping("/{id}")
	public BatchJob editBatchjob(@PathVariable long id) {
		return batchJobService.findBatchJob(id);
	}

	@PutMapping("/{id}")
	public BatchJob update(@RequestBody BatchJob batchjob, @PathVariable long id) {
		return batchJobService.update(batchjob, id);
	}

	@DeleteMapping("/{id}")
	public long delete(@PathVariable long id) {
		return batchJobService.delete(id);
	}
	
	@PostMapping("/execute/{username}")
	public ResponseEntity<Object> execute(@RequestBody BatchJob batchjob, @PathVariable String username) {
		System.out.println(batchjob.getJobLists());
		return batchJobService.execute(batchjob, username);
	}

}
