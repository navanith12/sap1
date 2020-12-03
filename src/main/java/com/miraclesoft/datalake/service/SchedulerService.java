package com.miraclesoft.datalake.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.miraclesoft.datalake.jco.SchedulerServiceImpl;
import com.miraclesoft.datalake.model.Scheduler;
import com.miraclesoft.datalake.repository.SchedulerRepository;

@Service
public class SchedulerService {
	
	@Autowired
	private SchedulerRepository schedulerRepository;
	
	@Autowired
	private SchedulerServiceImpl schedulerserviceImpl; 
	
	
//	private String cronvalue;
//	
//	@Bean
//	private String cronvalue() {
//		return cronvalue;		
//	}
	
	public Scheduler add(Scheduler scheduler) {		
		//scheduler.setCronDate("0 0/5 * 1/1 * ? *");
		scheduler.setStatus("Added to Pool");
		//cronvalue = scheduler.getCronDate();
		return schedulerRepository.save(scheduler);		
	}

	public List<Scheduler> list_all(String role, String username) {
		if("business".equals(role.toLowerCase())) {
			return schedulerRepository.findAllBycreatedBy(username);
		}
		else {
			return schedulerRepository.findAllByOrderByCreatedAtDesc();
		}
	}
	
	public List<Scheduler> list(String role, String username) {
		if("business".equals(role.toLowerCase())) {
			return schedulerRepository.findAllBycreatedBy(username);
		}
		else {
			return schedulerRepository.findAllByOrderByCreatedAtDesc();
		}
	}
	
	public List<Scheduler> listbydatefilter(String startdate, String enddate) {
		// TODO Auto-generated method stub
		return schedulerRepository.getListbyScheduleDate(startdate, enddate);
	}

	public Scheduler findScheduler(long id) {
		return (schedulerRepository.findById(id)).get();
	}

	public Scheduler update(Scheduler scheduler, long id) {
		scheduler.setId(id);
		return schedulerRepository.save(scheduler);
	}

	public long delete(long id) {
		schedulerRepository.deleteById(id);
		return id;
	}

	
	public String schedulefullload(Scheduler schedule, String username) throws Exception {
		if(schedule.getFrequency().equals("once")) {
			if(schedule.getJobtype().toLowerCase().equals("extractor")) {
				return schedulerserviceImpl.scheduleExtractorJob_once(schedule, username);
			}
			else if(schedule.getJobtype().toLowerCase().equals("bapi")) {
				return "Cannot Schedule bapi Job";
			}
			else if(schedule.getJobtype().toLowerCase().equals("table")) {
				return schedulerserviceImpl.scheduleTableJob_once(schedule, username);
			}
			else if(schedule.getJobtype().toLowerCase().equals("jobchain")) {
				return schedulerserviceImpl.scheduleBatchJob_once(schedule, username);
			}
			else {
				return "ERROR";
			}
		}
		else {
			if(schedule.getJobtype().toLowerCase().equals("extractor")) {
				System.out.println("I neeed to be here if not once");
				return schedulerserviceImpl.scheduleExtractorJob(schedule, username);
			}
			else if(schedule.getJobtype().toLowerCase().equals("bapi")) {
				return "Cannot schedule bapi job";
			}
			else if(schedule.getJobtype().toLowerCase().equals("Table")) {
				return schedulerserviceImpl.scheduleTableJob(schedule, username);
			}
			else if(schedule.getJobtype().toLowerCase().equals("jobchain")) {
				return schedulerserviceImpl.scheduleBatchJob(schedule, username);
			}
			else {
				return "ERROR";
			}
		}
		
	}

	public String cancelSchedule(Scheduler schedule) {
		// TODO Auto-generated method stub
		return schedulerserviceImpl.cancelSchedule(schedule, true);
	}
	
}
