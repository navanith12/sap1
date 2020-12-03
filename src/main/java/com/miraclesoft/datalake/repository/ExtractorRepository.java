package com.miraclesoft.datalake.repository;

import java.util.List;

import javax.persistence.OrderBy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.miraclesoft.datalake.model.Extractor;

@Repository
public interface ExtractorRepository extends JpaRepository<Extractor, String> {
	
	Extractor findByfunction(String functionName);
	
	Extractor findByname(String jobName);
	
	List<Extractor> findAllBycreatedByOrderByCreatedAtDesc(String username);

	List<Extractor> findAllByCategoryOrderByUpdatedDateDesc(String Category);
	
	List<Extractor> findAllByOrderByUpdatedDateDesc();
}