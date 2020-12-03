package com.miraclesoft.datalake.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.miraclesoft.datalake.model.Logs;
import com.miraclesoft.datalake.model.Monitoringmodel;
import com.miraclesoft.datalake.model.UniqLogs;
import com.miraclesoft.datalake.service.LogsService;

@RestController
@RequestMapping("/logs")
public class LogsController {

    @Autowired
    private LogsService logsService;

    @GetMapping("/instance/{Instance_id}")
    public List<Logs> fulllogList(@PathVariable String Instance_id) {
        return logsService.list1(Instance_id);
    }
    
    @PostMapping("/load")
    public List<UniqLogs> getLogs(@RequestBody Monitoringmodel monitoringModel){
    	return logsService.getLogs(monitoringModel);
    }

    
    @GetMapping("/load/{role}/{username}")
	public List<UniqLogs> loadUnique(@PathVariable String role, @PathVariable String username) {
		return logsService.loadUniq(role, username);
	}
    

    @GetMapping("/job_Name/{job_Name}/status/{status}/date/{startDate}/end/{endDate}/{role}/{username}")
    public List<UniqLogs> filterLoad( @PathVariable String job_Name,@PathVariable String[] status, @PathVariable("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,  @PathVariable("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,@PathVariable("role") String role, @PathVariable("username")String username){
        return logsService.getFilter(job_Name,status,startDate,endDate,role, username);
    }
    
    @GetMapping("/job_Name/{job_Name}/status/{status}/date/{startDate}/end/{endDate}")
    public List<UniqLogs> filterLoad( @PathVariable String job_Name,@PathVariable String[] status, @PathVariable("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,  @PathVariable("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate){
        return logsService.getFilter(job_Name,status,startDate,endDate);
    }
    
    
//    @GetMapping("/status/{status}/date/{startDate}/end/{endDate}/{role}/{username}")
//    public List<UniqLogs> DatefilterLoad(@PathVariable String[] status, @PathVariable("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,  @PathVariable("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate, @PathVariable("role") String role, @PathVariable("username") String username){
//        return logsService.getDateFilter(status,startDate,endDate, role, username);
//    }

    
    @GetMapping("/status/{status}/{role}/{username}")
    public List<UniqLogs> getStatus(@PathVariable String[] status, @PathVariable String role, @PathVariable String username){
        return logsService.statusOrder(status, role, username);
    }
    
    @GetMapping("/jobname")
    public List<String> jobName(){
        return logsService.getJobName();
    }

    @GetMapping("/update")
    public void updateTime(){
        logsService.durationUpdate();
    }

    @GetMapping("/updateFinished")
    public void updateFinishedTime(){
        logsService.updateDuration();
    }

    @GetMapping("/")
    public List<Logs> fulllogs() {
        return logsService.list();
    }
    
//    @GetMapping("/logsUpdate")
//    public void updateLogs() {
//    	logsService.updateLogs();
//    }
    
}
