package com.miraclesoft.datalake.jco;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.random.RandomGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.config.TriggerTask;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import com.miraclesoft.datalake.model.Bapi;
import com.miraclesoft.datalake.model.BatchJob;
import com.miraclesoft.datalake.model.Extractor;
import com.miraclesoft.datalake.model.Jobhistory;
import com.miraclesoft.datalake.model.Logs;
import com.miraclesoft.datalake.model.MonitoringInstance;
import com.miraclesoft.datalake.model.Scheduler;
import com.miraclesoft.datalake.model.Source;
import com.miraclesoft.datalake.model.Table;
import com.miraclesoft.datalake.mongo.model.SocketResponse;
import com.miraclesoft.datalake.service.BapiService;
import com.miraclesoft.datalake.service.BatchJobService;
import com.miraclesoft.datalake.service.ExtractorService;
import com.miraclesoft.datalake.service.JobHistoryService;
import com.miraclesoft.datalake.service.LogsService;
import com.miraclesoft.datalake.service.MonitoringInstanceService;
import com.miraclesoft.datalake.service.SchedulerService;
import com.miraclesoft.datalake.service.SourceService;
import com.miraclesoft.datalake.service.TableService;

@Component
public class SchedulerServiceImpl implements SchedulingConfigurer {

	Logger logger = LoggerFactory.getLogger(SchedulerServiceImpl.class);
	
	private TaskScheduler taskscheduler;

	private ScheduledFuture<?> scheduledFuture;

	@Autowired
	private ExtractorService extractorService;

	@Autowired
	private BapiService bapiService;

	@Autowired
	private SchedulerService schedulerService;
	
	@Autowired
	private TableService tableService;
	
	@Autowired
	private SourceService sourceService;

	@Autowired
	private JcoSapConnector jcoSapConnector;
	
	@Autowired
	private JobHistoryService jobHistoryService;
	
	@Autowired
	private MonitoringInstanceService monitoringInstanceService;
	
	@Autowired
	private BatchJobService batchJobService;
	
	@Autowired
	private BatchJobImpl batchjobImpl;
	
	
	ScheduledTaskRegistrar scheduledTaskRegistrar = new ScheduledTaskRegistrar();

