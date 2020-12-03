package com.miraclesoft.datalake.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.miraclesoft.datalake.jco.JcoSapConnector;
import com.miraclesoft.datalake.model.DbInsert;
import com.miraclesoft.datalake.model.Source;
import com.miraclesoft.datalake.model.Table;
import com.miraclesoft.datalake.model.table_filter;
import com.miraclesoft.datalake.mongo.model.SocketResponse;
import com.miraclesoft.datalake.repository.TableFiltersRepository;
import com.miraclesoft.datalake.repository.TableRepository;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;

@Service
public class TableService {
	
	Logger logger = LoggerFactory.getLogger(ExtractorService.class);

	@Autowired
	private TableRepository tableRepository;

	@Autowired
	SimpMessagingTemplate template;
	
	@Autowired
	private JcoSapConnector jcoSapConnector;

	@Autowired
	private SourceService sourceService;

	@Autowired
	private DbInsertService dbInsertService;
	
	@Autowired
	private TableFiltersRepository tableFiltersRepository;
	
	JSONObject jo;

	public Table add(Table table) {
		Set<table_filter> tablefilters = new HashSet<>();
		System.out.println(table.getFilteroptions().size());
		if(table.getFilteroptions().size() == 0) {
			table.setFilteroptions(null);
		}		
		else {
			for(table_filter tf: table.getFilteroptions()) {
				tablefilters.add(tableFiltersRepository.save(tf));
			}
			table.setFilteroptions(tablefilters);
		}
		return tableRepository.save(table);
	}
	
	public List<Table> list_all(String role, String username) {
		if ("business".equals(role.toLowerCase())) {
			return tableRepository.findAllBycreatedBy(username);
		} else {
			return tableRepository.findAllByOrderByUpdatedDateDesc();
		}
	}

	public List<Table> list(String category,String role, String username) {
		if ("all".equals(category.toLowerCase())) {
			if ("business".equals(role.toLowerCase())) {
				return tableRepository.findAllBycreatedBy(username);
			} else {
				return tableRepository.findAllByOrderByUpdatedDateDesc();
			} 
		}
		else {
			if ("business".equals(role.toLowerCase())) {
				return tableRepository.findAllBycreatedBy(username);
			} else {
				return tableRepository.findAllByCategoryOrderByUpdatedDateDesc(category);
			} 
		}
	}

	public Table findTable(String id) {
		return (tableRepository.findById(id)).get();
	}

	public Table update(Table table, String id) {
		table.setId(id);
		return tableRepository.save(table);
	}

	public String delete(String id) {
		tableRepository.deleteById(id);
		return id;
	}
	
	public Table getTablebyName(String name) {
		return tableRepository.findByname(name);
	}

	public List<String> tables_list(long sourceid) throws JCoException {
		System.out.println(sourceid);
		Source source = sourceService.findSource(sourceid);
		return jcoSapConnector.table_names(source);
	}

	public List<String> column_list(String table, long sourceid) throws JCoException {
		Source source = sourceService.findSource(sourceid);
		return jcoSapConnector.table_columns(table, source);
	}

	public ResponseEntity<InputStreamResource> localFile_response(Table table) throws IOException {
//		DbInsert dbInsert = dbInsertService.findDbInsert(table.getTargetId());
		Source source = sourceService.findSource(table.getSourceId());
		return jcoSapConnector.localFile_response(source, table);
	}

	public String mongoDb_response(Table table) throws IOException {
//		DbInsert dbInsert = dbInsertService.findDbInsert(table.getTargetId());
		Source source = sourceService.findSource(table.getSourceId());
		return jcoSapConnector.mongoDb_response(source, table);
	}

