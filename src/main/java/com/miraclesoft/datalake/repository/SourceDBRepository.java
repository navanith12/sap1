package com.miraclesoft.datalake.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import com.miraclesoft.datalake.model.SourceDatabase;

@Repository
public interface SourceDBRepository extends JpaRepository<SourceDatabase,String> {

	List<SourceDatabase> findAllByOrderByCreatedAtDesc();
}