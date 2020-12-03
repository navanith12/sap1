package com.miraclesoft.datalake.jco;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.util.concurrent.Service.State;
import com.miraclesoft.datalake.azure.AzureStorageService;
import com.miraclesoft.datalake.model.Bapi;
import com.miraclesoft.datalake.model.DbInsert;
import com.miraclesoft.datalake.model.Delta;
import com.miraclesoft.datalake.model.Extractor;
import com.miraclesoft.datalake.model.ExtractorServiceName;
import com.miraclesoft.datalake.model.FetchedRecordsCount;
import com.miraclesoft.datalake.model.FtpServer;
import com.miraclesoft.datalake.model.Packetsizeconfig;
import com.miraclesoft.datalake.model.SkipToken;
import com.miraclesoft.datalake.model.Source;
import com.miraclesoft.datalake.model.SourceDatabase;
import com.miraclesoft.datalake.model.Table;
import com.miraclesoft.datalake.model.table_filter;
import com.miraclesoft.datalake.mongo.model.Bapi_exportparam;
import com.miraclesoft.datalake.mongo.model.ImportParameterModel;
import com.miraclesoft.datalake.mongo.model.Metadata;
import com.miraclesoft.datalake.mongo.model.MetadataStructure;
import com.miraclesoft.datalake.mongo.model.SocketResponse;
import com.miraclesoft.datalake.mongo.service.MetadataStructureService;
import com.miraclesoft.datalake.service.DbInsertService;
import com.miraclesoft.datalake.service.DeltaService;
import com.miraclesoft.datalake.service.ExtractorServiceNameService;
import com.miraclesoft.datalake.service.FetchedRecordsCountService;
import com.miraclesoft.datalake.service.FtpServerService;
import com.miraclesoft.datalake.service.PacketsizeconfigService;
import com.miraclesoft.datalake.service.SkipTokenService;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFieldIterator;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoFunctionTemplate;
import com.sap.conn.jco.JCoListMetaData;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoRecordMetaData;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;
import com.sap.conn.jco.ext.DestinationDataProvider;

@Service
public class JcoSapConnector {

	Logger logger = LoggerFactory.getLogger(JcoSapConnector.class);

	@Autowired
	SimpMessagingTemplate template;

	@Autowired
	private DeltaService deltaService;

	@Autowired
	private JdbcTemplate jdbctemplate;

	@Autowired
	private MetadataStructureService metadataStructureService;

	@Autowired
	private ExtractorServiceNameService extractorServiceNameService;

	@Autowired
	private FtpServerService ftpservice;

	@Autowired
	private PacketsizeconfigService packetsizeconfigService;

	@Autowired
	private SkipTokenService skipTokenService;
	
	@Autowired
	private DbInsertService dbInsertService;

	@Autowired
	private MultiThread multithread;
	
	@Autowired
	private FetchedRecordsCountService fetchedRecordsCountService;
//		connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST, "192.168.1.30");
//		connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR, "01");
//		connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, "100");
//		connectProperties.setProperty(DestinationDataProvider.JCO_USER, "gkorukula");
//		connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, "Winter100");
//		connectProperties.setProperty(DestinationDataProvider.JCO_LANG, "en");

