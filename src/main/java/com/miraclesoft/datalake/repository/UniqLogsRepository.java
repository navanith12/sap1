package com.miraclesoft.datalake.repository;

import org.hibernate.validator.constraints.ParameterScriptAssert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.miraclesoft.datalake.model.UniqLogs;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@Repository
public interface UniqLogsRepository extends JpaRepository<UniqLogs, BigInteger> {
    @Transactional
    @Query(value = "select l1.INSTANCE_ID, l1.starttime, l2.status, l1.JOB_NAME, l1.job_id, l1.startdate, l1.dated, l1.event_id, l1.duration, l1.timetaken,l1.enddate,l1.created_by from logs l1 join logs l2 on l1.INSTANCE_ID=l2.INSTANCE_ID where l1.status='Initiated' \n" +
            "and l2.status in ('Finished', 'Cancelled')", nativeQuery = true)
    List<UniqLogs> getUniq();
    
    @Transactional
    @Query(value = "select l1.INSTANCE_ID, l1.starttime, l2.status, l1.JOB_NAME, l1.job_id, l1.startdate, l1.dated, l1.event_id, l1.duration,  l1.timetaken,l1.enddate,l1.created_by from logs l1 join logs l2 on l1.INSTANCE_ID=l2.INSTANCE_ID where l1.status='Initiated' \n" +
            "and l2.status in (:status) and l2.job_id like '%'+:job_Name+'%' and l2.startdate >= :startDate and l2.endDate <= :endDate", nativeQuery = true)
    List<UniqLogs> getFilter(@Param("job_Name") String job_Name, @Param("status") String[] status, @Param("startDate") Date startDate, @Param("endDate") Date endDate);
    
    @Transactional
    @Query(value = "select l1.INSTANCE_ID, l1.starttime, l2.status, l1.JOB_NAME, l1.job_id, l1.startdate, l1.dated, l1.event_id, l1.duration,  l1.timetaken,l1.enddate,l1.created_by from logs l1 join logs l2 on l1.INSTANCE_ID=l2.INSTANCE_ID where l1.status='Initiated' \n" +
            "and l2.status in (:status) and l2.job_id like '%'+:job_Name+'%' and l2.startdate >= :startDate and l2.endDate <= :endDate  and l2.created_by = :username", nativeQuery = true)
    List<UniqLogs> getFilter(@Param("job_Name") String job_Name, @Param("status") String[] status, @Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("username") String username);
    
    
    @Transactional
    @Query(value = "select l1.INSTANCE_ID, l1.starttime, l2.status, l1.JOB_NAME, l1.job_id, l1.startdate, l1.dated, l1.event_id,l1.duration, l1.timetaken, l1.enddate, l1.created_by from logs l1 join logs l2 on l1.INSTANCE_ID=l2.INSTANCE_ID where l1.status='Initiated' \n" +
            "and l2.status in (:status) and l2.created_by = :username", nativeQuery = true)
    List<UniqLogs> getStatus(@Param("status")String[] status, @Param("username") String username);
    
    @Transactional
    @Query(value = "select l1.INSTANCE_ID, l1.starttime, l2.status, l1.JOB_NAME, l1.job_id, l1.startdate, l1.dated, l1.event_id,l1.duration, l1.timetaken, l1.enddate, l1.created_by from logs l1 join logs l2 on l1.INSTANCE_ID=l2.INSTANCE_ID where l1.status='Initiated' \n" +
            "and l2.status in (:status)", nativeQuery = true)
    List<UniqLogs> getStatus(@Param("status")String[] status);
    
    @Transactional
    @Query(value = "select l1.INSTANCE_ID, l1.starttime, l2.status, l1.JOB_NAME, l1.job_id, l1.startdate, l1.dated, l1.event_id, l1.duration, l1.timetaken,l1.enddate,l1.created_by from logs l1 join logs l2 on l1.INSTANCE_ID=l2.INSTANCE_ID where l1.status='Initiated' \n" +
            "and l2.status in ('Finished', 'Cancelled') and l2.created_by = :username", nativeQuery = true)
    List<UniqLogs> findBycreatedby(@Param("username") String username);

    @Transactional
    @Query(value = "select l1.INSTANCE_ID, l1.starttime, l2.status, l1.JOB_NAME, l1.job_id, l1.startdate, l1.dated, l1.event_id, l1.duration, l1.timetaken,l1.enddate,l1.created_by from logs l1 join logs l2 on l1.INSTANCE_ID=l2.INSTANCE_ID where l1.status='Initiated' \n" +
            "and l2.status in ('Finished','Cancelled','Initiated')", nativeQuery = true)
    List<UniqLogs> getListStatus();

    @Transactional
    @Query(value = "select l1.INSTANCE_ID, l1.starttime, l1.status, l1.JOB_NAME, l1.job_id, l1.startdate, l1.dated, l1.event_id, l1.duration,l1.timetaken, l1.enddate,  l1.created_by from logs l1\r\n" + 
    		"where STARTTIME = (Select min(STARTTIME) from logs l3 where l3.INSTANCE_ID = l1.INSTANCE_ID and l3.STATUS = 'In Progress')\r\n" + 
    		"and INSTANCE_ID NOT IN (select l2.INSTANCE_ID from logs l2 where l2.status in ('Finished', 'Cancelled'))", nativeQuery = true)
    List<UniqLogs> getInProgress();
    
