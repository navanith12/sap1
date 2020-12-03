package com.miraclesoft.datalake.jco;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.miraclesoft.datalake.model.Extractor;
import com.miraclesoft.datalake.model.MonitoringInstance;
import com.miraclesoft.datalake.model.Source;
import com.miraclesoft.datalake.model.Table;
import com.miraclesoft.datalake.mongo.model.SocketResponse;
import com.miraclesoft.datalake.service.BapiService;
import com.miraclesoft.datalake.service.ExtractorService;
import com.miraclesoft.datalake.service.MonitoringInstanceService;
import com.miraclesoft.datalake.service.SourceService;
import com.miraclesoft.datalake.service.TableService;

@Service
public class BatchJobImpl {
	Logger logger = LoggerFactory.getLogger(BatchJobImpl.class);
	@Autowired
	private ExtractorService extractorService;
	
	@Autowired
	private TableService tableService;
	
	@Autowired
	private BapiService bapiService; 
	
	@Autowired
	private JcoSapConnector jcoSapConnector;
	
	@Autowired
	private SourceService sourceService;
	
	@Autowired
	private MonitoringInstanceService monitoringInstanceService;
	
	SocketResponse sr;
	List<String> socketResponse;
	
	public String batchjob(String[] jobLists, String username) throws SQLException {
		for (String batchjob : jobLists) {
			if(batchjob.toLowerCase().startsWith("e")) {
				Extractor extractor = extractorService.findExtractor_extractor(batchjob);
				String type = extractor.getTargetType();
				Source source = sourceService.findSource(extractor.getSourceId());
				MonitoringInstance mi = new MonitoringInstance(extractor.getName());
				monitoringInstanceService.add(mi);
				extractor.setInstanceid(mi.getInstanceid());
				extractorService.update(extractor, extractor.getId());
				logger.trace("Initiated@"+extractor.getName()+"!"+extractor.getInstanceid()+"*"+extractor.getFunction()+"%Batch ExtractorJob Recieved"+"#"+extractor.getCreatedBy());			
				socketResponse = new ArrayList<String>();
				socketResponse.add("Ignore XXX");
				sr = new SocketResponse(extractor.getName(), socketResponse);
				sr.setJobName(extractor.getName());
				switch(type) {
					case "full": try {
						jcoSapConnector.fullload(extractor, source, sr, username);
					} catch (Exception e) {	
						e.printStackTrace();
					}
					break;
					case "delta": try {
						jcoSapConnector.deltaLoad(extractor, source, sr, username);
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
					case "intitializewithoutdata": try {
						jcoSapConnector.initializewithoutdata(extractor, source, sr, username);
					} catch (IOException e) {
						e.printStackTrace();
					}
					break;
					case "intitializewithdata" : try {
						jcoSapConnector.initializewithdata(extractor, source, sr, username);
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				}
			}
			else {
				Table table =  tableService.findTable(batchjob);
				MonitoringInstance mi = new MonitoringInstance(table.getName());
				monitoringInstanceService.add(mi);
				table.setInstanceid(mi.getInstanceid());
				tableService.update(table, table.getId());
				Source source = sourceService.findSource(table.getSourceId());
				logger.trace("Initiated@"+table.getName()+"!"+table.getInstanceid()+"*"+table.getTable()+"%Batch Table job Recieved");
				try {
					jcoSapConnector.db_insert(source, table, sr);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return "Success";
	}
}