	public ResponseEntity<Object> db_insert(Table table, SocketResponse sr) throws IOException, SQLException {
		jo = new JSONObject();
		Source source = sourceService.findSource(table.getSourceId());
		try {
			logger.trace("Found the source@"+table.getName()+"!"+table.getInstanceid()+"*"+table.getTable()+"%Found the source "+source.getApplicationServer()+" Initiating the Job for Table load"+"#"+table.getCreatedBy());
			sr.getStatus().add("Found the source"+source.getApplicationServer()+" Initiating the Job for full load");		
			template.convertAndSend("/topic/user", sr);
			String result = jcoSapConnector.db_insert(source, table,sr);
			jo.put("Status", HttpStatus.OK);
			jo.put("Data", result);
			jo.put("Message", "Completed Execution");
			jo.put("Jobname", table.getName());
			return new ResponseEntity<Object>(jo.toMap(), HttpStatus.OK);
		} catch (Exception e) {
			logger.trace("Cancelled@"+table.getName()+"!"+table.getInstanceid()+"*"+table.getTable()+"% Can`t find the source "+source.getApplicationServer()+"Job Cannot be initiated"+ e.getMessage()+"#"+table.getCreatedBy());
			sr.getStatus().add("Can`t find the source "+source.getApplicationServer()+"Job Cannot be initiated"+ e.getMessage());
			template.convertAndSend("/topic/user", sr);
			jo.put("Status", HttpStatus.OK);
			jo.put("Data", e.getMessage());
			jo.put("Message", "Failed Execution");
			jo.put("Jobname", table.getName());
			return new ResponseEntity<Object>(jo.toMap(), HttpStatus.OK);
		}
	}
	
	public ResponseEntity<Object> db_insert(Table table, SocketResponse sr, String username, String password) throws IOException, SQLException {
		jo = new JSONObject();
		Source source = sourceService.findSource(table.getSourceId());
		source.setLogin(username);
		source.setPassword(password);
		JCoDestination dest = null;
		try {
			dest = JcoSapConnector.getSourceDestination(source);
			dest.ping();			
			logger.trace("Found the source@"+table.getName()+"!"+table.getInstanceid()+"*"+table.getTable()+"%Found the source "+source.getApplicationServer()+" Initiating the Job for Table load");
			sr.getStatus().add("Found the source"+source.getApplicationServer()+" Initiating the Job for Table");		
			template.convertAndSend("/topic/user", sr);
			String result = jcoSapConnector.db_insert(source, table,sr);
			jo.put("Status", HttpStatus.OK);
			jo.put("Data", result);
			jo.put("Message", "Completed Execution");
			jo.put("Jobname", table.getName());
		}
		catch(JCoException e){
			logger.trace("Cancelled@"+table.getName()+"!"+table.getInstanceid()+"*"+table.getTable()+"% Can`t find the source "+source.getApplicationServer()+"Job Cannot be initiated"+ e.getMessage());
			sr.getStatus().add("Can`t find the source "+source.getApplicationServer()+"Job Cannot be initiated"+ e.getMessage());
			template.convertAndSend("/topic/user", sr);
			jo.put("Status", HttpStatus.BAD_REQUEST);
			jo.put("Data", "Invalid Credentials");
			jo.put("Message", "Failed Execution "+e.getMessage());
			jo.put("Jobname", table.getName());
		}
		System.out.println(source.getLogin()+" "+source.getPassword());
		return new ResponseEntity<Object>(jo.toMap(), HttpStatus.OK);
	}

	public ResponseEntity<Object> userCheck(Table table, String username, String password) {
		jo = new JSONObject();
		Source source = sourceService.findSource(table.getSourceId());
		source.setLogin(username);
		source.setPassword(password);
		JCoDestination dest = null;
		try {
			dest = JcoSapConnector.getSourceDestination(source);
			dest.ping();			
//			logger.trace("Found the source@"+table.getName()+"!"+table.getInstanceid()+"*"+table.getTable()+"%Found the source "+source.getApplicationServer()+" Initiating the Job for Table load");
//			sr.getStatus().add("Found the source"+source.getApplicationServer()+" Initiating the Job for full load");		
//			template.convertAndSend("/topic/user", sr);
//			ResponseEntity<InputStreamResource> result = jcoSapConnector.db_insert(source, table);
			jo.put("Status", HttpStatus.OK);
			jo.put("Data", "Success");
			jo.put("Message", "Valid credentials Execution");	
			jo.put("Jobname", table.getName());
		}
		catch(JCoException e){
//			logger.trace("Cancelled@"+table.getName()+"!"+table.getInstanceid()+"*"+table.getTable()+"% Can`t find the source "+source.getApplicationServer()+"Job Cannot be initiated"+ e.getMessage());
//			sr.getStatus().add("Can`t find the source "+source.getApplicationServer()+"Job Cannot be initiated"+ e.getMessage());
//			template.convertAndSend("/topic/user", sr);
			jo.put("Status", HttpStatus.BAD_REQUEST);
			jo.put("Data", "Invalid Credentials");
			jo.put("Message", "Invalid Credentials");
			jo.put("Jobname", table.getName());
		}
		System.out.println(source.getLogin()+" "+source.getPassword());
		return new ResponseEntity<Object>(jo.toMap(), HttpStatus.OK);
	}

