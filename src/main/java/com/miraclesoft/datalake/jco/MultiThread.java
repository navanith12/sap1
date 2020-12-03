package com.miraclesoft.datalake.jco;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import javax.json.JsonArray;
import javax.json.JsonObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.miraclesoft.datalake.model.DbInsert;
import com.miraclesoft.datalake.model.Extractor;
import com.miraclesoft.datalake.mongo.model.Metadata;
import com.miraclesoft.datalake.mongo.model.SocketResponse;


@Component
public class MultiThread {
	
	Logger logger = LoggerFactory.getLogger(SaptodbInsert.class);
	
	@Autowired
	SimpMessagingTemplate template;

	
	public String multithreadInsert(int truncate, Extractor extractor, SocketResponse sr, SaptodbInsert sb,int numofthreads, int packetNumber, DbInsert dbinsert) {//		
		JsonArray results = sb.getResults();
		Metadata[] metadata = sb.getMetadata();
		String extractorName = sb.getExtractorName();
		logger.trace("Data fetch Completed@"+extractor.getName()+"!"+extractor.getInstanceid()+"*"+extractor.getFunction()+"%Package "+packetNumber+" : Data fetch from Web-Service : Completed"+"#"+extractor.getCreatedBy());
		sr.getStatus().add("Data fetch from Web-Service : Completed");
		template.convertAndSend("/topic/user", sr);
		int numThreads = numofthreads;		
		int threadWork = results.size() / numThreads;
		int threadNumber1 = 0;
		Thread[] threads = new Thread[numThreads];
		for (int i = 0; i < numThreads; ++i) {
			logger.trace("In Progress@"+extractor.getName()+"!"+extractor.getInstanceid()+"*"+extractor.getFunction()+"%Parallel process thread "+ i +" : Initiated"+"#"+extractor.getCreatedBy());
			try {
			sr.getStatus().add("Parallel process thread "+ i +" : Initiated");
			this.template.convertAndSend("/topic/user", sr);
			}catch(Exception e) {
				e.printStackTrace();
			}
			final int myStart = i * threadWork;
			final int myEnd = i == numThreads - 1 ? results.size() : (i + 1) * threadWork;
			final int threadNumber = threadNumber1;
			threads[i] = new Thread(new Runnable() {
				@SuppressWarnings("deprecation")
				public void run() {	
					int truncate1 = truncate;
					System.out.println(dbinsert.getConnectionName()+" "+dbinsert.toString());
					String dburl = "jdbc:sqlserver://"+dbinsert.getHostAddress()+":"+dbinsert.getPortnumber()+";databaseName="+dbinsert.getDbname();
//					if("sqlserver".equals(dbinsert.getDbType())) {
					
//					}
//					else {
//						dburl = "jdbc:oracle:thin:@"+dbinsert.getHostAddress()+":"+dbinsert.getPortnumber()+":"+dbinsert.getDbname();
//					}
					String username = dbinsert.getUsername();
					String password = dbinsert.getPassword();
					StringBuilder sql = new StringBuilder();
					sql.append("insert into \"" + extractorName + "\" ("); // for stored proc taking 2 parameters
					for (int i = 0; i < metadata.length; i++) {
						sql.append(metadata[i].getColumName() + ",");
					}
					sql.deleteCharAt(sql.lastIndexOf(","));
					sql.append(") VALUES(");
					for (int i = 0; i < metadata.length; i++) {
						sql.append("?,");
					}
					sql.deleteCharAt(sql.lastIndexOf(","));
					sql.append(")");

					try (Connection conn = DriverManager.getConnection(dburl, username, password);
							Statement statement = conn.createStatement()) {
						if (truncate1 == 0) {
							System.out.println("I Should be here only once");
							statement.execute("truncate table \"" + extractorName + "\"");
						}
						truncate1++;
						PreparedStatement ps = conn.prepareStatement(sql.toString());

						System.out.println("metadata size" + metadata.length);
						// System.out.println("keys size" + keys.size());
						long addbatchStarttime = System.currentTimeMillis();
						// for (JsonObject result : results.getValuesAs(JsonObject.class)) {
						for (int k = myStart; k < myEnd; ++k) {
							JsonObject result = results.getJsonObject(k);							
							for (int i = 0; i < metadata.length; i++) {
								if (metadata[i].getDataType().equals("String")) {
									ps.setString(i + 1, result.getString(metadata[i].getColumName(), ""));
								} else if (metadata[i].getDataType().equals("DateTime")) {
									String temp = result.getString(metadata[i].getColumName(), "");
									String formatted = "";
									if (temp != "") {
										temp = temp.substring(temp.lastIndexOf("(") + 1, temp.lastIndexOf(")"));
										Long temp1 = Long.parseLong(temp);
										java.sql.Date date1 = new java.sql.Date(temp1);
										DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
										format.setTimeZone(TimeZone.getTimeZone("GMT"));
										formatted = format.format(date1);
									}
									java.sql.Date date = (formatted == "") ? java.sql.Date.valueOf("1999-12-31")
											: java.sql.Date.valueOf(formatted);
									ps.setDate(i + 1, date);
								} else if (metadata[i].getDataType().equals("Boolean")) {
									ps.setBoolean(i + 1,
											Boolean.parseBoolean(result.getString(metadata[i].getColumName(), "")));
								} else if (metadata[i].getDataType().equals("Int32")) {
									ps.setInt(i + 1,
											Integer.parseInt(result.getString(metadata[i].getColumName(), "0")));
								} else if (metadata[i].getDataType().equals("Time")) {
									String temp = result.getString(metadata[i].getColumName(), "");
									Time time_sql;
									if (temp != "") {
										int hours = Integer.parseInt(temp.substring(2, 4));
										int minutes = Integer.parseInt(temp.substring(5, 7));
										int seconds = Integer.parseInt(temp.substring(8, 10));
										time_sql = new Time(hours, minutes, seconds);
									} else {
										time_sql = null;
									}
									ps.setTime(i + 1, time_sql);
								} else if (metadata[i].getDataType().equals("Decimal")) {
									ps.setDouble(i+1,
											Double.parseDouble(result.getString(metadata[i].getColumName(), "0")));
								}

							}
							ps.addBatch();
						}
						long addbatchendtime = System.currentTimeMillis();
						System.out.println("time taken to add data to batch: " + (addbatchendtime - addbatchStarttime));
						try {
							addbatchStarttime = System.currentTimeMillis();
							logger.trace("In Progress@"+extractor.getName()+"!"+extractor.getInstanceid()+"*"+extractor.getFunction()+"%Internal Processing for Data fetched from parallel process"+ threadNumber +" : Completed"+"#"+extractor.getCreatedBy());
							sr.getStatus().add("Internal Processing for Data fetched from parallel process"+ threadNumber +" : Completed");
							template.convertAndSend("/topic/user", sr);
							ps.executeLargeBatch();
							logger.trace("In Progress@"+extractor.getName()+"!"+extractor.getInstanceid()+"*"+extractor.getFunction()+"%Inserting Data to data target by parallel process"+threadNumber+": Completed"+"#"+extractor.getCreatedBy());
							sr.getStatus().add("Inserting Data to data target by parallel process"+threadNumber+": Completed");
							template.convertAndSend("/topic/user", sr);
							addbatchendtime = System.currentTimeMillis();
							System.out.println(
									"time taken to add data to batch: " + (addbatchendtime - addbatchStarttime));
						} catch (BatchUpdateException e) {
							long[] updatedRecords = e.getLargeUpdateCounts();
							int count = 1;
							for (long i : updatedRecords) {
								if (i == Statement.EXECUTE_FAILED) {
									count++;
								}								
							}
							System.out.println("Failed counts are : "+count);
							logger.trace("Failed Counts@"+extractor.getName()+"!"+extractor.getInstanceid()+"*"+extractor.getFunction()+"%Failed counts are "+threadNumber+": "+count+"#"+extractor.getCreatedBy());
							sr.getStatus().add("Failed counts are "+threadNumber+": "+count);
							template.convertAndSend("/topic/user", sr);
						}
						System.out.println("time taken to addbatch to db: " + (addbatchendtime - addbatchStarttime));
						System.out.println("testing.....  ");
						conn.commit();
					} catch (Exception e) {
						System.out.println(e.getMessage());
						logger.trace("Cancelled@"+extractor.getName()+"!"+extractor.getInstanceid()+"*"+extractor.getFunction()+"%Exception occured: "+e.getMessage()+"#"+extractor.getCreatedBy());
						sr.getStatus().add("%Exception occured: "+e.getMessage());
						template.convertAndSend("/topic/user", sr);
					}
				}				
			});
			threadNumber1++;
		}
		for (Thread t : threads)
			t.start();
		// now wait them to finish
		for (Thread t : threads) {
			try {
				t.join();
			} catch (InterruptedException ex) {
				System.out.println(ex.getMessage());
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		try {
			logger.trace("Added Data@"+extractor.getName()+"!"+extractor.getInstanceid()+"*"+extractor.getFunction()+"%Added data into table. Records inserted: "+results.size()+"#"+extractor.getCreatedBy());
		}catch(Exception e) {
			System.out.println(e.getMessage());
			logger.trace("Cancelled@"+extractor.getName()+"!"+extractor.getInstanceid()+"*"+extractor.getFunction()+"%"+e.getMessage());
		}
		return "Done";
	}

}
