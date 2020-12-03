package com.miraclesoft.datalake.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.miraclesoft.datalake.model.Table;

@Repository
public interface TableRepository extends JpaRepository<Table, String> {

//	List<Table> findAllByOrderByCreatedAtDesc();

	List<Table> findAllBycreatedBy(String username);
	
	Table findByname(String name);
	
	List<Table> findAllByCategoryOrderByUpdatedDateDesc(String category);
	
	List<Table> findAllByOrderByUpdatedDateDesc();
}