	public TaskScheduler threadPoolTaskScheduler() {
		System.out.println("In threadPoolTaskScheduler()");
		ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
		threadPoolTaskScheduler.setPoolSize(5);
		threadPoolTaskScheduler.setThreadNamePrefix("sap-");
		threadPoolTaskScheduler.initialize();
		return threadPoolTaskScheduler;
	}

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.setTaskScheduler(threadPoolTaskScheduler());
		scheduledTaskRegistrar.setTaskScheduler(threadPoolTaskScheduler());
	}

	public String scheduleExtractorJob(Scheduler schedule, String username) throws Exception {
		Extractor extractor = extractorService.findByJobName(schedule.getJobId());
//		System.out.println(schedule.getJobId());
		MonitoringInstance mi = new MonitoringInstance(extractor.getName());
		monitoringInstanceService.add(mi);
		extractor.setInstanceid(mi.getInstanceid());
		extractorService.update(extractor, extractor.getId());
		logger.trace("Initiated@"+extractor.getName()+"!"+extractor.getInstanceid()+"*"+extractor.getFunction()+"%Schedule Job Recieved"+"#"+username);
		logger.trace("In Progress@"+extractor.getName()+"!"+extractor.getInstanceid()+"*"+extractor.getFunction()+"%Received Extractor Job to run More than once"+"#"+username);
		System.out.println(extractorService.findByfunction_extractor(schedule.getJobId()));
		Source source = sourceService.findSource(extractor.getSourceId());
		CronSequenceGenerator cronTrigger = new CronSequenceGenerator(schedule.getCronDate());
        //Date next = cronTrigger.next(new Date());
		if (extractor.getTargetType().equals("full")) {
			Runnable exampleRunnable = new Runnable() {
				@Override
				public void run() {
					System.out.println(Thread.currentThread().getName());
					try {
						System.out.println("im in runnable method");
						try {
							schedule.setStatus("Running");
							schedulerService.update(schedule, schedule.getId());						
							long start = System.currentTimeMillis();
							logger.trace("In Progress@"+extractor.getName()+"!"+extractor.getInstanceid()+"*"+extractor.getFunction()+"%Extractor Full Job Initiated"+"#"+username);
							jcoSapConnector.fullload(extractor, source, new SocketResponse(), username);
							long end = System.currentTimeMillis();
							schedule.setStatus("Completed");
							Date next1 = cronTrigger.next(new Date());
							String previousDate = schedule.getScheduledDate();
							schedule.setScheduledDate(next1.toString());
							schedulerService.update(schedule, schedule.getId());
							logger.trace("Finished@"+extractor.getName()+"!"+extractor.getInstanceid()+"*"+extractor.getFunction()+"%Next run is scheduled on: "+next1+"#"+username);
							long millis = end - start;
							String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
									TimeUnit.MILLISECONDS.toMinutes(millis)
											- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
									TimeUnit.MILLISECONDS.toSeconds(millis)
											- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
							System.out.println(hms);
							Jobhistory jh = new Jobhistory(mi.getInstanceid(), extractor.getFunction(), extractor.getName(),extractor.getTargetType(), extractor.getTargetName(), previousDate, hms, schedule.getStatus());
							jobHistoryService.add(jh);
						} catch (Exception e) {
							schedule.setStatus("Error");
							logger.trace("Cancelled@"+extractor.getName()+"!"+extractor.getInstanceid()+"*"+extractor.getFunction()+"%Job Cancelled. Error: "+e.getMessage()+"#"+username);
							schedulerService.update(schedule, schedule.getId());
							e.printStackTrace();
						}
					} catch (Exception e) {
						schedule.setStatus("Error");
						logger.trace("Cancelled@"+extractor.getName()+"!"+extractor.getInstanceid()+"*"+extractor.getFunction()+"%Job Cancelled. Error: "+e.getMessage()+"#"+username);
						schedulerService.update(schedule, schedule.getId());
						e.printStackTrace();
					}
				}
			};
			taskscheduler = threadPoolTaskScheduler();
			System.out.println("thread pool task scheduler: "+threadPoolTaskScheduler());
			scheduledTaskRegistrar.setTaskScheduler(taskscheduler);
			//scheduledFuture = taskscheduler.schedule(exampleRunnable, new CronTrigger(schedule.getCronDate()));
			scheduledFuture = scheduledTaskRegistrar.getScheduler().schedule(exampleRunnable, new CronTrigger(schedule.getCronDate()));
			//scheduledTaskRegistrar.addCronTask(exampleRunnable,schedule.getCronDate());			
	        //System.out.println("Next Execution Time: " + next);
	        //schedule.setScheduledDate(next.toString());
			logger.trace("In Progress@"+extractor.getName()+"!"+extractor.getInstanceid()+"*"+extractor.getFunction()+"%Extractor Job Schedule on "+schedule.getScheduledDate()+"#"+username);
			schedule.setStatus("Scheduled");
			schedulerService.update(schedule, schedule.getId());
			System.out.println(schedule.getJobtype());
			return "task scheduled";
		} else if (extractor.getTargetType().equals("delta")) {
			Runnable exampleRunnable = new Runnable() {
				@Override
				public void run() {
					try {
						System.out.println("im in runnable method");
						try {
							schedule.setStatus("Running");
							schedulerService.update(schedule, schedule.getId());
							MonitoringInstance mi = new MonitoringInstance(extractor.getName());
							monitoringInstanceService.add(mi);
							extractor.setInstanceid(mi.getInstanceid());
							extractorService.update(extractor, extractor.getId());
							long start = System.currentTimeMillis();
							jcoSapConnector.fullload(extractor, source, new SocketResponse(), username);
							long end = System.currentTimeMillis();
							schedule.setStatus("Completed");
							Date next1 = cronTrigger.next(new Date());
							String previousDate = schedule.getScheduledDate();
							schedule.setScheduledDate(next1.toString());
							schedulerService.update(schedule, schedule.getId());							
							long millis = end - start;
							String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
									TimeUnit.MILLISECONDS.toMinutes(millis)
											- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
									TimeUnit.MILLISECONDS.toSeconds(millis)
											- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
							Jobhistory jh = new Jobhistory(mi.getInstanceid(), extractor.getFunction(), extractor.getName(),extractor.getTargetType(), extractor.getTargetName(), previousDate, hms, schedule.getStatus());
							jobHistoryService.add(jh);
						} catch (Exception e) {
							schedule.setStatus("Error");
							schedulerService.update(schedule, schedule.getId());
							e.printStackTrace();
						}
					} catch (Exception e) {
						schedule.setStatus("Error");
						schedulerService.update(schedule, schedule.getId());
						e.printStackTrace();
					}
				}
			};
			taskscheduler = threadPoolTaskScheduler();
			scheduledFuture = taskscheduler.schedule(exampleRunnable,
					new CronTrigger(schedule.getCronDate()));
			schedule.setStatus("Scheduled");
			schedulerService.update(schedule, schedule.getId());
			System.out.println(schedule.getJobtype());
			return "task scheduled";
		} else if (extractor.getTargetType().equals("intitializewithdata")) {

			Runnable exampleRunnable = new Runnable() {
				@Override
				public void run() {
					try {
						System.out.println("im in runnable method");
						try {
							schedule.setStatus("Running");
							schedulerService.update(schedule, schedule.getId());
							MonitoringInstance mi = new MonitoringInstance(extractor.getName());
							monitoringInstanceService.add(mi);
							extractor.setInstanceid(mi.getInstanceid());
							extractorService.update(extractor, extractor.getId());
							long start = System.currentTimeMillis();
							jcoSapConnector.fullload(extractor, source, new SocketResponse(), username);
							long end = System.currentTimeMillis();
							schedule.setStatus("Completed");
							Date next1 = cronTrigger.next(new Date());
							String previousDate = schedule.getScheduledDate();
							schedule.setScheduledDate(next1.toString());
							schedulerService.update(schedule, schedule.getId());							
							long millis = end - start;
							String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
									TimeUnit.MILLISECONDS.toMinutes(millis)
											- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
									TimeUnit.MILLISECONDS.toSeconds(millis)
											- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
							Jobhistory jh = new Jobhistory(mi.getInstanceid(), extractor.getFunction(), extractor.getName(),extractor.getTargetType(), extractor.getTargetName(), previousDate, hms, schedule.getStatus());
							jobHistoryService.add(jh);
						} catch (Exception e) {
							schedule.setStatus("Error");
							schedulerService.update(schedule, schedule.getId());
							e.printStackTrace();
						}
					} catch (Exception e) {
						schedule.setStatus("Error");
						schedulerService.update(schedule, schedule.getId());
						e.printStackTrace();
					}
				}
			};
			taskscheduler = threadPoolTaskScheduler();
			scheduledFuture = taskscheduler.schedule(exampleRunnable,
					new CronTrigger(schedule.getCronDate()));
			schedule.setStatus("Scheduled");
			schedulerService.update(schedule, schedule.getId());
			System.out.println(schedule.getJobtype());
			return "task scheduled";
		

		}else if (extractor.getTargetType().equals("intitializewithoutdata")) {

			Runnable exampleRunnable = new Runnable() {
				@Override
				public void run() {
					try {
						System.out.println("im in runnable method");
						try {
							schedule.setStatus("Running");
							schedulerService.update(schedule, schedule.getId());
							jcoSapConnector.initializewithoutdata(extractor, source, new SocketResponse(), username);
							Date next1 = cronTrigger.next(new Date());
							schedule.setScheduledDate(next1.toString());
							schedule.setStatus("Completed");
							schedulerService.update(schedule, schedule.getId());
						} catch (Exception e) {
							schedule.setStatus("Error");
							schedulerService.update(schedule, schedule.getId());
							e.printStackTrace();
						}
					} catch (Exception e) {
						schedule.setStatus("Error");
						schedulerService.update(schedule, schedule.getId());
						e.printStackTrace();
					}
				}
			};
			taskscheduler = threadPoolTaskScheduler();
			scheduledFuture = taskscheduler.schedule(exampleRunnable,
					new CronTrigger(schedule.getCronDate()));
			schedule.setStatus("Scheduled");
			schedulerService.update(schedule, schedule.getId());
			System.out.println(schedule.getJobtype());
			return "task scheduled";
		} else {
			return "error";
		}

	}
	
