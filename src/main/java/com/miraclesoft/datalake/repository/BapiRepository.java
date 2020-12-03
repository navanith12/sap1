package com.miraclesoft.datalake.repository;

import java.util.List;

import javax.persistence.OrderBy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.miraclesoft.datalake.model.Bapi;

@Repository
public interface BapiRepository extends JpaRepository<Bapi, String> {

	List<Bapi> findAllByOrderByCreatedAtDesc();

	List<Bapi> findAllBycreatedBy(String username);
	
	Bapi findByname(String bapijobName);
	
	@OrderBy("updatedDate Desc")
	List<Bapi> findAll();
	
}