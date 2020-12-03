package com.miraclesoft.datalake.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.miraclesoft.datalake.model.ExtractorData;

public interface ExtractorDataRepository extends JpaRepository<ExtractorData, Long> {

}
