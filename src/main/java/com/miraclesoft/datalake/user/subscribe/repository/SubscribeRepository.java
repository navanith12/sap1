package com.miraclesoft.datalake.user.subscribe.repository;

import com.miraclesoft.datalake.user.subscribe.model.SubscribeModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface SubscribeRepository extends JpaRepository<SubscribeModel, Long> {

    @Transactional
    @Query(value= "Select * from subscribe s where s.job_name = :job_name", nativeQuery = true)
    List<SubscribeModel> getAll(@Param("job_name")String job_name);

}