package com.miraclesoft.datalake.service;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.miraclesoft.datalake.jco.JcoSapConnector;
import com.miraclesoft.datalake.model.Extractor;
import com.miraclesoft.datalake.model.Source;
import com.miraclesoft.datalake.mongo.model.SocketResponse;
import com.miraclesoft.datalake.repository.ExtractorRepository;

@Service
public class ExtractorService {
	Logger logger = LoggerFactory.getLogger(ExtractorService.class);
	
	@Autowired
	SimpMessagingTemplate template;
	
	@Autowired
	private ExtractorRepository extractorRepository;
	
	@Autowired
	private SourceService sourceService;
	
	@Autowired
	private DbInsertService dbInsertService;
	
	@Autowired
	private FtpServerService ftpServerService;

	@Autowired
	private JcoSapConnector jcoSapConnector;
	
	@Autowired
	private JobHistoryService jobHistoryService;
	
	JSONObject jo;

	public ResponseEntity<Object> add(Extractor extractor) {
		Extractor ex = null;
		try {
			ex = extractorRepository.save(extractor);
			jo = new JSONObject();
			jo.put("Status", HttpStatus.OK);
			jo.put("Data",ex);
			jo.put("Message", "Success");
			return new ResponseEntity<Object>(jo.toMap(), HttpStatus.OK);
		}
		catch(Exception e) {
			System.out.println("Error duplicate");
			jo = new JSONObject();
			jo.put("Status", HttpStatus.BAD_REQUEST);
			jo.put("Data",ex);
			jo.put("Message", e.getMessage());
			return new ResponseEntity<Object>(jo.toMap(), HttpStatus.BAD_REQUEST);
		}
	}
	
	public List<Extractor> list_all(String role, String username) {
		if("business".equals(role.toLowerCase())) {
			return extractorRepository.findAllBycreatedByOrderByCreatedAtDesc(username);
		}
		else {
			return extractorRepository.findAllByOrderByUpdatedDateDesc();
		}
	}

	public ResponseEntity<Object> list(String category,String role, String username) {
		if ("all".equals(category.toLowerCase())) {
			if ("business".equals(role.toLowerCase())) {
				List<Extractor> list = extractorRepository.findAllBycreatedByOrderByCreatedAtDesc(username);
				JSONArray ja = new JSONArray(list);
				jo = new JSONObject();
				jo.put("Status", HttpStatus.OK);
				jo.put("Data", ja);
				jo.put("Message", "Success");
				System.out.println(jo);
				return new ResponseEntity<>(jo.toMap(), HttpStatus.OK);
			} else {
				List<Extractor> list = extractorRepository.findAllByOrderByUpdatedDateDesc();
				JSONArray ja = new JSONArray(list);
				jo = new JSONObject();
				jo.put("Status", HttpStatus.OK);
				jo.put("Data", ja);
				jo.put("Message", "Success");
				System.out.println(jo);
				return new ResponseEntity<>(jo.toMap(), HttpStatus.OK);
			} 
		}
		else {
			if ("business".equals(role.toLowerCase())) {
				List<Extractor> list = extractorRepository.findAllBycreatedByOrderByCreatedAtDesc(username);
				JSONArray ja = new JSONArray(list);
				jo = new JSONObject();
				jo.put("Status", HttpStatus.OK);
				jo.put("Data", ja);
				jo.put("Message", "Success");
				System.out.println(jo);
				return new ResponseEntity<>(jo.toMap(), HttpStatus.OK);
			} else {
				List<Extractor> list = extractorRepository.findAllByCategoryOrderByUpdatedDateDesc(category);
				JSONArray ja = new JSONArray(list);
				jo = new JSONObject();
				jo.put("Status", HttpStatus.OK);
				jo.put("Data", ja);
				jo.put("Message", "Success");
				System.out.println(jo);
				return new ResponseEntity<>(jo.toMap(), HttpStatus.OK);
			} 
		}
	}

	public ResponseEntity<Object> findExtractor(String id) {
		jo = new JSONObject();
		jo.put("Status", HttpStatus.OK);
		jo.put("Data",(extractorRepository.findById(id)).get());
		jo.put("Message", "Success");
		return new ResponseEntity<Object>(jo.toMap(), HttpStatus.OK);
	}
	
	public Extractor findExtractor_extractor(String id) {
		return  extractorRepository.findById(id).get();
	}
	
	public Extractor findByfunction_extractor(String function) {
		return extractorRepository.findByfunction(function);
	}
	
	public Extractor findByJobName(String jobName) {
		return extractorRepository.findByname(jobName);
	}
	
	public ResponseEntity<Object> findByfunction(String function) {
		Extractor ex = extractorRepository.findByfunction(function);
		jo = new JSONObject();
		jo.put("Status", HttpStatus.OK);
		jo.put("Data",ex);
		jo.put("Message", "Success");
		return new ResponseEntity<Object>(jo.toMap(), HttpStatus.OK);
	}

