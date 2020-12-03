package com.miraclesoft.datalake.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.miraclesoft.datalake.model.Scheduler;


@Repository
public interface SchedulerRepository extends JpaRepository<Scheduler, Long> {

	List<Scheduler> findAllByOrderByCreatedAtDesc();
	
	List<Scheduler> findAllBycreatedBy(String username);
	
	@Transactional
	@Query(value = "select s1.jobName, s1.jobtype, s1.jobId, s1.scheduledDate, s1.frequency, s1.cronDate, s1.status, s1.createdBy, s1.createdAt  from Scheduler s1 join Scheduler s2 on s1.id=s2.id where s2.createdBy >= :startdate and s2.createdBy <= :enddate")
	List<Scheduler> getListbyScheduleDate(@Param("startdate") String startdate, @Param("enddate") String enddate);
}