	public ResponseEntity<Object> preview_data(String tableName, String columns, long sourceid) {
		jo = new JSONObject();
		Source source = sourceService.findSource(sourceid);
		JSONArray result = jcoSapConnector.preview_table(source, tableName, columns);
		jo.put("Status", HttpStatus.OK);
		jo.put("Data", result);
		jo.put("Message", "Data retreived");
		return new ResponseEntity<Object>(jo.toMap(), HttpStatus.OK);
	}

	public ResponseEntity<Object> azure_response(Table table, SocketResponse sr) {
		jo = new JSONObject();
		Source source = sourceService.findSource(table.getSourceId());
		try {
			logger.trace("Found the source@"+table.getName()+"!"+table.getInstanceid()+"*"+table.getTable()+"%Found the source "+source.getApplicationServer()+" Initiating the Job for Table load"+"#"+table.getCreatedBy());
			sr.getStatus().add("Found the source"+source.getApplicationServer()+" Initiating the Job for full load");		
			template.convertAndSend("/topic/user", sr);
			String result = jcoSapConnector.azuredb_response(source, table, sr);
			jo.put("Status", HttpStatus.OK);
			jo.put("Data", result);
			jo.put("Message", "Completed Execution");
			jo.put("Jobname", table.getName());
			return new ResponseEntity<Object>(jo.toMap(), HttpStatus.OK);
		} catch (Exception e) {
			logger.trace("Cancelled@"+table.getName()+"!"+table.getInstanceid()+"*"+table.getTable()+"% Can`t find the source "+source.getApplicationServer()+"Job Cannot be initiated"+ e.getMessage()+"#"+table.getCreatedBy());
			sr.getStatus().add("Can`t find the source "+source.getApplicationServer()+"Job Cannot be initiated"+ e.getMessage());
			template.convertAndSend("/topic/user", sr);
			jo.put("Status", HttpStatus.OK);
			jo.put("Data", e.getMessage());
			jo.put("Message", "Failed Execution");
			jo.put("Jobname", table.getName());
			return new ResponseEntity<Object>(jo.toMap(), HttpStatus.OK);
		}
	}

	public ResponseEntity<Object> azure_response(Table table, SocketResponse sr, String username, String password) throws IOException {
		jo = new JSONObject();
		Source source = sourceService.findSource(table.getSourceId());
		source.setLogin(username);
		source.setPassword(password);
		JCoDestination dest = null;
		try {
			dest = JcoSapConnector.getSourceDestination(source);
			dest.ping();			
			logger.trace("Found the source@"+table.getName()+"!"+table.getInstanceid()+"*"+table.getTable()+"%Found the source "+source.getApplicationServer()+" Initiating the Job for Table load");
			sr.getStatus().add("Found the source"+source.getApplicationServer()+" Initiating the Job for Table");		
			template.convertAndSend("/topic/user", sr);
			String result = jcoSapConnector.azuredb_response(source, table,sr);
			jo.put("Status", HttpStatus.OK);
			jo.put("Data", result);
			jo.put("Message", "Completed Execution");
			jo.put("Jobname", table.getName());
		}
		catch(JCoException e){
			logger.trace("Cancelled@"+table.getName()+"!"+table.getInstanceid()+"*"+table.getTable()+"% Can`t find the source "+source.getApplicationServer()+"Job Cannot be initiated"+ e.getMessage());
			sr.getStatus().add("Can`t find the source "+source.getApplicationServer()+"Job Cannot be initiated"+ e.getMessage());
			template.convertAndSend("/topic/user", sr);
			jo.put("Status", HttpStatus.BAD_REQUEST);
			jo.put("Data", "Invalid Credentials");
			jo.put("Message", "Failed Execution "+e.getMessage());
			jo.put("Jobname", table.getName());
		}
		System.out.println(source.getLogin()+" "+source.getPassword());
		return new ResponseEntity<Object>(jo.toMap(), HttpStatus.OK);
	}
}
