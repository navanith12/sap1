package com.miraclesoft.datalake.service;

import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.miraclesoft.datalake.jco.JcoSapConnector;
import com.miraclesoft.datalake.model.Source;
import com.miraclesoft.datalake.repository.SourceRepository;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;

@Service
public class SourceService {

	@Autowired
	private SourceRepository sourceRepository;
	
	private JSONObject jo;

	public ResponseEntity<Object> add(Source source) {
		jo =new JSONObject();
		JCoDestination destination = null;
		String response = null;
		boolean check_destination = false;
		try {
			destination = JcoSapConnector.getSourceDestination(source);
			destination.ping();
			check_destination = true;
			if (check_destination) {
				Source sourceTemp = sourceRepository.save(source);
				response = "Source added successfully";
				jo.put("Status", HttpStatus.OK);
				jo.put("Data",sourceTemp);
				jo.put("Message",response);
			} else {
				response = "Invalid credentials";
				jo.put("Status", HttpStatus.BAD_REQUEST);
				jo.put("Data", "null");
				jo.put("Message",response);
			}
		} catch (JCoException e) {
			response = "Invalid credentials";
			jo.put("Status", HttpStatus.BAD_REQUEST);
			jo.put("Data", response);
			jo.put("Message",response);
			System.out.println(e.getMessage());
		}
		return new ResponseEntity<>(jo.toMap(), HttpStatus.OK);
		
	}
	
	public List<Source> list_all(){
		return sourceRepository.findAllByOrderByCreatedAtDesc();
	}

	
	public ResponseEntity<Object> list() {
		jo =new JSONObject();
		jo.put("Status", HttpStatus.OK);
		jo.put("Data", sourceRepository.findAllByOrderByCreatedAtDesc());
		jo.put("Message","List of Sources");
		return new ResponseEntity<>(jo.toMap(), HttpStatus.OK);
	}

	public ResponseEntity<Object> findASource(long id) {
		jo =new JSONObject();
		jo.put("Status", HttpStatus.OK);
		jo.put("Data", (sourceRepository.findById(id)).get());
		jo.put("Message","Source Retrieved");
		return new ResponseEntity<>(jo.toMap(), HttpStatus.OK);
	}
	
	public Source findSource(long id) {
		return (sourceRepository.findById(id).get());
	}

	public ResponseEntity<Object> update(Source source, long id) {
		source.setId(id);
		jo =new JSONObject();
		jo.put("Status", HttpStatus.OK);
		jo.put("Data", sourceRepository.save(source));
		jo.put("Message","Source Updated");
		return new ResponseEntity<>(jo.toMap(), HttpStatus.OK);
	}

	public ResponseEntity<Object> delete(long id) {
		sourceRepository.deleteById(id);
		jo =new JSONObject();
		jo.put("Status", HttpStatus.OK);
		jo.put("Data", id);
		jo.put("Message","Source Deleted");
		return new ResponseEntity<>(jo.toMap(), HttpStatus.OK);
	}

}
