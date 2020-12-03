package com.miraclesoft.datalake.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.miraclesoft.datalake.model.JobName;
import com.miraclesoft.datalake.model.UniqLogs;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface JobNameRepository extends JpaRepository<UniqLogs, String> {

    @Transactional
    @Query(value = "select DISTINCT l.job_name from dbo.LOGS l ", nativeQuery = true)
    List<String> getJobName();

}