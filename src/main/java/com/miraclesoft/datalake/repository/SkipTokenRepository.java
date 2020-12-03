package com.miraclesoft.datalake.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.miraclesoft.datalake.model.JobName;
import com.miraclesoft.datalake.model.SkipToken;
import com.miraclesoft.datalake.model.UniqLogs;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface SkipTokenRepository extends JpaRepository<SkipToken, Long> {

    SkipToken findByExtractorName(String extractorname);

}