package com.miraclesoft.datalake.service;

import java.sql.SQLException;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.miraclesoft.datalake.jco.BatchJobImpl;
import com.miraclesoft.datalake.model.BatchJob;
import com.miraclesoft.datalake.model.ExtractorData;
import com.miraclesoft.datalake.model.Jobhistory;
import com.miraclesoft.datalake.model.Scheduler;
import com.miraclesoft.datalake.repository.BatchJobRepository;
import com.miraclesoft.datalake.repository.ExtractorDataRepository;
import com.miraclesoft.datalake.repository.JobHistoryRepository;

@Service
public class BatchJobService {

	@Autowired
	private BatchJobRepository batchJobRepository;
	
	@Autowired
	private BatchJobImpl batchjobImpl;
	
	JSONObject jo;

	public BatchJob add(BatchJob batchjob) {
		batchJobRepository.save(batchjob);
		return batchjob;
	}

	public BatchJob findBatchJob(long id) {
		return batchJobRepository.findById(id).get();
	}
	
	public BatchJob findBatchJobbyName(String name) {
		return batchJobRepository.findByname(name);
	}

	public BatchJob update(BatchJob batchjob, long id) {
		batchjob.setId(id);
		return batchJobRepository.save(batchjob);
	}

	public long delete(long id) {
		batchJobRepository.deleteById(id);
		return id;

	}

	public List<BatchJob> list() {
		return batchJobRepository.findAll();
	}

	public ResponseEntity<Object> execute(BatchJob batchjob, String username) {
		jo = new JSONObject();		
		try {
			String res = batchjobImpl.batchjob(batchjob.getJobLists(), username);
			jo.put("Status", HttpStatus.OK);
			jo.put("Data", res);
			jo.put("Message", "Completed Execution");
		} catch (SQLException e) {
			jo.put("Status", HttpStatus.BAD_REQUEST);
			jo.put("Data", e.getMessage());
			jo.put("Message", "Execution Failes");
		}
		return new ResponseEntity<Object>(jo.toMap(), HttpStatus.OK);
	}

}
