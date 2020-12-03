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

import com.miraclesoft.datalake.model.Extractor;
import com.miraclesoft.datalake.mongo.model.Metadata;
import com.miraclesoft.datalake.mongo.model.SocketResponse;

public class SaptodbInsert extends Thread {

	Logger logger = LoggerFactory.getLogger(SaptodbInsert.class);
	
	@Autowired
	SimpMessagingTemplate template;
	
	private JsonArray results;
	private Metadata[] metadata;
	private String extractorName;

	public SaptodbInsert() {

	}

	public SaptodbInsert(JsonArray results, Metadata[] metadata, String extractorName) {
		this.results = results;
		this.metadata = metadata;
		this.extractorName = extractorName;
	}
	
	public JsonArray getResults() {
		return results;
	}

	public void setResults(JsonArray results) {
		this.results = results;
	}

	public Metadata[] getMetadata() {
		return metadata;
	}

	public void setMetadata(Metadata[] metadata) {
		this.metadata = metadata;
	}

	public String getExtractorName() {
		return extractorName;
	}

	public void setExtractorName(String extractorName) {
		this.extractorName = extractorName;
	}
	
	/** public String multithreadInsert(int truncate, Extractor extractor, SocketResponse sr) {
//		
		
		logger.trace("Data fetch Completed@"+extractor.getName()+"!"+extractor.getInstanceid()+"*"+extractor.getFunction()+"%Data fetch from Web-Service : Completed");
		try {
			sr.setStatus("Data fetch from Web-Service : Completed");
			System.out.println(sr.getJobName());
			System.out.println(sr.getStatus());
			template.convertAndSend("/topic/user", "Data fetch from Web-Service : Completed");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		int numThreads = 10;		
		int threadWork = results.size() / numThreads;
		int threadNumber1 = 0;
		Thread[] threads = new Thread[numThreads];
		for (int i = 0; i < numThreads; ++i) {
			logger.trace("In Progress@"+extractor.getName()+"!"+extractor.getInstanceid()+"*"+extractor.getFunction()+"%Parallel process thread "+ i +" : Initiated");
			try {
			sr.setStatus("Parallel process thread "+ i +" : Initiated");
			System.out.println(sr.getStatus());
			System.out.println(sr.getJobName());
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
					String dburl = "jdbc:sqlserver://172.17.0.141:1433;databaseName=SAPDATALAKE";
					String username = "SAPUSER";
					String password = "Miracle@1";

					StringBuilder sql = new StringBuilder();
					sql.append("insert into \"" + extractorName + "\" ("); // for stored proc taking 2 parameters
					for (int i = 0; i < metadata.size(); i++) {
						sql.append(metadata.get(i).getColumName() + ",");
					}
					sql.deleteCharAt(sql.lastIndexOf(","));
					sql.append(") VALUES(");
					for (int i = 0; i < metadata.size(); i++) {
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

						System.out.println("metadata size" + metadata.size());
						// System.out.println("keys size" + keys.size());
						long addbatchStarttime = System.currentTimeMillis();
						// for (JsonObject result : results.getValuesAs(JsonObject.class)) {
						for (int k = myStart; k < myEnd; ++k) {
							JsonObject result = results.getJsonObject(k);							
							for (int i = 0; i < metadata.size(); i++) {
								if (metadata.get(i).getDataType().equals("String")) {
									ps.setString(i + 1, result.getString(metadata.get(i).getColumName(), ""));
								} else if (metadata.get(i).getDataType().equals("DateTime")) {
									String temp = result.getString(metadata.get(i).getColumName(), "");
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
								} else if (metadata.get(i).getDataType().equals("Boolean")) {
									ps.setBoolean(i + 1,
											Boolean.parseBoolean(result.getString(metadata.get(i).getColumName(), "")));
								} else if (metadata.get(i).getDataType().equals("Int32")) {
									ps.setInt(i + 1,
											Integer.parseInt(result.getString(metadata.get(i).getColumName(), "0")));
								} else if (metadata.get(i).getDataType().equals("Time")) {
									String temp = result.getString(metadata.get(i).getColumName(), "");
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
								} else if (metadata.get(i).getDataType().equals("Decimal")) {
									ps.setDouble(i+1,
											Double.parseDouble(result.getString(metadata.get(i).getColumName(), "0")));
								}

							}
							ps.addBatch();
						}
						long addbatchendtime = System.currentTimeMillis();
						System.out.println("time taken to add data to batch: " + (addbatchendtime - addbatchStarttime));
						try {
							addbatchStarttime = System.currentTimeMillis();
							logger.trace("In Progress@"+extractor.getName()+"!"+extractor.getInstanceid()+"*"+extractor.getFunction()+"%Internal Processing for Data fetched from parallel process "+ threadNumber +" : Completed");
							sr.setStatus("Internal Processing for Data fetched from parallel process "+ threadNumber +" : Completed");
//							template.convertAndSend("/topic/user", sr);
							ps.executeLargeBatch();
							logger.trace("In Progress@"+extractor.getName()+"!"+extractor.getInstanceid()+"*"+extractor.getFunction()+"%Inserting Data to data target by parallel process "+ threadNumber+": Completed");
							sr.setStatus("Inserting Data to data target by parallel process "+threadNumber+": Completed");
//							template.convertAndSend("/topic/user", sr);
							addbatchendtime = System.currentTimeMillis();
							System.out.println(
									"time taken to add data to batch: " + (addbatchendtime - addbatchStarttime));
						} catch (BatchUpdateException e) {
							long[] updatedRecords = e.getLargeUpdateCounts();

							int count = 1;
							for (long i : updatedRecords) {
								if (i == Statement.EXECUTE_FAILED) {
									//System.out.print("Error on request " + count + ": Execute failed ");
									count++;
								}
//					                else {
//					                    System.out.print("Request " + count +": OK ");
//					                }
								
							}
							System.out.println("Failed counts are : "+count);
							logger.trace("Failed Counts@"+extractor.getName()+"!"+extractor.getInstanceid()+"*"+extractor.getFunction()+"%Failed counts are "+threadNumber+": "+count);
							sr.setStatus("Failed counts are "+threadNumber+": "+count);
							template.convertAndSend("/topic/user", sr);
						}
						System.out.println("time taken to addbatch to db: " + (addbatchendtime - addbatchStarttime));
						System.out.println("testing.....  ");
						conn.commit();
					} catch (Exception e) {
						System.out.println(e.getMessage());
						logger.trace("Failed@"+extractor.getName()+"!"+extractor.getInstanceid()+"*"+extractor.getFunction()+"%Exception occured: "+e.getMessage());
						sr.setStatus("%Exception occured: "+e.getMessage());
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
			}
		}
		return "Done";
	} **/

}
