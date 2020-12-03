package com.miraclesoft.datalake.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.miraclesoft.datalake.model.ExtractorData;
import com.miraclesoft.datalake.model.Jobhistory;
import com.miraclesoft.datalake.model.Scheduler;
import com.miraclesoft.datalake.repository.ExtractorDataRepository;
import com.miraclesoft.datalake.repository.JobHistoryRepository;

@Service
public class JobHistoryService {

	@Autowired
	private JobHistoryRepository jobHistoryRepository;

	public Jobhistory add(Jobhistory jobhistory) {
		jobHistoryRepository.save(jobhistory);
		return jobhistory;
	}
	
	public Jobhistory findJobHistory(Long id) {
		return jobHistoryRepository.findById(id).get();
	}

	public Jobhistory update(Jobhistory jobhistory, Long id) {
		jobhistory.setInstanceId(id);
		return jobHistoryRepository.save(jobhistory);
	}

	public Long delete(Long id) {
		jobHistoryRepository.deleteById(id);
		return id;
	
	}

	public List<Jobhistory> list() {
		return jobHistoryRepository.findAll();
	}

}
