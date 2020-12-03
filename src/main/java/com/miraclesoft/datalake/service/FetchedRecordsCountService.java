package com.miraclesoft.datalake.service;

import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.miraclesoft.datalake.model.FetchedRecordsCount;
import com.miraclesoft.datalake.repository.FetchedRecordsCountRepository;

@Service
public class FetchedRecordsCountService {

	@Autowired
	private FetchedRecordsCountRepository fetchedRecordsCountRepository;

	private JSONObject jo;

	public ResponseEntity<Object> add(FetchedRecordsCount fetchedRecordsCount) {
		jo = new JSONObject();
		FetchedRecordsCount frcr = fetchedRecordsCountRepository.save(fetchedRecordsCount);
		jo.put("Status", HttpStatus.OK);
		jo.put("Data", frcr);
		jo.put("Message", "Added Single Record");
		return new ResponseEntity<>(jo.toMap(), HttpStatus.OK);
	}

	public ResponseEntity<Object> findFetchedRecordsCount(long id) {
		jo = new JSONObject();
		FetchedRecordsCount frcr = fetchedRecordsCountRepository.findById(id).get();
		jo.put("Status", HttpStatus.OK);
		jo.put("Data", frcr);
		jo.put("Message", "Fetched Single Record");
		return new ResponseEntity<>(jo.toMap(), HttpStatus.OK);
	}

	public ResponseEntity<Object> update(FetchedRecordsCount fetchedRecordsCount, long id) {
		fetchedRecordsCount.setId(id);
		jo = new JSONObject();
		FetchedRecordsCount frcr = fetchedRecordsCountRepository.save(fetchedRecordsCount);
		jo.put("Status", HttpStatus.OK);
		jo.put("Data", frcr);
		jo.put("Message", "Updated Record");
		return new ResponseEntity<>(jo.toMap(), HttpStatus.OK);
	}

	public ResponseEntity<Object> delete(long id) {
		jo = new JSONObject();
		fetchedRecordsCountRepository.deleteById(id);
		jo.put("Status", HttpStatus.OK);
		jo.put("Data", id);
		jo.put("Message", "Record Deleted");
		return new ResponseEntity<>(jo.toMap(), HttpStatus.OK);

	}

	public ResponseEntity<Object> list() {
		jo = new JSONObject();
		List<FetchedRecordsCount> frcr = fetchedRecordsCountRepository.findAll();
		jo.put("Status", HttpStatus.OK);
		jo.put("Data", frcr);
		jo.put("Message", "Retrieved All data");
		return new ResponseEntity<>(jo.toMap(), HttpStatus.OK);
	}

}
