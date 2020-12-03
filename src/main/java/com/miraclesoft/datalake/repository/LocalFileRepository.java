package com.miraclesoft.datalake.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.miraclesoft.datalake.model.LocalFile;

@Repository
public interface LocalFileRepository extends MongoRepository<LocalFile, String> {

	List<LocalFile> findAllByOrderByCreatedAtDesc();

}