package com.miraclesoft.datalake.service;

import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.miraclesoft.datalake.model.FtpServer;
import com.miraclesoft.datalake.repository.FtpServerRepository;

@Service
public class FtpServerService {

	@Autowired
	private FtpServerRepository ftpServerRepository;

	private JSONObject jo;
	
	public ResponseEntity<Object> add(FtpServer ftpServer) {
		jo =new JSONObject();
		FtpServer fs = ftpServerRepository.save(ftpServer);
		jo.put("Status", HttpStatus.OK);
		jo.put("Data", fs);
		jo.put("Message","Added AzureDB");
		return new ResponseEntity<>(jo.toMap(), HttpStatus.OK);
	}
	
	public ResponseEntity<Object> list() {
			jo =new JSONObject();
			List<FtpServer> fs = ftpServerRepository.findAllByOrderByCreatedAtDesc();
			jo.put("Status", HttpStatus.OK);
			jo.put("Data", fs);
			jo.put("Message","List of AzureDB");
			return new ResponseEntity<>(jo.toMap(), HttpStatus.OK);
	}
	
	public List<FtpServer> list_all(){
		return ftpServerRepository.findAllByOrderByCreatedAtDesc();
	}


	public ResponseEntity<Object> findFtpServer(String id) {
		System.out.println("check value: "+(ftpServerRepository.findById(id)).isPresent());
		FtpServer fs = null;
		if((ftpServerRepository.findById(id)).isPresent()) {
			fs = (ftpServerRepository.findById(id)).get();
		}
		jo =new JSONObject();
		jo.put("Status", HttpStatus.OK);
		jo.put("Data", fs);
		jo.put("Message","Get One AzureDB");
		return new ResponseEntity<>(jo.toMap(), HttpStatus.OK);	
	}
	
	public FtpServer findOneFtpServer(String id) {
		return (ftpServerRepository.findById(id)).get();
	}

	public ResponseEntity<Object> update(FtpServer ftpServer, String id) {
		ftpServer.setId(id);
		FtpServer fs = ftpServerRepository.save(ftpServer);
		jo =new JSONObject();
		jo.put("Status", HttpStatus.OK);
		jo.put("Data", fs);
		jo.put("Message","List of AzureDB");
		return new ResponseEntity<>(jo.toMap(), HttpStatus.OK);
	}

	public ResponseEntity<Object> delete(String id) {
		ftpServerRepository.deleteById(id);
		jo =new JSONObject();
		List<FtpServer> fs = ftpServerRepository.findAllByOrderByCreatedAtDesc();
		jo.put("Status", HttpStatus.OK);
		jo.put("Data", id);
		jo.put("Message","Object Deleted");
		return new ResponseEntity<>(jo.toMap(), HttpStatus.OK);
	}

}
