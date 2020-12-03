package com.miraclesoft.datalake.service;

import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.miraclesoft.datalake.model.DbInsert;
import com.miraclesoft.datalake.model.FtpServer;
import com.miraclesoft.datalake.repository.DbInsertRepository;

@Service
public class DbInsertService {

	@Autowired
	private DbInsertRepository dbInsertRepository;

	private JSONObject jo;
	
	public ResponseEntity<Object> add(DbInsert dbInsert) {
		jo =new JSONObject();
		DbInsert db = dbInsertRepository.save(dbInsert);
		jo.put("Status", HttpStatus.OK);
		jo.put("Data", db);
		jo.put("Message","Added DB object");
		return new ResponseEntity<>(jo.toMap(), HttpStatus.OK);
	}
	
	public List<DbInsert> list_all(){
		return dbInsertRepository.findAllByOrderByCreatedAtDesc();
	}
	

	public ResponseEntity<Object> list() {
		jo = new JSONObject();
		List<DbInsert> db = dbInsertRepository.findAllByOrderByCreatedAtDesc();
		jo.put("Status", HttpStatus.OK);
		jo.put("Data", db);
		jo.put("Message", "List of Db objects");
		return new ResponseEntity<>(jo.toMap(), HttpStatus.OK);
	}

	public ResponseEntity<Object> findDbInsert(String id) {
		DbInsert db = null;
		System.out.println("check value: " + (dbInsertRepository.findById(id)).isPresent());
		if ((dbInsertRepository.findById(id)).isPresent()) {
			db = (dbInsertRepository.findById(id)).get();
		} 
		jo = new JSONObject();
		jo.put("Status", HttpStatus.OK);
		jo.put("Data", db);
		jo.put("Message", "One obejct of db");
		return new ResponseEntity<>(jo.toMap(), HttpStatus.OK);
	}
	
	public DbInsert findDbInsertByid(String id) {
		DbInsert db = null;
		System.out.println("check value: " + (dbInsertRepository.findById(id)).isPresent());
		if ((dbInsertRepository.findById(id)).isPresent()) {
			db = (dbInsertRepository.findById(id)).get();
		} 
		return db;
	}

	public ResponseEntity<Object> update(DbInsert dbInsert, String id) {
		dbInsert.setId(id);
		DbInsert db = dbInsertRepository.save(dbInsert);
		jo = new JSONObject();
		jo.put("Status", HttpStatus.OK);
		jo.put("Data", db);
		jo.put("Message", "Updated Db object");
		return new ResponseEntity<>(jo.toMap(), HttpStatus.OK);
	}

	public ResponseEntity<Object> delete(String id) {
		dbInsertRepository.deleteById(id);
		jo = new JSONObject();
		jo.put("Status", HttpStatus.OK);
		jo.put("Data", id);
		jo.put("Message", "Db object deleted");
		return new ResponseEntity<>(jo.toMap(), HttpStatus.OK);
	}

}
