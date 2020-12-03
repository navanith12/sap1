package com.miraclesoft.datalake.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.miraclesoft.datalake.model.DbInsert;
import com.miraclesoft.datalake.service.DbInsertService;

@RestController
@RequestMapping("/dbInsert")
public class DbInsertController {

	@Autowired
	private DbInsertService dbInsertService;

	@PostMapping("/")
	public ResponseEntity<Object> add(@RequestBody DbInsert dbInsert) {
		return dbInsertService.add(dbInsert);
	}

	@GetMapping("/")
	public ResponseEntity<Object> dbList() {
		return dbInsertService.list();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Object> getDbInsert(@PathVariable String id) {
		return dbInsertService.findDbInsert(id);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Object> update(@RequestBody DbInsert dbInsert, @PathVariable String id) {
		return dbInsertService.update(dbInsert, id);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Object> delete(@PathVariable String id) {
		return dbInsertService.delete(id);
	}

}