	public ResponseEntity<Object> update(Extractor extractor, String id) {
		extractor.setId(id);
		extractor.setUpdatedDate(new Date().toString());
		Extractor ex = extractorRepository.save(extractor);
		jo = new JSONObject();
		jo.put("Status", HttpStatus.OK);
		jo.put("Data",ex);
		jo.put("Message", "Success");
		return new ResponseEntity<Object>(jo.toMap(), HttpStatus.OK);
	}

	public ResponseEntity<Object> delete(String id) {
		jo = new JSONObject();
		try{
			jo.put("Status", HttpStatus.OK);
			jo.put("Data",id);
			jo.put("Message", "Successfully Deleted");
			extractorRepository.deleteById(id);
		}
		catch(Exception e) {
			jo.put("Status", HttpStatus.OK);
			jo.put("Data",e.getMessage());
			jo.put("Message", "Cannot deleted the Job");
		}
		return new ResponseEntity<Object>(jo.toMap(), HttpStatus.OK);
	}
	
	public ResponseEntity<Object> extractor_response(Extractor extractor, String username) {
		jo = new JSONObject();
		Source source = sourceService.findSource(extractor.getSourceId());

		try {
			jo.put("Status", HttpStatus.OK);
			jo.put("Data",jcoSapConnector.fullloadlocalftp(extractor,source,username));
			jo.put("Message", "Successfully Deleted");
		} catch (Exception e) {
			jo.put("Status", HttpStatus.OK);
			jo.put("Data",e.getMessage());
			jo.put("Message", "Successfully Deleted");
		}
		return new ResponseEntity<Object>(jo.toMap(), HttpStatus.OK);
	}

	public ResponseEntity<Object> jobinitwodata(Extractor extractor, SocketResponse sr, String username) throws IOException {
		Source source = sourceService.findSource(extractor.getSourceId());
		logger.trace("Found the source@"+extractor.getName()+"!"+extractor.getInstanceid()+"*"+extractor.getFunction()+"%Found the source "+source.getApplicationServer()+" Initiating the Job for Init W/O Data"+"#"+username);
		sr.getStatus().add("Found the source "+source.getApplicationServer()+" Initiating the Job for Init W/O Data"+"#"+username);		
		template.convertAndSend("/topic/user", sr);
		String response = jcoSapConnector.initializewithoutdata(extractor, source,sr,username);
		jo = new JSONObject();
		jo.put("Status", HttpStatus.OK);
		jo.put("Data", response);
		jo.put("Message", "Completed Execution");
		jo.put("Jobname", extractor.getName());
		return new ResponseEntity<Object>(jo.toMap(), HttpStatus.OK);
	}

	public ResponseEntity<InputStreamResource> structure_response(Extractor extractor) throws Exception {
		Source source = sourceService.findSource(extractor.getSourceId());
		return jcoSapConnector.getStructure(extractor.getFunction(), source.getApplicationServer(),source.getPort());
	}

	public ResponseEntity<Object> jobdeltaload(Extractor extractor, SocketResponse sr, String username) throws Exception {
		jo = new JSONObject();
		Source source = sourceService.findSource(extractor.getSourceId());
		logger.trace("Found the source@"+extractor.getName()+"!"+extractor.getInstanceid()+"*"+extractor.getFunction()+"%Found the source "+source.getApplicationServer()+" Initiating the Job for Delta Load"+"#"+username);
		sr.getStatus().add("Found the source "+source.getApplicationServer()+" Initiating the Job for Delta Load");
		template.convertAndSend("/topic/user", sr);
		String result = jcoSapConnector.deltaLoad(extractor, source, sr, username);
		jo.put("Status", HttpStatus.OK);
		jo.put("Data", result);
		jo.put("Message", "Completed Execution");
		jo.put("Jobname", extractor.getName());
		return new ResponseEntity<Object>(jo.toMap(), HttpStatus.OK);
	}

	
	@SuppressWarnings("deprecation")
	public ResponseEntity<Object> jobfullload(Extractor extractor, SocketResponse sr, String username) {
		jo = new JSONObject();
		Source source = sourceService.findSource(extractor.getSourceId());
		try {
			logger.trace("Found the source@"+extractor.getName()+"!"+extractor.getInstanceid()+"*"+extractor.getFunction()+"%Found the source "+source.getApplicationServer()+" Initiating the Job for full load"+"#"+username);
			sr.getStatus().add("Found the source"+source.getApplicationServer()+" Initiating the Job for full load");		
			template.convertAndSend("/topic/user", sr);
			String result = jcoSapConnector.fullload(extractor, source,sr, username);
			jo.put("Status", HttpStatus.OK);
			jo.put("Data", result);
			jo.put("Message", "Completed Execution");
			jo.put("Jobname", extractor.getName());
			return new ResponseEntity<Object>(jo.toMap(), HttpStatus.OK);
		} catch (Exception e) {
			logger.trace("Cancelled@"+extractor.getName()+"!"+extractor.getInstanceid()+"*"+extractor.getFunction()+"% Can`t find the source "+source.getApplicationServer()+"Job Cannot be initiated"+ e.getMessage()+"#"+username);
			sr.getStatus().add("Can`t find the source "+source.getApplicationServer()+"Job Cannot be initiated"+ e.getMessage());
			template.convertAndSend("/topic/user", sr);
			e.printStackTrace();
			jo.put("Status", HttpStatus.OK);
			jo.put("Data", e.getMessage());
			jo.put("Message", "Failed Execution");
			jo.put("Jobname", extractor.getName());
			return new ResponseEntity<Object>(jo.toMap(), HttpStatus.OK);
		}
	}

