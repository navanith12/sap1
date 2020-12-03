package com.miraclesoft.datalake.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
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
import com.miraclesoft.datalake.model.SourceDatabase;
import com.miraclesoft.datalake.service.DbInsertService;
import com.miraclesoft.datalake.service.SourceDBService;

@RestController
@RequestMapping("/sourcedb")
public class SourceDBController {

	@Autowired
	private SourceDBService sourceDBService;
	
	@Autowired
	private DbInsertService dbInsertService;

	@PostMapping("/")
	public ResponseEntity<Object> add(@RequestBody SourceDatabase sourceDatabase) {
		return sourceDBService.add(sourceDatabase);
	}

	@GetMapping("/")
	public ResponseEntity<Object> dbList() {
		return sourceDBService.list();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Object> getDbInsert(@PathVariable String id) {
		return sourceDBService.findDbInsert(id);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Object> update(@RequestBody SourceDatabase sourceDatabase, @PathVariable String id) {
		return sourceDBService.update(sourceDatabase, id);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Object> delete(@PathVariable String id) {
		return sourceDBService.delete(id);
	}
	
	@PostMapping("/gettables")
	public ResponseEntity<Object> getTables(@RequestBody SourceDatabase sourceDatabase) throws SQLException{
		return sourceDBService.getTables(sourceDatabase);
	}
	
	@PostMapping("/getcolumns/{tableName}")
	public ResponseEntity<Object> getColumns(@RequestBody SourceDatabase sourceDatabase, @PathVariable String tablename) throws SQLException{
		return sourceDBService.getColumns(sourceDatabase, tablename);
	}
	
	@PostMapping("/execute")
	public ResponseEntity<InputStreamResource> execute(@RequestBody SourceDatabase sourceDatabase, @PathVariable String tablename, @PathVariable String columns, @PathVariable String targetid) throws IOException{
		DbInsert db = dbInsertService.findDbInsertByid(targetid);
		return sourceDBService.execute(sourceDatabase, tablename, columns, db);
	}

}
