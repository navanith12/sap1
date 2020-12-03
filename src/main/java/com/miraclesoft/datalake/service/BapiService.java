package com.miraclesoft.datalake.service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.miraclesoft.datalake.controller.ExtractorController;
import com.miraclesoft.datalake.jco.JcoSapConnector;
import com.miraclesoft.datalake.model.Bapi;
import com.miraclesoft.datalake.model.Extractor;
import com.miraclesoft.datalake.model.Source;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.miraclesoft.datalake.mongo.model.Bapi_exportparam;
import com.miraclesoft.datalake.mongo.model.ImportParameterModel;
import com.miraclesoft.datalake.mongo.model.SocketResponse;
import com.miraclesoft.datalake.repository.BapiRepository;

@Service
public class BapiService {
	
	Logger logger = LoggerFactory.getLogger(BapiService.class);
	
	@Autowired
	SimpMessagingTemplate template;

	@Autowired
	private BapiRepository bapiRepository;
	
	@Autowired
	private SourceService sourceService;

	@Autowired
	private JcoSapConnector jcoSapConnector;

	public Bapi add(Bapi bapi) {
		return bapiRepository.save(bapi);
	}
	
	JSONObject jo;

	public List<Bapi> list_all(String role, String username) {
		if("business".equals(role.toLowerCase())) {
			return bapiRepository.findAllBycreatedBy(username);
		}
		else {
			return bapiRepository.findAllByOrderByCreatedAtDesc();
		}	
	}
	
	public List<Bapi> list(String role, String username) {
		if("business".equals(role.toLowerCase())) {
			return bapiRepository.findAllBycreatedBy(username);
		}
		else {
			return bapiRepository.findAllByOrderByCreatedAtDesc();
		}		
	}

	public Bapi findBapi(String id) {
		return (bapiRepository.findById(id)).get();
	}
	
	public Bapi findBybapiName(String bapijobName) {
		return bapiRepository.findByname(bapijobName);
	}

	public Bapi update(Bapi bapi, String id) {
		bapi.setId(id);
		bapi.setUpdatedDate(new Date());
		return bapiRepository.save(bapi);
	}

	public String delete(String id) {
		bapiRepository.deleteById(id);
		return id;
	}

	public ResponseEntity<InputStreamResource> bapi_response(Bapi bapi) throws IOException {
		Source source = sourceService.findSource(bapi.getSourceId());
		return jcoSapConnector.bapi_response(bapi,source);
	}

	public List<ImportParameterModel> bapi_getimpparam(Bapi bapi) throws IOException {
		Source source = sourceService.findSource(bapi.getSourceId());
		logger.trace("In Progress@"+bapi.getName()+"!"+bapi.getInstanceid()+"*"+bapi.getFunction()+"%getting import parameters from SAP"+"#"+bapi.getCreatedBy());
		return jcoSapConnector.getResponse_importparams(bapi,source);
	}

	public ResponseEntity<InputStreamResource> bapi_gettables(Bapi bapi, List<ImportParameterModel> list) throws JCoException, IOException {
		jo = new JSONObject();
		Source source = sourceService.findSource(bapi.getSourceId());
		logger.trace("In Progress@"+bapi.getName()+"!"+bapi.getInstanceid()+"*"+bapi.getFunction()+"%Connecting to source"+"#"+bapi.getCreatedBy());
//		try {
//			ResponseEntity<InputStreamResource> result = jcoSapConnector.bapi_gettables(bapi,source, list);
//			jo.put("Status", HttpStatus.OK);
//			jo.put("Data",result);
//			jo.put("Message","Success");
//		}
//		catch(Exception e) {
//			jo.put("Status", HttpStatus.BAD_REQUEST);
//			jo.put("Data",e.getMessage());
//			jo.put("Message","Error");
//		}
		return jcoSapConnector.bapi_gettables(bapi,source, list);		
	}

	public  ResponseEntity<Object> bapi_getparams(Bapi bapi) throws IOException {
		jo = new JSONObject();
		Source source = sourceService.findSource(bapi.getSourceId());
		logger.trace("In Progress@"+bapi.getName()+"!"+bapi.getInstanceid()+"*"+bapi.getFunction()+"%Connecting to source with "+source.getLogin()+" username"+"#"+bapi.getCreatedBy());
		try {
			List<String> result = jcoSapConnector.getparams(bapi,source);
			jo.put("Status", HttpStatus.OK);
			jo.put("Data",result);
			jo.put("Message","Success");
			jo.put("Jobname", bapi.getName());
		}
		catch(Exception e) {
			jo.put("Status", HttpStatus.BAD_REQUEST);
			jo.put("Data",e.getMessage());
			jo.put("Message","Error");
			jo.put("Jobname", bapi.getName());
		}
		return new ResponseEntity<>(jo.toMap(), HttpStatus.OK);	
	}

