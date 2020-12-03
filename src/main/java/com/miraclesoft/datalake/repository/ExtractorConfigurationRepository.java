package com.miraclesoft.datalake.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.miraclesoft.datalake.model.ExtractorConfiguration;

public interface ExtractorConfigurationRepository extends JpaRepository<ExtractorConfiguration, Long>{

}
