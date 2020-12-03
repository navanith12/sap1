package com.miraclesoft.datalake.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.miraclesoft.datalake.model.Packetsizeconfig;

public interface PacketsizeconfigRepository extends JpaRepository<Packetsizeconfig, Long> {
	
	Packetsizeconfig findByExtractorName(String extractorName); 
}
