package com.miraclesoft.datalake.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

import com.miraclesoft.datalake.model.MonitoringInstance;
import com.miraclesoft.datalake.model.Table;
import com.miraclesoft.datalake.mongo.model.SocketResponse;
import com.miraclesoft.datalake.service.MonitoringInstanceService;
import com.miraclesoft.datalake.service.SourceService;
import com.miraclesoft.datalake.service.TableService;
import com.sap.conn.jco.JCoException;

@RestController
@RequestMapping("/table")
public class TableController {
	
	Logger logger = LoggerFactory.getLogger(ExtractorController.class);

	@Autowired
	private TableService tableService;
	
	@Autowired
	SimpMessagingTemplate template;
	
	@Autowired
	private MonitoringInstanceService monitoringInstanceService;

	@PostMapping("/")
	public Table add(@RequestBody Table table) {
		return tableService.add(table);
	}

	@GetMapping("/{category}/{role}/{username}")
	public List<Table> tableList(@PathVariable String category,@PathVariable String role, @PathVariable String username) {
		return tableService.list(category,role, username);
	}

	@GetMapping("/{id}")
	public Table getTable(@PathVariable String id) {
		return tableService.findTable(id);
	}

	@PutMapping("/{id}")
	public Table update(@RequestBody Table table, @PathVariable String id) {
		return tableService.update(table, id);
	}

	@DeleteMapping("/{id}")
	public String delete(@PathVariable String id) {
		return tableService.delete(id);
	}

	@GetMapping("/tables/{sourceid}")
	public List<String> tables_list(@PathVariable long sourceid) throws JCoException {
		return tableService.tables_list(sourceid);
	}

	@GetMapping("/{table}/columns/{sourceid}")
	public List<String> column_list(@PathVariable String table, @PathVariable long sourceid) throws JCoException {
		return tableService.column_list(table,sourceid);
	}

	@PostMapping("/local")
	public ResponseEntity<InputStreamResource> localFile_response(@RequestBody Table table) throws IOException {
		return tableService.localFile_response(table);
	}
	
	@PostMapping("/mongodb")
	public String mongoDb_response(@RequestBody Table table) throws IOException {
		return tableService.mongoDb_response(table);
	}
	
	@GetMapping("/preview/{tableName}/{columns}/{sourceid}")
	public ResponseEntity<Object> preview_data(@PathVariable String tableName, @PathVariable String columns, @PathVariable long sourceid) throws IOException {
		return tableService.preview_data(tableName,columns, sourceid);
	}
	
	@PostMapping("/mssql")
	public ResponseEntity<Object> db_insert(@RequestBody Table table) throws IOException, SQLException {
		MonitoringInstance mi = new MonitoringInstance(table.getName());
		monitoringInstanceService.add(mi);
		table.setInstanceid(mi.getInstanceid());
		tableService.update(table, table.getId());
		logger.trace("Initiated@"+table.getName()+"!"+table.getInstanceid()+"*"+table.getTable()+"%Job Recieved"+"#"+table.getCreatedBy());
		List<String> socketResponse = new ArrayList<String>();
		socketResponse.add("Job Recieved");
		SocketResponse sr = new SocketResponse(table.getName(), socketResponse);
		template.convertAndSend("/topic/user", sr);
		logger.trace("In Progress@"+table.getName()+"!"+table.getInstanceid()+"*"+table.getTable()+"%Table Job initiated"+"#"+table.getCreatedBy());
		sr.getStatus().add("Table Job initiated");
		sr.setJobName(table.getName());
		template.convertAndSend("/topic/user", sr);
		return tableService.db_insert(table,sr);
	}
	
	@PostMapping("/azuredb")
	public ResponseEntity<Object> azure_response(@RequestBody Table table) throws IOException, SQLException {
		MonitoringInstance mi = new MonitoringInstance(table.getName());
		monitoringInstanceService.add(mi);
		table.setInstanceid(mi.getInstanceid());
		tableService.update(table, table.getId());
		logger.trace("Initiated@"+table.getName()+"!"+table.getInstanceid()+"*"+table.getTable()+"%Job Recieved"+"#"+table.getCreatedBy());
		List<String> socketResponse = new ArrayList<String>();
		socketResponse.add("Job Recieved");
		SocketResponse sr = new SocketResponse(table.getName(), socketResponse);
		template.convertAndSend("/topic/user", sr);
		logger.trace("In Progress@"+table.getName()+"!"+table.getInstanceid()+"*"+table.getTable()+"%Table Job initiated"+"#"+table.getCreatedBy());
		sr.getStatus().add("Table Job initiated");
		sr.setJobName(table.getName());
		template.convertAndSend("/topic/user", sr);
		return tableService.azure_response(table,sr);
	}
	
	@PostMapping("/{username}/{password}")
	public ResponseEntity<Object> userCheck(@RequestBody Table table, @PathVariable String username, @PathVariable String password) {
		return tableService.userCheck(table,username, password);
	}
	
	@PostMapping("/mssql/{username}/{password}")
	public ResponseEntity<Object> db_insert(@RequestBody Table table, @PathVariable String username, @PathVariable String password) throws IOException, SQLException {
		MonitoringInstance mi = new MonitoringInstance(table.getName());
		monitoringInstanceService.add(mi);
		table.setInstanceid(mi.getInstanceid());
		tableService.update(table, table.getId());
		logger.trace("Initiated@"+table.getName()+"!"+table.getInstanceid()+"*"+table.getTable()+"%Job Recieved");
		List<String> socketResponse = new ArrayList<String>();
		socketResponse.add("Job Recieved");
		SocketResponse sr = new SocketResponse(table.getName(), socketResponse);
		template.convertAndSend("/topic/user", sr);
		logger.trace("In Progress@"+table.getName()+"!"+table.getInstanceid()+"*"+table.getTable()+"%Table Job initiated");
		sr.getStatus().add("Table Job initiated");
		template.convertAndSend("/topic/user", sr);
		return tableService.db_insert(table,sr,username,password);
	}
	
	@PostMapping("/azuredb/{username}/{password}")
	public ResponseEntity<Object> azure_responsea(@RequestBody Table table, @PathVariable String username, @PathVariable String password) throws IOException, SQLException {
		System.out.println("I n eed to be here");
		MonitoringInstance mi = new MonitoringInstance(table.getName());
		monitoringInstanceService.add(mi);
		table.setInstanceid(mi.getInstanceid());
		tableService.update(table, table.getId());
		logger.trace("Initiated@"+table.getName()+"!"+table.getInstanceid()+"*"+table.getTable()+"%Job Recieved");
		List<String> socketResponse = new ArrayList<String>();
		socketResponse.add("Job Recieved");
		SocketResponse sr = new SocketResponse(table.getName(), socketResponse);
		template.convertAndSend("/topic/user", sr);
		logger.trace("In Progress@"+table.getName()+"!"+table.getInstanceid()+"*"+table.getTable()+"%Table Job initiated");
		sr.getStatus().add("Table Job initiated");
		template.convertAndSend("/topic/user", sr);
		return tableService.azure_response(table,sr,username,password);
	}
	
}
