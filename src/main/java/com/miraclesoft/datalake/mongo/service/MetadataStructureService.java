package com.miraclesoft.datalake.mongo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.miraclesoft.datalake.mongo.model.MetadataStructure;
import com.miraclesoft.datalake.mongo.repository.MetadataStructureRepository;

@Service
public class MetadataStructureService {

	@Autowired
	private MetadataStructureRepository metadataStructureRepository;


	public MetadataStructure add(MetadataStructure metadataStruc) {
		return metadataStructureRepository.save(metadataStruc);
	}

	public List<MetadataStructure> list() {
		return metadataStructureRepository.findAllByOrderByCreatedAtDesc();
	}

	public MetadataStructure update(MetadataStructure metadataStruc, long id) {
		metadataStruc.setId(id);
		return metadataStructureRepository.save(metadataStruc);
	}

	public long delete(long id) {
		metadataStructureRepository.deleteById(id);
		return id;
	}
	
	public MetadataStructure getDeltaByExtractorName(String extractorName) {
		return metadataStructureRepository.findByExtractorName(extractorName);
	}

}