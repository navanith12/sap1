package com.miraclesoft.datalake.mongo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.miraclesoft.datalake.mongo.model.MetadataStructure;

@Repository
public interface MetadataStructureRepository extends JpaRepository<MetadataStructure, Long> {

	List<MetadataStructure> findAllByOrderByCreatedAtDesc();
	
	MetadataStructure findByExtractorName(String extractorName);

}