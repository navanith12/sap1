package com.miraclesoft.datalake.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.miraclesoft.datalake.model.Source;
import com.miraclesoft.datalake.service.SourceService;

@RestController
@RequestMapping("/source")
public class SourceController {

	@Autowired
	private SourceService sourceService;

	@PostMapping("/")
	public ResponseEntity<Object> add(@RequestBody Source source) {
		return sourceService.add(source);
	}

	@GetMapping("/")
	public ResponseEntity<Object> sourceList() {
		return sourceService.list();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Object> getSource(@PathVariable long id) {
		return sourceService.findASource(id);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Object> update(@RequestBody Source source, @PathVariable long id) {
		return sourceService.update(source, id);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Object> delete(@PathVariable long id) {
		return sourceService.delete(id);
	}

}
