package com.miraclesoft.datalake.json;

import java.awt.Font;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLRecoverableException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.miraclesoft.datalake.model.Source;
import com.miraclesoft.datalake.model.Table;
import com.miraclesoft.datalake.mongo.model.ImportParameterModel;
import com.miraclesoft.datalake.mongo.model.Metadata;
import com.miraclesoft.datalake.mongo.model.MetadataStructure;
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

import antlr.collections.Stack;
import ch.qos.logback.core.util.SystemInfo;

public class Jsonsample {

	@Autowired
	private JdbcTemplate jdbctemplate;

	public static void main(String[] args) throws JCoException, IOException, ParseException, Exception {
		try {
			// step1 load the driver class
			Class.forName("oracle.jdbc.driver.OracleDriver");

			// step2 create the connection object
			Connection con = DriverManager.getConnection("jdbc:oracle:thin:@//172.17.0.154:1521/ELEKTRADB", "ELEKTRA_ADM","ELEKTRA");
//			Connection con = DriverManager.getConnection("jdbc:oracle:thin:@//172.17.0.154:1521/ELEKTRADB", "ELEKTRA_ADM","ELEKTRA");
			// step3 create the statement object
			Statement stmt = con.createStatement();

			// step4 execute query
			ResultSet rs = stmt.executeQuery("SELECT * FROM TAB");
			System.out.println(rs);
		} catch (SQLRecoverableException e) {
			System.out.println("Error: "+e.getMessage());
		}
//		String url = "jdbc:sqlserver://172.17.0.141:1433;databaseName=SAPDATALAKE";
//		String username = "SAPUSER";
//		String password = "Miracle@1";
//		List<String> tablesList = new ArrayList<String>();
//		try (Connection conn = DriverManager.getConnection(url, username, password)) {
//			DatabaseMetaData dbm = conn.getMetaData();
//			ResultSet rs = dbm.getColumns("SAPDATALAKE", null,"0MAT_VEND_ATTR", null);
//			System.out.println(rs);
//			int i=0;
//			while(rs.next()) {
//				String tablename = rs.getString("COLUMN_NAME");
//				tablesList.add(tablename);
//			}
//			System.out.println(tablesList.size());
//			for(String a : tablesList) {
//				System.out.println(a);
//			}
//		}catch(Exception e){
//			System.out.println(e.getMessage());
//			tablesList.add(e.getMessage());
//		}

	}

	public static void db_insert() throws IOException, SQLException, JCoException {

		JCoDestination destination = getDestination();
		System.out.println("destination:" + destination);
		JCoFunction function = destination.getRepository().getFunctionTemplate("RFC_READ_TABLE").getFunction();

		function.getImportParameterList().setValue("QUERY_TABLE", "DD02L");

		JCoTable returnFields = function.getTableParameterList().getTable("FIELDS");

		System.out.println(returnFields);

	}