	public ResponseEntity<Object> jobinitwdata(Extractor extractor, SocketResponse sr, String username) throws Exception {
		jo = new JSONObject();
		Source source = sourceService.findSource(extractor.getSourceId());
		logger.trace("Found the source@"+extractor.getName()+"!"+extractor.getInstanceid()+"*"+extractor.getFunction()+"%Found the source "+source.getApplicationServer()+" Initiating the Job for Init W/ Data"+"#"+username);
		sr.getStatus().add("Found the source"+source.getApplicationServer()+" Initiating the Job for Init W/ Data");		
		template.convertAndSend("/topic/user", sr);
		String response = jcoSapConnector.initializewithdata(extractor, source, sr,username);
		jo.put("Status", HttpStatus.OK);
		jo.put("Data", response);
		jo.put("Message", "Completed Execution");
		jo.put("Jobname", extractor.getName());
		return new ResponseEntity<Object>(jo.toMap(), HttpStatus.OK);
	}

	public ResponseEntity<InputStreamResource> deltalocalftp(Extractor extractor, String username) throws Exception {
		String targetName = extractor.getTargetId();
		Source source = sourceService.findSource(extractor.getSourceId());
		if(null != dbInsertService.findDbInsert(targetName)) {
			
		}
		else if(null != ftpServerService.findFtpServer(targetName)) {
			//FtpServer fs = ftpServerService.findFtpServer(targetid);
			System.out.println("in ftp service");
			extractor.setTargetType("ftp");
			return jcoSapConnector.deltalocalftp(extractor, source,username);
		}
		else {
			//localfile return
			extractor.setTargetType("localfile");
			return jcoSapConnector.deltalocalftp(extractor, source, username);
		}
		return null;
	}

	public ResponseEntity<InputStreamResource> jobftplocalfullload(Extractor extractor, SocketResponse sr, String username) {
		jo = new JSONObject();
		Source source = sourceService.findSource(extractor.getSourceId());
		try {
			logger.trace("Found the source@"+extractor.getName()+"!"+extractor.getInstanceid()+"*"+extractor.getFunction()+"%Found the source "+source.getApplicationServer()+" Initiating the Job for full load"+"#"+username);
			sr.getStatus().add("Found the source"+source.getApplicationServer()+" Initiating the Job for full load");		
			template.convertAndSend("/topic/user", sr);
			ResponseEntity<InputStreamResource> result = jcoSapConnector.fullloadlocalftp(extractor, source, username);
			jo.put("Status", HttpStatus.OK);
			jo.put("Data", result);
			jo.put("Message", "Completed Execution");
			jo.put("Jobname", extractor.getName());
			return result;
		} catch (Exception e) {
			logger.trace("Cancelled@"+extractor.getName()+"!"+extractor.getInstanceid()+"*"+extractor.getFunction()+"% Error: "+ e.getMessage()+"#"+username);
			sr.getStatus().add("Can`t find the source "+source.getApplicationServer()+"Job Cannot be initiated"+ e.getMessage());
			template.convertAndSend("/topic/user", sr);
			e.printStackTrace();
			jo.put("Status", HttpStatus.OK);
			jo.put("Data", e.getMessage());
			jo.put("Message", "Failed Execution");
			jo.put("Jobname", extractor.getName());
		}
		return null;
	}

	public ResponseEntity<Object> preview_data(String extractorName, long sourceId) throws IOException {
		jo = new JSONObject();
		Source source = sourceService.findSource(sourceId);
		JSONArray preview_data = jcoSapConnector.preview_data(extractorName, source);
		jo.put("Status", HttpStatus.OK);
		jo.put("Data", preview_data);
		jo.put("Message", "Completed Execution");
		return new ResponseEntity<Object>(jo.toMap(), HttpStatus.OK);
	}

}
