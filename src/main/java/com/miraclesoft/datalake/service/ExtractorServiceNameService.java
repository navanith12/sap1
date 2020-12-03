package com.miraclesoft.datalake.service;

import java.util.List;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.miraclesoft.datalake.model.ExtractorServiceName;
import com.miraclesoft.datalake.repository.ExtractorServiceNameRepository;


@Service
public class ExtractorServiceNameService {
	
	@Autowired
	private ExtractorServiceNameRepository extractorServiceNameRepository;
	
	private JSONObject jo;
	
	public ResponseEntity<Object> add(ExtractorServiceName extractorServiceName){
		jo = new JSONObject(); 
		ExtractorServiceName esn = extractorServiceNameRepository.save(extractorServiceName);
		jo.put("Status", HttpStatus.OK);
		jo.put("Data",esn);
		jo.put("Message", "Success");
		return new ResponseEntity<Object>(jo.toMap(),HttpStatus.OK);
	}
	
	public ResponseEntity<Object> delete(Long id){
		jo = new JSONObject(); 
		extractorServiceNameRepository.deleteById(id);
		jo.put("Status", HttpStatus.OK);
		jo.put("Data",id);
		jo.put("Message", "Success");
		return new ResponseEntity<Object>(jo.toMap(),HttpStatus.OK);
	}
	
	public ResponseEntity<Object> list(){
		List<ExtractorServiceName> listof = extractorServiceNameRepository.findAll();
		jo = new JSONObject();
		jo.put("Status", HttpStatus.OK);
		jo.put("Data", listof);
		jo.put("Message", "Success");
		return new ResponseEntity<Object>(jo.toMap(),HttpStatus.OK);
	}
	
	public ResponseEntity<Object> update(ExtractorServiceName extractorServiceName, Long id){
		jo = new JSONObject();
		ExtractorServiceName esn;
		try
		{
			extractorServiceName.setId(id);
			esn = extractorServiceNameRepository.save(extractorServiceName);
			jo.put("Status", HttpStatus.OK);
			jo.put("Data", esn);
			jo.put("Message", "Updated");
		}
		catch(Exception e){
			jo.put("Status", HttpStatus.OK);
			jo.put("Data","null");
			jo.put("Message", "Update Failed");
		}
		return new ResponseEntity<Object>(jo.toMap(),HttpStatus.OK);
	}
	
	public ResponseEntity<Object> findExtractorServiceName(Long id){
		jo = new JSONObject();
		Optional<ExtractorServiceName> esn;
		try {
			esn = extractorServiceNameRepository.findById(id);
			jo.put("Status", HttpStatus.OK);
			jo.put("Data", esn);
			jo.put("Message", "Updated");
			return new ResponseEntity<Object>(jo.toMap(), HttpStatus.OK);
		}
		catch(Exception e) {
			jo.put("Status", HttpStatus.OK);
			jo.put("Data", "Failed");
			jo.put("Message", "Update Failed");
			return new ResponseEntity<Object>(jo.toMap(), HttpStatus.OK);
		}
	}
	
	public ExtractorServiceName findbyExtractorName(String name) {
		return extractorServiceNameRepository.findByextractorName(name);
	}

}
