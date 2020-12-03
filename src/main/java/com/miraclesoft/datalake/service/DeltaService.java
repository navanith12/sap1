package com.miraclesoft.datalake.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.miraclesoft.datalake.model.Delta;
import com.miraclesoft.datalake.repository.DeltaRepository;

@Service
public class DeltaService {

	@Autowired
	private DeltaRepository deltaRepository;

	public Delta addDelta(Delta delta) {
		return deltaRepository.save(delta);
	}

	public Delta updateDelta(Delta delta, long id) {
		delta.setId(id);
		return deltaRepository.save(delta);
	}

	public Delta getDeltaByExtractor(String extractor) {
		return deltaRepository.findByExtractor(extractor);
	}
	
	public List<Delta> listofdelta(){
		return deltaRepository.findAll();
	}
	
	public long deleteDelta(long id) {
		deltaRepository.deleteById(id);
		return id;
	}

}
