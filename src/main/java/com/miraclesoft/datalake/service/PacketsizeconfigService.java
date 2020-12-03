package com.miraclesoft.datalake.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.miraclesoft.datalake.model.ExtractorData;
import com.miraclesoft.datalake.model.Packetsizeconfig;
import com.miraclesoft.datalake.model.Scheduler;
import com.miraclesoft.datalake.repository.ExtractorDataRepository;
import com.miraclesoft.datalake.repository.JobHistoryRepository;
import com.miraclesoft.datalake.repository.PacketsizeconfigRepository;

@Service
public class PacketsizeconfigService {

	@Autowired
	private PacketsizeconfigRepository packetsizeconfigRepository;

	public Packetsizeconfig add(Packetsizeconfig packetsizeconfig) {
		packetsizeconfigRepository.save(packetsizeconfig);
		return packetsizeconfig;
	}
	
	public Packetsizeconfig findById(Long id) {
		return packetsizeconfigRepository.findById(id).get();
	}

	public Packetsizeconfig update(Packetsizeconfig packetsizeconfig, Long id) {
		packetsizeconfig.setId(id);
		return packetsizeconfigRepository.save(packetsizeconfig);
	}

	public Long delete(Long id) {
		packetsizeconfigRepository.deleteById(id);
		return id;
	
	}
	
	public Packetsizeconfig findByExtractorName(String name) {
		return packetsizeconfigRepository.findByExtractorName(name);
	}

	public List<Packetsizeconfig> list() {
		return packetsizeconfigRepository.findAll();
	}

}
