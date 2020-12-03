package com.miraclesoft.datalake.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.miraclesoft.datalake.model.ExtractorServiceName;

@Repository
public interface ExtractorServiceNameRepository extends JpaRepository<ExtractorServiceName, Long> {
	
	ExtractorServiceName findByextractorName(String name);
	
	List<ExtractorServiceName> findBysourcetype(String sourceType);
}
