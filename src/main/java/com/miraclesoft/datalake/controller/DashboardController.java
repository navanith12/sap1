package com.miraclesoft.datalake.controller;

import java.util.List;

import javax.json.Json;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.miraclesoft.datalake.model.Scheduler;
import com.miraclesoft.datalake.service.BapiService;
import com.miraclesoft.datalake.service.DbInsertService;
import com.miraclesoft.datalake.service.ExtractorService;
import com.miraclesoft.datalake.service.FtpServerService;
import com.miraclesoft.datalake.service.LogsService;
import com.miraclesoft.datalake.service.SchedulerService;
import com.miraclesoft.datalake.service.SourceService;
import com.miraclesoft.datalake.service.TableService;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {
	
	@Autowired
	private ExtractorService extractorService;
	
	@Autowired
	private BapiService bapiService;
	
	@Autowired
	private TableService tableService;
	
	@Autowired
	private SourceService sourceService;
	
	@Autowired
	private FtpServerService ftpServerService;
	
	@Autowired
	private DbInsertService dbinsertService;
	
	@Autowired
	private SchedulerService schedulerService;

	@Autowired
	private LogsService logsService;
	
	@GetMapping("/hello")
	public String hello() {
		return "Welcome";
	}
	
	@GetMapping("/{role}/{username}")
	public ResponseEntity<Object> getCount(@PathVariable String role, @PathVariable String username){
		int extractorCount=0;
		int bapiCount=0;
		int tableCount=0;
		int sourceCount=0;
		int ftpcount=0;
		int dbinsertcount=0;
		int targetCount=0;
		int schedulerextraccount=0;
		int schedulertableccount=0;
		int monitoringProgrescount=0;
		int monitoringfinishcount=0;
		int monitoringcancelcount=0;
		
		extractorCount = extractorService.list_all(role,username).size();
		bapiCount = bapiService.list_all(role,username).size();
		tableCount = tableService.list_all(role,username).size();
		
		sourceCount = sourceService.list_all().size();
		
		ftpcount = ftpServerService.list_all().size();
		dbinsertcount = dbinsertService.list_all().size();
		
		targetCount = ftpcount + dbinsertcount;
	
		String[] progress = new String[1];
		progress[0] = "In Progress";
		monitoringProgrescount = logsService.statusOrder(progress,role, username).size();
		progress[0] = "Finished";
		monitoringfinishcount = logsService.statusOrder(progress,role, username).size();
		progress[0] = "Cancelled";
		monitoringcancelcount = logsService.statusOrder(progress,role, username).size();
		
		List<Scheduler> listforcount = schedulerService.list_all(role, username);
		
		for(Scheduler s: listforcount) {
			if(s.getJobtype().toLowerCase().equals("extractor")) {
				schedulerextraccount++;
			}
			else {
				schedulertableccount++;
			}
		}
		
//		"{\r\n" + 
//		"        jobs : {\r\n" + 
//		"            bapi : "+bapiCount + 
//		"            table: "+tableCount + 
//		"            extractor: "+extractorCount + 
//		"        },\r\n" + 
//		"        schedule : {\r\n" + 
//		"            bapi : "+schedulerbapicount + 
//		"            table: "+schedulertableccount + 
//		"            extractor: "+schedulerextraccount + 
//		"        },\r\n" + 
//		"        systems : {\r\n" + 
//		"            target : "+targetCount + 
//		"            source: "+sourceCount + 
//		"        },\r\n" + 
//		"        monitoring : {\r\n" + 
//		"            progress : "+monitoringProgrescount + 
//		"            cancel: "+monitoringcancelcount + 
//		"            complete: "+monitoringfinishcount + 
//		"        }\r\n" + 
//		"    }"
		
		JSONObject jo = new JSONObject();
		JSONObject jobsobject = new JSONObject();
		jobsobject.put("bapi", bapiCount);
		jobsobject.put("table", tableCount);
		jobsobject.put("extractor", extractorCount);
		
		JSONObject scheduleobject = new JSONObject();
		scheduleobject.put("table", schedulertableccount);
		scheduleobject.put("extractor",schedulerextraccount);
		
		JSONObject systemObject = new JSONObject();
		systemObject.put("target", targetCount);
		systemObject.put("source", sourceCount);
		
		JSONObject monitobject = new JSONObject();
		monitobject.put("progress", monitoringProgrescount);
		monitobject.put("cancel", monitoringcancelcount);
		monitobject.put("complete",monitoringfinishcount);
		
		
		JSONObject dataobject = new JSONObject();
		dataobject.put("jobs", jobsobject);
		dataobject.put("schedule", scheduleobject);
		dataobject.put("systems", systemObject);
		dataobject.put("monitoring", monitobject);
		
		
		jo.put("Status", HttpStatus.OK);
		jo.put("Data",dataobject);
		jo.put("Message", "Success");
		
		
		System.out.println(jo.get("Data"));
		
		return new ResponseEntity<Object>(jo.toMap(), HttpStatus.OK);
	}
	
}
