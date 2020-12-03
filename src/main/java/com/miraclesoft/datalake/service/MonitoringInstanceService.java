package com.miraclesoft.datalake.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.miraclesoft.datalake.model.ExtractorData;
import com.miraclesoft.datalake.model.Jobhistory;
import com.miraclesoft.datalake.model.Logs;
import com.miraclesoft.datalake.model.MonitoringInstance;
import com.miraclesoft.datalake.model.Scheduler;
import com.miraclesoft.datalake.repository.ExtractorDataRepository;
import com.miraclesoft.datalake.repository.JobHistoryRepository;
import com.miraclesoft.datalake.repository.LogsRepository;
import com.miraclesoft.datalake.repository.MonitoringInstanceRepository;

@Service
public class MonitoringInstanceService {

	@Autowired
	private MonitoringInstanceRepository monitoringInstanceRepository;

	public MonitoringInstance add(MonitoringInstance monitoringInstance) {
		monitoringInstanceRepository.save(monitoringInstance);
		return monitoringInstance;
	}
	
	public MonitoringInstance findJobHistory(Long id) {
		return monitoringInstanceRepository.findById(id).get();
	}

	public MonitoringInstance update(MonitoringInstance monitoringInstance, Long id) {
		monitoringInstance.setInstanceid(id);
		return monitoringInstanceRepository.save(monitoringInstance);
	}

	public Long delete(Long id) {
		monitoringInstanceRepository.deleteById(id);
		return id;
	
	}

	public List<MonitoringInstance> list() {
		return monitoringInstanceRepository.findAll();
	}
	
	

}
