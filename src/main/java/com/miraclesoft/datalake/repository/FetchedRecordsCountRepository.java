package com.miraclesoft.datalake.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.miraclesoft.datalake.model.FetchedRecordsCount;

@Repository
public interface FetchedRecordsCountRepository extends JpaRepository<FetchedRecordsCount, Long> {

}
