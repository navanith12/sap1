package com.miraclesoft.datalake.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.miraclesoft.datalake.model.FetchedRecordsCount;
import com.miraclesoft.datalake.model.FtpServer;
import com.miraclesoft.datalake.service.FetchedRecordsCountService;

@RestController
@RequestMapping("/datareconcilation")
public class FetchedRecordsCountController {

	@Autowired
	private FetchedRecordsCountService fetchedRecordsCountService;

	@GetMapping("/")
	public ResponseEntity<Object> list() {
		return fetchedRecordsCountService.list();
	}

}
