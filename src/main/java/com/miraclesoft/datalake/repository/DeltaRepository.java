package com.miraclesoft.datalake.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.miraclesoft.datalake.model.Delta;

@Repository
public interface DeltaRepository extends JpaRepository<Delta, Long> {

	@Modifying
	@Query("UPDATE Delta d SET d.current_token = :current_token, d.previous_token = :previous_token where d.extractor = :extractor")
	int updateDelta(String current_token, String previous_token, String extractor);

	Delta findByExtractor(String extractor);
	
	List<Delta> findAllBycreatedBy(String username);

}