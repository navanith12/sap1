package com.miraclesoft.datalake.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sap.conn.jco.JCoException;
import com.sun.mail.imap.IMAPBodyPart;
import com.miraclesoft.datalake.model.Bapi;
import com.miraclesoft.datalake.model.MonitoringInstance;
import com.miraclesoft.datalake.mongo.model.Bapi_exportparam;
import com.miraclesoft.datalake.mongo.model.ImportParameterModel;
import com.miraclesoft.datalake.mongo.model.SocketResponse;
import com.miraclesoft.datalake.service.BapiService;
import com.miraclesoft.datalake.service.MonitoringInstanceService;

@RestController
@RequestMapping("/bapi")
public class BapiController {

	Logger logger = LoggerFactory.getLogger(BapiController.class);
	
	@Autowired
	SimpMessagingTemplate template;
	
	@Autowired
	private BapiService bapiService;
	
	@Autowired
	private MonitoringInstanceService monitoringInstanceService;
	

	@PostMapping("/")
	public Bapi add(@RequestBody Bapi bapi) {
		return bapiService.add(bapi);
	}

	@GetMapping("/{role}/{username}")
	public List<Bapi> bapiList(@PathVariable String role,  @PathVariable String username) {
		return bapiService.list(role, username);
	}

	@GetMapping("/{id}")
	public Bapi getBapi(@PathVariable String id) {
		return bapiService.findBapi(id);
	}

	@PutMapping("/{id}")
	public Bapi update(@RequestBody Bapi bapi, @PathVariable String id) {
		return bapiService.update(bapi, id);
	}

	@DeleteMapping("/{id}")
	public String delete(@PathVariable String id) {
		return bapiService.delete(id);
	}

	@PostMapping("/execute")
	public ResponseEntity<InputStreamResource> bapi_response(@RequestBody Bapi bapi) throws IOException {
		return bapiService.bapi_response(bapi);
	}
	
	@PostMapping("/importparam")//1
	public List<ImportParameterModel> bapi_getimpparam(@RequestBody Bapi bapi) throws IOException {
		System.out.println(bapi);
		MonitoringInstance mi = new MonitoringInstance(bapi.getName());
		monitoringInstanceService.add(mi);
		bapi.setInstanceid(mi.getInstanceid());
		bapiService.update(bapi, bapi.getId());
		logger.trace("Initiated@"+bapi.getName()+"!"+bapi.getInstanceid()+"*"+bapi.getFunction()+"%Job Received"+"#"+bapi.getCreatedBy());
		logger.trace("In Progress@"+bapi.getName()+"!"+bapi.getInstanceid()+"*"+bapi.getFunction()+"%Bapi Job initiated"+"#"+bapi.getCreatedBy());
		return bapiService.bapi_getimpparam(bapi);
	}
	
	@PostMapping("/executejob/{id}")
	public ResponseEntity<InputStreamResource> bapi_gettables(@RequestBody List<ImportParameterModel> list, @PathVariable String id) throws IOException, JCoException {		
		for(ImportParameterModel i : list) {
			System.out.println(i.getParamName());
		}
		Bapi bapi = bapiService.findBapi(id);
		logger.trace("In Progress@"+bapi.getName()+"!"+bapi.getInstanceid()+"*"+bapi.getFunction()+"%Connecting to source"+"#"+bapi.getCreatedBy());
		return bapiService.bapi_gettables(bapi,list);
	}
	
	@PostMapping("/tableparams")
	public ResponseEntity<Object> bapi_getparams(@RequestBody Bapi bapi) throws IOException {
		logger.trace("In Progress@"+bapi.getName()+"!"+bapi.getInstanceid()+"*"+bapi.getFunction()+"%Received Import Params from User"+"#"+bapi.getCreatedBy());
		return bapiService.bapi_getparams(bapi);
	}
	
	@PostMapping("/exportparams/{id}")
	public ResponseEntity<Object> bapi_getexportparams(@RequestBody List<ImportParameterModel> list, @PathVariable String id) throws IOException, JCoException {
		Bapi bapi = bapiService.findBapi(id);	
		logger.trace("In Progress@"+bapi.getName()+"!"+bapi.getInstanceid()+"*"+bapi.getFunction()+"%Connecting to source"+"#"+bapi.getCreatedBy());
		return bapiService.bapi_getexportparam(bapi,list);
	}

	@PostMapping("/executejob/{id}/{username}/{password}")//3
	public ResponseEntity<Object> bapi_gettables(@RequestBody List<ImportParameterModel> list, @PathVariable String id,@PathVariable String username, @PathVariable String password) throws IOException, JCoException {
		for(ImportParameterModel i : list) {
			System.out.println(i.getParamName());
		}
		Bapi bapi = bapiService.findBapi(id);
		logger.trace("In Progress@"+bapi.getName()+"!"+bapi.getInstanceid()+"*"+bapi.getFunction()+"%Connecting to source"+"#"+bapi.getCreatedBy());
		return bapiService.bapi_gettables(bapi,list,username,password);
	}
	
	@PostMapping("/tableparams/{username}/{password}")//2
	public ResponseEntity<Object> bapi_getparams(@RequestBody Bapi bapi,@PathVariable String username, @PathVariable String password) throws IOException {
		logger.trace("In Progress@"+bapi.getName()+"!"+bapi.getInstanceid()+"*"+bapi.getFunction()+"%Received Import Params from User"+"#"+bapi.getCreatedBy());
		return bapiService.bapi_getparams(bapi,username, password);
	}
	
	@PostMapping("/exportparams/{id}/{username}/{password}")//2
	public ResponseEntity<Object> bapi_getexportparams(@RequestBody List<ImportParameterModel> list, @PathVariable String id,@PathVariable String username, @PathVariable String password) throws IOException, JCoException {
		Bapi bapi = bapiService.findBapi(id);	
		logger.trace("In Progress@"+bapi.getName()+"!"+bapi.getInstanceid()+"*"+bapi.getFunction()+"%Connecting to source"+"#"+bapi.getCreatedBy());	
		return bapiService.bapi_getexportparam(bapi,list,username,password);
	}	
	
	@PostMapping("/cancelled")
	public ResponseEntity<Object> cancel(@RequestBody Bapi bapi) {
		return bapiService.cancel(bapi);
	}
}
