package com.miraclesoft.datalake.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.miraclesoft.datalake.model.Logs;
import com.miraclesoft.datalake.model.Monitoringmodel;
import com.miraclesoft.datalake.model.UniqLogs;
import com.miraclesoft.datalake.repository.JobNameRepository;
import com.miraclesoft.datalake.repository.LogsRepository;
import com.miraclesoft.datalake.repository.UniqLogsRepository;

@Service
public class LogsService {

    @Autowired
    private LogsRepository logsRepository;
    @Autowired
    private UniqLogsRepository uniqLogsRepository;
    @Autowired
    private JobNameRepository jobNameRepository;

    public List<Logs> list1(String instance_id) {
        return logsRepository.getLogs(instance_id);
    }
    public List<Logs> list(){
        return logsRepository.findAll();
    }

    public List<UniqLogs> loadUniq(String role, String username) {
    	if("business".equals(role.toLowerCase())) {
    		return uniqLogsRepository.findBycreatedby(username);
    	}
    	else {
    		return uniqLogsRepository.getUniq();
    	}
        
    }

    public List<String> getJobName() {
        return jobNameRepository.getJobName();
    }
    public List<UniqLogs> statusOrder(String[] status, String role, String username) {
    	if("business".equals(role)) {
    		if(Arrays.asList(status).contains("In Progress")){
                return uniqLogsRepository.getInProgress(username);
            }
            else {
                return uniqLogsRepository.getStatus(status, username);
            }

    	}
    	else {
    		if(Arrays.asList(status).contains("In Progress")){
                return uniqLogsRepository.getInProgress();
            }
            else {
                return uniqLogsRepository.getStatus(status);
            }

    	}
      }

    public List<UniqLogs> getFilter(String job_Name,String[] status,  Date startDate, Date endDate, String role, String username) {
    	if("business".equals(role)) {
    		if(Arrays.asList(status).contains("In Progress")){
	            return uniqLogsRepository.getInProgress(username);
	        }
	        return uniqLogsRepository.getFilter( job_Name,status, startDate, endDate, username);
    	}
    	else {
	        if(Arrays.asList(status).contains("In Progress")){
	            return uniqLogsRepository.getInProgress();
	        }
	        return uniqLogsRepository.getFilter( job_Name,status, startDate, endDate);
    	}
    }
    
    public List<UniqLogs> getFilter(String job_Name,String[] status,  Date startDate, Date endDate) {
	        if(Arrays.asList(status).contains("In Progress")){
	            return uniqLogsRepository.getInProgress();
	        }
	        return uniqLogsRepository.getFilter( job_Name,status, startDate, endDate);
    }
    
    
    public void durationUpdate() {
        uniqLogsRepository.getUpdateDuration();
    }

    public void updateDuration() {
        uniqLogsRepository.getFinishedDuration();
    }
    
