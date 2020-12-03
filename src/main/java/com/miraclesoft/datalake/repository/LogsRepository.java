package com.miraclesoft.datalake.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.miraclesoft.datalake.model.Logs;
import com.miraclesoft.datalake.model.UniqLogs;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.List;

@Repository
public interface LogsRepository extends JpaRepository<Logs, BigInteger> {
    @Transactional
    @Query(value = "select * from dbo.LOGS l where l.instance_id = :instance_id", nativeQuery = true)
    List<Logs> getLogs(@Param("instance_id") String instance_id);

}
