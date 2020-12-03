package com.miraclesoft.datalake.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.miraclesoft.datalake.model.Table;
import com.miraclesoft.datalake.model.table_filter;

@Repository
public interface TableFiltersRepository extends JpaRepository<table_filter, Long> {

	//List<Table> findAllByOrderByCreatedAtDesc();
}