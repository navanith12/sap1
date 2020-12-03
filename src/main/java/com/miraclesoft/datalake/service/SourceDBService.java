package com.miraclesoft.datalake.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.miraclesoft.datalake.jco.JcoSapConnector;
import com.miraclesoft.datalake.model.DbInsert;
import com.miraclesoft.datalake.model.SourceDatabase;
import com.miraclesoft.datalake.repository.SourceDBRepository;

@Service
public class SourceDBService {

	@Autowired
	private SourceDBRepository sourceDBRepository;
	
	@Autowired
	private JcoSapConnector jcoSapConnector;

	private JSONObject jo;
	
	public ResponseEntity<Object> add(SourceDatabase sourceDatabase) {
		jo =new JSONObject();
		SourceDatabase db = sourceDBRepository.save(sourceDatabase);
		jo.put("Status", HttpStatus.OK);
		jo.put("Data", db);
		jo.put("Message","Added DB object");
		return new ResponseEntity<>(jo.toMap(), HttpStatus.OK);
	}
	
	public List<SourceDatabase> list_all(){
		return sourceDBRepository.findAllByOrderByCreatedAtDesc();
	}
	

	public ResponseEntity<Object> list() {
		jo = new JSONObject();
		List<SourceDatabase> db = sourceDBRepository.findAllByOrderByCreatedAtDesc();
		jo.put("Status", HttpStatus.OK);
		jo.put("Data", db);
		jo.put("Message", "List of Db objects");
		return new ResponseEntity<>(jo.toMap(), HttpStatus.OK);
	}

	public ResponseEntity<Object> findDbInsert(String id) {
		SourceDatabase db = null;
		System.out.println("check value: " + (sourceDBRepository.findById(id)).isPresent());
		if ((sourceDBRepository.findById(id)).isPresent()) {
			db = (sourceDBRepository.findById(id)).get();
		} 
		jo = new JSONObject();
		jo.put("Status", HttpStatus.OK);
		jo.put("Data", db);
		jo.put("Message", "One obejct of db");
		return new ResponseEntity<>(jo.toMap(), HttpStatus.OK);
	}
	
	public SourceDatabase findDbInsertByid(String id) {
		SourceDatabase db = null;
		System.out.println("check value: " + (sourceDBRepository.findById(id)).isPresent());
		if ((sourceDBRepository.findById(id)).isPresent()) {
			db = (sourceDBRepository.findById(id)).get();
		} 
		return db;
	}

	public ResponseEntity<Object> update(SourceDatabase sourceDatabase, String id) {
		sourceDatabase.setId(id);
		SourceDatabase db = sourceDBRepository.save(sourceDatabase);
		jo = new JSONObject();
		jo.put("Status", HttpStatus.OK);
		jo.put("Data", db);
		jo.put("Message", "Updated sourceDatabase object");
		return new ResponseEntity<>(jo.toMap(), HttpStatus.OK);
	}

	public ResponseEntity<Object> delete(String id) {
		sourceDBRepository.deleteById(id);
		jo = new JSONObject();
		jo.put("Status", HttpStatus.OK);
		jo.put("Data", id);
		jo.put("Message", "sourceDatabase object deleted");
		return new ResponseEntity<>(jo.toMap(), HttpStatus.OK);
	}

	public ResponseEntity<Object> getTables(SourceDatabase sourceDatabase) throws SQLException {
		// TODO Auto-generated method stub
		List<String> listofTables = jcoSapConnector.getTablesFromSourceDb(sourceDatabase);
		jo = new JSONObject();
		jo.put("Status", HttpStatus.OK);
		jo.put("Data", listofTables);
		jo.put("Message", "List of Tables");
		return new ResponseEntity<>(jo.toMap(), HttpStatus.OK);
	}

	public ResponseEntity<Object> getColumns(SourceDatabase sourceDatabase, String tablename) throws SQLException {
		List<String> listofColumns = jcoSapConnector.getColumnsFromTable(sourceDatabase, tablename);
		jo = new JSONObject();
		jo.put("Status", HttpStatus.OK);
		jo.put("Data", listofColumns);
		jo.put("Message", "List of Columns");
		return new ResponseEntity<>(jo.toMap(), HttpStatus.OK);
	}

	public ResponseEntity<InputStreamResource> execute(SourceDatabase sourceDatabase, String tablename, String columns, DbInsert db) throws IOException {
		ResponseEntity<InputStreamResource> result = jcoSapConnector.executeDb(sourceDatabase, tablename, columns, db);
		return result;
	}

}