    public void updateLogs() { 
		String dburl = "jdbc:sqlserver://172.17.0.141:1433;databaseName=SAPDATALAKE";
		String username = "SAPUSER";
		String password = "Miracle@1";
		
		String insertinto = "INSERT INTO DBO.LOGS( DATED, STARTDATE, ENDDATE, STARTTIME, JOB_NAME, INSTANCE_ID ,MESSAGE, STATUS,CREATED_BY,JOB_ID, duration) " + 
				"SELECT DATEADD(s, CONVERT(BIGINT, timestmp ) / 1000, CONVERT(DATETIME, '1-1-1970 20:00:00'))," + 
				"Cast(Dateadd(second, Cast(timestmp AS BIGINT) / 1000, '19700101') AS DATE)," + 
				"Cast(Dateadd(second, Cast(timestmp AS BIGINT) / 1000, '19700101') AS DATE)," + 
				"DATEADD(s, CONVERT(BIGINT, timestmp ) / 1000, CONVERT(TIME, '20:00:00'))," + 
				"SUBSTRING(FORMATTED_MESSAGE ,CHARINDEX('*',FORMATTED_MESSAGE )+1,ABS(CHARINDEX('%',FORMATTED_MESSAGE)-1)- CHARINDEX('*', FORMATTED_MESSAGE))," + 
				"SUBSTRING(FORMATTED_MESSAGE,CHARINDEX('!',FORMATTED_MESSAGE)+1, ABS(CHARINDEX('*',FORMATTED_MESSAGE)-1)-CHARINDEX('!',FORMATTED_MESSAGE)) ," + 
				"Substring(FORMATTED_MESSAGE, CHARINDEX('%',FORMATTED_MESSAGE)+1,ABS(CHARINDEX('#',FORMATTED_MESSAGE)-1)-CHARINDEX('%',FORMATTED_MESSAGE))," + 
				"SUBSTRING(FORMATTED_MESSAGE,0, CHARINDEX('@',FORMATTED_MESSAGE)) ," + 
				"Substring(FORMATTED_MESSAGE, CHARINDEX('#',FORMATTED_MESSAGE)+1,DATALENGTH(FORMATTED_MESSAGE)) ," + 
				"SUBSTRING(FORMATTED_MESSAGE,CHARINDEX('@',FORMATTED_MESSAGE)+1, ABS(CHARINDEX('!',FORMATTED_MESSAGE)-1)-CHARINDEX('@',FORMATTED_MESSAGE))," + 
				"'00:00:00'" + 
				"FROM logging_event where DATEADD(s, CONVERT(BIGINT, timestmp ) / 1000, CONVERT(DATETIME, '1-1-1970 20:00:00')) NOT IN (Select dated from LOGS)";
		String update1 = "update logs set duration = '00:00:00.0000000'";
		String update = "Update l1 set l1.duration = DATEADD(SECOND,-DATEDIFF(second ,l2.starttime,l1.starttime), l1.duration)from logs l1 join logs l2 on l1.INSTANCE_ID = l2.instance_id where l2.status in ('Finished', 'Cancelled') and l1.status='Initiated'";
		String duration = "UPDATE LOGS SET TIMETAKEN = DURATION";
		
		try (Connection conn = DriverManager.getConnection(dburl, username, password);
				Statement statement = conn.createStatement();) {
			boolean insertexec = statement.execute(insertinto);
			System.out.println(insertexec);
			boolean updatedate = statement.execute(update1);
			boolean updateexec = statement.execute(update);
			System.out.println(updateexec);
			boolean dura = statement.execute(duration);
		
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}

//	public List<UniqLogs> getDateFilter(String[] status, Date startDate, Date endDate,String role,String username) {
//		if("business".equals(role)) {
//			return uniqLogsRepository.getDateFilter(status, startDate, endDate,username);
//		}
//		else {
//			return uniqLogsRepository.getDateFilter(status, startDate, endDate);
//		}
//		
//	}
	public List<UniqLogs> getLogs(Monitoringmodel monitoringModel) {
		String role = monitoringModel.getRole();
		Date startdate = monitoringModel.getStartdate();
		Date enddate = monitoringModel.getEnddate();
		System.out.println(startdate+" "+enddate);
		List<UniqLogs> uniqLogs = new ArrayList<UniqLogs>();
		if(role.toLowerCase().equals("business")) {
			List<String> status = monitoringModel.getStatus();
			for(String st: status ) {
				if(st.equals("In Progress")) {
					uniqLogs.addAll(uniqLogsRepository.getLogswithInprogress(startdate, enddate,monitoringModel.getCreatedby()));
				}
				else {
					uniqLogs.addAll(uniqLogsRepository.getLogs(st, startdate, enddate,monitoringModel.getCreatedby()));
				}
			}			
		}
		else {
			List<String> status = monitoringModel.getStatus();
			for(String st: status ) {
				if(st.equals("In Progress")) {
					uniqLogs.addAll(uniqLogsRepository.getLogswithInprogres(startdate, enddate));
				}
				else {
					uniqLogs.addAll(uniqLogsRepository.getLogs(st, startdate, enddate));
				}
			}
		}
		return uniqLogs;
	}


}