//	public String scheduleBapiJob(Scheduler schedule) throws Exception {
//			Bapi bapi = bapiService.findBybapiName(schedule.getJobId());
//			Source source = sourceService.findSource(bapi.getSourceId());
//			Runnable exampleRunnable = new Runnable() {
//				@Override
//				public void run() {
//					try {
//						System.out.println("im in runnable method");
//						try {
//							schedule.setStatus("Running");
//							schedulerService.update(schedule, schedule.getId());
//							jcoSapConnector.bapi_response(bapi, source);
//							schedule.setStatus("Completed");
//							schedulerService.update(schedule, schedule.getId());
//						} catch (Exception e) {
//							schedule.setStatus("Error");
//							schedulerService.update(schedule, schedule.getId());
//							e.printStackTrace();
//						}
//					} catch (Exception e) {
//						schedule.setStatus("Error");
//						schedulerService.update(schedule, schedule.getId());
//						e.printStackTrace();
//					}
//				}
//			};
//			taskscheduler = threadPoolTaskScheduler();
//			scheduledFuture = taskscheduler.schedule(exampleRunnable,
//					new CronTrigger(schedule.getCronDate()));
//			schedule.setStatus("Scheduled");
//			schedulerService.update(schedule, schedule.getId());
//			System.out.println(schedule.getJobtype());
//			return "task scheduled";
//	}

	@SuppressWarnings("deprecation")
	public String scheduleExtractorJob_once(Scheduler schedule, String username) throws Exception {
		if (schedule.getJobtype().equals("extractor")) {
			Extractor extractor = extractorService.findByJobName(schedule.getJobId());
			MonitoringInstance mi = new MonitoringInstance(extractor.getName());
			monitoringInstanceService.add(mi);
			extractor.setInstanceid(mi.getInstanceid());
			extractorService.update(extractor, extractor.getId());
			logger.trace("Initiated@"+extractor.getName()+"!"+extractor.getInstanceid()+"*"+extractor.getFunction()+"%Schedule Job Recieved"+"#"+username);
			logger.trace("In Progress@"+extractor.getName()+"!"+extractor.getInstanceid()+"*"+extractor.getFunction()+"%Received Extractor Job to run once"+"#"+username);
			System.out.println(extractorService.findByJobName(schedule.getJobId()));
			Source source = sourceService.findSource(extractor.getSourceId());
			Runnable exampleRunnable = new Runnable() {
				@Override
				public void run() {
					try {
						try{
							schedule.setStatus("Running");
							schedulerService.update(schedule, schedule.getId());
							logger.trace("Scheduled Extractor Job initiated@"+extractor.getName()+"!"+extractor.getInstanceid()+"*"+extractor.getFunction()+"%Extractor Job initiated"+"#"+username);
							jcoSapConnector.fullload(extractor, source, null, username);
							logger.trace("Finished@"+extractor.getName()+"!"+extractor.getInstanceid()+"*"+extractor.getFunction()+"%Job Successfully Completed"+"#"+username);
							schedule.setStatus("Completed");
							schedulerService.update(schedule, schedule.getId());
						}
						catch(Exception e) {
							logger.trace("Cancelled@"+extractor.getName()+"!"+extractor.getInstanceid()+"*"+extractor.getFunction()+"%Job Cancelled. Reason: "+e.getMessage()+"#"+username);
							schedule.setStatus("Error");
							schedulerService.update(schedule, schedule.getId());
							e.printStackTrace();
						}
					} catch (Exception e) {
						schedule.setStatus("Error");
						logger.trace("Cancelled@"+extractor.getName()+"!"+extractor.getInstanceid()+"*"+extractor.getFunction()+"%Job Cancelled. Reason: "+e.getMessage()+"#"+username);
						schedulerService.update(schedule, schedule.getId());
						e.printStackTrace();
					}
				}
			};
			taskscheduler = threadPoolTaskScheduler();
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
			Date dt = null;
			try {
				dt = format.parse(schedule.getScheduledDate());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			System.out.println(dt);
			System.out.println(schedule.getScheduledDate());
			scheduledFuture = taskscheduler.schedule(exampleRunnable, dt);
			logger.trace("In Progress@"+extractor.getName()+"!"+extractor.getInstanceid()+"*"+extractor.getFunction()+"%Extractor Job Schedule on "+schedule.getScheduledDate()+"#"+username);
			schedule.setStatus("Scheduled");
			schedulerService.update(schedule, schedule.getId());
			System.out.println(schedule.getJobtype());
			return "task scheduled";
		} 
		else {
			return "error";
		}

	}
	
//	@SuppressWarnings("deprecation")
//	public String scheduleBapiJob_once(Scheduler schedule) throws Exception {
//			Bapi bapi = bapiService.findBybapiName(schedule.getJobId());
//			Source source = sourceService.findSource(bapi.getSourceId());
//			Runnable exampleRunnable = new Runnable() {
//				@Override
//				public void run() {
//					try {
//						System.out.println("im in runnable method");
//						try{
//							schedule.setStatus("Running");
//							schedulerService.update(schedule, schedule.getId());
//							jcoSapConnector.bapi_response(bapi, source);
//							schedule.setStatus("Completed");
//							schedulerService.update(schedule, schedule.getId());
//						}
//						catch(Exception e) {
//							schedule.setStatus("Error");
//							schedulerService.update(schedule, schedule.getId());
//							e.printStackTrace();
//						}
//					} catch (Exception e) {
//						schedule.setStatus("Error");
//						schedulerService.update(schedule, schedule.getId());
//						e.printStackTrace();
//					}
//				}
//			};
//			taskscheduler = threadPoolTaskScheduler();
//			Date dt = Date.from(Instant.parse(schedule.getScheduledDate()).minusSeconds(Date.from(Instant.parse(schedule.getScheduledDate())).getSeconds()));	
//			System.out.println(dt);
//			scheduledFuture = taskscheduler.schedule(exampleRunnable, dt);
//			schedule.setStatus("Scheduled");
//			schedulerService.update(schedule, schedule.getId());
//			System.out.println(schedule.getJobtype());
//			return "task scheduled";
//	}
//
	
	public void test() {
			if ("extractor".equals("full")) {
				if ("extractor".equals("db")) {

				} else if ("extractor".equals("ftp") || "extractor".equals("local")) {

				} else {
					String res = "Invalid selection";
				}
			} else if ("extractor".equals("delta")) {
				if ("extractor".equals("db")) {

				} else if ("extractor".equals("ftp") || "extractor".equals("local")) {

				} else {
					String res = "Invalid selection";
				}

			} else if ("extractor".equals("initwdata")) {
				if ("extractor".equals("db")) {

				} else if ("extractor".equals("ftp") || "extractor".equals("local")) {

				} else {
					String res = "Invalid selection";
				}

			} else if ("extractor".equals("initwodata")) {

			} else {
				String res = "Invalid selection";
			}
	}

	public String scheduleTableJob_once(Scheduler schedule, String username) {
		Table table = tableService.getTablebyName(schedule.getJobId());
		Source source = sourceService.findSource(table.getSourceId());
		Runnable exampleRunnable = new Runnable() {
			@Override
			public void run() {
				try {
					System.out.println("im in runnable method");
					try{
						schedule.setStatus("Running");
						schedulerService.update(schedule, schedule.getId());
						jcoSapConnector.db_insert(source, table, new SocketResponse());
						schedule.setStatus("Completed");
						schedulerService.update(schedule, schedule.getId());
					}
					catch(Exception e) {
						schedule.setStatus("Error");
						schedulerService.update(schedule, schedule.getId());
						e.printStackTrace();
					}
				} catch (Exception e) {
					schedule.setStatus("Error");
					schedulerService.update(schedule, schedule.getId());
					e.printStackTrace();
				}
			}
		};
		taskscheduler = threadPoolTaskScheduler();
		Date dt = Date.from(Instant.parse(schedule.getScheduledDate()).minusSeconds(Date.from(Instant.parse(schedule.getScheduledDate())).getSeconds()));	
		System.out.println(dt);
		scheduledFuture = taskscheduler.schedule(exampleRunnable, dt);
		schedule.setStatus("Scheduled");
		schedulerService.update(schedule, schedule.getId());
		System.out.println(schedule.getJobtype());
		return "task scheduled";
}

	public String scheduleTableJob(Scheduler schedule, String username) {
		Table table = tableService.getTablebyName(schedule.getJobId());
		Source source = sourceService.findSource(table.getSourceId());
		Runnable exampleRunnable = new Runnable() {
			@Override
			public void run() {
				try {
					System.out.println("im in runnable method");
					try {
						schedule.setStatus("Running");
						schedulerService.update(schedule, schedule.getId());
						jcoSapConnector.db_insert(source, table, new SocketResponse());
						schedule.setStatus("Completed");
						schedulerService.update(schedule, schedule.getId());
					} catch (Exception e) {
						schedule.setStatus("Error");
						schedulerService.update(schedule, schedule.getId());
						e.printStackTrace();
					}
				} catch (Exception e) {
					schedule.setStatus("Error");
					schedulerService.update(schedule, schedule.getId());
					e.printStackTrace();
				}
			}
		};
		taskscheduler = threadPoolTaskScheduler();
		scheduledFuture = taskscheduler.schedule(exampleRunnable,
				new CronTrigger(schedule.getCronDate()));
		schedule.setStatus("Scheduled");
		schedulerService.update(schedule, schedule.getId());
		System.out.println(schedule.getJobtype());
		return "task scheduled";
	}
	

	public String scheduleBatchJob_once(Scheduler schedule, String username) {
		BatchJob batchjob = batchJobService.findBatchJobbyName(schedule.getJobId());
		Runnable exampleRunnable = new Runnable() {
			@Override
			public void run() {
				try {
					System.out.println("im in runnable method");
					try{
						schedule.setStatus("Running");
						schedulerService.update(schedule, schedule.getId());
						batchjobImpl.batchjob(batchjob.getJobLists(), username);
						schedule.setStatus("Completed");
						schedulerService.update(schedule, schedule.getId());
					}
					catch(Exception e) {
						schedule.setStatus("Error");
						schedulerService.update(schedule, schedule.getId());
						e.printStackTrace();
					}
				} catch (Exception e) {
					schedule.setStatus("Error");
					schedulerService.update(schedule, schedule.getId());
					e.printStackTrace();
				}
			}
		};
		taskscheduler = threadPoolTaskScheduler();
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
		Date dt = null;
		try {
			dt = format.parse(schedule.getScheduledDate());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		System.out.println(dt);
		System.out.println(schedule.getScheduledDate());
		scheduledFuture = taskscheduler.schedule(exampleRunnable, dt);
		schedule.setStatus("Scheduled");
		schedulerService.update(schedule, schedule.getId());
		System.out.println(schedule.getJobtype());
		return "task scheduled";
	}
	
	
	public String scheduleBatchJob(Scheduler schedule, String username) {
		BatchJob batchjob = batchJobService.findBatchJobbyName(schedule.getJobId());
		CronSequenceGenerator cronTrigger = new CronSequenceGenerator(schedule.getCronDate());
		Runnable exampleRunnable = new Runnable() {
			@Override
			public void run() {
				try {
					System.out.println("im in runnable method");
					try {
						schedule.setStatus("Running");
						schedulerService.update(schedule, schedule.getId());
						batchjobImpl.batchjob(batchjob.getJobLists(), username);
						schedule.setStatus("Completed");
						schedulerService.update(schedule, schedule.getId());
					} catch (Exception e) {
						schedule.setStatus("Error");
						schedulerService.update(schedule, schedule.getId());
						e.printStackTrace();
					}
				} catch (Exception e) {
					schedule.setStatus("Error");
					schedulerService.update(schedule, schedule.getId());
					e.printStackTrace();
				}
			}
		};
		taskscheduler = threadPoolTaskScheduler();
		System.out.println("thread pool task scheduler: "+threadPoolTaskScheduler());
		scheduledTaskRegistrar.setTaskScheduler(taskscheduler);
		scheduledFuture = scheduledTaskRegistrar.getScheduler().schedule(exampleRunnable, new CronTrigger(schedule.getCronDate()));
		schedule.setStatus("Scheduled");
		schedulerService.update(schedule, schedule.getId());
		System.out.println(schedule.getJobtype());
		return "task scheduled";
	}

	public String cancelSchedule(Scheduler schedule, Boolean cancelValue) {
//		System.out.println(scheduledTaskRegistrar.getCronTaskList().get(0).getExpression());
//		System.out.println(scheduledTaskRegistrar.getCronTaskList().get(0).getClass().getName());
//		System.out.println(scheduledTaskRegistrar.getCronTaskList().get(0).getClass());
//		System.out.println(scheduledTaskRegistrar.getCronTaskList());
		if (scheduledFuture.cancel(cancelValue)) {
			schedule.setStatus("Cancelled");
			schedulerService.update(schedule, schedule.getId());
		} else {
			schedule.setStatus("Error");
			schedulerService.update(schedule, schedule.getId());
		}

		return "Operation Cancelled";
	}

}