	public static JCoDestination getSourceDestination(Source source) throws JCoException {

		String destinationName = "sap_system_without_pool";

		Properties connectProperties = new Properties();
		connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST, source.getApplicationServer());
		connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR, Integer.toString(source.getInstances()));
		connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, Integer.toString(source.getClient()));
		connectProperties.setProperty(DestinationDataProvider.JCO_USER, source.getLogin());
		connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, source.getPassword());
		connectProperties.setProperty(DestinationDataProvider.JCO_LANG, "en");

		File destCfg = new File(destinationName + ".jcoDestination");
		try {
			FileOutputStream fos = new FileOutputStream(destCfg, false);
			connectProperties.store(fos, "for tests only !");
			fos.close();
		} catch (Exception e) {
			throw new RuntimeException("Unable to create the destination files", e);
		}

		JCoDestination destination = JCoDestinationManager.getDestination(destinationName);

		return destination;
	}

	public List<String> table_names(Source source) throws JCoException {

		List<String> table_list = new ArrayList<>();

		JCoDestination destination = getSourceDestination(source);
		System.out.println("destination:" + destination);
		JCoFunction function = destination.getRepository().getFunctionTemplate("RFC_READ_TABLE").getFunction();

		function.getImportParameterList().setValue("QUERY_TABLE", "DD02L");
		function.getImportParameterList().setValue("DELIMITER", ",");
		function.getImportParameterList().setValue("ROWCOUNT", Integer.valueOf(10000));

		try {
			/* code to get data of a specified row TRANSP and field name TABNAME */

			JCoTable returnOptions = function.getTableParameterList().getTable("OPTIONS");
			returnOptions.appendRow();
			returnOptions.setValue("TEXT", " TABCLASS  = 'TRANSP'");
			System.out.println("test: " + returnOptions.getValue(0));

			JCoTable returnFields = function.getTableParameterList().getTable("FIELDS");
			returnFields.appendRow();
			returnFields.setValue("FIELDNAME", "TABNAME");

			function.execute(destination);

			JCoTable jcoTabled = function.getTableParameterList().getTable("DATA");

			int numRows = jcoTabled.getNumRows();

			if (numRows > 0) {
				for (int iRow = 0; iRow < numRows; iRow++) {
					jcoTabled.setRow(iRow);
					String sMessage = jcoTabled.getString("WA");
					table_list.add(sMessage);
				}
			}
		} catch (AbapException e) {
			System.out.println(e.toString());
		}
		return table_list;

	}

	public List<String> table_names_reg(String tablereg, Source source) throws JCoException {

		List<String> table_list = new ArrayList<>();

		JCoDestination destination = getSourceDestination(source);
		System.out.println("destination:" + destination);
		JCoFunction function = destination.getRepository().getFunctionTemplate("RFC_READ_TABLE").getFunction();

		function.getImportParameterList().setValue("QUERY_TABLE", "DD02L");
		function.getImportParameterList().setValue("DELIMITER", ",");
		function.getImportParameterList().setValue("ROWCOUNT", Integer.valueOf(10000));

		try {
			/* code to get data of a specified row TRANSP and field name TABNAME */

			JCoTable returnOptions = function.getTableParameterList().getTable("OPTIONS");
			returnOptions.appendRow();
			returnOptions.setValue("TEXT", " TABCLASS  = 'TRANSP'");
			returnOptions.appendRow();
			returnOptions.setValue("TEXT", "TABNAME = 'T02*");
			System.out.println("test: " + returnOptions.getValue(0));

			JCoTable returnFields = function.getTableParameterList().getTable("FIELDS");
			returnFields.appendRow();
			returnFields.setValue("FIELDNAME", "TABNAME");

			function.execute(destination);

			JCoTable jcoTabled = function.getTableParameterList().getTable("DATA");

			int numRows = jcoTabled.getNumRows();

			if (numRows > 0) {
				for (int iRow = 0; iRow < numRows; iRow++) {
					jcoTabled.setRow(iRow);
					String sMessage = jcoTabled.getString("WA");
					table_list.add(sMessage);
				}
			}
		} catch (AbapException e) {
			System.out.println(e.toString());
		}
		return table_list;

	}

	public List<String> table_columns(String tableName, Source source) throws JCoException {
		List<String> column_list = new ArrayList<>();
		try {

			JCoDestination destination = getSourceDestination(source);
			System.out.println("destination:" + destination);
			JCoFunction function = destination.getRepository().getFunction("DDIF_FIELDINFO_GET");

			JCoParameterList func_param = function.getImportParameterList();

			func_param.setValue("TABNAME", tableName);

			function.execute(destination);

			System.out.println("Setting FIELDS");

			function.execute(destination);

			JCoTable jcoTabled = function.getTableParameterList().getTable("DFIES_TAB");
			jcoTabled.appendRow();
			jcoTabled.setValue("FIELDNAME", "FIELDNAME");
			jcoTabled.getFieldCount();
			System.out.println("num of rows: " + jcoTabled.getNumRows());

			int numRows = jcoTabled.getNumRows();
			System.out.println("numRows > " + numRows);

			if (numRows > 0) {
				for (int iRow = 0; iRow < numRows; iRow++) {
					jcoTabled.setRow(iRow);
					String table = jcoTabled.getString("FIELDNAME");
					column_list.add(table);
				}
				System.out.println(column_list);
			}
		} catch (AbapException e) {
			System.out.println(e.toString());
		}

		return column_list;
	}

	public JSONArray preview_table(Source source, String tableName, String columns) {

		JSONArray data_array = new JSONArray();
		JSONObject columnValues = new JSONObject();
		// headers.add("Prefer", "odata.track-changes");
		try {
			JCoDestination destination = getSourceDestination(source);
			String[] column = new String[columns.split(",").length];
			if (columns.contains(",")) {
				column = columns.split(",");
			} else if(!columns.contains(",")){
				column[0] = columns;
			}else {
				column = null;
			}
			
			int rowCount = 100;

			JCoFunction function = destination.getRepository().getFunctionTemplate("RFC_READ_TABLE").getFunction();

			function.getImportParameterList().setValue("QUERY_TABLE", tableName.toUpperCase());
			function.getImportParameterList().setValue("DELIMITER", ":");

			//System.out.println("columns length: " + column.length);
			if(column != null && column.length > 0) {
				for (int i = 0; i < column.length; i++) {
					JCoTable returnFields = function.getTableParameterList().getTable("FIELDS");
					returnFields.appendRow();
					returnFields.setValue("FIELDNAME", column[i].trim());
					returnFields.getFieldCount();
				}
			}			
			
			function.execute(destination);
			JCoTable jcoTabled = function.getTableParameterList().getTable("DATA");
			
			int numRows1 = jcoTabled.getNumRows();
			System.out.println(numRows1);
			if (rowCount > numRows1) {
				rowCount = numRows1;
			}

			if (rowCount > 0) {			
				for (int iRow = 0; iRow < rowCount; iRow++) {
						jcoTabled.setRow(iRow);
						String[] data_msg = jcoTabled.getString("WA").split(":");
						if (data_msg.length <= column.length) {
							for (int col = 0; col < data_msg.length; col++) {
								if (data_msg[col].equals(null) || data_msg[col].equals("")|| data_msg[col].trim().length() < 1) {
									data_msg[col] = "0";
								}
								System.out.println(data_msg[col].trim());
								columnValues.put(column[col], data_msg[col].trim());
							}
						}
						data_array.put(columnValues);
					}
					
					
				}
			
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
		
		return data_array;
	
	}
	
	public String db_insert(Source source, Table table, SocketResponse sr) throws IOException, SQLException {
		// headers.add("Prefer", "odata.track-changes");
		DbInsert dbinsert = dbInsertService.findDbInsertByid(table.getTargetId());
		try {
			JCoDestination destination = getSourceDestination(source);
			String tableName = table.getTable();
			String[] column = new String[table.getFields().split(",").length];
			System.out.println("Fields are : " + table.getFields().split(",").length);
			if (table.getFields().contains(",")) {
				column = table.getFields().split(",");
			} else if (!table.getFields().contains(",")) {
				column[0] = table.getFields();
			} else {
				column = null;
			}

			for (String a : column) {
				System.out.println("column: " + a);
			}

			int rowCount = table.getRows();
			// List<String> data = new ArrayList<>();
			JCoFunction function = destination.getRepository().getFunctionTemplate("RFC_READ_TABLE").getFunction();

			function.getImportParameterList().setValue("QUERY_TABLE", tableName.toUpperCase());
			function.getImportParameterList().setValue("DELIMITER", ":");

			// System.out.println("columns length: " + column.length);
			if (column != null && column.length > 0) {
				for (int i = 0; i < column.length; i++) {
					JCoTable returnFields = function.getTableParameterList().getTable("FIELDS");
					returnFields.appendRow();
					returnFields.setValue("FIELDNAME", column[i]);
					returnFields.getFieldCount();
				}
			}
			JCoTable returnFields = function.getTableParameterList().getTable("FIELDS");

			if (table.getFilteroptions().size() > 0) {
				Set<table_filter> table_options = table.getFilteroptions();
				for (table_filter options : table_options) {
					String optionvalue = "\" " + options.getColumnName() + " " + options.getOperator() + " " + "'"
							+ options.getColumnValue() + "'\"";
					System.out.println(optionvalue);
					JCoTable returnOptions = function.getTableParameterList().getTable("OPTIONS");
					returnOptions.appendRow();
					returnOptions.setValue("TEXT", optionvalue);
				}
			}

			System.out.println("after filters");
			// filter options creation
			// filter-options ended
			logger.trace("Found the source@" + table.getName() + "!" + table.getInstanceid() + "*" + table.getTable()
					+ "%Table Job sent to SAP" + "#" + table.getCreatedBy());
			sr.getStatus().add("Table Job sent to SAP");
			template.convertAndSend("/topic/user", sr);
			function.execute(destination);
			logger.trace("Found the source@" + table.getName() + "!" + table.getInstanceid() + "*" + table.getTable()
					+ "%Table Data Received from SAP" + "#" + table.getCreatedBy());
			sr.getStatus().add("Table Data Received from SAP");
			template.convertAndSend("/topic/user", sr);
			JCoTable jcoTabled = function.getTableParameterList().getTable("DATA");
//			System.out.println(returnFields);
//			System.out.println(jcoTabled);
			int numRows1 = jcoTabled.getNumRows();
			System.out.println(numRows1);
			if (rowCount >= numRows1) {
				rowCount = numRows1;
			}

			if (rowCount > 0) {
				String dburl = "jdbc:sqlserver://"+dbinsert.getHostAddress()+":"+dbinsert.getPortnumber()+";databaseName="+dbinsert.getDbname();
				String username = dbinsert.getUsername();
				String password = dbinsert.getPassword();

				StringBuilder sql = new StringBuilder();
				StringBuilder cresql = new StringBuilder();
				cresql.append("CREATE TABLE \"" + tableName + "\" (ID INT IDENTITY(1,1) PRIMARY KEY,");
				for (int i = 0; i < column.length; i++) {
					// columnsRow.createCell(i + 1).setCellValue(column[i]);
					cresql.append(column[i] + " ");
					cresql.append("VARCHAR(50)");
					cresql.append(", ");
				}
				cresql.deleteCharAt(cresql.lastIndexOf(","));
				cresql.append(");");
				if (checktable(table.getTable(), dbinsert)) {
					try (Connection conn = DriverManager.getConnection(dburl, username, password)) {
						Statement st = conn.createStatement();
						st.execute(cresql.toString());
					} catch (SQLException s) {
						s.printStackTrace();
						System.out.println("SQL statement is not executed!");
					}
					logger.trace("In Progress@" + table.getName() + "!" + table.getInstanceid() + "*" + table.getTable()
							+ "%Table Created in Database" + "#" + table.getCreatedBy());
					sr.getStatus().add("Table Created in Database");
					template.convertAndSend("/topic/user", sr);
				} else {
					logger.trace("In Progress@" + table.getName() + "!" + table.getInstanceid() + "*" + table.getTable()
							+ "%Table Present in Database" + "#" + table.getCreatedBy());
					sr.getStatus().add("Table Present in Database");
					template.convertAndSend("/topic/user", sr);
				}
				sql.append("insert into \"" + tableName + "\" (");
				// tableRow.createCell(1).setCellValue(tableName);
				// Create a Row

				for (int i = 0; i < column.length; i++) {
					// columnsRow.createCell(i + 1).setCellValue(column[i]);
					sql.append(column[i] + ",");
				}

				sql.deleteCharAt(sql.lastIndexOf(","));
				sql.append(") VALUES(");

				for (int i = 0; i < column.length; i++) {
					// columnsRow.createCell(i + 1).setCellValue(column[i]);
					sql.append("?,");
				}
				sql.deleteCharAt(sql.lastIndexOf(","));
				sql.append(")");

				try (Connection conn = DriverManager.getConnection(dburl, username, password);
						Statement statement = conn.createStatement()) {
					System.out.println(sql.toString());
					PreparedStatement ps = conn.prepareStatement(sql.toString());

					for (int iRow = 0; iRow < rowCount; iRow++) {
						jcoTabled.setRow(iRow);
						String[] data_msg = jcoTabled.getString("WA").split(":");
						// System.out.print("length is :" + data_msg.length + " ");
						System.out.println(data_msg.length + " " + column.length);
						if (data_msg.length <= column.length) {
							for (int col = 0; col < data_msg.length; col++) {
								if (data_msg[col].equals(null) || data_msg[col].equals("")
										|| data_msg[col].trim().length() < 1) {
									data_msg[col] = "0";
								}
								ps.setString(col + 1, data_msg[col].trim());
							}
							ps.addBatch();
						}
					}
					logger.trace("In Progress@" + table.getName() + "!" + table.getInstanceid() + "*" + table.getTable()
							+ "%Table Data added to Batch" + "#" + table.getCreatedBy());
					sr.getStatus().add("Table Data added to Batch");
					template.convertAndSend("/topic/user", sr);
					try {
						System.out.println(ps.executeBatch());
						logger.trace("In Progress@" + table.getName() + "!" + table.getInstanceid() + "*"
								+ table.getTable() + "%Table Data added to Database" + "#" + table.getCreatedBy());
						sr.getStatus().add("Table Data added to Database");
						template.convertAndSend("/topic/user", sr);
						System.out.println(ps.getLargeUpdateCount());
					} catch (Exception e) {
						logger.trace("Cancelled@" + table.getName() + "!" + table.getInstanceid() + "*"
								+ table.getTable() + "% Can`t find the source " + source.getApplicationServer()
								+ "Job Cannot be initiated" + e.getMessage() + "#" + table.getCreatedBy());
						sr.getStatus().add("Error: " + e.getMessage());
						template.convertAndSend("/topic/user", sr);
						System.out.println(e.getMessage());
						return "Cancelled";
					}
					conn.close();
				}

			}
		} catch (Exception e) {
			logger.trace("Cancelled@" + table.getName() + "!" + table.getInstanceid() + "*" + table.getTable()
					+ "% Can`t find the source " + source.getApplicationServer() + "Job Cannot be initiated"
					+ e.getMessage() + "#" + table.getCreatedBy());
			sr.getStatus().add("Error" + e.getMessage());
			template.convertAndSend("/topic/user", sr);
			e.printStackTrace();
			return "Cancelled";
		}
		long countOfRecords = getrowCount(table.getTable(), dbinsert);
		logger.trace("In Progress@" + table.getName() + "!" + table.getInstanceid() + "*" + table.getTable()
				+ "%Added all the data into table. Total records inserted: " + countOfRecords + "#"
				+ table.getCreatedBy());
		sr.getStatus().add("Added all the data into table. Total records inserted: " + countOfRecords);
		sr.setJobStatus("In Progress");
		template.convertAndSend("/topic/user", sr);
		logger.trace("Finished@" + table.getName() + "!" + table.getInstanceid() + "*" + table.getTable()
				+ "%Job Completed Successfully" + "#" + table.getCreatedBy());
		sr.getStatus().add("Job Completed Successfully");
		sr.setJobStatus("Finished");
		template.convertAndSend("/topic/user", sr);
		return "Done inserting records";
	}

	public ResponseEntity<InputStreamResource> localFile_response(Source source, Table table) throws IOException {
		ResponseEntity<InputStreamResource> response = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		XSSFWorkbook workbook = new XSSFWorkbook();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.add("Pragma", "no-cache");
		headers.add("Expires", "0");
		// headers.add("Prefer", "odata.track-changes");
		try {
			JCoDestination destination = getSourceDestination(source);
			String tableName = table.getTable();
			String[] column = new String[table.getFields().length()];
			if (table.getFields().contains(",")) {
				column = table.getFields().split(",");
			} else {
				column[0] = table.getFields();
			}

			int rowCount = table.getRows();
			System.out.println(rowCount);
			List<String> data = new ArrayList<>();
			JCoFunction function = destination.getRepository().getFunctionTemplate("RFC_READ_TABLE").getFunction();

			function.getImportParameterList().setValue("QUERY_TABLE", tableName.toUpperCase());
			function.getImportParameterList().setValue("DELIMITER", ",");

			System.out.println("columns length: " + column.length);
			for (int i = 0; i < column.length; i++) {
				JCoTable returnFields = function.getTableParameterList().getTable("FIELDS");
				returnFields.appendRow();
				returnFields.setValue("FIELDNAME", column[i]);
				returnFields.getFieldCount();
			}

			if (table.getFilteroptions().size() > 0) {
				Set<table_filter> table_options = table.getFilteroptions();
				for (table_filter options : table_options) {
					String optionvalue = "\" " + options.getColumnName() + " " + options.getOperator() + " " + "'"
							+ options.getColumnValue() + "'\"";
					System.out.println(optionvalue);
					JCoTable returnOptions = function.getTableParameterList().getTable("OPTIONS");
					returnOptions.appendRow();
					returnOptions.setValue("TEXT", optionvalue);
				}
			}
			System.out.println("after filters");
			function.execute(destination);

			JCoTable jcoTabled = function.getTableParameterList().getTable("DATA");
			int numRows1 = jcoTabled.getNumRows();

			if (numRows1 <= rowCount) {
				rowCount = numRows1;
			}

			if (rowCount > 0) {
				XSSFSheet sheet = workbook.createSheet("Data");

				Font headerFont = workbook.createFont();
				headerFont.setBold(true);
				// headerFont.setBold(true);
				headerFont.setFontHeightInPoints((short) 14);
				headerFont.setColor(IndexedColors.RED.getIndex());

				CellStyle headerCellStyle = workbook.createCellStyle();
				headerCellStyle.setFont(headerFont);

				Row tableRow = sheet.createRow(0);
				tableRow.createCell(0).setCellValue("Table:");
				tableRow.createCell(1).setCellValue(tableName);
				// Create a Row
				Row columnsRow = sheet.createRow(1);
				columnsRow.createCell(0).setCellValue("#");

//				 MongoClient mongo = new MongoClient( "localhost" , 27017 );
//                 MongoDatabase database = mongo.getDatabase("sap_data_lake");
//                 MongoCollection<Document> collection = database.getCollection("sampleTestCollection");
//				 Document doc = new Document();
//				 JsonObject objtest;

				for (int i = 0; i < column.length; i++) {
					columnsRow.createCell(i + 1).setCellValue(column[i]);
				}

				for (int iRow = 0; iRow < rowCount; iRow++) {
					jcoTabled.setRow(iRow);
					String sMessage = jcoTabled.getString("WA");
					Row row = sheet.createRow(iRow + 2);
					String[] data_msg = jcoTabled.getString("WA").split(",");
					row.createCell(0).setCellValue(iRow + 1);
					for (int col = 0; col < data_msg.length; col++) {
						row.createCell(col + 1).setCellValue(data_msg[col]);
					}
					data.add(sMessage);
				}
			}
			workbook.write(bos);
			byte[] bytes = bos.toByteArray();
			InputStream is = new ByteArrayInputStream(bytes);
			InputStreamResource resource = new InputStreamResource(is);
			response = ResponseEntity.ok().headers(headers).contentLength(bytes.length)
					.contentType(MediaType.parseMediaType("application/octet-stream")).body(resource);
		} catch (JCoException e) {
			e.printStackTrace();
		} finally {
			workbook.close();
			bos.close();
		}
		return response;
	}
	
	@SuppressWarnings("deprecation")
	public String azuredb_response(Source source, Table table, SocketResponse sr) throws IOException{
		String FILE_TO = "C:\\test\\" + table.getName() + ".csv";
		// headers.add("Prefer", "odata.track-changes");
		try {
			JCoDestination destination = getSourceDestination(source);
			String tableName = table.getTable();
			String[] column = new String[table.getFields().length()];
			if (table.getFields().contains(",")) {
				column = table.getFields().split(",");
			} else {
				column[0] = table.getFields();
			}

			int rowCount = table.getRows();
			List<String> data = new ArrayList<>();
			JCoFunction function = destination.getRepository().getFunctionTemplate("RFC_READ_TABLE").getFunction();

			function.getImportParameterList().setValue("QUERY_TABLE", tableName.toUpperCase());
			function.getImportParameterList().setValue("DELIMITER", ",");

			System.out.println("columns length: " + column.length);
			for (int i = 0; i < column.length; i++) {
				JCoTable returnFields = function.getTableParameterList().getTable("FIELDS");
				returnFields.appendRow();
				returnFields.setValue("FIELDNAME", column[i]);
				returnFields.getFieldCount();
			}

			if (table.getFilteroptions().size() > 0) {
				Set<table_filter> table_options = table.getFilteroptions();
				for (table_filter options : table_options) {
					String optionvalue = "\" " + options.getColumnName() + " " + options.getOperator() + " " + "'"
							+ options.getColumnValue() + "'\"";
					System.out.println(optionvalue);
					JCoTable returnOptions = function.getTableParameterList().getTable("OPTIONS");
					returnOptions.appendRow();
					returnOptions.setValue("TEXT", optionvalue);
				}
			}
			System.out.println("after filters");
			function.execute(destination);

			StringBuilder azure_sb = new StringBuilder();
			JCoTable jcoTabled = function.getTableParameterList().getTable("DATA");
			int numRows1 = jcoTabled.getNumRows();

			if (numRows1 <= rowCount) {
				rowCount = numRows1;
			}

			if (rowCount > 0) {
				
				for (int i = 0; i < column.length; i++) {
					azure_sb.append(column[i]);
					if (i < column.length - 1) {
						azure_sb.append(",");
					}
				}
				azure_sb.append("\n");
				for (int iRow = 0; iRow < rowCount; iRow++) {
					jcoTabled.setRow(iRow);
					String sMessage = jcoTabled.getString("WA");
					String[] data_msg = jcoTabled.getString("WA").split(",");
					azure_sb.append(sMessage);
					azure_sb.append("\n");
					data.add(sMessage);
				}
			}
			
			String azurecsv = azure_sb.toString();
			System.out.println(azurecsv);
			File azure_file = new File(FILE_TO);
			FileUtils.writeStringToFile(azure_file, azurecsv);
			String storageName = "miraclesapdatalake";
			String accessKey = "7QH/vni3BuRc4EpwjzUBoqVqdKI6BAuLeo9owLU1mR3/YnrYezuJAnKXKMYIhn/d7Qt/A7WlKVLOBv5ePX2F+A==";
			AzureStorageService azureFileUtil = new AzureStorageService(accessKey, storageName);
			String containerName = "sapdatalake";
			azureFileUtil.uploadDownloadtoAzure(containerName, FILE_TO, "upload");
			logger.trace("Finished@" + table.getName() + "!" + table.getInstanceid() + "*" + table.getTable()
			+ "%Job Completed Successfully" + "#" + table.getCreatedBy());
			sr.getStatus().add("Job Completed Successfully");
			sr.setJobStatus("Finished");
//			workbook.write(bos);
//			byte[] bytes = bos.toByteArray();
//			InputStream is = new ByteArrayInputStream(bytes);
//			InputStreamResource resource = new InputStreamResource(is);
//			response = ResponseEntity.ok().headers(headers).contentLength(bytes.length)
//					.contentType(MediaType.parseMediaType("application/octet-stream")).body(resource);
		} catch (JCoException e) {
			e.printStackTrace();
		} 
		return "Azure file uploaded";
	
	}

	public String mongoDb_response(Source source, Table table) throws IOException {

		try {
			JCoDestination destination = getSourceDestination(source);
			String tableName = table.getTable();
			String[] column = new String[table.getFields().length()];
			if (table.getFields().contains(",")) {
				column = table.getFields().split(",");
			} else {
				column[0] = table.getFields();
			}

			int rowCount = table.getRows();
			List<String> data = new ArrayList<>();
			JCoFunction function = destination.getRepository().getFunctionTemplate("RFC_READ_TABLE").getFunction();

			function.getImportParameterList().setValue("QUERY_TABLE", tableName.toUpperCase());
			System.out.println(tableName);
			function.getImportParameterList().setValue("DELIMITER", ",");

			for (int i = 0; i < column.length; i++) {
				JCoTable returnFields = function.getTableParameterList().getTable("FIELDS");
				returnFields.appendRow();
				returnFields.setValue("FIELDNAME", column[i]);
				System.out.println("length: " + column.length);
				returnFields.getFieldCount();
			}

			if (table.getFilteroptions().size() > 0) {
				Set<table_filter> table_options = table.getFilteroptions();
				for (table_filter options : table_options) {
					String optionvalue = "\" " + options.getColumnName() + " " + options.getOperator() + " " + "'"
							+ options.getColumnValue() + "'\"";
					System.out.println(optionvalue);
					JCoTable returnOptions = function.getTableParameterList().getTable("OPTIONS");
					returnOptions.appendRow();
					returnOptions.setValue("TEXT", optionvalue);
				}
			}

//			// filter options creation
//			if (table.getOption() != null && table.getOperator() != null && table.getValue() != null) {
//				JCoTable returnOptions = function.getTableParameterList().getTable("OPTIONS");
//				returnOptions.appendRow();
//				returnOptions.setValue("TEXT", table.getOption() + table.getOperator() + "'" + table.getValue() + "'");
//				System.out.println("testing: " + table.getOption() + table.getOperator() + table.getValue());
//				System.out.println(returnOptions.getValue(0));
//				// "\""+table.getOption()+table.getOperator()+"'"+table.getValue()+"'\""
//			}
//
//			if (table.getOption1() != null && table.getOperator1() != null && table.getValue1() != null) {
//				JCoTable returnOptions = function.getTableParameterList().getTable("OPTIONS");
//				returnOptions.appendRow();
//				returnOptions.setValue("TEXT", table.getOption() + table.getOperator() + "'" + table.getValue() + "'");
//				System.out.println(returnOptions.getValue(0));
//				// "\""+table.getOption()+table.getOperator()+"'"+table.getValue()+"'\""
//			}
//
//			if (table.getOption2() != null && table.getOperator2() != null && table.getValue2() != null) {
//				JCoTable returnOptions = function.getTableParameterList().getTable("OPTIONS");
//				returnOptions.appendRow();
//				returnOptions.setValue("TEXT", table.getOption() + table.getOperator() + "'" + table.getValue() + "'");
//				System.out.println(returnOptions.getValue(0));
//				// "\""+table.getOption()+table.getOperator()+"'"+table.getValue()+"'\""
//			}

			function.execute(destination);

			JCoTable jcoTabled = function.getTableParameterList().getTable("DATA");
			int numRows1 = jcoTabled.getNumRows();

			if (numRows1 <= rowCount) {
				rowCount = numRows1;
			}

			if (rowCount > 0) {

				JSONArray ja = new JSONArray();
				for (int iRow = 0; iRow < rowCount; iRow++) {
					JSONObject jo = new JSONObject();
					jcoTabled.setRow(iRow);
					String sMessage = jcoTabled.getString("WA");
					String[] data_msg = jcoTabled.getString("WA").split(",");
					for (int col = 0; col < data_msg.length; col++) {
						jo.put(column[col], data_msg[col]);
						System.out.println("document is inserted successfully");
					}
					data.add(sMessage);

					ja.put(jo);

				}

				MongoClient mongoClient = new MongoClient("localhost", 27017);
				MongoDatabase db = mongoClient.getDatabase("sap_data_lake1");
				MongoCollection<Document> collection = db.getCollection("sampleTestCollection1");

				List<Document> jsonList = new ArrayList<Document>();
				for (Object object : ja) {
					JSONObject jsonStr = (JSONObject) (object);
					Document jsnObject = Document.parse(jsonStr.toString());
					jsonList.add(jsnObject);

				}
				collection.insertMany(jsonList);
				mongoClient.close();
			}

		} catch (JCoException e) {
			e.printStackTrace();
		}

		return "Success";

	}

	public ResponseEntity<InputStreamResource> bapi_response(Bapi bapi, Source source) throws IOException {

		ResponseEntity<InputStreamResource> response = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		XSSFWorkbook workbook = new XSSFWorkbook();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.add("Pragma", "no-cache");
		headers.add("Expires", "0");
		if (bapi.getFunction().equals("BAPI_COMPANYCODE_GETLIST")) {
			try {
				JCoDestination destination = getSourceDestination(source);
				JCoFunctionTemplate function = destination.getRepository()
						.getFunctionTemplate("BAPI_COMPANYCODE_GETLIST");
				JCoFunction func = function.getFunction();
				try {
					func.execute(destination);
					JCoTable obj1 = func.getTableParameterList().getTable("COMPANYCODE_LIST");
					int rowNum = obj1.getNumRows();
					System.out.println("number of rows are: " + rowNum);
					if (obj1.getNumRows() > 0) {
						XSSFSheet sheet = workbook.createSheet("Data");
						Font headerFont = workbook.createFont();
						headerFont.setBold(true);
						headerFont.setFontHeightInPoints((short) 14);
						headerFont.setColor(IndexedColors.RED.getIndex());

						CellStyle headerCellStyle = workbook.createCellStyle();
						headerCellStyle.setFont(headerFont);

						Row tableRow = sheet.createRow(0);
						tableRow.createCell(0).setCellValue("Bapi Function:");
						tableRow.createCell(1).setCellValue(bapi.getFunction());
						// Create a Row
						Row columnsRow = sheet.createRow(1);
						columnsRow.createCell(0).setCellValue("#");
						columnsRow.createCell(1).setCellValue("COMP_CODE");
						columnsRow.createCell(2).setCellValue("COMP_NAME");
						if (rowNum > 0) {
							for (int iRow = 0; iRow < rowNum; iRow++) {
								obj1.setRow(iRow);
								String sMessage = obj1.getString("COMP_CODE");
								String smessge_1 = obj1.getString("COMP_NAME");
								Row row = sheet.createRow(iRow + 2);
								String[] data_msg = new String[] { sMessage, smessge_1 };
								row.createCell(0).setCellValue(iRow + 1);
								for (int col = 0; col < data_msg.length; col++) {
									row.createCell(col + 1).setCellValue(data_msg[col]);
								}
								System.out.println(sMessage);
							}

						}
					}
				} catch (JCoException e) {
					e.printStackTrace();
				}

				workbook.write(bos);
				byte[] bytes = bos.toByteArray();
				InputStream is = new ByteArrayInputStream(bytes);
				InputStreamResource resource = new InputStreamResource(is);
				response = ResponseEntity.ok().headers(headers).contentLength(bytes.length)
						.contentType(MediaType.parseMediaType("application/octet-stream")).body(resource);
			} catch (JCoException e) {
				e.printStackTrace();
			} finally {
				workbook.close();
				bos.close();
			}
		} else if (bapi.getFunction().equals("BAPI_USER_GET_DETAIL")) {
			// add another bapi function
			try {
				JCoDestination destination = getSourceDestination(source);
				JCoFunctionTemplate function = destination.getRepository().getFunctionTemplate("BAPI_USER_GET_DETAIL");

				if (function == null)
					throw new RuntimeException("BAPI_USER_GET_DETAIL not found in SAP.");
				try {
					JCoFunction func = function.getFunction();
					func.getImportParameterList().setValue("USERNAME", "SMTPUSER");
					func.execute(destination);
					JCoTable table = func.getTableParameterList().getTable("PROFILES");
					int rowNum = table.getNumRows();
					System.out.println("number of rows are: " + rowNum);
					if (table.getNumRows() > 0) {
						XSSFSheet sheet = workbook.createSheet("Data");

						Font headerFont = workbook.createFont();
						headerFont.setBold(true);
						// headerFont.setBold(true);
						headerFont.setFontHeightInPoints((short) 14);
						headerFont.setColor(IndexedColors.RED.getIndex());

						CellStyle headerCellStyle = workbook.createCellStyle();
						headerCellStyle.setFont(headerFont);

						Row tableRow = sheet.createRow(0);
						tableRow.createCell(0).setCellValue("Bapi Function:");
						tableRow.createCell(1).setCellValue(bapi.getFunction());
						// Create a Row
						Row columnsRow = sheet.createRow(1);
						columnsRow.createCell(0).setCellValue("#");
						columnsRow.createCell(1).setCellValue("BAPI-PROF");
						columnsRow.createCell(2).setCellValue("BAPI-P TEXT");
						if (rowNum > 0) {
							for (int iRow = 0; iRow < rowNum; iRow++) {
								table.setRow(iRow);
								String sMessage = table.getString("BAPIPROF");
								String smessge_1 = table.getString("BAPIPTEXT");
								Row row = sheet.createRow(iRow + 2);
								String[] data_msg = new String[] { sMessage, smessge_1 };
								row.createCell(0).setCellValue(iRow + 1);
								for (int col = 0; col < data_msg.length; col++) {
									row.createCell(col + 1).setCellValue(data_msg[col]);
								}
								System.out.println(sMessage);
							}

						}
					}
				} catch (JCoException e) {
					e.printStackTrace();
				}

				workbook.write(bos);
				byte[] bytes = bos.toByteArray();
				InputStream is = new ByteArrayInputStream(bytes);
				InputStreamResource resource = new InputStreamResource(is);
				response = ResponseEntity.ok().headers(headers).contentLength(bytes.length)
						.contentType(MediaType.parseMediaType("application/octet-stream")).body(resource);
			} catch (JCoException e) {
				e.printStackTrace();
			} finally {
				workbook.close();
				bos.close();
			}
		}
		return response;
	}

	public List<ImportParameterModel> getResponse_importparams(Bapi bapi, Source source) throws IOException {
		List<ImportParameterModel> listofipm = new ArrayList<ImportParameterModel>();
		try {
			JCoDestination destination = getSourceDestination(source);
			JCoFunctionTemplate function = destination.getRepository()
					.getFunctionTemplate("RFC_GET_FUNCTION_INTERFACE");
			JCoFunction func = function.getFunction();
			System.out.println(func);
			if (func == null) {
				logger.trace("Cancelled@" + bapi.getName() + "!" + bapi.getInstanceid() + "*" + bapi.getFunction()
						+ "%RFC_GET_FUNCTION_INTERFACE not found in SAP" + "#" + bapi.getCreatedBy());
				throw new RuntimeException("RFC_GET_FUNCTION_INTERFACE not found in SAP.");
			} else {
				try {
					func.getImportParameterList().setValue("FUNCNAME", bapi.getFunction());
					func.execute(destination);
					JCoTable tableName = func.getTableParameterList().getTable("PARAMS");

					int rowNum = tableName.getNumRows();
					System.out.print(tableName);
					System.out.println(tableName.getMetaData());
					System.out.println(tableName.getString("PARAMCLASS"));
					System.out.println(tableName.getString("EXID"));

					System.out.println("number of rows are: " + rowNum);

					if (rowNum > 0) {

						for (int iRow = 0; iRow < rowNum; iRow++) {
							tableName.setRow(iRow);
							String paramValue = tableName.getString("PARAMCLASS");
							if (paramValue.equalsIgnoreCase("I")) {
//								String smessge_0 = tableName.getString("PARAMCLASS");String smessge_1 = tableName.getString("PARAMETER");String smessge_9 = tableName.getString("DEFAULT");								
								ImportParameterModel imp = new ImportParameterModel(tableName.getString("PARAMETER"),
										tableName.getString("DEFAULT"));
								listofipm.add(imp);

							}
						}

					}

				} catch (JCoException e) {
					logger.trace("Cancelled@" + bapi.getName() + "!" + bapi.getInstanceid() + "*" + bapi.getFunction()
							+ "%" + e.getMessage() + "#" + bapi.getCreatedBy());
					e.printStackTrace();
				}
			}
		} catch (JCoException e) {
			logger.trace("Cancelled@" + bapi.getName() + "!" + bapi.getInstanceid() + "*" + bapi.getFunction() + "%"
					+ e.getMessage() + "#" + bapi.getCreatedBy());
			e.printStackTrace();
		}
		logger.trace("" + bapi.getName() + "!" + bapi.getInstanceid() + "*" + bapi.getFunction()
				+ "%Recieved Import Params from SAP" + "#" + bapi.getCreatedBy());
		return listofipm;
	}

	public List<String> getparams(Bapi bapi, Source source) throws IOException {
		List<String> listofipm = new ArrayList<String>();
		try {
			JCoDestination destination = getSourceDestination(source);
			logger.trace("In Progress@" + bapi.getName() + "!" + bapi.getInstanceid() + "*" + bapi.getFunction()
					+ "%Connected to source" + "#" + bapi.getCreatedBy());
			JCoFunctionTemplate function = destination.getRepository()
					.getFunctionTemplate("RFC_GET_FUNCTION_INTERFACE");
			JCoFunction func = function.getFunction();
			System.out.println(func);
			if (func == null) {
				logger.trace("Cancelled@" + bapi.getName() + "!" + bapi.getInstanceid() + "*" + bapi.getFunction()
						+ "%Failed to connect to Function Interface" + "#" + bapi.getCreatedBy());
				throw new RuntimeException("RFC_GET_FUNCTION_INTERFACE not found in SAP.");
			} else {
				try {
					func.getImportParameterList().setValue("FUNCNAME", bapi.getFunction());
					func.execute(destination);
					JCoTable tableName = func.getTableParameterList().getTable("PARAMS");

					int rowNum = tableName.getNumRows();
					System.out.print(tableName);
					System.out.println(tableName.getMetaData());

					System.out.println("number of rows are: " + rowNum);
					logger.trace("In Progress@" + bapi.getName() + "!" + bapi.getInstanceid() + "*" + bapi.getFunction()
							+ "%Recieving Table names from Source" + "#" + bapi.getCreatedBy());
					if (rowNum > 0) {

						for (int iRow = 0; iRow < rowNum; iRow++) {
							tableName.setRow(iRow);
							String paramValue = tableName.getString("PARAMCLASS");
							if (paramValue.equals("T")) {
								String paramoptval = tableName.getString("PARAMETER");
								listofipm.add(paramoptval);
							}
						}

					}
					logger.trace("In Progress@" + bapi.getName() + "!" + bapi.getInstanceid() + "*" + bapi.getFunction()
							+ "%Table names Recieved" + "#" + bapi.getCreatedBy());

				} catch (JCoException e) {
					logger.trace("Cancelled@" + bapi.getName() + "!" + bapi.getInstanceid() + "*" + bapi.getFunction()
							+ "%" + e.getMessage() + "#" + bapi.getCreatedBy());
					e.printStackTrace();
				}
			}
		} catch (JCoException e) {
			logger.trace("Cancelled@" + bapi.getName() + "!" + bapi.getInstanceid() + "*" + bapi.getFunction() + "%"
					+ e.getMessage() + "#" + bapi.getCreatedBy());
			e.printStackTrace();
		}
		logger.trace("In Progress@" + bapi.getName() + "!" + bapi.getInstanceid() + "*" + bapi.getFunction()
				+ "%Sent Tables to User" + "#" + bapi.getCreatedBy());
		System.out.println(listofipm);
		return listofipm;
	}

	public List<Bapi_exportparam> get_exportparams(Bapi bapi, Source source, List<ImportParameterModel> list)
			throws IOException, JCoException {
		System.out.println("##############################################################");
		List<Bapi_exportparam> all_param = new ArrayList<>();
		try {
			JCoDestination destination = getSourceDestination(source);
			JCoFunctionTemplate function = destination.getRepository().getFunctionTemplate(bapi.getFunction());
			JCoFunction func = function.getFunction();

			if (func == null) {
				logger.trace("Cancelled@" + bapi.getName() + "!" + bapi.getInstanceid() + "*" + bapi.getFunction()
						+ "%bapi.getFunction()+\" not found in SAP." + "#" + bapi.getCreatedBy());
				throw new RuntimeException(bapi.getFunction() + " not found in SAP.");
			} else {
				try {
					for (ImportParameterModel ipm : list) {
						System.out.println("values are: " + ipm.getParamName() + " " + ipm.getDefaultValue() + " "
								+ ipm.getParamValue());
						if (ipm.getParamValue().equals("null")) {
							System.out.println(ipm.getParamName() + " " + ipm.getDefaultValue());
							func.getImportParameterList().setValue(ipm.getParamName(), ipm.getDefaultValue());
						} else {
							func.getImportParameterList().setValue(ipm.getParamName(), ipm.getParamValue());
						}
					}
					func.execute(destination);
					logger.trace("In Progress@" + bapi.getName() + "!" + bapi.getInstanceid() + "*" + bapi.getFunction()
							+ "%Receiving Export Params from source" + "#" + bapi.getCreatedBy());
					System.out.println(func.getExportParameterList().getMetaData());
					JCoListMetaData export_metadata = func.getFunctionTemplate().getExportParameterList();
					List<String> exportparam_names = new ArrayList<>();
					for (int i = 0; i < export_metadata.getFieldCount(); i++) {
						exportparam_names.add(export_metadata.getName(i));
					}
					func.execute(destination);
					for (String exportparam : exportparam_names) {
						List<String> exportparameter_names = new ArrayList<>();
						JCoStructure struct = func.getExportParameterList().getStructure(exportparam);
						for (JCoFieldIterator e = struct.getFieldIterator(); e.hasNextField();) {
							JCoField field = e.nextField();
							exportparameter_names.add(field.getName() + ":\t" + field.getString());
						}
						Bapi_exportparam bapi_exportparam = new Bapi_exportparam(exportparam, exportparameter_names);
						all_param.add(bapi_exportparam);
					}
					logger.trace("In Progress@" + bapi.getName() + "!" + bapi.getInstanceid() + "*" + bapi.getFunction()
							+ "%Receiving Export Params from source" + "#" + bapi.getCreatedBy());
				} catch (JCoException e) {
					logger.trace("Cancelled@" + bapi.getName() + "!" + bapi.getInstanceid() + "*" + bapi.getFunction()
							+ "%" + e.getMessage() + "#" + bapi.getCreatedBy());
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			logger.trace("Cancelled@" + bapi.getName() + "!" + bapi.getInstanceid() + "*" + bapi.getFunction() + "%"
					+ e.getMessage() + "#" + bapi.getCreatedBy());
			e.printStackTrace();
		}
		logger.trace("In Progress@" + bapi.getName() + "!" + bapi.getInstanceid() + "*" + bapi.getFunction()
				+ "%Export Params sent to User" + "#" + bapi.getCreatedBy());
		return all_param;
	}

	public ResponseEntity<InputStreamResource> bapi_gettables(Bapi bapi, Source source, List<ImportParameterModel> list)
			throws JCoException, IOException {
		System.out.println("##############################################################");
		ResponseEntity<InputStreamResource> response = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		XSSFWorkbook workbook = new XSSFWorkbook();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.add("Pragma", "no-cache");
		headers.add("Expires", "0");
		// add another bapi function
		try {
			JCoDestination destination = getSourceDestination(source);
			JCoFunctionTemplate function = destination.getRepository().getFunctionTemplate(bapi.getFunction());
			JCoFunction func = function.getFunction();
			if (func == null) {
				throw new RuntimeException(bapi.getFunction() + " not found in SAP.");
			} else {
				try {
					for (ImportParameterModel ipm : list) {
						System.out.println("values are: " + ipm.getParamName() + " " + ipm.getDefaultValue() + " "
								+ ipm.getParamValue());
						if (ipm.getParamValue().equals("null")) {
							System.out.println(ipm.getParamName() + " " + ipm.getDefaultValue());
							func.getImportParameterList().setValue(ipm.getParamName(), ipm.getDefaultValue());
						} else {
							func.getImportParameterList().setValue(ipm.getParamName(), ipm.getParamValue());
						}
					}
					JCoListMetaData tables_metadata = func.getFunctionTemplate().getTableParameterList();
					List<String> table_names = new ArrayList<>();
					for (int i = 0; i < tables_metadata.getFieldCount(); i++) {
						table_names.add(tables_metadata.getName(i));
					}
					func.execute(destination);
					logger.trace("In Progress@" + bapi.getName() + "!" + bapi.getInstanceid() + "*" + bapi.getFunction()
							+ "%Receiving Tables data from source" + "#" + bapi.getCreatedBy());
					for (String table : table_names) {
						JCoTable tableName = func.getTableParameterList().getTable(table);
						int rowNum = tableName.getNumRows();
						if (rowNum > 0) {
							XSSFSheet sheet = workbook.createSheet(table);
							// row 0
							Row tableRow = sheet.createRow(0);
							tableRow.createCell(0).setCellValue(bapi.getFunction() + ":");

							// row 1
							Row columnsRow = sheet.createRow(1);
							columnsRow.createCell(0).setCellValue("#");
							JCoRecordMetaData tableMeta = tableName.getRecordMetaData();
							List<String> columns_names = new ArrayList<>();
							for (int j = 0; j < tableMeta.getFieldCount(); j++) {
								columns_names.add(tableMeta.getName(j));
							}
							int excelRows = 0;
							for (String j1 : columns_names) {
								excelRows++;
								columnsRow.createCell(excelRows).setCellValue(j1);
							}
							// col size = 5, number rows = 3
							int tablerow = 0;
							for (int iRow = 2; iRow < rowNum + 2; iRow++) {
								Row row = sheet.createRow(iRow);
								tableName.setRow(tablerow);
								tablerow++;
								row.createCell(0).setCellValue(tablerow);
								for (int col = 0; col < columns_names.size(); col++) {
									// System.out.println("table data:
									// "+tableName.getString(columns_names.get(col)));
									row.createCell(col + 1).setCellValue(tableName.getString(columns_names.get(col)));
								}
							}
						}
					}
					logger.trace("In Progress@" + bapi.getName() + "!" + bapi.getInstanceid() + "*" + bapi.getFunction()
							+ "%Tables data sent to User for download" + "#" + bapi.getCreatedBy());
					logger.trace("Finished@" + bapi.getName() + "!" + bapi.getInstanceid() + "*" + bapi.getFunction()
							+ "%Job Completed Successfully" + "#" + bapi.getCreatedBy());
					workbook.write(bos);
					byte[] bytes = bos.toByteArray();
					InputStream is = new ByteArrayInputStream(bytes);
					InputStreamResource resource = new InputStreamResource(is);
					response = ResponseEntity.ok().headers(headers).contentLength(bytes.length)
							.contentType(MediaType.parseMediaType("application/octet-stream")).body(resource);
				} catch (JCoException e) {
					e.printStackTrace();
				}
			}
		} finally {
			workbook.close();
			bos.close();
		}
		return response;
	}

	public String initializewithoutdata(Extractor extractor, Source source, SocketResponse sr, String username) throws IOException {
		String sourceid = source.getApplicationServer();
		String extractorName = extractor.getFunction();
		String port = source.getPort();
		URL url = null;
		String token = null;
		String previousDelta = null;
		String extractorServiceName;
		try {
			ExtractorServiceName esn = extractorServiceNameService.findbyExtractorName(extractorName);
			extractorServiceName = esn.getServiceName();
			logger.trace("Service Configured@" + extractor.getName() + "!" + extractor.getInstanceid() + "*"
					+ extractor.getFunction() + "%Extractor Service name configured");
			sr.getStatus().add("Extractor Service name configured");
			template.convertAndSend("/topic/user", sr);
		} catch (Exception e) {
			logger.trace("Cancelled@" + extractor.getName() + "!" + extractor.getInstanceid() + "*"
					+ extractor.getFunction() + "%Extractor Service name not configured");
			sr.getStatus().add("Extractor Service name not configured");
			template.convertAndSend("/topic/user", sr);
			return "Extractor Service name not configured";
		}
		if (extractorName.length() > 14) {
			url = new URL("http://" + sourceid + ":" + port + "/sap/opu/odata/sap/" + extractorServiceName + "/TDeltFor"
					+ extractorName + "/?$format=json");
		}

		else {
			url = new URL("http://" + sourceid + ":" + port + "/sap/opu/odata/sap/" + extractorServiceName
					+ "/TerminateDeltasForEntityOf" + extractorName + "/?$format=json");

//			if(extractorName.equals("0INFO_REC_ATTR")) {
//				url = new URL("http://192.168.1.30:8001/sap/opu/odata/sap/Z_ODATA_0INFO_REC_ATTR_1_SRV/TerminateDeltasForEntityOf0INFO_REC_ATTR/?$format=json");
//			}
//			if(extractorName.equals("2LIS_02_ACC")) {
//				url = new URL("http://192.168.1.30:8001/sap/opu/odata/sap/Z_ODATA_2LIS_02_ACC_1_SRV_02/TerminateDeltasForEntityOf2LIS_02_ACC/?$format=json");
//			}
//			if(extractorName.equals("0FI_GL_14")) {
//				url = new URL("http://192.168.1.30:8001/sap/opu/odata/sap/Z_ODATA_0FI_GL_14_1_SRV/TerminateDeltasForEntityOf0FI_GL_14/?$format=json");
//			}

		}
		HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
		try {
			httpConnection.connect();
			logger.trace("Connected@" + extractor.getName() + "!" + extractor.getInstanceid() + "*"
					+ extractor.getFunction() + "%Connection to SAP Web-Service : Successful");
			sr.getStatus().add("Connection to SAP Web-Service : Successful");
			template.convertAndSend("/topic/user", sr);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			logger.trace("Cancelled@" + extractor.getName() + "!" + extractor.getInstanceid() + "*"
					+ extractor.getFunction() + "%Connection to SAP Web-Service : UnSucessful");
			sr.getStatus().add("Connection to SAP Web-Service : UnSucessful");
			template.convertAndSend("/topic/user", sr);
		}
		System.out.println("im here-1");
		try (InputStream is = httpConnection.getInputStream(); JsonReader rdr = Json.createReader(is);) {
			JsonObject obj = rdr.readObject();
			JsonObject d = obj.getJsonObject("d");
			System.out.println(obj);
			JsonObject terminateDelta;
			if (extractorName.length() > 14) {
				terminateDelta = d.getJsonObject("TDeltFor" + extractorName + "");
			} else {
				terminateDelta = d.getJsonObject("TerminateDeltasForEntityOf" + extractorName + "");
			}
			System.out.println("im here-2");
			Boolean resultFlag = terminateDelta.getBoolean("ResultFlag");
			if (resultFlag || !resultFlag) {
				url = new URL("http://" + sourceid + ":" + port + "/sap/opu/odata/sap/" + extractorServiceName
						+ "/EntityOf" + extractorName + "/?$format=json");
//				if(extractorName.equals("0INFO_REC_ATTR")) {
//					url = new URL("http://192.168.1.30:8001/sap/opu/odata/sap/Z_ODATA_0INFO_REC_ATTR_1_SRV/EntityOf0INFO_REC_ATTR/?$format=json");
//				}
//				if(extractorName.equals("2LIS_02_ACC")) {
//					url = new URL("http://192.168.1.30:8001/sap/opu/odata/sap/Z_ODATA_2LIS_02_ACC_1_SRV_02/EntityOf2LIS_02_ACC/?$format=json");
//				}
//				if(extractorName.equals("0FI_GL_14")) {
//					url = new URL("http://192.168.1.30:8001/sap/opu/odata/sap/Z_ODATA_0FI_GL_14_1_SRV/EntityOf0FI_GL_14/?$format=json");
//				}
				System.out.println("im here-3");
				httpConnection = (HttpURLConnection) url.openConnection();
				httpConnection.setRequestProperty("Prefer", "odata.track-changes");
				httpConnection.connect();
				try (InputStream is1 = httpConnection.getInputStream(); JsonReader rdr1 = Json.createReader(is1);) {
					obj = rdr1.readObject();
					d = obj.getJsonObject("d");
					JsonArray results = d.getJsonArray("results");
					if (results.size() > 0) {
						url = new URL("http://" + sourceid + ":" + port + "/sap/opu/odata/sap/" + extractorServiceName
								+ "/DeltaLinksOfEntityOf" + extractorName + "/?$format=json");
//						if(extractorName.equals("0INFO_REC_ATTR")) {
//							url = new URL("http://192.168.1.30:8001/sap/opu/odata/sap/Z_ODATA_0INFO_REC_ATTR_1_SRV/DeltaLinksOfEntityOf0INFO_REC_ATTR/?$format=json");
//						}
//						if(extractorName.equals("2LIS_02_ACC")) {
//							url = new URL("http://192.168.1.30:8001/sap/opu/odata/sap/Z_ODATA_2LIS_02_ACC_1_SRV_02/DeltaLinksOfEntityOf2LIS_02_ACC/?$format=json");
//						}
//						if(extractorName.equals("0FI_GL_14")) {
//							url = new URL("http://192.168.1.30:8001/sap/opu/odata/sap/Z_ODATA_0FI_GL_14_1_SRV/DeltaLinksOfEntityOf0FI_GL_14/?$format=json");
//						}
						System.out.println("im here-4");
						httpConnection = (HttpURLConnection) url.openConnection();
						httpConnection.setRequestProperty("Prefer", "odata.track-changes");
						httpConnection.connect();
						logger.trace("Initiated@" + extractor.getName() + "!" + extractor.getInstanceid() + "*"
								+ extractor.getFunction() + "%Delta token fetch from Source: Initiated");
						sr.getStatus().add("Delta token fetch from Source: Initiated");
						template.convertAndSend("/topic/user", sr);
						try (InputStream is2 = httpConnection.getInputStream();
								JsonReader rdr2 = Json.createReader(is2);) {
							obj = rdr2.readObject();
							d = obj.getJsonObject("d");
							results = d.getJsonArray("results");
							for (JsonObject result : results.getValuesAs(JsonObject.class)) {
								if (result.getBoolean("IsInitialLoad")) {
									System.out.println("im here-5");
									logger.trace("In Progress@" + extractor.getName() + "!" + extractor.getInstanceid()
											+ "*" + extractor.getFunction()
											+ "%Delta token fetch from Source: Successful");
									sr.getStatus().add("Delta token fetch from Source: Successful");
									template.convertAndSend("/topic/user", sr);
									token = result.getString("DeltaToken", "");
									createUpdateDelta(extractorName, token, previousDelta, new Date());
									logger.trace("In Progress@" + extractor.getName() + "!" + extractor.getInstanceid()
											+ "*" + extractor.getFunction()
											+ "%Delta token added to Control table: Successful");
									sr.getStatus().add("Delta token added to Control table: Successful");
									template.convertAndSend("/topic/user", sr);
								} else {
									logger.trace("Cancelled@" + extractor.getName() + "!" + extractor.getInstanceid()
											+ "*" + extractor.getFunction() + "%Is initial load is false");
									sr.getStatus().add("Is initial load is false");
									template.convertAndSend("/topic/user", sr);
									return "Is initial load is false";
								}
								break;
							}
						} catch (Exception e) {
							logger.trace("Cancelled@" + extractor.getName() + "!" + extractor.getInstanceid() + "*"
									+ extractor.getFunction() + "%" + e.getMessage() + ".");
							sr.getStatus().add(e.getMessage() + ".");
							template.convertAndSend("/topic/user", sr);
							return e.getMessage();
						}
					} else {
						logger.trace("Cancelled@" + extractor.getName() + "!" + extractor.getInstanceid() + "*"
								+ extractor.getFunction() + "%Odata db not setup correctly in " + extractorName + ".");
						sr.getStatus().add("Odata db not setup correctly in" + extractorName + ".");
						template.convertAndSend("/topic/user", sr);
						return "Odata db not setup correctly in " + extractorName + "";
					}
				} catch (Exception e) {
					logger.trace("Cancelled@" + extractor.getName() + "!" + extractor.getInstanceid() + "*"
							+ extractor.getFunction() + "%" + e.getMessage() + ".");
					sr.getStatus().add(e.getMessage() + ".");
					template.convertAndSend("/topic/user", sr);
					return e.getMessage();
				}

			} else {
				logger.trace("Cancelled@" + extractor.getName() + "!" + extractor.getInstanceid() + "*"
						+ extractor.getFunction() + "%Existing delta markers could not be deleted");
				sr.getStatus().add("Existing delta markers could not be deleted");
				template.convertAndSend("/topic/user", sr);
				return "Existing delta markers could not be deleted";
			}
		} catch (Exception e) {
			return e.getMessage();
		}
		System.out.println("im here-5");
		logger.trace(
				"In Progress@" + extractor.getName() + "!" + extractor.getInstanceid() + "*" + extractor.getFunction()
						+ "%Delta Token Added Successfully: Completed" + "#" + username);
		sr.getStatus().add("Delta Token Added Successfully: Completed");
		template.convertAndSend("/topic/user", sr);
		logger.trace("In Progress@" + extractor.getName() + "!" + extractor.getInstanceid() + "*"
				+ extractor.getFunction() + "%Job Init W/O data completed" + "#" + username);
		sr.getStatus().add("Job Init W/O data completed");
		sr.setJobStatus("In Progress");
		template.convertAndSend("/topic/user", sr);
		logger.trace("Finished@" + extractor.getName() + "!" + extractor.getInstanceid() + "*" + extractor.getFunction()
				+ "%Job Completed Successfully" + "#" + username);
		sr.getStatus().add("Job Init W/O data completed");
		sr.setJobStatus("Finished");
		template.convertAndSend("/topic/user", sr);
		return "Delta Token Added Successfully";
	}

	public String initializewithdata(Extractor extractor, Source source, SocketResponse sr, String username) throws Exception {
		String sourceid = source.getApplicationServer();
		String extractorName = extractor.getFunction();
		String port = source.getPort();
		URL url = null;
		String token = null;
		String previousDelta = null;
		String extractorServiceName;
		boolean isbwtype = "bw".equals(extractor.getSourceType().toLowerCase()) ? true : false;
		try {
			ExtractorServiceName esn = extractorServiceNameService.findbyExtractorName(extractorName);
			extractorServiceName = esn.getServiceName();
			logger.trace("Service Configured@" + extractor.getName() + "!" + extractor.getInstanceid() + "*"
					+ extractor.getFunction() + "%Extractor Service name configured" + "#" + username);
			sr.getStatus().add("Extractor Service name configured");
			template.convertAndSend("/topic/user", sr);
		} catch (Exception e) {
			logger.trace(
					"Cancelled@" + extractor.getName() + "!" + extractor.getInstanceid() + "*" + extractor.getFunction()
							+ "%Extractor Service name not configured" + "#" + username);
			sr.getStatus().add("Extractor Service name configured");
			template.convertAndSend("/topic/user", sr);
			return "Extractor Service name not configured";
		}
		if(isbwtype){
			if (extractorName.length() > 14) {
				url = new URL("http://" + sourceid + ":" + port + "/sap/opu/odata/sap/" + extractorServiceName
						+ "/TDeltFor" + extractorName + "/?$format=json");
			} else {
				url = new URL("http://" + sourceid + ":" + port + "/sap/opu/odata/sap/" + extractorServiceName
						+ "/TerminateDeltasForFactsOf" + extractorName + "/?$format=json");
			}
		}
		else {
			if (extractorName.length() > 14) {
				url = new URL("http://" + sourceid + ":" + port + "/sap/opu/odata/sap/" + extractorServiceName
						+ "/TDeltFor" + extractorName + "/?$format=json");
			} else {
				url = new URL("http://" + sourceid + ":" + port + "/sap/opu/odata/sap/" + extractorServiceName
						+ "/TerminateDeltasForEntityOf" + extractorName + "/?$format=json");
			}
		}
		HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
		httpConnection.connect();
		logger.trace(
				"Connected@" + extractor.getName() + "!" + extractor.getInstanceid() + "*" + extractor.getFunction()
						+ "%Connection to SAP Web-Service : Successful" + "#" + username);
		sr.getStatus().add("Connection to SAP Web-Service : Successful");
		template.convertAndSend("/topic/user", sr);
		try (InputStream is = httpConnection.getInputStream(); JsonReader rdr = Json.createReader(is);) {
			JsonObject obj = rdr.readObject();
			JsonObject d = obj.getJsonObject("d");
			JsonObject terminateDelta;
			if (isbwtype) {
				if (extractorName.length() > 14) {
					terminateDelta = d.getJsonObject("TDeltFor" + extractorName + "");
				} else {
					terminateDelta = d.getJsonObject("TerminateDeltasForFactsOf" + extractorName + "");
				} 
			}
			else {
				if (extractorName.length() > 14) {
					terminateDelta = d.getJsonObject("TDeltFor" + extractorName + "");
				} else {
					terminateDelta = d.getJsonObject("TerminateDeltasForEntityOf" + extractorName + "");
				} 
			}
			Boolean resultFlag = terminateDelta.getBoolean("ResultFlag");
			if (resultFlag || !resultFlag) {
				// need to remove new Jobhistory(), its dummy
				logger.trace("In Progress@" + extractor.getName() + "!" + extractor.getInstanceid() + "*"
						+ extractor.getFunction() + "%Full Load Initiated" + "#" + username);
				sr.getStatus().add("Full Load Initiated");
				template.convertAndSend("/topic/user", sr);
				String fullreturn = fullload(extractor, source, sr,username);
				logger.trace("In Progress@" + extractor.getName() + "!" + extractor.getInstanceid() + "*"
						+ extractor.getFunction() + "%" + fullreturn + "#" + username);
				sr.getStatus().add(fullreturn);
				template.convertAndSend("/topic/user", sr);
				System.out.println(fullreturn);
				if (fullreturn.length() > 0) {
					if("bw".equals(extractor.getSourceType().toLowerCase())) {
						url = new URL("http://" + sourceid + ":" + port + "/sap/opu/odata/sap/" + extractorServiceName
								+ "/DeltaLinksOfFactsOf" + extractorName + "/?$format=json");
					}else {
						url = new URL("http://" + sourceid + ":" + port + "/sap/opu/odata/sap/" + extractorServiceName
								+ "/DeltaLinksOfEntityOf" + extractorName + "/?$format=json");
					}
					logger.trace("Delta Fetch Initiated@" + extractor.getName() + "!" + extractor.getInstanceid() + "*"
							+ extractor.getFunction() + "%Delta token fetch from Source: Initiated" + "#"
							+ username);
					sr.getStatus().add("Delta token fetch from Source: Initiated");
					template.convertAndSend("/topic/user", sr);
					httpConnection = (HttpURLConnection) url.openConnection();
					System.out.println("verify" + url);
//					httpConnection.setRequestProperty("Prefer", "odata.track-changes");
					System.out.println("Verify" + httpConnection.getRequestProperty("Prefer"));
					httpConnection.connect();
					try (InputStream is2 = httpConnection.getInputStream(); JsonReader rdr2 = Json.createReader(is2);) {
						obj = rdr2.readObject();
						d = obj.getJsonObject("d");
						JsonArray results = d.getJsonArray("results");
						for (JsonObject result : results.getValuesAs(JsonObject.class)) {
							token = result.getString("DeltaToken", "");
							logger.trace("In Progress@" + extractor.getName() + "!" + extractor.getInstanceid() + "*"
									+ extractor.getFunction() + "%Delta token fetch from Source: Successful" + "#"
									+ username);
							sr.getStatus().add("Delta token fetch from Source: Successful");
							template.convertAndSend("/topic/user", sr);
							createUpdateDelta(extractorName, token, previousDelta, new Date());
							logger.trace("In Progress@" + extractor.getName() + "!" + extractor.getInstanceid() + "*"
									+ extractor.getFunction() + "%Delta token added to Control table: Successful" + "#"
									+ username);
							sr.getStatus().add("Delta token added to Control table: Successful");
							template.convertAndSend("/topic/user", sr);

							break;
						}
					} catch (Exception e) {
						System.out.println("inner catch");
						e.printStackTrace();
						logger.trace("Cancelled@" + extractor.getName() + "!" + extractor.getInstanceid() + "*"
								+ extractor.getFunction() + "%" + e.getMessage() + "." + "#"
								+ username);
						sr.getStatus().add(e.getMessage() + ".");
						template.convertAndSend("/topic/user", sr);
						return e.getMessage();
					}
				} else {
					logger.trace("Cancelled@" + extractor.getName() + "!" + extractor.getInstanceid() + "*"
							+ extractor.getFunction() + "%Odata db not setup correctly in " + extractorName + "." + "#"
							+ username);
					sr.getStatus().add("Odata db not setup correctly in " + extractorName + ".");
					template.convertAndSend("/topic/user", sr);
					return "Odata db not setup correctly in " + extractorName + "";
				}
			} else {
				logger.trace("Cancelled@" + extractor.getName() + "!" + extractor.getInstanceid() + "*"
						+ extractor.getFunction() + "%Existing delta markers could not be deleted" + "#"
						+ username);
				sr.getStatus().add("Existing delta markers could not be deleted");
				template.convertAndSend("/topic/user", sr);
				return "Existing delta markers could not be deleted";
			}
		} catch (Exception e) {
			System.out.println("outer catch");
			e.printStackTrace();
			return e.getMessage();
		}
		logger.trace("In Progress@" + extractor.getName() + "!" + extractor.getInstanceid() + "*"
				+ extractor.getFunction() + "%Full Load Data and Delta Token Added Successfully: Completed" + "#"
				+ username);
		sr.getStatus().add("Full Load Data and Delta Token Added Successfully: Completed");
		sr.setJobStatus("In Progress");
		template.convertAndSend("/topic/user", sr);
		logger.trace("Finished@" + extractor.getName() + "!" + extractor.getInstanceid() + "*" + extractor.getFunction()
				+ "%Job Completed Successfully" + "#" + username);
		sr.getStatus().add("Job Completed Successfully");
		sr.setJobStatus("Finished");
		template.convertAndSend("/topic/user", sr);
		return "Full Load Data and Delta Token Added Successfully";
	}
	
	public JSONArray preview_data(String extractor, Source source) throws IOException {
//		System.out.println("extractor:" + extractor.getTargetType() + "    " + extractor.getFunction());
		String extractorName = extractor;
		String sourceid = source.getApplicationServer();
		String port = source.getPort();
		URL url = null;
		String extractorServiceName;
		Packetsizeconfig psc = null;
		JSONArray preview_array = new JSONArray();
		try {
			ExtractorServiceName esn = extractorServiceNameService.findbyExtractorName(extractorName);
			extractorServiceName = esn.getServiceName();
		} catch (Exception e) {
			preview_array = new JSONArray();
			e.printStackTrace();
			return preview_array.put("Extractor Service name not configured");
		}
		
		url = new URL("http://" + sourceid + ":" + port + "/sap/opu/odata/sap/" + extractorServiceName + "/EntityOf"
					+ extractorName + "/?$format=json");

		HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
			if (packetsizeconfigService.findByExtractorName(extractorName) != null) {
				psc = packetsizeconfigService.findByExtractorName(extractorName);
				System.out.println(psc.getExtractorName() + " " + psc.getPacketsize() + " " + psc.toString());
				httpConnection.setRequestProperty("Prefer", "odata.maxpagesize=" + psc.getPacketsize());
				httpConnection.connect();
				httpConnection.setConnectTimeout(60000);
			} else if (packetsizeconfigService.findByExtractorName("DEFAULT") != null) {
				psc = packetsizeconfigService.findByExtractorName("DEFAULT");
				System.out.println(psc.getExtractorName() + " " + psc.getPacketsize() + " " + psc.toString());
				httpConnection.setRequestProperty("Prefer", "odata.maxpagesize=" + psc.getPacketsize());
				httpConnection.connect();
				httpConnection.setConnectTimeout(60000);
			} else {
				httpConnection.connect();
			}

		try (InputStream is = httpConnection.getInputStream(); JsonReader rdr = Json.createReader(is);) {
			JsonObject obj = rdr.readObject();
			JsonObject d = obj.getJsonObject("d");
			JsonArray results = d.getJsonArray("results");
			int size = 100;
			if (size > results.size()) {
				size = results.size();
			}
			for (int i = 0; i < size; i++) {
				preview_array.put(results.get(i));
			}
		} catch (Exception e) {
			preview_array = new JSONArray();
			e.printStackTrace();
			return preview_array.put(e.getMessage());
		}

		return preview_array;

	}
	

	@SuppressWarnings("deprecation")
	@MessageMapping("/user") // rest end point a web socket end point and we will push job full load
	@SendTo("/topic/user") // push it to the broker back end is pushing the message to the UI send response
							// to this topic and should be read by UI
	public String fullload(Extractor extractor, Source source, SocketResponse sr, String username) throws Exception {
		System.out.println("extractor:" + extractor.getTargetType() + "    " + extractor.getFunction());
		String extractorName = extractor.getFunction();
		String sourceid = source.getApplicationServer();
		String port = source.getPort();
		Boolean flag;
		URL url = null;
		String extractorServiceName;
		String extractorsourceSubtype;
		int packetNumber = 0;
		Packetsizeconfig psc = null;
		SkipToken st = null;
		int recordfetchedUrl;
		DbInsert dbinsert = dbInsertService.findDbInsertByid(extractor.getTargetId());		
		try {
			ExtractorServiceName esn = extractorServiceNameService.findbyExtractorName(extractorName);
			extractorServiceName = esn.getServiceName();
			extractorsourceSubtype = esn.getSourceSubType();
		} catch (Exception e) {
			logger.trace(
					"Cancelled@" + extractor.getName() + "!" + extractor.getInstanceid() + "*" + extractor.getFunction()
							+ "%Extractor Service name not configured" + "#" + username);
			sr.getStatus().add("Extractor Service name not configured");
			template.convertAndSend("/topic/user", sr);
			return "Extractor Service name not configured";
		}
		logger.trace("Service Configured@" + extractor.getName() + "!" + extractor.getInstanceid() + "*"
				+ extractor.getFunction() + "%Extractor Service name configured" + "#" + username);
		sr.getStatus().add("Extractor Service name configured");
		template.convertAndSend("/topic/user", sr);
		if ("bw".equals(extractor.getSourceType().toLowerCase())) {
			if("Master Data Infoobject".toLowerCase().equals(extractorsourceSubtype.toLowerCase())) {
				System.out.println("In master data infoobject");
				url = new URL("http://"+sourceid+":"+port+"/sap/opu/odata/sap/"+extractorServiceName+"/AttrOf"+extractorName+"/?$format=json");
			}else if("Info Cube".toLowerCase().equals(extractorsourceSubtype.toLowerCase())) {
				System.out.println("In info cube");
				url = new URL("http://"+sourceid+":"+port+"/sap/opu/odata/sap/"+extractorServiceName+"/FactsOf"+extractorName+"/?$format=json");
			}else if("DSO".toLowerCase().equals(extractorsourceSubtype.toLowerCase())) {
				System.out.println("In DSO");
				url = new URL("http://"+sourceid+":"+port+"/sap/opu/odata/sap/"+extractorServiceName+"/FactsOf"+extractorName+"/?$format=json");
			}else if("Bex Query".toLowerCase().equals(extractorsourceSubtype.toLowerCase())) {
				System.out.println("In Bex Query");
				url = new URL("http://"+sourceid+":"+port+"/sap/opu/odata/sap/"+extractorServiceName+"/"+extractorName+"Results/?$format=json");
			}	
		}
		else {
//			if ("full".equals(extractor.getTargetType()) || extractor.getTargetType().toLowerCase().startsWith("init")) {
				System.out.println("http://" + sourceid + ":" + port + "/sap/opu/odata/sap/" + extractorServiceName
						+ "/EntityOf" + extractorName + "/?$format=json");
				url = new URL("http://" + sourceid + ":" + port + "/sap/opu/odata/sap/" + extractorServiceName
						+ "/EntityOf" + extractorName + "/?$format=json");
//			if(extractorName.equals("0INFO_REC_ATTR")) {
//				url = new URL("http://192.168.1.30:8001/sap/opu/odata/sap/"+extractorServiceName+"/EntityOf0INFO_REC_ATTR/?$format=json");
//			}

//			}
		}

		Metadata[] metadata;
		try {
			if (metadataStructureService.getDeltaByExtractorName(extractorName) == null) {
				createMetadata(extractor, source);
				MetadataStructure ms = metadataStructureService.getDeltaByExtractorName(extractorName);
				metadata = new Metadata[ms.getMetadata().length];
				metadata = ms.getMetadata();
				if (checktable(extractorName, dbinsert)) {
					System.out.println(ms.getSql());
					createtable(ms.getSql(), dbinsert);
				}
			} else {
				MetadataStructure ms = metadataStructureService.getDeltaByExtractorName(extractorName);
				metadata = new Metadata[ms.getMetadata().length];
				metadata = ms.getMetadata();
				if (checktable(extractorName, dbinsert)) {
					createtable(ms.getSql(), dbinsert);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}

		HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
		if (extractor.getTargetType().equals("full")) {
			if (packetsizeconfigService.findByExtractorName(extractorName) != null) {
				psc = packetsizeconfigService.findByExtractorName(extractorName);
				System.out.println(psc.getExtractorName() + " " + psc.getPacketsize() + " " + psc.toString());
				httpConnection.setRequestProperty("Prefer", "odata.maxpagesize=" + psc.getPacketsize());
				httpConnection.connect();
				httpConnection.setConnectTimeout(60000);
			} else if (packetsizeconfigService.findByExtractorName("DEFAULT") != null) {
				psc = packetsizeconfigService.findByExtractorName("DEFAULT");
				System.out.println(psc.getExtractorName() + " " + psc.getPacketsize() + " " + psc.toString());
				httpConnection.setRequestProperty("Prefer", "odata.maxpagesize=" + psc.getPacketsize());
				httpConnection.connect();
				httpConnection.setConnectTimeout(60000);
			} else {
				httpConnection.connect();
			}
		} else {
			if (packetsizeconfigService.findByExtractorName(extractorName) != null) {
				psc = packetsizeconfigService.findByExtractorName(extractorName);
				System.out.println(psc.getExtractorName() + " " + psc.getPacketsize() + " " + psc.toString());
				httpConnection.setRequestProperty("Prefer",
						"odata.maxpagesize=" + psc.getPacketsize() + ",odata.track-changes");
				httpConnection.connect();
			} else if (packetsizeconfigService.findByExtractorName("DEFAULT") != null) {
				psc = packetsizeconfigService.findByExtractorName("DEFAULT");
				System.out.println(psc.getExtractorName() + " " + psc.getPacketsize() + " " + psc.toString());
				httpConnection.setRequestProperty("Prefer",
						"odata.maxpagesize=" + psc.getPacketsize() + ",odata.track-changes");
				httpConnection.connect();
			} else {
				httpConnection.setRequestProperty("Prefer", "odata.track-changes");
				httpConnection.connect();
			}
		}
		logger.trace(
				"Connected@" + extractor.getName() + "!" + extractor.getInstanceid() + "*" + extractor.getFunction()
						+ "%Connection to SAP Web-Service : Successful" + "#" + username);
		sr.getStatus().add("Connection to SAP Web-Service : Successful");
		template.convertAndSend("/topic/user", sr);
		logger.trace("Extractor Job initiated@" + extractor.getName() + "!" + extractor.getInstanceid() + "*"
				+ extractor.getFunction() + "%Data fetch from Web-Service : Initiated" + "#"
				+ username);
		sr.getStatus().add("Data fetch from Web-Service : Initiated");
		template.convertAndSend("/topic/user", sr);
		long startTime = System.currentTimeMillis();
		int truncate = 0;
		do {
			flag = true;
			System.out.println(httpConnection.getRequestProperty("Prefer"));
			try (InputStream is = httpConnection.getInputStream(); JsonReader rdr = Json.createReader(is);) {
//				System.out.println(rdr.readObject());
				JsonObject obj = rdr.readObject();
				JsonObject d = obj.getJsonObject("d");
				JsonArray results = d.getJsonArray("results");
				recordfetchedUrl = results.size();
				// create a class saptodbinsert(JsonArray results, List<Metadata> metadata,
				// String extractorName)
				SaptodbInsert sb = new SaptodbInsert(results, metadata, extractorName);
//				System.out.println(sb.getExtractorName());
//				System.out.println(sb.getResults());
//				System.out.println(sb.getMetadata());
				packetNumber++;
//				multithread.multithreadInsert(truncate, extractor, sr,sb, psc.getNumberofthreads(), packetNumber);
				String threadOutput = multithread.multithreadInsert(truncate, extractor, sr, sb,psc.getNumberofthreads(), packetNumber, dbinsert);
				System.out.println("thread execution output : " + threadOutput);
				// query prep and execution
				/*
				 * String dburl = "jdbc:sqlserver://172.17.0.141:1433;databaseName=SAPDATALAKE";
				 * String username = "SAPUSER"; String password = "Miracle@1";
				 * 
				 * StringBuilder sql = new StringBuilder(); sql.append("insert into \"" +
				 * extractorName + "\" ("); // for stored proc taking 2 parameters for (int i =
				 * 0; i < metadata.size(); i++) { sql.append(metadata.get(i).getColumName() +
				 * ","); } sql.deleteCharAt(sql.lastIndexOf(",")); sql.append(") VALUES("); for
				 * (int i = 0; i < metadata.size(); i++) { sql.append("?,"); }
				 * sql.deleteCharAt(sql.lastIndexOf(",")); sql.append(")");
				 * 
				 * try (Connection conn = DriverManager.getConnection(dburl, username,
				 * password); Statement statement = conn.createStatement()) { if (truncate == 0)
				 * { System.out.println("I Should be here only once");
				 * statement.execute("truncate table \"" + extractorName + "\""); } truncate++;
				 * PreparedStatement ps = conn.prepareStatement(sql.toString());
				 * 
				 * System.out.println("metadata size" + metadata.size());
				 * //System.out.println("keys size" + keys.size()); long addbatchStarttime =
				 * System.currentTimeMillis();
				 * 
				 * for (JsonObject result : results.getValuesAs(JsonObject.class)) { for (int i
				 * = 0; i < metadata.size() ; i++) { if
				 * (metadata.get(i).getDataType().equals("String")) { ps.setString(i+1,
				 * result.getString(metadata.get(i).getColumName(), "")); } else if
				 * (metadata.get(i).getDataType().equals("DateTime")) { String temp =
				 * result.getString(metadata.get(i).getColumName(), ""); String formatted = "";
				 * if (temp != "") { temp = temp.substring(temp.lastIndexOf("(") + 1,
				 * temp.lastIndexOf(")")); Long temp1 = Long.parseLong(temp); java.sql.Date
				 * date1 = new java.sql.Date(temp1); DateFormat format = new
				 * SimpleDateFormat("yyyy-MM-dd");
				 * format.setTimeZone(TimeZone.getTimeZone("GMT")); formatted =
				 * format.format(date1); } java.sql.Date date = (formatted == "") ?
				 * java.sql.Date.valueOf("1999-12-31") : java.sql.Date.valueOf(formatted);
				 * ps.setDate(i+1, date); } else if
				 * (metadata.get(i).getDataType().equals("Boolean")) { ps.setBoolean(i+1,
				 * Boolean.parseBoolean(result.getString(metadata.get(i).getColumName(), "")));
				 * } else if (metadata.get(i).getDataType().equals("Int32")) { ps.setInt(i+1,
				 * Integer.parseInt(result.getString(metadata.get(i).getColumName(), "0"))); }
				 * else if (metadata.get(i).getDataType().equals("Time")) { String temp =
				 * result.getString(metadata.get(i).getColumName(), ""); Time time_sql; if (temp
				 * != "") { int hours = Integer.parseInt(temp.substring(2, 4)); int minutes =
				 * Integer.parseInt(temp.substring(5, 7)); int seconds =
				 * Integer.parseInt(temp.substring(8, 10)); time_sql = new Time(hours, minutes,
				 * seconds); } else { time_sql = null; } ps.setTime(i+1, time_sql); } else if
				 * (metadata.get(i).getDataType().equals("Decimal")) { ps.setDouble(i,
				 * Double.parseDouble(result.getString(metadata.get(i).getColumName(), "0"))); }
				 * 
				 * } ps.addBatch(); } long addbatchendtime = System.currentTimeMillis();
				 * System.out.println("time taken to add data to batch: "+(addbatchendtime -
				 * addbatchStarttime)); try { addbatchStarttime = System.currentTimeMillis();
				 * ps.executeLargeBatch(); addbatchendtime = System.currentTimeMillis();
				 * System.out.println("time taken to add data to batch: "+(addbatchendtime -
				 * addbatchStarttime)); } catch(BatchUpdateException e) { long[] updatedRecords
				 * = e.getLargeUpdateCounts();
				 * 
				 * int count = 1; for (long i : updatedRecords) { if (i ==
				 * Statement.EXECUTE_FAILED) { System.out.print("Error on request " + count
				 * +": Execute failed "); } // else { // System.out.print("Request " + count
				 * +": OK "); // } count++; }
				 * 
				 * } System.out.println("time taken to addbatch to db: "+(addbatchendtime -
				 * addbatchStarttime)); System.out.println("testing.....  "); conn.commit(); }
				 * catch(Exception e) { return e.getMessage(); }
				 */
				// end of query prep and execution
				if (d.containsKey("__next")) {
					URL previoustoken = null;
					URL temp = url;
					url = new URL(d.getString("__next"));
					System.out.println("URL: " + url + ",extractorName: " + extractorName);
					st = skipTokenService.findByString(extractorName);
					if (st == null) {
						st = new SkipToken(extractorName, url, previoustoken);
						skipTokenService.add(st);
					} else {
						previoustoken = temp;
						temp = null;
						st = new SkipToken(extractorName, url, previoustoken);
						skipTokenService.update(st, skipTokenService.findByString(extractorName).getId());
					}
					httpConnection = (HttpURLConnection) url.openConnection();
					httpConnection.connect();
					httpConnection.setConnectTimeout(60000);
				} else {
					if (st != null) {
						skipTokenService.delete(skipTokenService.findByString(extractorName).getId());
					}
					break;
				}
			} catch (Exception e) {
				logger.trace("Cancelled@" + extractor.getName() + "!" + extractor.getInstanceid() + "*"
						+ extractor.getFunction() + "%Exception occured: " + e.getMessage() + "#"
						+ username);
				sr.getStatus().add("%Exception occured: " + e.getMessage());
				template.convertAndSend("/topic/user", sr);
				e.printStackTrace();
				return e.getMessage();
			}
			truncate++;
		} while (flag);
		long endTime = System.currentTimeMillis();
		long duration = (endTime - startTime);
		System.out.println("response time: " + duration);
		long countOfRecords = getrowCount(extractorName,dbinsert);
		if (extractor.getTargetType().equals("full")) {
			logger.trace("In Progress@" + extractor.getName() + "!" + extractor.getInstanceid() + "*"
					+ extractor.getFunction() + "%Added all the data into table. Total records inserted: "
					+ countOfRecords + "#" + username);
			sr.getStatus().add("Added all the data into table. Total records inserted: " + countOfRecords);
			sr.setJobStatus("In Progress");
			template.convertAndSend("/topic/user", sr);
			logger.trace("Finished@" + extractor.getName() + "!" + extractor.getInstanceid() + "*"
					+ extractor.getFunction() + "%Job Completed Successfully" + "#" + username);
			sr.getStatus().add("Job Completed Successfully");
			sr.setJobStatus("Finished");
			template.convertAndSend("/topic/user", sr);
		}

		final String emailid = "kbourishetty@miraclesoft.com";
        final String password = "Miracle@1234";
        String fromEmail = "kbourishetty@miraclesoft.com";


        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.miraclesoft.com");
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailid,password);
            }
        });
        MimeMessage msg = new MimeMessage(session);
        InternetAddress[] addresses = new InternetAddress[1];
        addresses[0]=new InternetAddress("kbourishetty@miraclesoft.com");

        try {
            msg.setFrom(new InternetAddress(fromEmail));
            msg.setRecipients(Message.RecipientType.TO, addresses );
            msg.setSubject("Job Update of: "+extractor.getName());
            msg.setText("Job Name: "+extractor.getName()+"\nFunction Name: "+extractor.getFunction()+"\nStatus: Completed execution");
            Transport.send(msg);
            System.out.println("Sent message");
        } catch (MessagingException e) {
            e.printStackTrace();
            return e.getMessage();
        }
        FetchedRecordsCount frc = new FetchedRecordsCount(extractor.getName(), extractor.getInstanceid(), recordfetchedUrl, countOfRecords);
        fetchedRecordsCountService.add(frc);
		return "Full Load Data is inserted into Database";

	}

	@SuppressWarnings("deprecation")
	public String deltaLoad(Extractor extractor, Source source, SocketResponse sr, String username) throws Exception {
		System.out.println("extractor:" + extractor.getTargetType() + "    " + extractor.getFunction());
		String extractorName = extractor.getFunction();
		String deltaextractorName = extractorName + "_DELTA";
//		String deltaextractorName = extractorName; //delete
		Delta delta = deltaService.getDeltaByExtractor(extractorName);
		String previousDelta = null;
		String sourceid = source.getApplicationServer();
		String port = source.getPort();
		String returnmessage = "";
		URL url = null;
		String token = null;
		String extractorServiceName;
		String extractorsourceSubtype;
		DbInsert dbinsert = dbInsertService.findDbInsertByid(extractor.getTargetId());
		if (delta == null) {
			createUpdateDelta(extractorName, "", previousDelta, new Date());
			delta = deltaService.getDeltaByExtractor(extractorName);
		} else if (delta.getExtractor().equals(extractor.getFunction())) {
			System.out.println("current token==" + delta.getCurrent_token());
			delta.setPrevious_token(delta.getCurrent_token());
			previousDelta = delta.getPrevious_token();
		}
		try {
			ExtractorServiceName esn = extractorServiceNameService.findbyExtractorName(extractorName);
			extractorServiceName = esn.getServiceName();
			extractorsourceSubtype = esn.getSourceSubType();
			logger.trace("Service Configured@" + extractor.getName() + "!" + extractor.getInstanceid() + "*"
					+ extractor.getFunction() + "%Extractor Service name configured" + "#" + username);
			sr.getStatus().add("Extractor Service name configured");
			template.convertAndSend("/topic/user", sr);
		} catch (Exception e) {
			logger.trace(
					"Cancelled@" + extractor.getName() + "!" + extractor.getInstanceid() + "*" + extractor.getFunction()
							+ "%Extractor Service name not configured" + "#" + username);
			sr.getStatus().add("Extractor Service name not configured");
			template.convertAndSend("/topic/user", sr);
			sr.setJobStatus("Cancelled");
			return "Extractor Service name not configured";
		}
		if("bw".equals(extractor.getSourceType().toLowerCase())) {
			if("Info Cube".toLowerCase().equals(extractorsourceSubtype.toLowerCase()))
				url = new URL("http://"+ sourceid + ":"+ port +"/sap/opu/odata/sap/"+ extractorServiceName+"/DeltaLinksOfFactsOf"+ extractorName +"('"+delta.getCurrent_token()+"')/ChangesAfter/?$format=json");
			else if("DSO".toLowerCase().equals(extractorsourceSubtype.toLowerCase()))
				url = new URL(" http://"+ sourceid + ":" + port +"/sap/opu/odata/sap/"+extractorServiceName+"/DeltaLinksOfFactsOf"+extractorName+"('"+delta.getCurrent_token()+"')/ChangesAfter/?$format=json ");
		}
		else {
		url = new URL("http://" + sourceid + ":" + port + "/sap/opu/odata/sap/" + extractorServiceName
				+ "/DeltaLinksOfEntityOf" + extractorName + "('" + delta.getCurrent_token()
				+ "')/ChangesAfter/?$format=json");
		}

//		if (source.getSystemId().equals("PUB") && extractor.getFunction().equals("2LIS_02_ACC")) {
//			System.out.println("i`m in 2LIS_02_ACC");
//				url = new URL("http://" + sourceid + ":" + port + "/sap/opu/odata/sap/Z_ODATA_2LIS_02_ACC_1_SRV_02/DeltaLinksOfEntityOf2LIS_02_ACC" + "('" + delta.getCurrent_token()
//						+ "')/ChangesAfter/?$format=json");
//		}
//		if(extractorName.equals("0FI_GL_14")) {
//			url = new URL("http://" + sourceid + ":" + port + "/sap/opu/odata/sap/Z_ODATA_0FI_GL_14_1_SRV/DeltaLinksOfEntityOf0FI_GL_14" + "('" + delta.getCurrent_token()
//			+ "')/ChangesAfter/?$format=json");
//		}
//		for full load data
//		url = new URL("http://192.168.1.30:8001/sap/opu/odata/sap/Z_ODATA_0FI_GL_4_SRV/EntityOf0FI_GL_4/?$format=json&$filter=BUKRS%20eq%20%27US10%27%20and%20FISCPER%20eq%20%272020003%27");
//		if(extractorName.equals("0INFO_REC_ATTR")) {
//			url = new URL("http://192.168.1.30:8001/sap/opu/odata/sap/Z_ODATA_0INFO_REC_ATTR_1_SRV/EntityOf0INFO_REC_ATTR/?$format=json");
//		}

		Metadata[] metadata;
		if (metadataStructureService.getDeltaByExtractorName(deltaextractorName) == null) {
			createMetadata(extractor, source);
			MetadataStructure ms = metadataStructureService.getDeltaByExtractorName(deltaextractorName);
			metadata = new Metadata[ms.getMetadata().length];
			metadata = ms.getMetadata();
			if (checktable(deltaextractorName, dbinsert)) {
				createtable(ms.getSql(), dbinsert);
			}
		} else {
			MetadataStructure ms = metadataStructureService.getDeltaByExtractorName(deltaextractorName);
			metadata = new Metadata[ms.getMetadata().length];
			metadata = ms.getMetadata();
			if (checktable(deltaextractorName, dbinsert)) {
				createtable(ms.getSql(), dbinsert);
			}
		}
		HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
		System.out.println("test: " + httpConnection.getURL());
		// to remove odata.maxpagesize=15000
		httpConnection.setRequestProperty("Prefer", "odata.track-changes"); // uncomment
		httpConnection.setConnectTimeout(120000);
		try {
			httpConnection.connect();
			logger.trace(
					"Connected@" + extractor.getName() + "!" + extractor.getInstanceid() + "*" + extractor.getFunction()
							+ "%Connection to SAP Web-Service : Successful" + "#" + username);
			sr.getStatus().add("Connection to SAP Web-Service : Successful");
			template.convertAndSend("/topic/user", sr);
			logger.trace(
					"Initiated@" + extractor.getName() + "!" + extractor.getInstanceid() + "*" + extractor.getFunction()
							+ "%Data fetch from Web-Service : Initiated" + "#" + username);
			sr.getStatus().add("Data fetch from Web-Service : Initiated");
			template.convertAndSend("/topic/user", sr);
		} catch (Exception e) {
			logger.trace(
					"Cancelled@" + extractor.getName() + "!" + extractor.getInstanceid() + "*" + extractor.getFunction()
							+ "%Connection to SAP Web-Service FAILED : Cancelled" + "#" + username);
			sr.getStatus().add("Connection to SAP Web-Service FAILED : Cancelled");
			sr.setJobStatus("Cancelled");
			template.convertAndSend("/topic/user", sr);
		}

		try (InputStream is = httpConnection.getInputStream(); JsonReader rdr = Json.createReader(is);) {
			JsonObject obj = rdr.readObject();
			JsonObject d = obj.getJsonObject("d");
			JsonArray results = d.getJsonArray("results");
			List<String> keys = new ArrayList<String>();
			for (Metadata md : metadata) {
				keys.add(md.getColumName());
			}
			String dburl = "jdbc:sqlserver://"+dbinsert.getHostAddress()+":"+dbinsert.getPortnumber()+";databaseName="+dbinsert.getDbname();
			String dbusername = dbinsert.getUsername();
			String password = dbinsert.getPassword();

			StringBuilder sql = new StringBuilder();
			sql.append("insert into \"" + deltaextractorName + "\" (");
			for (int i = 0; i < keys.size(); i++) {
				sql.append(keys.get(i) + ",");
			}
			sql.deleteCharAt(sql.lastIndexOf(","));
			sql.append(") VALUES(");
			for (int i = 0; i < keys.size(); i++) {
				sql.append("?,");
			}
			sql.deleteCharAt(sql.lastIndexOf(","));
			sql.append(")");
			System.out.println(sql.toString());
			try (Connection conn = DriverManager.getConnection(dburl, dbusername, password);
					Statement statement = conn.createStatement();) {
//				statement.execute("truncate table \"" + deltaextractorName + "\"");
				PreparedStatement ps = conn.prepareStatement(sql.toString());

				System.out.println("metadata size" + metadata.length);
				System.out.println("keys size" + keys.size());

				if (results.size() > 0) {
					logger.trace("In Progress@" + extractor.getName() + "!" + extractor.getInstanceid() + "*"
							+ extractor.getFunction() + "%Data Adding to Batch : Initiated" + "#"
							+ username);
					sr.getStatus().add("Data Adding to Batch : Initiated");
					template.convertAndSend("/topic/user", sr);
					for (JsonObject result : results.getValuesAs(JsonObject.class)) {
						for (int i = 0; i < keys.size(); i++) {
							// System.out.println(i+" metadata datatype: "+ metadata.get(i).getDataType()+"
							// result value: "+result.getString(keys.get(i),""));
							if (metadata[i].getDataType().equals("String")) {
								ps.setString(i + 1, result.getString(keys.get(i), ""));
							} else if (metadata[i].getDataType().equals("DateTime")) {
								String temp = result.getString(keys.get(i), "");
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
								ps.setBoolean(i + 1, Boolean.parseBoolean(result.getString(keys.get(i), "")));
							} else if (metadata[i].getDataType().equals("Int32")) {
								ps.setInt(i + 1, Integer.parseInt(result.getString(keys.get(i), "0")));
							} else if (metadata[i].getDataType().equals("Time")) {
								String temp = result.getString(keys.get(i), "");
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
//							 ps.setDouble(i, Double.parseDouble(result.getString(keys.get(i), "0.0")));
								ps.setString(i + 1, result.getString(keys.get(i), ""));
							}

						}
						ps.addBatch();
					}
					logger.trace("In Progress@" + extractor.getName() + "!" + extractor.getInstanceid() + "*"
							+ extractor.getFunction() + "%Data Adding to Batch : Completed" + "#"
							+ username);
					sr.getStatus().add("Data Adding to Batch : Completed");
					template.convertAndSend("/topic/user", sr);
					logger.trace("In Progress@" + extractor.getName() + "!" + extractor.getInstanceid() + "*"
							+ extractor.getFunction() + "%Batch Data Adding to Database : Initiated" + "#"
							+ username);
					sr.getStatus().add("Batch Data Adding to Database : Initiated");
					template.convertAndSend("/topic/user", sr);
					ps.executeBatch();
					conn.commit();
					long countOfRecords = results.size();
					logger.trace("In Progress@" + extractor.getName() + "!" + extractor.getInstanceid() + "*"
							+ extractor.getFunction() + "%Batch Data Adding to Database : Completed. Records Inserted"
							+ countOfRecords + "#" + username);
					sr.getStatus().add("Batch Data Adding to Database : Completed. Records Inserted" + countOfRecords);
					template.convertAndSend("/topic/user", sr);
					logger.trace("In Progress@" + extractor.getName() + "!" + extractor.getInstanceid() + "*"
							+ extractor.getFunction() + "%Records Inserted into table " + deltaextractorName + ": "
							+ countOfRecords + "#" + username);
					sr.getStatus().add("Batch Data Adding to Database : Completed. Records Inserted" + countOfRecords);
					template.convertAndSend("/topic/user", sr);
					returnmessage = "Delta has data and inserted data";
				} else {
					logger.trace("In Progress@" + extractor.getName() + "!" + extractor.getInstanceid() + "*"
							+ extractor.getFunction() + "%Delta has no data. No data inserted" + "#"
							+ username);
					sr.getStatus().add("Delta has no data. No data inserted");
					template.convertAndSend("/topic/user", sr);
					returnmessage = "Delta has no data. No data inserted";
				}

			}
			String deltaToken = d.getString("__delta", "");
			token = deltaToken.length() > 0
					? deltaToken.substring(deltaToken.lastIndexOf("=") + 2, deltaToken.length() - 1)
					: null;
			System.out.println("token:" + token);
			createUpdateDelta(extractorName, token, previousDelta, new Date());

			returnmessage = returnmessage + " and added token";
			logger.trace("In progress@" + extractor.getName() + "!" + extractor.getInstanceid() + "*"
					+ extractor.getFunction() + "%Added Token Successfully" + "#" + username);
			sr.getStatus().add("Added Token Successfully");
			sr.setJobStatus("In Progress");
			template.convertAndSend("/topic/user", sr);
			logger.trace("Finished@" + extractor.getName() + "!" + extractor.getInstanceid() + "*"
					+ extractor.getFunction() + "%Job Completed Successfully" + "#" + username);
			sr.getStatus().add("Job Completed Successfully");
			sr.setJobStatus("Finished");
			template.convertAndSend("/topic/user", sr);
		} catch (Exception e) {
			logger.trace(
					"Cancelled@" + extractor.getName() + "!" + extractor.getInstanceid() + "*" + extractor.getFunction()
							+ "%" + e.getMessage() + " Error in " + extractorName + "#" + username);
			sr.getStatus().add(e.getMessage() + " Error in " + extractorName);
			template.convertAndSend("/topic/user", sr);
			sr.setJobStatus("Cancelled");
			return e.getMessage() + " Error in " + extractorName;
		}
		return returnmessage;
	}

	@SuppressWarnings("deprecation")
	public ResponseEntity<InputStreamResource> deltalocalftp(Extractor extractor, Source source, String username) throws Exception {
		String target = extractor.getTargetType();
		String targetid = extractor.getTargetId();
		String FILE_TO = "e:\\test\\" + extractor.getFunction() + "_full.csv";
		System.out.println("extractor:" + extractor.getTargetType() + "    " + extractor.getFunction());

		ResponseEntity<InputStreamResource> response = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		String extractorName = extractor.getFunction();
		// Delta delta = new Delta();
		Delta delta = deltaService.getDeltaByExtractor(extractorName);
		String previousDelta = "";
		String sourceid = source.getApplicationServer();
		String port = source.getPort();
		URL url = null;
		String token = null;
		if (delta == null) {
			createUpdateDelta(extractorName, "", previousDelta, new Date());
			delta = deltaService.getDeltaByExtractor(extractorName);
		} else if (delta.getExtractor().equals(extractor.getFunction())) {
			System.out.println("current token==" + delta.getCurrent_token());
			url = new URL("http://" + sourceid + ":" + port + "/sap/opu/odata/sap/Z_ODATA_" + extractorName
					+ "_SRV/DeltaLinksOfEntityOf" + extractorName + "('" + delta.getCurrent_token()
					+ "')/ChangesAfter/?$format=json");

			delta.setPrevious_token(delta.getCurrent_token());
			previousDelta = delta.getPrevious_token();
		} else {
			HttpHeaders headers = new HttpHeaders();
			headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
			headers.add("Pragma", "no-cache");
			headers.add("Expires", "0");

			String testing = "No delta value present";
			byte[] bytes = new byte[(int) testing.length()];
			InputStream inputStream = IOUtils.toInputStream(testing, "UTF-8");
			InputStreamResource resource = new InputStreamResource(inputStream);
			response = ResponseEntity.ok().headers(headers).contentLength(bytes.length)
					.contentType(MediaType.parseMediaType("application/octet-stream")).body(resource);
			return response;
		}
		url = new URL("http://" + sourceid + ":" + port + "/sap/opu/odata/sap/Z_ODATA_" + extractorName
				+ "_SRV/EntityOf" + extractorName + "/?$format=json");

		HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
		httpConnection.setRequestProperty("Prefer", "odata.track-changes");
		System.out.println("url is: " + httpConnection.getURL());
		httpConnection.connect();

		try (InputStream is = httpConnection.getInputStream(); JsonReader rdr = Json.createReader(is);) {
			JsonObject obj = rdr.readObject();
			JsonObject d = obj.getJsonObject("d");
			JsonArray results = d.getJsonArray("results");
			Iterator<JsonValue> iterator = results.iterator();
			List<String> keys = new ArrayList<String>();
			int brk = 0;
			while (iterator.hasNext()) {
				JsonObject jObject = (JsonObject) iterator.next();
				for (String key : jObject.keySet()) {
					keys.add(key);
				}
				brk++;
				if (brk == 1) {
					break;
				}
			}
			StringBuilder sb = new StringBuilder();
			for (int i = 1; i < keys.size(); i++) {
				sb.append(keys.get(i));
				if (i < keys.size() - 1) {
					sb.append(",");
				}
			}
			System.out.println(sb.toString());
			sb.append("\n");
			for (JsonObject result : results.getValuesAs(JsonObject.class)) {
				for (int i = 1; i < keys.size(); i++) {
					String value = result.getString(keys.get(i), "");
					sb.append(value);
					if (i < keys.size() - 1) {
						sb.append(",");
					}
				}
				sb.append("\n");
			}
			String csv = sb.toString();
			File file = new File(FILE_TO);
			FileUtils.writeStringToFile(file, csv);

			String deltaToken = d.getString("__delta", "");
			token = deltaToken.length() > 0
					? deltaToken.substring(deltaToken.lastIndexOf("=") + 2, deltaToken.length() - 1)
					: null;
			System.out.println("token:" + token);
			createUpdateDelta(extractorName, token, previousDelta, new Date());
		}
		HttpHeaders headers = new HttpHeaders();
		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.add("Pragma", "no-cache");
		headers.add("Expires", "0");

		File file = new File(FILE_TO);
		if (target.equals("localfile")) {
			byte[] bytes = new byte[(int) file.length()];
			InputStream inputStream = new FileInputStream(FILE_TO);
			InputStreamResource resource = new InputStreamResource(inputStream);
			response = ResponseEntity.ok().headers(headers).contentLength(bytes.length)
					.contentType(MediaType.parseMediaType("application/octet-stream")).body(resource);
			return response;
		}

		else {
//				String storageName = "miraclesapdatalake";
//				String accessKey = "7QH/vni3BuRc4EpwjzUBoqVqdKI6BAuLeo9owLU1mR3/YnrYezuJAnKXKMYIhn/d7Qt/A7WlKVLOBv5ePX2F+A==";
//				AzureStorageService azureFileUtil = new AzureStorageService(accessKey, storageName);
//				String containerName = "sapdatalake";
//				azureFileUtil.uploadDownloadtoAzure(containerName, FILE_TO, "upload")
			FtpServer ftpserver = ftpservice.findOneFtpServer(targetid);
			String storageName = ftpserver.getStorageName();
			String accessKey = ftpserver.getAccessKey();
			AzureStorageService azureFileUtil = new AzureStorageService(accessKey, storageName);
			String containerName = ftpserver.getContainerName();
			azureFileUtil.uploadDownloadtoAzure(containerName, FILE_TO, "upload");
			String testing = "Successfully Inserted data into Azure";
			byte[] bytes = new byte[(int) testing.length()];
			InputStream inputStream = IOUtils.toInputStream(testing, "UTF-8");
			InputStreamResource resource = new InputStreamResource(inputStream);
			response = ResponseEntity.ok().headers(headers).contentLength(bytes.length)
					.contentType(MediaType.parseMediaType("application/octet-stream")).body(resource);
			return response;
		}
	}

	@SuppressWarnings("deprecation")
	public ResponseEntity<InputStreamResource> fullloadlocalftp(Extractor extractor, Source source, String username) throws Exception {
		String FILE_TO = null;
		String AZURE_FILE = null;
		System.out.println("extractor:" + extractor.getTargetType() + "    " + extractor.getFunction());
		String extractorServiceName = null;
		ResponseEntity<InputStreamResource> response = null;
		String extractorName = extractor.getFunction();
		// Delta delta = new Delta();
		Packetsizeconfig psc = null;
		SkipToken st = null;
		Boolean flag;
		URL url = null;
		int packetNumber = 0;
		try {
			ExtractorServiceName esn = extractorServiceNameService.findbyExtractorName(extractorName);
			extractorServiceName = esn.getServiceName();
		} catch (Exception e) {
			logger.trace("Service Configured@" + extractor.getName() + "!" + extractor.getInstanceid() + "*"
					+ extractor.getFunction() + "%Extractor Service name configured" + "#" + username);
			System.out.println("Extractor Service name not configured");
		}
		logger.trace("Service Configured@" + extractor.getName() + "!" + extractor.getInstanceid() + "*"
				+ extractor.getFunction() + "%Extractor Service name configured" + "#" + username);
		if (skipTokenService.findByString(extractorName) != null) {
			FILE_TO = "C:\\test\\" + extractorName + "_full.csv";
			AZURE_FILE = "C:\\test\\" + extractorName + "_" + packetNumber + "_full.csv";
			url = skipTokenService.findByString(extractorName).getCurrentSkiptoken();
			logger.trace("Service Configured@" + extractor.getName() + "!" + extractor.getInstanceid() + "*"
					+ extractor.getFunction() + "%Extractor Service name configured" + "#" + username);
		} else {
			if ("full".equals(extractor.getTargetType())) {
				FILE_TO = "C:\\test\\" + extractorName + "_full.csv";
				AZURE_FILE = "C:\\test\\" + extractorName + "_" + packetNumber + "_full.csv";
				// create a new object for delta token
				url = new URL("http://192.168.1.30:8001/sap/opu/odata/sap/" + extractorServiceName + "/EntityOf"
						+ extractorName + "/?$format=json");
			} else if ("initializewithdata".equals(extractor.getTargetType())) {
				FILE_TO = "C:\\test\\" + extractorName + "initwdata.csv";
				AZURE_FILE = "C:\\test\\" + extractorName + "_" + packetNumber + "_initwdata.csv";
				// create a new object for delta token
				url = new URL("http://192.168.1.30:8001/sap/opu/odata/sap/" + extractorServiceName + "/EntityOf"
						+ extractorName + "/?$format=json");
			}
		}
		HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
		if (extractor.getTargetType().equals("full")) {
			if (packetsizeconfigService.findByExtractorName(extractorName) != null) {
				psc = packetsizeconfigService.findByExtractorName(extractorName);
				System.out.println(psc.getExtractorName() + " " + psc.getPacketsize() + " " + psc.toString());
				httpConnection.setRequestProperty("Prefer", "odata.maxpagesize=" + psc.getPacketsize());
				httpConnection.connect();
				httpConnection.setConnectTimeout(60000);
			} else if (packetsizeconfigService.findByExtractorName("DEFAULT") != null) {
				psc = packetsizeconfigService.findByExtractorName("DEFAULT");
				System.out.println(psc.getExtractorName() + " " + psc.getPacketsize() + " " + psc.toString());
				httpConnection.setRequestProperty("Prefer", "odata.maxpagesize=" + psc.getPacketsize());
				httpConnection.connect();
				httpConnection.setConnectTimeout(60000);
			} else {
				httpConnection.connect();
			}
		} else {
			if (packetsizeconfigService.findByExtractorName(extractorName) != null) {
				psc = packetsizeconfigService.findByExtractorName(extractorName);
				System.out.println(psc.getExtractorName() + " " + psc.getPacketsize() + " " + psc.toString());
				httpConnection.setRequestProperty("Prefer",
						"odata.maxpagesize=" + psc.getPacketsize() + ",odata.track-changes");
				httpConnection.connect();
			} else if (packetsizeconfigService.findByExtractorName("DEFAULT") != null) {
				psc = packetsizeconfigService.findByExtractorName("DEFAULT");
				System.out.println(psc.getExtractorName() + " " + psc.getPacketsize() + " " + psc.toString());
				httpConnection.setRequestProperty("Prefer",
						"odata.maxpagesize=" + psc.getPacketsize() + ",odata.track-changes");
				httpConnection.connect();
			} else {
				httpConnection.setRequestProperty("Prefer", "odata.track-changes");
				httpConnection.connect();
			}
		}
		logger.trace("In Progress@" + extractor.getName() + "!" + extractor.getInstanceid() + "*"
				+ extractor.getFunction() + "%Packet Size Configured" + "#" + username);
		logger.trace(
				"In Progress@" + extractor.getName() + "!" + extractor.getInstanceid() + "*" + extractor.getFunction()
						+ "%Connection to SAP Web-Service : Successful" + "#" + username);
		StringBuilder sb = new StringBuilder();
		StringBuilder azure_sb;
		int pkeys = 0;
		do {
			flag = true;
			azure_sb = new StringBuilder();
//			System.out.println(httpConnection.getRequestProperty("Prefer"));
			try (InputStream is = httpConnection.getInputStream(); JsonReader rdr = Json.createReader(is);) {
//				System.out.println(rdr.readObject());
				JsonObject obj = rdr.readObject();
				JsonObject d = obj.getJsonObject("d");
				JsonArray results = d.getJsonArray("results");
				logger.trace("Data fetch Completed@" + extractor.getName() + "!" + extractor.getInstanceid() + "*"
						+ extractor.getFunction() + "%Package " + packetNumber
						+ " : Data fetch from Web-Service : Completed" + "#" + username);
//				System.out.println(packetNumber++);
				Iterator<JsonValue> iterator = results.iterator();
				List<String> keys = new ArrayList<String>();
				pkeys++;
				if (pkeys == 1) {
					int brk = 0;
					while (iterator.hasNext()) {
						JsonObject jObject = (JsonObject) iterator.next();
						for (String key : jObject.keySet()) {
							keys.add(key);
						}
						brk++;
						if (brk == 1) {
							break;
						}
					}
					for (int i = 1; i < keys.size(); i++) {
						sb.append(keys.get(i));
						azure_sb.append(keys.get(i));
						if (i < keys.size() - 1) {
							sb.append(",");
							azure_sb.append(",");
						}
					}
					sb.append("\n");
					azure_sb.append("\n");
				}
//				System.out.println(azure_sb.toString());
				logger.trace("In Progress@" + extractor.getName() + "!" + extractor.getInstanceid() + "*"
						+ extractor.getFunction() + "%Data is inserting into Blob File" + "#"
						+ username);
				for (JsonObject result : results.getValuesAs(JsonObject.class)) {
					for (int i = 1; i < keys.size(); i++) {
						sb.append(result.getString(keys.get(i), ""));
						azure_sb.append(result.getString(keys.get(i), ""));
						if (i < keys.size() - 1) {
							sb.append(",");
							azure_sb.append(",");
						}
					}
					sb.append("\n");
					azure_sb.append("\n");
				}
				System.out.println(azure_sb.toString());
				String azurecsv = azure_sb.toString();
				File azure_file = new File(AZURE_FILE);
				FileUtils.writeStringToFile(azure_file, azurecsv);
				logger.trace("In Progress@" + extractor.getName() + "!" + extractor.getInstanceid() + "*"
						+ extractor.getFunction() + "%Data converted to Blob" + "#" + username);
				String storageName = "miraclesapdatalake";
				String accessKey = "7QH/vni3BuRc4EpwjzUBoqVqdKI6BAuLeo9owLU1mR3/YnrYezuJAnKXKMYIhn/d7Qt/A7WlKVLOBv5ePX2F+A==";
				AzureStorageService azureFileUtil = new AzureStorageService(accessKey, storageName);
				String containerName = "sapdatalake";
				System.out.println(AZURE_FILE);
				URI filepathValue = azureFileUtil.uploadDownloadtoAzure(containerName, AZURE_FILE, "upload");
				packetNumber++;
				logger.trace("In Progress@" + extractor.getName() + "!" + extractor.getInstanceid() + "*"
						+ extractor.getFunction() + "%File Uploaded Into blob. Blob URI is: " + filepathValue + "#"
						+ username);
				AZURE_FILE = "C:\\test\\" + extractorName + "_" + packetNumber + "_full.csv";

				if (d.containsKey("__next")) {
					URL previoustoken = null;
					URL temp = url;
					url = new URL(d.getString("__next"));
					System.out.println("URL: " + url + ",extractorName: " + extractorName);
					st = skipTokenService.findByString(extractorName);
					if (st == null) {
						st = new SkipToken(extractorName, url, previoustoken);
						skipTokenService.add(st);
					} else {
						previoustoken = temp;
						temp = null;
						st = new SkipToken(extractorName, url, previoustoken);
						skipTokenService.update(st, skipTokenService.findByString(extractorName).getId());
					}
					httpConnection = (HttpURLConnection) url.openConnection();
					httpConnection.connect();
					httpConnection.setConnectTimeout(60000);
				} else {
					if (st != null) {
						skipTokenService.delete(skipTokenService.findByString(extractorName).getId());
					}
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(e.getMessage());
				continue;
			}

		} while (flag);

		String csv = sb.toString();
		File file = new File(FILE_TO);
		FileUtils.writeStringToFile(file, csv);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.add("Pragma", "no-cache");
		headers.add("Expires", "0");

		byte[] bytes = new byte[(int) file.length()];
		InputStream inputStream = new FileInputStream(FILE_TO);
		InputStreamResource resource = new InputStreamResource(inputStream);
		response = ResponseEntity.ok().headers(headers).contentLength(bytes.length)
				.contentType(MediaType.parseMediaType("application/octet-stream")).body(resource);

		logger.trace("Finished@" + extractor.getName() + "!" + extractor.getInstanceid() + "*" + extractor.getFunction()
				+ "%Job Completed Successfully" + "#" + username);
//		String storageName = "miraclesapdatalake";
//		String accessKey = "7QH/vni3BuRc4EpwjzUBoqVqdKI6BAuLeo9owLU1mR3/YnrYezuJAnKXKMYIhn/d7Qt/A7WlKVLOBv5ePX2F+A==";
//		AzureStorageService azureFileUtil = new AzureStorageService(accessKey, storageName);
//		String containerName = "sapdatalake";
//		azureFileUtil.uploadDownloadtoAzure(containerName, FILE_TO, "upload");

		return response;
	}

	private void createUpdateDelta(String extractorName, String token, String previousDelta, Date date) {
		Delta temp = new Delta(extractorName, token, previousDelta, new Date());
		Delta del = deltaService.getDeltaByExtractor(extractorName);
		if (del != null) {
			long id = del.getId();
			deltaService.updateDelta(temp, id);
		} else {
			deltaService.addDelta(temp);
		}
	}

	private Metadata[] createMetadata(Extractor extractor, Source source) throws Exception {
		String extractorName = extractor.getFunction();
		String type = extractor.getTargetType();
		// String type = "delta";
		String sourceid = source.getApplicationServer();
		String port = source.getPort();
		String extractorServiceName = null;
		try {
			ExtractorServiceName esn = extractorServiceNameService.findbyExtractorName(extractorName);
			extractorServiceName = esn.getServiceName();
			logger.trace("Service Configured@" + extractor.getName() + "!" + extractor.getInstanceid() + "*"
					+ extractor.getFunction() + "%Extractor Service name configured");
		} catch (Exception e) {
			logger.trace("Cancelled@" + extractor.getName() + "!" + extractor.getInstanceid() + "*"
					+ extractor.getFunction() + "%Extractor Service name not configured");
			System.out.println("Extractor Service name not configured");
		}
		String xmlFileUrl = "http://" + sourceid + ":" + port + "/sap/opu/odata/sap/" + extractorServiceName
				+ "/$metadata";
		URL url = new URL(xmlFileUrl);
//		if (source.getSystemId().equals("PUB") && extractor.getFunction().equals("2LIS_02_ACC")) {
//			System.out.println("i`m in 2LIS_02_ACC");
//			url = new URL("http://192.168.1.30:8001/sap/opu/odata/sap/Z_ODATA_2LIS_02_ACC_1_SRV_02/$metadata");
//
//		}
//		else if (source.getSystemId().equals("PUB") && extractor.getFunction().equals("0FI_GL_14")) {
//			System.out.println("i`m in 0FI_GL_14");
//			url = new URL("http://192.168.1.30:8001/sap/opu/odata/sap/Z_ODATA_0FI_GL_14_1_SRV/$metadata");
//
//		}
//		else if(extractorName.equals("0INFO_REC_ATTR")) {
//			url = new URL("http://192.168.1.30:8001/sap/opu/odata/sap/Z_ODATA_0INFO_REC_ATTR_1_SRV/$metadata");
//		}
		url.openConnection();
		System.out.println("downloading file from " + xmlFileUrl + " ...");

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		org.w3c.dom.Document doc = dBuilder.parse(url.openStream());

		NodeList nList = doc.getElementsByTagName("EntityType");

		StringBuilder sb = new StringBuilder();
		StringBuilder primarykeys = new StringBuilder();
		Metadata[] metadata = null;
		if (type.equals("delta")) {
			extractorName = extractorName + "_DELTA";
			sb.append("CREATE TABLE \"" + extractorName + "\" (ID INT IDENTITY(1,1) PRIMARY KEY,");
		} else {
			sb.append("CREATE TABLE \"" + extractorName + "\" (");
		}
		for (int i = 0; i < 1; i++) {
			System.out.println("Processing element " + (i + 1) + "/" + nList.getLength());
			Node node = nList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				NodeList prods = element.getElementsByTagName("Property");
				NodeList propertyKey = element.getElementsByTagName("PropertyRef");
				System.out.println(propertyKey.getLength());
				primarykeys.append(" PRIMARY KEY(");
				if (prods.getLength() != propertyKey.getLength()) {
					for (int j = 0; j < propertyKey.getLength(); j++) {
						Node propkey = propertyKey.item(j);
						// System.out.println("length is : "+propertyKey.getLength());
						if (propkey.getNodeType() == Node.ELEMENT_NODE) {
							Element product = (Element) propkey;
							System.out.println(product.hasAttributes());
							NamedNodeMap columns = product.getAttributes();
							for (int l = 0; l < columns.getLength(); l++) {
								System.out.println(columns.item(l));
							}
							String columnName = product.getAttribute("Name");
							System.out.println(columnName);
							primarykeys.append(columnName + ",");
							System.out.print("testing: " + columnName + " ");
							System.out.println(primarykeys);
						}
					}
				} else {
					sb.append("ID INT IDENTITY(1,1) PRIMARY KEY,");
					System.out.println("i need to implement dynamic primary keys");
				}
				metadata = new Metadata[prods.getLength()];
				for (int j = 0; j < prods.getLength(); j++) {
					Node prod = prods.item(j);
					if (prod.getNodeType() == Node.ELEMENT_NODE) {
						Element product = (Element) prod;
						String columnName = product.getAttribute("Name");
						sb.append(columnName + " ");
						String dataType = product.getAttribute("Type")
								.substring(product.getAttribute("Type").lastIndexOf(".") + 1);
						String maxLength = product.getAttribute("MaxLength");
						int lmr = maxLength == "" ? 255 : Integer.parseInt(maxLength);
						Metadata temp = new Metadata(columnName, dataType);
						metadata[j] = temp;
						if (dataType.equals("String")) {
							sb.append("VARCHAR(" + lmr + ")");
						} else if (dataType.equals("DateTime")) {
							sb.append("DATE");
						} else if (dataType.equals("Boolean")) {
							sb.append("BOOLEAN");
						} else if (dataType.equals("Int32")) {
							sb.append("INT");
						} else if (dataType.equals("Time")) {
							sb.append("TIME");
						} else if (dataType.equals("Decimal")) {
							sb.append("DECIMAL");
						}
						String primaryKey = product.getAttribute("Nullable");
						if (primaryKey.length() > 0) {
							boolean key = primaryKey.equals("false") ? true : false;
							if (key)
								sb.append("");
						}
						sb.append(", ");
					}
				}
			}
		}

		if (type.equals("delta")) {
			sb.deleteCharAt(sb.lastIndexOf(","));
			sb.append(");");
		} else if (primarykeys.length() > 14) {
			primarykeys.deleteCharAt(primarykeys.lastIndexOf(","));
			primarykeys.append(")");
			String pk = primarykeys.toString();
			sb.append(pk);
			sb.append(");");
		} else {
			sb.deleteCharAt(sb.lastIndexOf(","));
			sb.append(");");
		}
		String sql = sb.toString();
		System.out.println(sql);
		MetadataStructure ms = new MetadataStructure(extractorName, metadata, sql, sourceid, new Date());
		metadataStructureService.add(ms);
		return metadata;
//      Boolean checkTable = checktable(extractorName);

//		if(checkTable) {
//			System.out.println(sb);
//			 createtable(sql);
//			 System.out.println("table created successfully");
//		}		
		// jdbctemplate.execute(sql);
	}

	private void createtable(String sql, DbInsert dbinsert) throws SQLException {
		System.out.println(dbinsert.getConnectionName()+" "+dbinsert.toString());
		
		String dburl = "jdbc:sqlserver://"+dbinsert.getHostAddress()+":"+dbinsert.getPortnumber()+";databaseName="+dbinsert.getDbname();
		String username = dbinsert.getUsername();
		String password = dbinsert.getPassword();
//		jdbctemplate.execute(sql);
//		
		try (Connection con = DriverManager.getConnection(dburl,username,password);
                Statement stmt = con.createStatement();){
                	stmt.execute(sql);
                }
	}

	public ResponseEntity<InputStreamResource> getStructure(String extractorName, String sourceid, String port)
			throws Exception {
		ResponseEntity<InputStreamResource> response = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.add("Pragma", "no-cache");
		headers.add("Expires", "0");
		String xmlFileUrl = "http://" + sourceid + ":" + port + "/sap/opu/odata/sap/Z_ODATA_" + extractorName
				+ "_SRV/$metadata";
		URL url = new URL(xmlFileUrl);
		url.openConnection();
		System.out.println("downloading file from " + xmlFileUrl + " ...");

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		org.w3c.dom.Document doc = dBuilder.parse(url.openStream());

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Data");

		Font headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerFont.setFontHeightInPoints((short) 14);
		headerFont.setColor(IndexedColors.RED.getIndex());

		CellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.setFont(headerFont);

		NodeList nList = doc.getElementsByTagName("EntityType");

		for (int i = 0; i < 1; i++) {
			Node node = nList.item(i);
			Row tableRow = sheet.createRow(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				// String substanceName =
				// element.getElementsByTagName("Property").item(0).getTextContent();
				NodeList prods = element.getElementsByTagName("Property");
				for (int j = 0; j < prods.getLength(); j++) {
					// System.out.println(prods.getLength());
					Node prod = prods.item(j);
					if (prod.getNodeType() == Node.ELEMENT_NODE) {
						Element product = (Element) prod;
						String xtractorName = product.getAttribute("Name");
						String dataType = product.getAttribute("Type")
								.substring(product.getAttribute("Type").lastIndexOf(".") + 1);
						String maxLength = product.getAttribute("MaxLength");
						int lmr = maxLength == "" ? 255 : Integer.parseInt(maxLength);
						tableRow.createCell(j).setCellValue(xtractorName + " (" + dataType + "," + lmr + ")");
					}
				}
			}
		}

		workbook.write(bos);
		byte[] bytes = bos.toByteArray();
		InputStream is = new ByteArrayInputStream(bytes);
		InputStreamResource resource = new InputStreamResource(is);
		response = ResponseEntity.ok().headers(headers).contentLength(bytes.length)
				.contentType(MediaType.parseMediaType("application/octet-stream")).body(resource);
		workbook.close();
		System.out.println("Structure downloaded successfully");
		return response;

	}

	private Boolean checktable(String extractorName, DbInsert dbinsert) throws SQLException {
		System.out.println(dbinsert.getConnectionName());
		String url = "jdbc:sqlserver://"+dbinsert.getHostAddress()+":"+dbinsert.getPortnumber()+";databaseName="+dbinsert.getDbname()+"";
		String username = dbinsert.getUsername();
		String password = dbinsert.getPassword();

		try (Connection conn = DriverManager.getConnection(url, username, password)) {
			DatabaseMetaData dbm = conn.getMetaData();
			ResultSet rs = dbm.getTables(null, null, extractorName, null);
			if (rs.next()) {
				return false;
			} else {
				return true;
			}
		}
	}

	public static int getrowCount(String extractorname, DbInsert dbinsert) throws SQLException {
		System.out.println(dbinsert.getConnectionName());
		String url = "jdbc:sqlserver://"+dbinsert.getHostAddress()+":"+dbinsert.getPortnumber()+";databaseName="+dbinsert.getDbname()+"";
		String username = dbinsert.getUsername();
		String password = dbinsert.getPassword();

		int count = 0;

		try (Connection conn = DriverManager.getConnection(url, username, password)) {
			Statement st = conn.createStatement();
			ResultSet res = st.executeQuery("SELECT COUNT(*) FROM " + "\"" + extractorname + "\"");
			System.out.println(res.getFetchSize());
			while (res.next()) {
				count = res.getInt(1);
			}
			System.out.println("Number of row:" + count);
		} catch (SQLException s) {
			s.printStackTrace();
			System.out.println("SQL statement is not executed!");
		}
		return count;
	}

	private void runstoreproc(String extractorName) throws SQLException {
		String url = "jdbc:sqlserver://172.17.0.141:1433;databaseName=SAPDATALAKE";
		String username = "SAPUSER";
		String password = "Miracle@1";

		String SPsql = "EXEC \"" + extractorName + "_proc\""; // for stored proc taking 2 parameters
		try (Connection conn = DriverManager.getConnection(url, username, password)) {
			PreparedStatement ps = conn.prepareStatement(SPsql);
			ps.setEscapeProcessing(true);
			ps.setQueryTimeout(2);
			// ResultSet rs = ps.executeQuery();
			ps.execute();
		}
	}

	public List<String> getTablesFromSourceDb(SourceDatabase sourceDatabase) throws SQLException {
		System.out.println(sourceDatabase.getConnectionName());
		String url = "jdbc:sqlserver://"+sourceDatabase.getHostAddress()+":"+sourceDatabase.getPortnumber()+";databaseName="+sourceDatabase.getDbname()+"";
		String username = sourceDatabase.getUsername();
		String password = sourceDatabase.getPassword();
		List<String> tablesList = new ArrayList<String>();
		try (Connection conn = DriverManager.getConnection(url, username, password)) {
			DatabaseMetaData dbm = conn.getMetaData();
			ResultSet rs = dbm.getTables("SAPDATALAKE", null,"%", new String[] {"TABLE"});
			while(rs.next()) {
				String tablename = rs.getString("TABLE_NAME");
				tablesList.add(tablename);
			}
			System.out.println(tablesList.size());
			tablesList.remove(tablesList.size()-1);
			tablesList.remove(tablesList.size()-1);
		}catch(Exception e){
			System.out.println(e.getMessage());
			tablesList.add(e.getMessage());
		}
		
		return tablesList;
	}

	public List<String> getColumnsFromTable(SourceDatabase sourceDatabase, String tableName) throws SQLException {
		System.out.println(sourceDatabase.getConnectionName());
		String url = "jdbc:sqlserver://"+sourceDatabase.getHostAddress()+":"+sourceDatabase.getPortnumber()+";databaseName="+sourceDatabase.getDbname()+"";
		String username = sourceDatabase.getUsername();
		String password = sourceDatabase.getPassword();
		List<String> columnsList = new ArrayList<String>();
		try (Connection conn = DriverManager.getConnection(url, username, password)) {
			DatabaseMetaData dbm = conn.getMetaData();
			ResultSet rs = dbm.getColumns("SAPDATALAKE", null,tableName, null);
			while(rs.next()) {
				String columnName = rs.getString("TABLE_NAME");
				columnsList.add(columnName);
			}
		}catch(Exception e){
			System.out.println(e.getMessage());
			columnsList.add(e.getMessage());
		}
		
		return columnsList;
	}

	public ResponseEntity<InputStreamResource> executeDb(SourceDatabase sourceDatabase, String tablename, String columns, DbInsert db) throws IOException {
		ResponseEntity<InputStreamResource> response = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		XSSFWorkbook workbook = new XSSFWorkbook();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.add("Pragma", "no-cache");
		headers.add("Expires", "0");
		String url = "jdbc:sqlserver://"+sourceDatabase.getHostAddress()+":"+sourceDatabase.getPortnumber()+";databaseName="+sourceDatabase.getDbname()+"";
		String username = sourceDatabase.getUsername();
		String password = sourceDatabase.getPassword();
		try (Connection conn = DriverManager.getConnection(url, username, password)) {
			Statement stmt = conn.createStatement();
			StringBuilder sql = new StringBuilder();
			String[] col = new String[columns.length()];
			col = columns.split(",");
			sql = sql.append("select ");
			for(String c : col) {
				sql.append(c);
			}
			sql.append(" from "+tablename);
			ResultSet rs = stmt.executeQuery(sql.toString());
			String target_url = "jdbc:sqlserver://"+db.getHostAddress()+":"+db.getPortnumber()+";databaseName="+db.getDbname()+"";
			String target_username = db.getUsername();
			String target_password = db.getPassword();
			try (Connection target_conn = DriverManager.getConnection(target_url, target_username, target_password)){
				Statement target_stmt = target_conn.createStatement();
				while(rs.next()) {
					target_stmt.execute(rs.getString(1));
				}
			}			
			XSSFSheet sheet = workbook.createSheet("Data");

			Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			// headerFont.setBold(true);
			headerFont.setFontHeightInPoints((short) 14);
			headerFont.setColor(IndexedColors.RED.getIndex());

			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFont(headerFont);

			Row tableRow = sheet.createRow(0);
			tableRow.createCell(0).setCellValue("Table:");
			tableRow.createCell(1).setCellValue(tablename);
			// Create a Row
			Row columnsRow = sheet.createRow(1);
			columnsRow.createCell(0).setCellValue("#");
			
			//columns as a header
			for (int i = 0; i < col.length; i++) {
				columnsRow.createCell(i + 1).setCellValue(col[i]);
			}
			int iRow = 0;
			while (rs.next()) {
				Row row = sheet.createRow(iRow + 2);
				row.createCell(0).setCellValue(iRow + 1);
				for (int column = 0; column < col.length; column++) {
					row.createCell(column + 1).setCellValue(rs.getString(col[column]));
				}
				iRow++;
			}
			workbook.write(bos);
			byte[] bytes = bos.toByteArray();
			InputStream is = new ByteArrayInputStream(bytes);
			InputStreamResource resource = new InputStreamResource(is);
			response = ResponseEntity.ok().headers(headers).contentLength(bytes.length)
					.contentType(MediaType.parseMediaType("application/octet-stream")).body(resource);
		}catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			workbook.close();
			bos.close();
		}
		
		return response;
	}
}
