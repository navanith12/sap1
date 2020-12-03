package com.miraclesoft.datalake.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.miraclesoft.datalake.model.FtpServer;

@Repository
public interface FtpServerRepository extends JpaRepository<FtpServer, String> {

	List<FtpServer> findAllByOrderByCreatedAtDesc();

}