    @Transactional
    @Query(value = "select l1.INSTANCE_ID, l1.starttime, l1.status, l1.JOB_NAME, l1.job_id, l1.startdate, l1.dated, l1.event_id, l1.duration,l1.timetaken, l1.enddate,  l1.created_by from logs l1\r\n" + 
    		"where STARTTIME = (Select min(STARTTIME) from logs l3 where l3.INSTANCE_ID = l1.INSTANCE_ID and l3.STATUS = 'In Progress')\r\n" + 
    		"and l1.created_by = :username and INSTANCE_ID NOT IN (select l2.INSTANCE_ID from logs l2 where l2.status in ('Finished', 'Cancelled'))", nativeQuery = true)
    List<UniqLogs> getInProgress(@Param("username") String username);

    @Transactional
    @Modifying
    @Query(value = "If Not EXISTS (Select * from logs l where DURATION ='23:59:59') Update l2 set l2.duration = DATEADD(SECOND,-DATEDIFF(second ,l2.starttime,l1.starttime), l1.duration)from logs l2 join logs l1 on l1.INSTANCE_ID = l2.instance_id where l2.status in ('In Progress') \n" +
            "and l2.STATUS Not in ('Finished', 'Cancelled') and l1.status='Initiated'", nativeQuery = true)
    void getUpdateDuration();

    @Transactional
    @Modifying
    @Query(value = "If Not EXISTS (Select * from logs l where DURATION ='23:59:59')Update l1 set l1.duration = DATEADD(SECOND,-DATEDIFF(second ,l2.starttime,l1.starttime), l1.duration)from logs l1" +
            " join logs l2 on l1.INSTANCE_ID = l2.instance_id where l2.status in ('Finished', 'Cancelled') and l1.status='Initiated'\n", nativeQuery = true)
    void getFinishedDuration();
    
    
    @Transactional
    @Query(value = "select l1.INSTANCE_ID, l1.starttime, l2.status, l1.JOB_NAME, l1.job_id, l1.startdate, l1.dated, l1.event_id, l1.duration,  l1.timetaken,l1.enddate,l1.created_by from logs l1 join logs l2 on l1.INSTANCE_ID=l2.INSTANCE_ID where l1.status='Initiated' \n" +
            "and l2.status in (:status) and l2.startdate >= :startDate and l2.endDate <= :endDate", nativeQuery = true)
    List<UniqLogs> getLogs(@Param("status") String status, @Param("startDate") Date startDate, @Param("endDate") Date endDate);
    
    @Transactional
    @Query(value = "select l1.INSTANCE_ID, l1.starttime, l2.status, l1.JOB_NAME, l1.job_id, l1.startdate, l1.dated, l1.event_id, l1.duration,  l1.timetaken,l1.enddate,l1.created_by from logs l1 join logs l2 on l1.INSTANCE_ID=l2.INSTANCE_ID where l1.status='Initiated' \n" +
            "and l2.status in (:status) and l2.startdate >= :startDate and l2.endDate <= :endDate and l2.created_by = :username", nativeQuery = true)
    List<UniqLogs> getLogs(@Param("status") String status, @Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("username") String username);
    
    @Transactional
    @Query(value = "select l1.INSTANCE_ID, l1.starttime, l1.status, l1.JOB_NAME, l1.job_id, l1.startdate, l1.dated, l1.event_id, l1.duration,l1.timetaken, l1.enddate,  l1.created_by from logs l1 where STARTTIME = (Select min(STARTTIME) from logs l3 where l3.INSTANCE_ID = l1.INSTANCE_ID and l3.STATUS = 'In Progress'  and l3.startdate >= :startDate and l3.endDate <= :endDate)   and INSTANCE_ID NOT IN (select l2.INSTANCE_ID from logs l2 where l2.status in ('Finished', 'Cancelled'))", nativeQuery = true)
    List<UniqLogs> getLogswithInprogres(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
    
    @Transactional
    @Query(value = "select l1.INSTANCE_ID, l1.starttime, l1.status, l1.JOB_NAME, l1.job_id, l1.startdate, l1.dated, l1.event_id, l1.duration,l1.timetaken, l1.enddate,  l1.created_by from logs l1 where STARTTIME = (Select min(STARTTIME) from logs l3 where l3.INSTANCE_ID = l1.INSTANCE_ID and l3.STATUS = 'In Progress'  and l3.startdate >= :startDate and l3.endDate <= :endDate and l3.created_by = :username)   and INSTANCE_ID NOT IN (select l2.INSTANCE_ID from logs l2 where l2.status in ('Finished', 'Cancelled'))", nativeQuery = true)
    List<UniqLogs> getLogswithInprogress(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("username") String username);
    
}