	public static JCoDestination getDestination() throws JCoException {

		String destinationName = "sap_test";

		Properties connectProperties = new Properties();
		connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST, "192.168.1.30");
		connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR, "01");
		connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, "100");
		connectProperties.setProperty(DestinationDataProvider.JCO_USER, "gkorukula");
		connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, "Winter100");
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

	public static List<String> table_names_reg() throws JCoException {

		List<String> table_list = new ArrayList<>();

		JCoDestination destination = getDestination();
		System.out.println("destination:" + destination);
		JCoFunction function = destination.getRepository().getFunctionTemplate("RFC_READ_TABLE").getFunction();

		function.getImportParameterList().setValue("QUERY_TABLE", "DD02L");
		function.getImportParameterList().setValue("DELIMITER", ",");
		// function.getImportParameterList().setValue("ROWCOUNT",
		// Integer.valueOf(10000));

		try {
			/* code to get data of a specified row TRANSP and field name TABNAME */

			JCoTable returnOptions = function.getTableParameterList().getTable("OPTIONS");
			returnOptions.appendRow();
			System.out.println(returnOptions.getMetaData());
			returnOptions.setValue("TEXT", "TABCLASS  = 'TRANSP'");
			System.out.println(returnOptions.getValue("TEXT"));

			JCoTable returnOptions1 = function.getTableParameterList().getTable("OPTIONS");
			returnOptions1.appendRow();
			returnOptions1.setValue("TEXT", "TABNAME LIKE 'T02[*]'");
			System.out.println(returnOptions1.getValue("TEXT"));

			JCoTable returnFields = function.getTableParameterList().getTable("FIELDS");
			System.out.println(returnFields.getMetaData());
			returnFields.appendRow();
			returnFields.setValue("FIELDNAME", "TABNAME");

			function.execute(destination);

			JCoTable jcoTabled = function.getTableParameterList().getTable("DATA");

			int numRows = jcoTabled.getNumRows();
			System.out.println(numRows);
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
		for (String a : table_list) {
			System.out.print(a + ",");
		}
		return table_list;

	}

	public static List<ImportParameterModel> getResponse_importparams() throws IOException {

		List<ImportParameterModel> listofipm = new ArrayList<ImportParameterModel>();
		try {
			JCoDestination destination = getDestination();
			JCoFunctionTemplate function = destination.getRepository()
					.getFunctionTemplate("RFC_GET_FUNCTION_INTERFACE");
			JCoFunction func = function.getFunction();
			System.out.println(func);
			if (func == null) {
				throw new RuntimeException("RFC_GET_FUNCTION_INTERFACE not found in SAP.");
			} else {
				try {
					func.getImportParameterList().setValue("FUNCNAME", "BAPI_USER_GET_DETAIL");
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
								String smessge_0 = tableName.getString("PARAMCLASS");
								String smessge_1 = tableName.getString("PARAMETER");
								String smessge_9 = tableName.getString("DEFAULT");
								System.out.println("Param: " + smessge_0);
								System.out.println("Parameter: " + smessge_1);
								System.out.println("Defult: " + smessge_9);
								ImportParameterModel imp = new ImportParameterModel(tableName.getString("PARAMETER"),
										tableName.getString("DEFAULT"));
								listofipm.add(imp);
							}
						}

					}

				} catch (JCoException e) {
					e.printStackTrace();
				}
			}
		} catch (JCoException e) {
			e.printStackTrace();
		}
		return listofipm;

	}

	public static void gettables() throws FileNotFoundException, IOException, JCoException {

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
			JCoDestination destination = getDestination();
			JCoFunctionTemplate function = destination.getRepository().getFunctionTemplate("BAPI_USER_GET_DETAIL");
			JCoFunction func = function.getFunction();
			if (func == null) {
				throw new RuntimeException("BAPI_USER_GET_DETAILS not found in SAP.");
			} else {
				try {
					func.getImportParameterList().setValue("USERNAME", "GKORUKULA");
					func.getImportParameterList().setValue("CACHE_RESULTS", "X");
					JCoListMetaData tables_metadata = func.getFunctionTemplate().getTableParameterList();
					List<String> table_names = new ArrayList<>();
					for (int i = 0; i < tables_metadata.getFieldCount(); i++) {
						table_names.add(tables_metadata.getName(i));
					}
					func.execute(destination);

					for (String table : table_names) {
						JCoTable tableName = func.getTableParameterList().getTable(table);
						int rowNum = tableName.getNumRows();
						if (rowNum > 0) {
							XSSFSheet sheet = workbook.createSheet(table);
							// row 0
							Row tableRow = sheet.createRow(0);
							tableRow.createCell(0).setCellValue("BAPI_USER_GET_DETAIL:");

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
					workbook.write(new FileOutputStream("sample2.xlsx"));
				} catch (JCoException e) {
					e.printStackTrace();
				}
			}
		} finally {
			workbook.close();
			bos.close();
		}
		System.out.println("##############################################################");
	}

	public static ResponseEntity<InputStreamResource> getResponse1() throws IOException {

		ResponseEntity<InputStreamResource> response = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		XSSFWorkbook workbook = new XSSFWorkbook();
		HttpHeaders headers = new HttpHeaders();
//		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
//		headers.add("Pragma", "no-cache");
//		headers.add("Expires", "0");
		try {
			JCoDestination destination = getDestination();
			JCoFunctionTemplate function = destination.getRepository()
					.getFunctionTemplate("RFC_GET_FUNCTION_INTERFACE");
			JCoFunction func = function.getFunction();
			System.out.println(func);
			if (func == null) {
				throw new RuntimeException("RFC_GET_FUNCTION_INTERFACE not found in SAP.");
			} else {
				try {
					func.getImportParameterList().setValue("FUNCNAME", "BAPI_GL_GETGLACCBALANCE");
					func.execute(destination);
					JCoTable tableName = func.getTableParameterList().getTable("PARAMS");

					System.out.println("before table");
					int rowNum = tableName.getNumRows();
					System.out.print(tableName);
//					System.out.println(tableName.getMetaData());
//					System.out.println(tableName.getString("PARAMCLASS"));
//					System.out.println(tableName.getString("EXID"));
					System.out.println("number of rows are: " + rowNum);

					if (rowNum > 0) {
						XSSFSheet sheet = workbook.createSheet("Data");
//						Font headerFont = workbook.createFont();
//						headerFont.setBold(true);
//						// headerFont.setBold(true);
//						headerFont.setFontHeightInPoints((short) 14);
//						headerFont.setColor(IndexedColors.RED.getIndex());
//						CellStyle headerCellStyle = workbook.createCellStyle();
//						headerCellStyle.setFont(headerFont);

						System.out.println("completed column creation ");
						System.out.println(rowNum);
						for (int iRow = 0; iRow < rowNum; iRow++) {
							tableName.setRow(iRow);
							String paramValue = tableName.getString("PARAMCLASS");
							if (paramValue.equals("I")) {

								String smessge_0 = tableName.getString("PARAMCLASS");
								System.out.println("entered in to loop");
								String smessge_1 = tableName.getString("PARAMETER");
								String smessge_2 = tableName.getString("TABNAME");
								String smessge_3 = tableName.getString("FIELDNAME");
								String smessge_4 = tableName.getString("EXID");
								String smessge_5 = tableName.getString("POSITION");
								String smessge_6 = tableName.getString("OFFSET");
								String smessge_7 = tableName.getString("INTLENGTH");
								String smessge_8 = tableName.getString("DECIMALS");
								String smessge_9 = tableName.getString("DEFAULT");
								String smessge_10 = tableName.getString("PARAMTEXT");
								String smessge_11 = tableName.getString("OPTIONAL");
								Row row = sheet.createRow(iRow + 2);
								String[] data_msg = new String[] { smessge_0, smessge_1, smessge_2, smessge_3,
										smessge_4, smessge_5, smessge_6, smessge_7, smessge_8, smessge_9, smessge_10,
										smessge_11 };

//								System.out.println(smessge_0);
								System.out.print(iRow + " " + smessge_1);
//								System.out.println(smessge_2);
								System.out.print(" " + smessge_3);
								System.out.print(" " + smessge_4);
//								System.out.println(smessge_5);
//								System.out.println(smessge_6);
//								System.out.println(smessge_7);
//								System.out.println(smessge_8);
								System.out.print(" " + smessge_9);
								System.out.println();
//								System.out.println(smessge_10);
//								System.out.println(smessge_11);
								row.createCell(0).setCellValue(iRow + 1);

								for (int col = 0; col < data_msg.length - 1; col++) {
									row.createCell(col + 1).setCellValue(data_msg[col]);
								}
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
			}
		} catch (JCoException e) {
			e.printStackTrace();
		} finally {
			workbook.close();
			bos.close();
		}
		return response;
	}

	public static Map<String, List<String>> getResponse2() throws IOException, JCoException {
		System.out.println("##############################################################");
		Map<String, List<String>> all_param = new HashMap<>();
		try {
			JCoDestination destination = getDestination();
			JCoFunctionTemplate function = destination.getRepository().getFunctionTemplate("BAPI_GL_GETGLACCBALANCE");
			JCoFunction func = function.getFunction();

			if (func == null) {
				throw new RuntimeException("BAPI_GL_GETGLACCBALANCE not found in SAP.");
			} else {
				try {
					func.getImportParameterList().setValue("COMPANYCODE", "US10");
					func.getImportParameterList().setValue("CURRENCYTYPE", "10");
					func.getImportParameterList().setValue("FISCALYEAR", "2020");
					func.getImportParameterList().setValue("GLACCT", "0099999999");
					func.execute(destination);
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
						all_param.put(exportparam, exportparameter_names);
					}

				} catch (JCoException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(all_param);
		System.out.println("##############################################################");
		return all_param;
	}

//public static ResponseEntity<InputStreamResource> getResponse2() throws IOException,JCoException {
//
//		ResponseEntity<InputStreamResource> response = null;
//		ByteArrayOutputStream bos = new ByteArrayOutputStream();
//		XSSFWorkbook workbook = new XSSFWorkbook();
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
//		headers.add("Pragma", "no-cache");
//		headers.add("Expires", "0");
//		// add another bapi function
//		try {
//			JCoDestination destination = getDestination();
//			JCoFunctionTemplate function = destination.getRepository().getFunctionTemplate("BAPI_USER_GET_DETAIL");
//			JCoFunction func = function.getFunction();
//			System.out.println(func);
//			if (func == null) {
//				throw new RuntimeException("BAPI_USER_GET_DETAILS not found in SAP.");
//			} else {
//				func.getImportParameterList().setValue("USERNAME", "GKORUKULA");
//				func.getImportParameterList().setValue("CACHE_RESULTS","X");
//				JCoListMetaData tables_metadata = func.getFunctionTemplate().getTableParameterList();
//				List<String> table_names = new ArrayList<>();
//				for(int i=0;i<tables_metadata.getFieldCount();i++) {
//					table_names.add(tables_metadata.getName(i));
//				}
//			
//				
//				func.execute(destination);
//
//				for(String i : table_names) {
////						System.out.println(i);
//					String table=i;
////						System.out.println(table);
//					JCoTable tableName = func.getTableParameterList().getTable(table);
//					int rowNum = tableName.getNumRows();
////						System.out.println(rowNum);
//					if (rowNum > 0) {
//						System.out.println(table);
//						System.out.println(tableName);
//						XSSFSheet sheet = workbook.createSheet("GKORUKULA");
//						Font headerFont = workbook.createFont();
//						headerFont.setBold(true);
//							// headerFont.setBold(true);
//						headerFont.setFontHeightInPoints((short) 14);
//						headerFont.setColor(IndexedColors.RED.getIndex());
//						CellStyle headerCellStyle = workbook.createCellStyle();
//						headerCellStyle.setFont(headerFont);
//						Row tableRow = sheet.createRow(0);
//						tableRow.createCell(0).
////							    tableRow.createCell(1).setCellValue(bapi.getFunction());
////	                          Create a Row
//						  Row1 XSSFRow columnsRow = sheet.createRow(1);
//							columnsRow.createCell(0).setCellValue("#");
//							columnsRow.createCell(1).setCellValue("BAPI-PROF");
//						
//							columnsRow.createCell(2).setCellValue("BAPI-P TEXT");
//							columnsRow.createCell(3).setCellValue("B");
//							columnsRow.createCell(3).setCellValue("B");
//							if (rowNum > 0) {
//								for (int iRow = 0; iRow < rowNum; iRow++) {
//									tableName.setRow(iRow);
//									String sMessage = tableName.getString("BAPIPROF");
//									String smessge_1 = tableName.getString("BAPIPTEXT");
//									Row row = sheet.createRow(iRow + 2);
//									String[] data_msg = new String[] { sMessage, smessge_1 };
//									System.out.println(sMessage);
//									System.out.println(smessge_1);
//									row.createCell(0).setCellValue(iRow + 1);
//									for (int col = 0; col < data_msg.length; col++) {
//										row.createCell(col + 1).setCellValue(data_msg[col]);
//									}
//								}
////									System.out.println(sMessage);
//						}
//					workbook.write(bos);
//					byte[] bytes = bos.toByteArray();
//					InputStream is = new ByteArrayInputStream(bytes);
//					InputStreamResource resource = new InputStreamResource(is);
//					response = ResponseEntity.ok().headers(headers).contentLength(bytes.length)
//							.contentType(MediaType.parseMediaType("application/octet-stream")).body(resource);
//				}
//				}
//			}
//		}
//			 finally {
//				workbook.close();
//				bos.close();
//			}
////			System.out.println(bos);
//			return response;
//	}
//}

//public static ResponseEntity<InputStreamResource> getResponse3() throws IOException, JCoException {
//
//	ResponseEntity<InputStreamResource> response = null;
//	ByteArrayOutputStream bos = new ByteArrayOutputStream();
//	XSSFWorkbook workbook = new XSSFWorkbook();
//	HttpHeaders headers = new HttpHeaders();
//	headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
//	headers.add("Pragma", "no-cache");
//	headers.add("Expires", "0");
//	// add another bapi function
//	try {
//		JCoDestination destination = getDestination();
//		JCoFunctionTemplate function = destination.getRepository().getFunctionTemplate("BAPI_USER_GET_DETAIL");
//		JCoFunction func = function.getFunction();
//		
//		System.out.println(func);
//		if (func == null) {
//			throw new RuntimeException("BAPI_USER_GET_DETAILS not found in SAP.");
//		} else {
//			try {
//			func.getImportParameterList().setValue("USERNAME", "GKORUKULA");
//			func.getImportParameterList().setValue("CACHE_RESULTS","X");
//			JCoListMetaData tables_metadata = func.getFunctionTemplate().getTableParameterList();
//			List<String> table_names = new ArrayList<>();
//			for(int i=0;i<tables_metadata.getFieldCount();i++) {
//				table_names.add(tables_metadata.getName(i));
//			}
//		
//			
//			func.execute(destination);
//			XSSFSheet sheet = workbook.createSheet("Data{j}");
//			Font headerFont = workbook.createFont();
//			headerFont.setBold(true);
//			// headerFont.setBold(true);
//			headerFont.setFontHeightInPoints((short) 14);
//			headerFont.setColor(IndexedColors.RED.getIndex());
//			CellStyle headerCellStyle = workbook.createCellStyle();
//			headerCellStyle.setFont(headerFont); 
//			for(String i : table_names) {
////					System.out.println(i);
//				String table=i;
////					System.out.println(table);
//				JCoTable tableName = func.getTableParameterList().getTable(table);
//				int rowNum = tableName.getNumRows();
////					System.out.println(rowNum);
//				if (rowNum > 0) {
//					System.out.println(table);
//					System.out.println(tableName);
////					List<String> columns_names = new ArrayList<>();
//					JCoRecordMetaData tableMeta = tableName.getRecordMetaData();
//					for (int j= 0; j< tableMeta.getFieldCount(); j++) {
//						List<String> columns_names = new ArrayList<>();
//						columns_names.add(tableMeta.getName(j));
//						
//						System.out.println(columns_names);
//						Row tableRow = sheet.createRow(0);
//						tableRow.createCell(0).setCellValue("BAPI_USER_GET_DETAIL:");
//						for(String j1:columns_names) {
//							String column_data=j1;
//							System.out.println(column_data);
//							Row columnsRow = sheet.createRow(1);
//							columnsRow.createCell(0).setCellValue(column_data);
//							System.out.println("completed column creation ");	
//							}
//						List<String> columns_values = new ArrayList<>();
//						List<String> sMessage = new ArrayList<>();
//						if(rowNum>0) {
//							for(int iRow = 0; iRow<rowNum;iRow++) {
//								for(String k: columns_names)	{
//									tableName.setRow(iRow);
//									System.out.println(tableName.getString(k));
//									Row row = sheet.createRow(iRow + 2);
//									row.createCell(0).setCellValue(iRow + 1);
//									for (int col = 0; col < columns_values.indexOf(tableName); col++) {
//										row.createCell(col + 1).setCellValue(columns_values.indexOf(col));
//									}
//							}		
//						}		
//				}
//				}
//
//				} 
//			}
//			}
//			catch (JCoException e) {
//					e.printStackTrace();
//				}
//				workbook.write(bos);
//				byte[] bytes = bos.toByteArray();
//				InputStream is = new ByteArrayInputStream(bytes);
//				InputStreamResource resource = new InputStreamResource(is);
//				response = ResponseEntity.ok().headers(headers).contentLength(bytes.length)
//						.contentType(MediaType.parseMediaType("application/octet-stream")).body(resource);
//			}
//		} 
//		 finally {
//			workbook.close();
//			bos.close();
//		}
//		return response;
//	}

//
//public static ResponseEntity<InputStreamResource> getResponse2() throws IOException, JCoException {
//	System.out.println("##############################################################");
//	ResponseEntity<InputStreamResource> response = null;
//	ByteArrayOutputStream bos = new ByteArrayOutputStream();
//	XSSFWorkbook workbook = new XSSFWorkbook();
//	HttpHeaders headers = new HttpHeaders();
//	headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
//	headers.add("Pragma", "no-cache");
//	headers.add("Expires", "0");
//	// add another bapi function
//	try {
//		JCoDestination destination = getDestination();
//		JCoFunctionTemplate function = destination.getRepository().getFunctionTemplate("BAPI_USER_GET_DETAIL");
//		JCoFunction func = function.getFunction();
//		
//		
//		if (func == null) {
//			throw new RuntimeException("BAPI_USER_GET_DETAILS not found in SAP.");
//		} else {
//			try {
//			func.getImportParameterList().setValue("USERNAME", "GKORUKULA");
//			func.getImportParameterList().setValue("CACHE_RESULTS","X");
//			JCoListMetaData tables_metadata = func.getFunctionTemplate().getTableParameterList();
//			List<String> table_names = new ArrayList<>();
//			for(int i=0;i<tables_metadata.getFieldCount();i++) {
//				table_names.add(tables_metadata.getName(i));
//			}	
//			func.execute(destination);
//			XSSFSheet sheet = workbook.createSheet("Data");
//			Font headerFont = workbook.createFont();
//			headerFont.setBold(true);
//			// headerFont.setBold(true);
//			headerFont.setFontHeightInPoints((short) 14);
//			headerFont.setColor(IndexedColors.RED.getIndex());
//			CellStyle headerCellStyle = workbook.createCellStyle();
//			headerCellStyle.setFont(headerFont); 
//			Row tableRow = sheet.createRow(0);
//			tableRow.createCell(0).setCellValue("BAPI_USER_GET_DETAIL:");
//			Row columnsRow = sheet.createRow(1);
//			columnsRow.createCell(0).setCellValue("#");
//
//			for(String table : table_names) {
//				System.out.println("111111111");
//				JCoTable tableName = func.getTableParameterList().getTable(table);
////				System.out.println(tableName);
//				int rowNum = tableName.getNumRows();
////					System.out.println(rowNum);
//				if (rowNum > 0) {
//					System.out.println(tableName);
//					JCoRecordMetaData tableMeta = tableName.getRecordMetaData();
//					List<String> columns_names = new ArrayList<>();
//					for (int j= 0; j< tableMeta.getFieldCount(); j++) {
////						System.out.println(tableMeta.getName(j));
//						columns_names.add(tableMeta.getName(j));
//					
//					}
//					int excelRows=0;
//					
//					System.out.println(columns_names);
//					for(String j1:columns_names) {
//				excelRows=excelRows+1;
//						System.out.println(excelRows);
////						System.out.println(j1);
//						columnsRow.createCell(excelRows).setCellValue(j1);
//					}
////					JCoField column_fields=tableName.get
//					List<String> columns_values = new ArrayList<>();
//					List<String> sMessage = new ArrayList<>();
//					if(rowNum>0) {
//						for(int iRow = 0; iRow<rowNum;iRow++) {
//							for(String j: columns_names)	{
//								tableName.setRow(iRow);
//								Row row = sheet.createRow(iRow + 2);
////								System.out.println(tableName.getString(j));
//								row.createCell(excelRows).setCellValue(iRow + 1);
//						
//							for (int col = 0; col < columns_values.indexOf(tableName); col++) {
//								row.createCell(col + 1).setCellValue(columns_values.indexOf(col));
//							}	
//							
//						}		
//				}
//				}
////							if (rowNum > 0) {
////								System.out.println(table);
////								System.out.println(tableName);
////								JCoRecordMetaData tableMeta = tableName.getRecordMetaData();
////					List<String> columns_names = new ArrayList<>();
////								for (int j= 0; j< tableMeta.getFieldCount(); j++) {
////									columns_names.add(tableMeta.getName(j));
////								
////								}
////								System.out.println(columns_names);
////								Row tableRow = sheet.createRow(0);
////								tableRow.createCell(0).setCellValue("BAPI_USER_GET_DETAIL:");
////								for(String j1:columns_names) {
////									String column_data=j1;
////									System.out.println(column_data);
////									Row columnsRow = sheet.createRow(1);
////									columnsRow.createCell(0).setCellValue(column_data);
////									System.out.println("completed column creation ");	
////								}
//////								JCoField column_fields=tableName.get
////								List<String> columns_values = new ArrayList<>();
////								List<String> sMessage = new ArrayList<>();
////								if(rowNum>0) {
////									for(int iRow = 0; iRow<rowNum;iRow++) {
////										for(String j: columns_names)	{
////											tableName.setRow(iRow);
////											System.out.println(tableName.getString(j));
////											Row row = sheet.createRow(iRow + 2);
////											row.createCell(0).setCellValue(iRow + 1);
////									
////										for (int col = 0; col < columns_values.indexOf(tableName); col++) {
////											row.createCell(col + 1).setCellValue(columns_values.indexOf(col));
////										}	
////										
////									}		
////							}
////							}
//						
//		}
//				workbook.write(bos);
//				byte[] bytes = bos.toByteArray();
//				InputStream is = new ByteArrayInputStream(bytes);
//				InputStreamResource resource = new InputStreamResource(is);
//				response = ResponseEntity.ok().headers(headers).contentLength(bytes.length)
//						.contentType(MediaType.parseMediaType("application/octet-stream")).body(resource);
//				
//				System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
//	}
//			
//			}
//		catch (JCoException e) {
//			e.printStackTrace();
//		}
//		}}finally {
//		workbook.close();
//		bos.close();
//	}
//	System.out.println("##############################################################");
//return response;
//}
//}

}