	public ResponseEntity<Object> bapi_getexportparam(Bapi bapi,  List<ImportParameterModel> list) throws IOException, JCoException {		
		jo = new JSONObject();
		Source source = sourceService.findSource(bapi.getSourceId());
		logger.trace("In Progress@"+bapi.getName()+"!"+bapi.getInstanceid()+"*"+bapi.getFunction()+"%Connecting to source"+"#"+bapi.getCreatedBy());
		try {
			List<Bapi_exportparam> result = jcoSapConnector.get_exportparams(bapi, source, list);
			jo.put("Status", HttpStatus.OK);
			jo.put("Data",result);
			jo.put("Message","Success");
			jo.put("Jobname", bapi.getName());
		}
		catch(Exception e) {
			jo.put("Status", HttpStatus.BAD_REQUEST);
			jo.put("Data",e.getMessage());
			jo.put("Message","Error");
			jo.put("Jobname", bapi.getName());
		}
		return new ResponseEntity<>(jo.toMap(), HttpStatus.OK);	
	}
	
	public ResponseEntity<Object> bapi_gettables(Bapi bapi, List<ImportParameterModel> list, String username, String password) throws JCoException, IOException {	
		jo = new JSONObject();
		Source source = sourceService.findSource(bapi.getSourceId());
		source.setLogin(username);
		source.setPassword(password);
		JCoDestination dest = null;
		try {
			dest = JcoSapConnector.getSourceDestination(source);
			dest.ping();
			logger.trace("In Progress@"+bapi.getName()+"!"+bapi.getInstanceid()+"*"+bapi.getFunction()+"%Connecting to source"+"#"+bapi.getCreatedBy());
			ResponseEntity<InputStreamResource> result = jcoSapConnector.bapi_gettables(bapi,source, list);
			jo.put("Status", HttpStatus.OK);
			jo.put("Data",result);
			jo.put("Message","Success");
			jo.put("Jobname", bapi.getName());
			
		}
		catch(JCoException e){
			jo.put("Status", HttpStatus.BAD_REQUEST);
			jo.put("Data","Invalid Credentials");
			jo.put("Message",e.getMessage());
			jo.put("Jobname", bapi.getName());
		}
		System.out.println(source.getLogin()+" "+source.getPassword());
		return new ResponseEntity<>(jo.toMap(), HttpStatus.OK);		
	}

	public ResponseEntity<Object> bapi_getparams(Bapi bapi, String username, String password) throws IOException {		
		jo = new JSONObject();
		Source source = sourceService.findSource(bapi.getSourceId());
		source.setLogin(username);
		source.setPassword(password);
		JCoDestination dest = null;
		try {
			dest = JcoSapConnector.getSourceDestination(source);
			dest.ping();
			logger.trace("In Progress@"+bapi.getName()+"!"+bapi.getInstanceid()+"*"+bapi.getFunction()+"%Connecting to source with "+source.getLogin()+" username"+"#"+bapi.getCreatedBy());
			List<String> result = jcoSapConnector.getparams(bapi,source);
			jo.put("Status", HttpStatus.OK);
			jo.put("Data",result);
			jo.put("Message","Success");
			jo.put("Jobname", bapi.getName());
			
		}
		catch(JCoException e){
			logger.trace("In Progress@"+bapi.getName()+"!"+bapi.getInstanceid()+"*"+bapi.getFunction()+"%Connecting to source"+"#"+bapi.getCreatedBy());
			jo.put("Status", HttpStatus.BAD_REQUEST);
			jo.put("Data","Invalid Credentials");
			jo.put("Message",e.getMessage());
			jo.put("Jobname", bapi.getName());
		}
//		System.out.println(source.getLogin()+" "+source.getPassword());
		return new ResponseEntity<>(jo.toMap(), HttpStatus.OK);
	}

	public ResponseEntity<Object> bapi_getexportparam(Bapi bapi,  List<ImportParameterModel> list, String username, String password) throws IOException, JCoException {
		jo = new JSONObject();
		Source source = sourceService.findSource(bapi.getSourceId());
		source.setLogin(username);
		source.setPassword(password);
		JCoDestination dest = null;
		try {
			dest = JcoSapConnector.getSourceDestination(source);
			dest.ping();
			logger.trace("In Progress@"+bapi.getName()+"!"+bapi.getInstanceid()+"*"+bapi.getFunction()+"%Connecting to source"+"#"+bapi.getCreatedBy());
			List<Bapi_exportparam> result = jcoSapConnector.get_exportparams(bapi, source, list);
			jo.put("Status", HttpStatus.OK);
			jo.put("Data",result);
			jo.put("Message","Success");
			jo.put("Jobname", bapi.getName());
		}
		catch(JCoException e){
			logger.trace("In Progress@"+bapi.getName()+"!"+bapi.getInstanceid()+"*"+bapi.getFunction()+"%Connecting to source Failed"+"#"+bapi.getCreatedBy());
			jo.put("Status", HttpStatus.BAD_REQUEST);
			jo.put("Data","Invalid Credentials");
			jo.put("Message",e.getMessage());
			jo.put("Jobname", bapi.getName());
		}
		System.out.println(source.getLogin()+" "+source.getPassword());
		return new ResponseEntity<>(jo.toMap(), HttpStatus.OK);
	}

	public ResponseEntity<Object> cancel(Bapi bapi) {
		jo = new JSONObject();
		jo.put("Status", HttpStatus.OK);
		jo.put("Data","Cancelled");
		jo.put("Message","Cancelled");
		bapi.getInstanceid();
		logger.trace("Cancelled@"+bapi.getName()+"!"+bapi.getInstanceid()+"*"+bapi.getFunction()+"%Job Cancelled by USER"+"#"+bapi.getCreatedBy());
		return new ResponseEntity<>(jo.toMap(), HttpStatus.OK);		
	}
}
