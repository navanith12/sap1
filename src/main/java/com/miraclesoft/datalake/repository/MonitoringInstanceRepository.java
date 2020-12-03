package com.miraclesoft.datalake.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.miraclesoft.datalake.model.Logs;
import com.miraclesoft.datalake.model.MonitoringInstance;

public interface MonitoringInstanceRepository extends JpaRepository<MonitoringInstance, Long> {

}
