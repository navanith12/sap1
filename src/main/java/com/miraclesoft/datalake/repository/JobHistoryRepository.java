package com.miraclesoft.datalake.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.miraclesoft.datalake.model.Jobhistory;

public interface JobHistoryRepository extends JpaRepository<Jobhistory, Long> {

}
