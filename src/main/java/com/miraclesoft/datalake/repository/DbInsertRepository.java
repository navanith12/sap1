package com.miraclesoft.datalake.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.miraclesoft.datalake.model.DbInsert;

@Repository
public interface DbInsertRepository extends JpaRepository<DbInsert, String> {

	List<DbInsert> findAllByOrderByCreatedAtDesc();
}