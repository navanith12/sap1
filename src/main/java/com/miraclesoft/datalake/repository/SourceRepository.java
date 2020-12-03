package com.miraclesoft.datalake.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.miraclesoft.datalake.model.Source;

@Repository
public interface SourceRepository extends JpaRepository<Source, Long> {

	List<Source> findAllByOrderByCreatedAtDesc();

}