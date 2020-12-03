package com.miraclesoft.datalake.controller;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.miraclesoft.datalake.model.Scheduler;
import com.miraclesoft.datalake.service.SchedulerService;



@RestController
@RequestMapping("/scheduler")
public class SchedulerController {
	
	@Autowired
	private SchedulerService schedulerService;

	@PostMapping("/")
	public Scheduler add(@RequestBody Scheduler schedule) {
		if(!schedule.getFrequency().equals("once")) {
		CronSequenceGenerator cronTrigger = new CronSequenceGenerator(schedule.getCronDate());
        Date next = cronTrigger.next(new Date());
        schedule.setScheduledDate(next.toString());
		}
		return schedulerService.add(schedule);
	}

	@GetMapping("/{role}/{username}")
	public List<Scheduler> schedulerList(@PathVariable String role, @PathVariable String username) {
		return schedulerService.list(role,username);
	}
	
	@GetMapping("/startdate/{startdate}/enddate/{enddate}")
	public List<Scheduler> schedulerListbyDate(@PathVariable String startdate, @PathVariable String enddate){
		return schedulerService.listbydatefilter(startdate, enddate);
	}

	@GetMapping("/{id}")
	public Scheduler getScheduler(@PathVariable long id) {
		return schedulerService.findScheduler(id);
	}

	@PutMapping("/{id}")
	public Scheduler update(@RequestBody Scheduler schedule, @PathVariable long id) {
		schedule.setId(id);
		schedule.setStatus("Updated and Added to Pool");
		if (!schedule.getFrequency().equals("once")) {
			CronSequenceGenerator cronTrigger = new CronSequenceGenerator(schedule.getCronDate());
			Date next = cronTrigger.next(new Date());
			schedule.setScheduledDate(next.toString());
		}
		return schedulerService.update(schedule, id);
	}

	@DeleteMapping("/{id}")
	public long delete(@PathVariable long id) {
		return schedulerService.delete(id);
	}
	
	@PostMapping("/jobschedule/{username}")
	public String schedulefulload(@RequestBody Scheduler schedule,@PathVariable String username) throws Exception {	
		System.out.println("im in controller");
		return schedulerService.schedulefullload(schedule, username);
	}
	
	@PostMapping("/cancel")
	public String cancelSchedule(@RequestBody Scheduler schedule) {
		return schedulerService.cancelSchedule(schedule);
	}
	
}
