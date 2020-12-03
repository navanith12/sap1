package com.miraclesoft.datalake.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.miraclesoft.datalake.model.LocalFile;
import com.miraclesoft.datalake.repository.LocalFileRepository;

@Service
public class LocalFileService {

	@Autowired
	private LocalFileRepository localFileRepository;

	public LocalFile add(LocalFile localFile) {
		return localFileRepository.save(localFile);
	}

	public List<LocalFile> list() {
		return localFileRepository.findAllByOrderByCreatedAtDesc();
	}

	public LocalFile findLocalFile(String id) {
		return (localFileRepository.findById(id)).get();
	}

	public LocalFile update(LocalFile localFile, String id) {
		localFile.setId(id);
		return localFileRepository.save(localFile);
	}

	public String delete(String id) {
		localFileRepository.deleteById(id);
		return id;
	}

}
