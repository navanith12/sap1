package com.miraclesoft.datalake.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.miraclesoft.datalake.model.Extractor;
import com.miraclesoft.datalake.model.Jobhistory;
import com.miraclesoft.datalake.model.MonitoringInstance;
import com.miraclesoft.datalake.mongo.model.SocketResponse;
import com.miraclesoft.datalake.service.ExtractorService;
import com.miraclesoft.datalake.service.JobHistoryService;
import com.miraclesoft.datalake.service.MonitoringInstanceService;

@RestController
@RequestMapping("/extractor")
public class ExtractorController {
	Logger logger = LoggerFactory.getLogger(ExtractorController.class);
	
	@Autowired
	SimpMessagingTemplate template;
	
	@Autowired
	private ExtractorService extractorService;
	
	@Autowired
	private MonitoringInstanceService monitoringInstanceService;

	SocketResponse sr;
	List<String> socketResponse;

	@PostMapping("/")
	public ResponseEntity<Object> add(@RequestBody Extractor extractor) {		
		return extractorService.add(extractor);
	}

	@GetMapping("/{category}/{role}/{username}")
	public ResponseEntity<Object> extractorList(@PathVariable String category, @PathVariable String role, @PathVariable String username) {
		return extractorService.list(category,role,username);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Object> getExtractor(@PathVariable String id) {
		return extractorService.findExtractor(id);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Object> update(@RequestBody Extractor extractor, @PathVariable String id) {
		extractor.setUpdatedDate(new Date().toString());
		return extractorService.update(extractor, id);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Object> delete(@PathVariable String id) {
		return extractorService.delete(id);
	}

	@PostMapping("/execute/{username}")
	public ResponseEntity<Object> extractor_response(@RequestBody Extractor extractor, String username) throws Exception {
		return extractorService.extractor_response(extractor, username);
	}
	
	@PostMapping("/execute/jobinitwodata/{username}")
	public ResponseEntity<Object> jobinitwodata(@RequestBody Extractor extractor, @PathVariable String username) throws IOException {
		MonitoringInstance mi = new MonitoringInstance(extractor.getName());
		monitoringInstanceService.add(mi);
		extractor.setInstanceid(mi.getInstanceid());
		extractorService.update(extractor, extractor.getId());
		logger.trace("Job Init W/O Data initiated@"+extractor.getName()+"!"+extractor.getInstanceid()+"*"+extractor.getFunction()+"%Job Init W/O Data initiated"+"#"+username);
		socketResponse = new ArrayList<String>();
		socketResponse.add("Job Init W/O Data initiated");
		sr = new SocketResponse(extractor.getName(), socketResponse);
		sr.setJobName(extractor.getName());
		template.convertAndSend("/topic/user", sr);
		return extractorService.jobinitwodata(extractor,sr, username);
	}
	
	@PostMapping("/execute/structure/{username}")
	public ResponseEntity<InputStreamResource> structure_response(@RequestBody Extractor extractor, @PathVariable String username) throws Exception {
		return extractorService.structure_response(extractor);
	}
	
	@GetMapping("/preview/{extractor}/{sourceid}")
	public ResponseEntity<Object> preview_data(@PathVariable String extractor, @PathVariable long sourceid) throws IOException{
		String extractorName = extractor;
		return extractorService.preview_data(extractorName, sourceid);
	}
	
@PostMapping("/execute/deltaload/{username}")
	public ResponseEntity<Object> jobdeltaload(@RequestBody Extractor extractor,@PathVariable String username) throws Exception  {
		MonitoringInstance mi = new MonitoringInstance(extractor.getName());
		monitoringInstanceService.add(mi);
		extractor.setInstanceid(mi.getInstanceid());
		extractorService.update(extractor, extractor.getId());
		logger.trace("Delta Job initiated@"+extractor.getName()+"!"+extractor.getInstanceid()+"*"+extractor.getFunction()+"%Delta Job initiated"+"#"+username);
		socketResponse = new ArrayList<String>();
		socketResponse.add("Delta Job initiated");
		sr = new SocketResponse(extractor.getName(), socketResponse);
		sr.setJobName(extractor.getName());
		sr.setJobStatus("Inititated");
		sr.setJobStatus("In Progress");
		return extractorService.jobdeltaload(extractor,sr,username);
	}
	
	@PostMapping("/execute/fullload/{username}")
	public ResponseEntity<Object> jobfulload(@RequestBody Extractor extractor, @PathVariable String username) throws Exception {
		MonitoringInstance mi = new MonitoringInstance(extractor.getName());
		monitoringInstanceService.add(mi);
		extractor.setInstanceid(mi.getInstanceid());
		extractorService.update(extractor, extractor.getId());
		logger.trace("Initiated@"+extractor.getName()+"!"+extractor.getInstanceid()+"*"+extractor.getFunction()+"%Job Received"+"#"+username);
		socketResponse = new ArrayList<String>();
		socketResponse.add("Job Received");
		sr = new SocketResponse(extractor.getName(), socketResponse);
		sr.setJobStatus("Initiated");
		sr.setJobName(extractor.getName());
		template.convertAndSend("/topic/user", sr);
		logger.trace("In Progress@"+extractor.getName()+"!"+extractor.getInstanceid()+"*"+extractor.getFunction()+"%Extractor Job initiated"+"#"+username);
		sr.getStatus().add("Extractor Job initiated");
		sr.setJobStatus("In Progress");
		template.convertAndSend("/topic/user", sr);
		return extractorService.jobfullload(extractor,sr, username);
	}
	
	@PostMapping("/execute/jobinitwdata/{username}")
	public ResponseEntity<Object> jobinitwdata(@RequestBody Extractor extractor, @PathVariable String username) throws Exception {
		MonitoringInstance mi = new MonitoringInstance(extractor.getName());
		monitoringInstanceService.add(mi);
		extractor.setInstanceid(mi.getInstanceid());
		extractorService.update(extractor, extractor.getId());
		logger.trace("Initiated@"+extractor.getName()+"!"+extractor.getInstanceid()+"*"+extractor.getFunction()+"%Job Init W/ Data Received"+"#"+username);
		logger.trace("In Progress@"+extractor.getName()+"!"+extractor.getInstanceid()+"*"+extractor.getFunction()+"%Job Init W/ Data initiated"+"#"+username);
		socketResponse = new ArrayList<String>();
		socketResponse.add("Job Init W/ Data initiated");
		sr = new SocketResponse(extractor.getName(), socketResponse);
		sr.setJobName(extractor.getName());
		template.convertAndSend("/topic/user", sr);
		return extractorService.jobinitwdata(extractor,sr,username);
	}
	
	@PostMapping("/execute/deltalocalftp/{username}")
	public ResponseEntity<InputStreamResource> deltalocalftp(@RequestBody Extractor extractor, String username) throws Exception {
		System.out.println("testing extractor targetname (should be id ): "+extractor.getTargetId());
		return extractorService.deltalocalftp(extractor, username);
	}	
	
	@PostMapping("/execute/localftpfullload/{username}")
	public ResponseEntity<InputStreamResource> jobftplocalfulload(@RequestBody Extractor extractor, @PathVariable String username) throws Exception {
		MonitoringInstance mi = new MonitoringInstance(extractor.getName());
		monitoringInstanceService.add(mi);
		extractor.setInstanceid(mi.getInstanceid());
		extractorService.update(extractor, extractor.getId());
		logger.trace("Initiated@"+extractor.getName()+"!"+extractor.getInstanceid()+"*"+extractor.getFunction()+"%Job Received"+"#"+username);
		socketResponse = new ArrayList<String>();
		socketResponse.add("Job Received");
		sr = new SocketResponse(extractor.getName(), socketResponse);
		sr.setJobStatus("Initiated");
		sr.setJobName(extractor.getName());
		template.convertAndSend("/topic/user", sr);
		logger.trace("In Progress@"+extractor.getName()+"!"+extractor.getInstanceid()+"*"+extractor.getFunction()+"%Extractor Job initiated"+"#"+username);
		sr.getStatus().add("Extractor Job initiated");
		sr.setJobStatus("In Progress");
		template.convertAndSend("/topic/user", sr);
		return extractorService.jobftplocalfullload(extractor,sr, username);
	}
}
