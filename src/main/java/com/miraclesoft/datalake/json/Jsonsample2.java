package com.miraclesoft.datalake.json;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.util.SystemOutLogger;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.miraclesoft.datalake.model.Source;
import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoFunctionTemplate;
import com.sap.conn.jco.JCoListMetaData;
import com.sap.conn.jco.JCoMetaData;
import com.sap.conn.jco.JCoRecordMetaData;
import com.sap.conn.jco.JCoTable;
import com.sap.conn.jco.ext.DestinationDataProvider;

import ch.qos.logback.classic.db.names.TableName;

public class Jsonsample2 {
	public static void main(String[] args) throws JCoException, IOException, ParseException, Exception {

		Instant ins = Instant.parse("2020-06-08T16:19:09.000Z");
		SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy HH:mm");
		DateFormat df = new SimpleDateFormat("dd-M-yyyy HH:mm");
		sdf.setTimeZone(TimeZone.getTimeZone("EST"));
		Date d = Date.from(ins);
		String str = sdf.format(d);

		System.out.println(ins);
		System.out.println(d);

		System.out.println(str);
		
		
		System.out.println(df.parse(str));
	
		
		getResponse1();

//		HashMap<String, List<String>> keysofExt = new HashMap<>();
//		List<String> values = new ArrayList<String>();
//		values.add("CHARTACCTS");
//		values.add("ACCOUNT");
//		keysofExt.put("0ACCOUNT_ATTR", values);
//		String extractorName = "0ACCOUNT_ATTR";
//		String sourceid = "192.168.1.30"; 
//		String port = "8001";
//		String xmlFileUrl = "http://" + sourceid + ":" + port + "/sap/opu/odata/sap/Z_ODATA_" + extractorName
//				+ "_SRV/$metadata";
//		URL url = new URL(xmlFileUrl);
//		url.openConnection();
//		System.out.println("downloading file from " + xmlFileUrl + " ...");
//
//		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
//		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
//		org.w3c.dom.Document doc = dBuilder.parse(url.openStream());
//
//		NodeList nList = doc.getElementsByTagName("EntityType");
//
//		StringBuilder sb = new StringBuilder();
//		StringBuilder primarykeys = new StringBuilder();
//		sb.append("CREATE TABLE \"" + extractorName + "\" (");
//		List<Metadata> metadata = new ArrayList<>();
//		for (int i = 0; i < 1; i++) {
//			System.out.println("Processing element " + (i + 1) + "/" + nList.getLength());
//			Node node = nList.item(i);
//			if (node.getNodeType() == Node.ELEMENT_NODE) {
//				Element element = (Element) node;
//				NodeList prods = element.getElementsByTagName("Property");
//				NodeList propertyKey = element.getElementsByTagName("PropertyRef");
//				primarykeys.append(" PRIMARY KEY(");
//				if(prods.getLength() != propertyKey.getLength()) {
//					for (int j = 0; j < propertyKey.getLength(); j++) {
//						Node propkey = propertyKey.item(j);
//						//System.out.println("length is : "+propertyKey.getLength());
//						if (propkey.getNodeType() == Node.ELEMENT_NODE) {
//							Element product = (Element) propkey;
//							String columnName = product.getAttribute("Name");
//							primarykeys.append(columnName+",");
//							System.out.println("test: "+columnName);
//						}
//					}
//				}
//				else {
//					System.out.println("i need to implement dynamic primary keys");
//					//return "add keys to this extractor "+extractorName;
//				}
//				for (int j = 0; j < prods.getLength(); j++) {
//					Node prod = prods.item(j);
//					if (prod.getNodeType() == Node.ELEMENT_NODE) {
//						Element product = (Element) prod;
//						String columnName = product.getAttribute("Name");
//						sb.append(columnName + " ");
//						String dataType = product.getAttribute("Type")
//								.substring(product.getAttribute("Type").lastIndexOf(".") + 1);
//						String maxLength = product.getAttribute("MaxLength");
//						int lmr = maxLength == "" ? 255 : Integer.parseInt(maxLength);
//						Metadata temp = new Metadata(columnName, dataType);
//						metadata.add(temp);
//						if (dataType.equals("String")) {
//							sb.append("VARCHAR(" + lmr + ")");
//						} else if (dataType.equals("DateTime")) {
//							sb.append("DATE");
//						} else if (dataType.equals("Boolean")) {
//							sb.append("BOOLEAN");
//						} else if (dataType.equals("Int32")) {
//							sb.append("INT");
//						} else if (dataType.equals("Time")) {
//							sb.append("TIME");
//						} else if (dataType.equals("Decimal")) {
//							sb.append("DECIMAL");
//						}
//						String primaryKey = product.getAttribute("Nullable");
//						if (!primaryKey.equals("")) {
//							boolean key = primaryKey.equals("false") ? true : false;
//							if (key)
//								sb.append(" NOT NULL");
//						}
//						sb.append(", ");
//					}
//				}			
//			}
//		}
//		primarykeys.deleteCharAt(primarykeys.lastIndexOf(","));
//		primarykeys.append(")");
//		//sb.deleteCharAt(sb.lastIndexOf(","));
//		String pk = primarykeys.toString();
//		sb.append(pk);
//		sb.append(");");
//		String sql = sb.toString();
//		System.out.println(sql);

//		//table_names();
//		long startTime = System.nanoTime();
//		   //Measure execution time for this method
//	 
//	    long endTime = System.nanoTime();
//	 
//	    long durationInNano = (endTime - startTime); 
//	    long durationInMillis = TimeUnit.NANOSECONDS.toMillis(durationInNano);  //Total execution time in nano seconds
//	     
////	    System.out.println(durationInNano);
////	    System.out.println(durationInMillis);
//	    
//	 // milliseconds 
//	    //long miliSec = 15685920; 
//  
//        // Creating date format 
//        //DateFormat simple = new SimpleDateFormat("dd MMM yyyy HH:mm:ss:SSS Z"); 
//  
//        // Creating date from milliseconds 
//        // using Date() constructor 
//        //Date result = new Date(miliSec); 
//  
//        // Formatting Date according to the 
//        // given format 
//        //System.out.println(simple.format(result));
//        
//        //Date date = new Date(1586476800000L);
//	    String temp = "Date(1568592000000)";
//	    temp = temp.substring(temp.lastIndexOf("(")+1, temp.lastIndexOf(")"));
//	    Long temp1 = Long.parseLong(temp);
//        java.sql.Date date1  = new java.sql.Date(temp1);
//        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//        format.setTimeZone(TimeZone.getTimeZone("GMT"));
//        String formatted = format.format(date1);
//        java.sql.Date dt = null;
//        System.out.println(dt);
////		URL url = new URL("http://192.168.1.30:8001/sap/opu/odata/sap/Z_ODATA_2LIS_02_HDR_SRV/DeltaLinksOfEntityOf2LIS_02_HDR/?$format=json");
////		HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
////		httpConnection.setRequestProperty("Prefer", "odata.track-changes");
////		httpConnection.connect();
////		
////		try (InputStream is = httpConnection.getInputStream(); JsonReader rdr = Json.createReader(is);) {
////			JsonObject obj = rdr.readObject();
////			JsonObject d = obj.getJsonObject("d");
////			JsonArray results = d.getJsonArray("results");
////			for(JsonObject result : results.getValuesAs(JsonObject.class)) {
////				JsonObject changesafter = result.getJsonObject("ChangesAfter");
////				JsonObject defered = changesafter.getJsonObject("__deferred");
////				String uri = defered.getString("uri");
////				System.out.println(uri);
////				String token = uri.substring(uri.lastIndexOf("(")+2,uri.lastIndexOf(")")-1);
////				System.out.println(token);
////
////			};
////			
////		}
//		
	}

	public static List<String> table_names() throws JCoException {

		List<String> table_list = new ArrayList<>();

		JCoDestination destination = getDestination();
		System.out.println("destination:" + destination);
		JCoFunction function = destination.getRepository().getFunctionTemplate("NAMETAB_GET").getFunction();

		function.getImportParameterList().setValue("TABNAME", "ZOXPUB0107");

		try {
			/* code to get data of a specified row TRANSP and field name TABNAME */

			function.execute(destination);

			JCoTable jcoTabled = function.getTableParameterList().getTable("NAMETAB");

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

	public List<String> table_names1(Source source) throws JCoException {

		List<String> table_list = new ArrayList<>();

		JCoDestination destination = getDestination();
		System.out.println("destination:" + destination);
		JCoFunction function = destination.getRepository().getFunctionTemplate("RFC_GET_FUNCTION_INTERFACE")
				.getFunction();

		function.getImportParameterList().setValue("FUNC_NAME", "BAPI_COMPANY_CODE_GETLIST");

		try {
			/* code to get data of a specified row TRANSP and field name TABNAME */

			JCoTable returnOptions = function.getTableParameterList().getTable("");
			returnOptions.appendRow();
			returnOptions.setValue("TEXT", " TABCLASS  = 'TRANSP'");
			System.out.println("test: " + returnOptions.getValue(0));

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

	public static ResponseEntity<InputStreamResource> getResponse() throws IOException {

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
			System.out.println(func);
			if (func == null) {
				throw new RuntimeException("BAPI_USER_GET_DETAIL not found in SAP.");
			} else {
				try {
					func.getImportParameterList().setValue("USERNAME", "GKORUKULA");
					func.execute(destination);
					JCoTable tableName = func.getTableParameterList().getTable("PROFILES");
					System.out.println(tableName.getString("BAPIPROF"));
					int rowNum = tableName.getNumRows();
					System.out.println("number of rows are: " + rowNum);
//					JCoListMetaData tableName =  func.getTableParameterList().getListMetaData();ggh
//					String[] tableName =  func.getTableParameterList().getListMetaData();

//	            	JCoTable[] tableName = table.getTable();
//					 String[] tableName = {"FIELDS","OPTIONS"};
//					for(int GetCurrentTable=0;GetCurrentTable<tableName.getLength(0);GetCurrentTable++)
//					{
//						JCoTable table = tableName[i];
//						JCoTable table = tableName[GetCurrentTable];
//  					int rowNum = table.getNumRows();
//						int rowNum=20;
//						if (table.getNumRows() > 0) {
					if (rowNum > 0) {
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
//						    tableRow.createCell(1).setCellValue(bapi.getFunction());
//                          Create a Row
						Row columnsRow = sheet.createRow(1);
						columnsRow.createCell(0).setCellValue("#");
						columnsRow.createCell(1).setCellValue("BAPI-PROF");
						columnsRow.createCell(2).setCellValue("BAPI-P TEXT");
						columnsRow.createCell(3).setCellValue("B");
						columnsRow.createCell(3).setCellValue("B");
						if (rowNum > 0) {
							for (int iRow = 0; iRow < rowNum; iRow++) {
								tableName.setRow(iRow);
								String sMessage = tableName.getString("BAPIPROF");
								String smessge_1 = tableName.getString("BAPIPTEXT");
								Row row = sheet.createRow(iRow + 2);
								String[] data_msg = new String[] { sMessage, smessge_1 };
								System.out.println(sMessage);
								System.out.println(smessge_1);
								row.createCell(0).setCellValue(iRow + 1);
								for (int col = 0; col < data_msg.length; col++) {
									row.createCell(col + 1).setCellValue(data_msg[col]);
								}
							}
//								System.out.println(sMessage);
						}
					}
//					}
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
//		System.out.println(bos);
		return response;
	}

	private String[] getTable() {
		// TODO Auto-generated method stub
		return null;
	}

	public static JCoDestination getDestination() throws JCoException {

		String destinationName = "sap_system_without_pool";

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
	
	public void table_names_reg() throws JCoException {


		List<String> table_list = new ArrayList<>();

		JCoDestination destination = getDestination();
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
			returnOptions.appendRow();
			//returnOptions.setvalue("");

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
		//return table_list;

	
	}

	public static ResponseEntity<InputStreamResource> getResponse1() throws IOException {

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

					System.out.println("before table");
//					JCoParameterField parm=func.getTableParameterList().get
					int rowNum = tableName.getNumRows();
					System.out.print(tableName);
					System.out.println(tableName.getMetaData());
					System.out.println(tableName.getString("PARAMCLASS"));
					System.out.println(tableName.getString("EXID"));

					System.out.println("number of rows are: " + rowNum);


//					String[] tableName =  func.getTableParameterList().getListMetaData();

//	            	JCoTable[] tableName = table.getTable();
//					 String[] tableName = {"FIELDS","OPTIONS"};
//					for(int GetCurrentTable=0;GetCurrentTable<tableName.getLength(0);GetCurrentTable++)
//					{
//						JCoTable table = tableName[i];
//						JCoTable table = tableName[GetCurrentTable];
//  					int rowNum = table.getNumRows();
//						int rowNum=20;
//						if (table.getNumRows() > 0) {
					if (rowNum > 0) {
						XSSFSheet sheet = workbook.createSheet("Data");
						Font headerFont = workbook.createFont();
						headerFont.setBold(true);
						// headerFont.setBold(true);
						headerFont.setFontHeightInPoints((short) 14);
						headerFont.setColor(IndexedColors.RED.getIndex());
						CellStyle headerCellStyle = workbook.createCellStyle();
						headerCellStyle.setFont(headerFont);

						Row tableRow = sheet.createRow(0);
						tableRow.createCell(0).setCellValue("RFC_GET_FUNCTION_INTERFACE:");
//						    tableRow.createCell(1).setCellValue(bapi.getFunction());
//                          Create a Row
						Row columnsRow = sheet.createRow(1);
						columnsRow.createCell(0).setCellValue("#");
						columnsRow.createCell(1).setCellValue("P");
						columnsRow.createCell(2).setCellValue("PARAMETER");
						columnsRow.createCell(3).setCellValue("TABNAME");
						columnsRow.createCell(3).setCellValue("FIELDNAME");
						columnsRow.createCell(4).setCellValue("E");
						columnsRow.createCell(5).setCellValue("POSITION");
						columnsRow.createCell(6).setCellValue("OFFSET");
						columnsRow.createCell(7).setCellValue("INTLENGTH");
						columnsRow.createCell(8).setCellValue("DECIMALS");
						columnsRow.createCell(9).setCellValue("DEFAULT");
						columnsRow.createCell(10).setCellValue("PARAMTEXT");
						columnsRow.createCell(9).setCellValue("OPTIONAL");
						System.out.println("completed column creation ");
						System.out.println(rowNum);
						for (int iRow = 0; iRow < rowNum; iRow++) {
							tableName.setRow(iRow);
							String paramValue = tableName.getString("PARAMCLASS");
//								System.out.println(paramValue);
							if (paramValue.equalsIgnoreCase("I")) {
//									System.out.println("entering into p");
////									String smessge_0 = tableName.getString("P");
//									char message1 =tableName.getChar('P');	
//									String smessge_0=String.valueOf(message1);
//									System.out.println(smessge_0);
//									String smessage_0=String.valueOf(message);
								String smessge_0 = tableName.getString("PARAMCLASS");
//									System.out.println(smessge_0);
//								if(smessge_0.equalsIgnoreCase("I")) {
								System.out.println("entered in to loop");
								String smessge_1 = tableName.getString("PARAMETER");
//										System.out.println(smessge_1);
								String smessge_2 = tableName.getString("TABNAME");
//										System.out.println(smessge_2);
								String smessge_3 = tableName.getString("FIELDNAME");
//										System.out.println(smessge_3);
//									    char message1 =tableName.getChar('E');	
//									    String smessge_4=String.valueOf(message1);
								String smessge_4 = tableName.getString("EXID");
//									    System.out.println(smessge_4);
								String smessge_5 = tableName.getString("POSITION");
//									    System.out.println(smessge_5);
								String smessge_6 = tableName.getString("OFFSET");
//									    System.out.println(smessge_6);
								String smessge_7 = tableName.getString("INTLENGTH");
//									    System.out.println(smessge_7);
								String smessge_8 = tableName.getString("DECIMALS");
//									    System.out.println(smessge_8);
								String smessge_9 = tableName.getString("DEFAULT");
//									    System.out.println(smessge_9);
								String smessge_10 = tableName.getString("PARAMTEXT");
//    								    System.out.println(smessge_10);
//    								    System.out.println("entered paramtext");
								String smessge_11 = tableName.getString("OPTIONAL");
								Row row = sheet.createRow(iRow + 2);
								String[] data_msg = new String[] { smessge_0, smessge_1, smessge_2, smessge_3,
										smessge_4, smessge_5, smessge_6, smessge_7, smessge_8, smessge_9, smessge_10,
										smessge_11 };
//     								    data_msg=(String[])resizeArray(data_msg,10000);
//     								    String[] data_msg=Arrays.copyOf(data_msg1, 10000);
//     								    System.out.println(data_msg.length);
								System.out.println(smessge_0);
								System.out.println(smessge_1);
								System.out.println(smessge_2);
								System.out.println(smessge_3);
								System.out.println(smessge_4);
								System.out.println(smessge_5);
								System.out.println(smessge_6);
								System.out.println(smessge_7);
								System.out.println(smessge_8);
								System.out.println(smessge_9);
								System.out.println(smessge_10);
								System.out.println(smessge_11);
								row.createCell(0).setCellValue(iRow + 1);

								for (int col = 0; col < data_msg.length - 1; col++) {
//									    	System.out.println(data_msg[col]);
									row.createCell(col + 1).setCellValue(data_msg[col]);
								}
							}
						}

//								System.out.println(sMessage);
					}

//					}

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
//		System.out.println(bos);
		return response;
	}

	public static ResponseEntity<InputStreamResource> getResponse2() throws IOException, JCoException {
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
				throw new RuntimeException("BAPI_CONTROLLINGAREA_GETDETAIL not found in SAP.");
			} else {
				try {
				func.getImportParameterList().setValue("CONTROLLINGAREAID", "BE01");
				JCoListMetaData tables_metadata = func.getFunctionTemplate().getTableParameterList();
				System.out.println(tables_metadata);
				List<String> table_names = new ArrayList<>();
				System.out.println(tables_metadata.getFieldCount());
				for(int i=0;i<tables_metadata.getFieldCount();i++) {
					table_names.add(tables_metadata.getName(i));
				}	
				func.execute(destination);
				for(String table : table_names) {	
					System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
					int excel_rows=0;
					int excel_columns=0;
					JCoTable tableName = func.getTableParameterList().getTable(table);
					int table_rows = tableName.getNumRows();
					if(table_rows>0) {	
						XSSFSheet sheet = workbook.createSheet(table);
						Font headerFont = workbook.createFont();
						headerFont.setBold(true);
					// headerFont.setBold(true);
						headerFont.setFontHeightInPoints((short) 14);
						headerFont.setColor(IndexedColors.RED.getIndex());
						CellStyle headerCellStyle = workbook.createCellStyle();
						headerCellStyle.setFont(headerFont); 
						Row tableRow = sheet.createRow(0);
						tableRow.createCell(0).setCellValue("BAPI_CONTROLLINGAREA_GETDETAIL:");
						
						System.out.println(tableName);
						JCoRecordMetaData tableMeta = tableName.getRecordMetaData();
						
						List<String> columns_names_List = new ArrayList<>();
						for (int j= 0; j< tableMeta.getFieldCount(); j++) {
							columns_names_List.add(tableMeta.getName(j));
							}	
						for(String columnName:columns_names_List) {
							Row columnsRow = sheet.createRow(1);
							columnsRow.createCell(excel_columns+1).setCellValue(columnName);
							}	
						for(int iRow = 0; iRow<table_rows;iRow++) {
							for(String j: columns_names_List)	{
								tableName.setRow(iRow);
								tableRow.createCell(excel_rows+1).setCellValue(tableName.getString(j));
								System.out.println(tableName.getString(j));
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
					
					System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		
				
				}
			catch (JCoException e) {
				e.printStackTrace();
			}
			}}finally {
			workbook.close();
			bos.close();
		}
		System.out.println("##############################################################");
	return response;
	}

							
						
	public static ResponseEntity<InputStreamResource> getResponse3() throws IOException, JCoException {

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
			
			System.out.println(func);
			if (func == null) {
				throw new RuntimeException("BAPI_USER_GET_DETAILS not found in SAP.");
			} else {
				try {
				func.getImportParameterList().setValue("USERNAME", "GKORUKULA");
				func.getImportParameterList().setValue("CACHE_RESULTS","X");
				JCoListMetaData tables_metadata = func.getFunctionTemplate().getTableParameterList();
				List<String> table_names = new ArrayList<>();
				for(int i=0;i<tables_metadata.getFieldCount();i++) {
					table_names.add(tables_metadata.getName(i));
				}	
				func.execute(destination);
				XSSFSheet sheet = workbook.createSheet("Data{j}");
				Font headerFont = workbook.createFont();
				headerFont.setBold(true);
				// headerFont.setBold(true);
				headerFont.setFontHeightInPoints((short) 14);
				headerFont.setColor(IndexedColors.RED.getIndex());
				CellStyle headerCellStyle = workbook.createCellStyle();
				headerCellStyle.setFont(headerFont); 
				List<String> columns_names = new ArrayList<>();

				for(String i : table_names) {
//						System.out.println(i);
					String table=i;
//						System.out.println(table);
					JCoTable tableName = func.getTableParameterList().getTable(table);
					int rowNum = tableName.getNumRows();
//						System.out.println(rowNum);
					if (rowNum > 0) {
						System.out.println(table);
						System.out.println(tableName);
						JCoRecordMetaData tableMeta = tableName.getRecordMetaData();
						for (int j= 0; j< tableMeta.getFieldCount(); j++) {
							columns_names.add(tableMeta.getName(j));
						
						}
						System.out.println(columns_names);
						Row tableRow = sheet.createRow(0);
						tableRow.createCell(0).setCellValue("BAPI_USER_GET_DETAIL:");
						for(String j1:columns_names) {
							String column_data=j1;
							System.out.println(column_data);
							Row columnsRow = sheet.createRow(1);
							columnsRow.createCell(0).setCellValue(column_data);
							System.out.println("completed column creation ");	
						}
//						JCoField column_fields=tableName.get
						List<String> columns_values = new ArrayList<>();
						List<String> sMessage = new ArrayList<>();
						if(rowNum>0) {
							for(int iRow = 0; iRow<rowNum;iRow++) {
								for(String j: columns_names)	{
									tableName.setRow(iRow);
									System.out.println(tableName.getString(j));
									Row row = sheet.createRow(iRow + 2);
									row.createCell(0).setCellValue(iRow + 1);
							
								for (int col = 0; col < columns_values.indexOf(tableName); col++) {
									row.createCell(col + 1).setCellValue(columns_values.indexOf(col));
								}	
								
							}		
					}
					}
				
			}
					break;
		}
				
			 
				workbook.write(bos);
				byte[] bytes = bos.toByteArray();
				InputStream is = new ByteArrayInputStream(bytes);
				InputStreamResource resource = new InputStreamResource(is);
				response = ResponseEntity.ok().headers(headers).contentLength(bytes.length)
						.contentType(MediaType.parseMediaType("application/octet-stream")).body(resource);}
			catch (JCoException e) {
				e.printStackTrace();
			}
			}}finally {
			workbook.close();
			bos.close();
		}
	
	return response;
	}

	public static ResponseEntity<InputStreamResource> getResponse211() throws IOException, JCoException {
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
		func.getImportParameterList().setValue("CACHE_RESULTS","X");
		JCoListMetaData tables_metadata = func.getFunctionTemplate().getTableParameterList();
		List<String> table_names = new ArrayList<>();
		for(int i=0;i<tables_metadata.getFieldCount();i++) {
		table_names.add(tables_metadata.getName(i));
		}	
		func.execute(destination);

		for(String table : table_names) {
		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		JCoTable tableName = func.getTableParameterList().getTable(table);
//			System.out.println(tableName);
		int rowNum = tableName.getNumRows();
//			System.out.println(rowNum);
		if (rowNum > 0) {
		XSSFSheet sheet = workbook.createSheet(table);
		Font headerFont = workbook.createFont();
		headerFont.setBold(true);
		// headerFont.setBold(true);
		headerFont.setFontHeightInPoints((short) 14);
		headerFont.setColor(IndexedColors.RED.getIndex());
		CellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.setFont(headerFont); 
		Row tableRow = sheet.createRow(0);
		tableRow.createCell(0).setCellValue("BAPI_USER_GET_DETAIL:");
		Row columnsRow = sheet.createRow(1);
		columnsRow.createCell(0).setCellValue("#");


		System.out.println(tableName);
		JCoRecordMetaData tableMeta = tableName.getRecordMetaData();
		List<String> columns_names = new ArrayList<>();
		for (int j= 0; j< tableMeta.getFieldCount(); j++) {
		System.out.println(tableMeta.getName(j));
		columns_names.add(tableMeta.getName(j));

		}
		int excelRows=0;
		System.out.println(columns_names);

		for(String j1:columns_names) {
		excelRows=excelRows+1;
//			System.out.println(excelRows);
//			System.out.println(j1);
		columnsRow.createCell(excelRows).setCellValue(j1);
}
		int columns=0;
		System.out.println(excelRows);
		int size_col=columns_names.size();
	    int tablerow=0;
	    for (int iRow = 2; iRow < rowNum+2; iRow++) {
	    	Row row = sheet.createRow(iRow);

	    	tableName.setRow(tablerow);	
	    	tablerow++;
	    	row.createCell(0).setCellValue(tablerow);
	    	for(int col=0;col<columns_names.size();col++)
	    	{
	    	row.createCell(col+1).setCellValue(tableName.getString(columns_names.get(col)));
	    	}
	    	}
	    	System.out.println("@@@@@@@@@@@@@@@@@@");
		}	
		}
		workbook.write(bos);
		byte[] bytes = bos.toByteArray();
		InputStream is = new ByteArrayInputStream(bytes);
		InputStreamResource resource = new InputStreamResource(is);
		response = ResponseEntity.ok().headers(headers).contentLength(bytes.length)
		.contentType(MediaType.parseMediaType("application/octet-stream")).body(resource);

		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		}
		catch (JCoException e) {
		e.printStackTrace();
		}
		}}finally {
		workbook.close();
		bos.close();
		}
		System.out.println("##############################################################");
		//return response;
		return response;
		}

	
	public static ResponseEntity<InputStreamResource> getResponse212() throws IOException, JCoException {
		System.out.println("##############################################################");
		ResponseEntity<InputStreamResource> response = null;
//		ByteArrayOutputStream bos = new ByteArrayOutputStream();
//		XSSFWorkbook workbook = new XSSFWorkbook();
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
//		headers.add("Pragma", "no-cache");
//		headers.add("Expires", "0");
//		// add another bapi function
		try {
			JCoDestination destination = getDestination();
			JCoFunctionTemplate function = destination.getRepository().getFunctionTemplate("RFC_GET_FUNCTION_INTERFACE");
			JCoFunction func = function.getFunction();
			if (func == null) {
			throw new RuntimeException("BAPI_USER_GET_DETAILS not found in SAP.");
			} else {
			func.getImportParameterList().setValue("FUNCNAME", "BAPI_USER_GET_DETAIL");
			JCoListMetaData tables_metadata = func.getFunctionTemplate().getTableParameterList();
			List<String> table_names = new ArrayList<>();
			for(int i=0;i<tables_metadata.getFieldCount();i++) {
			table_names.add(tables_metadata.getName(i));
			}
			System.out.println(table_names);
			func.execute(destination);
			
			for(String table : table_names) {
			System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
			JCoTable tableName = func.getTableParameterList().getTable(table);
//				System.out.println(tableName);
			int rowNum = tableName.getNumRows();
//				System.out.println(rowNum);
			if (rowNum > 0) {
//			XSSFSheet sheet = workbook.createSheet(table);
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

				System.out.println(tableName);
				JCoRecordMetaData tableMeta = tableName.getRecordMetaData();
				List<String> columns_names = new ArrayList<>();
				for (int j= 0; j< tableMeta.getFieldCount(); j++) {
				System.out.println(tableMeta.getName(j));
				columns_names.add(tableMeta.getName(j));

				}
			
			
		}
			}
			}
		}
			catch (JCoException e) {
				e.printStackTrace();
				}
		return response;
	}
}

	
	
		
	
	
	
//	public static ResponseEntity<InputStreamResource> getResponse1() throws IOException {
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
//			JCoFunctionTemplate function = destination.getRepository()
//					.getFunctionTemplate("RFC_GET_FUNCTION_INTERFACE");
//			JCoFunction func = function.getFunction();
//			System.out.println(func);
//			if (func == null) {
//				throw new RuntimeException("RFC_GET_FUNCTION_INTERFACE not found in SAP.");
//			} else {
//				try {
//					func.getImportParameterList().setValue("FUNCNAME", "BAPI_USER_GET_DETAIL");
//					func.execute(destination);
//					JCoTable tableName = func.getTableParameterList().getTable("PARAMS");
//
//					System.out.println("before table");
////					JCoParameterField parm=func.getTableParameterList().get
//					int rowNum = tableName.getNumRows();
//					System.out.print(tableName);
//					System.out.println(tableName.getMetaData());
//					System.out.println(tableName.getString("PARAMCLASS"));
//					System.out.println(tableName.getString("EXID"));
//
//					System.out.println("number of rows are: " + rowNum);
//
//
////					String[] tableName =  func.getTableParameterList().getListMetaData();
//
////	            	JCoTable[] tableName = table.getTable();
////					 String[] tableName = {"FIELDS","OPTIONS"};
////					for(int GetCurrentTable=0;GetCurrentTable<tableName.getLength(0);GetCurrentTable++)
////					{
////						JCoTable table = tableName[i];
////						JCoTable table = tableName[GetCurrentTable];
////  					int rowNum = table.getNumRows();
////						int rowNum=20;
////						if (table.getNumRows() > 0) {
//					if (rowNum > 0) {
//						XSSFSheet sheet = workbook.createSheet("Data");
//						Font headerFont = workbook.createFont();
//						headerFont.setBold(true);
//						// headerFont.setBold(true);
//						headerFont.setFontHeightInPoints((short) 14);
//						headerFont.setColor(IndexedColors.RED.getIndex());
//						CellStyle headerCellStyle = workbook.createCellStyle();
//						headerCellStyle.setFont(headerFont);
//
//						Row tableRow = sheet.createRow(0);
//						tableRow.createCell(0).setCellValue("RFC_GET_FUNCTION_INTERFACE:");
////						    tableRow.createCell(1).setCellValue(bapi.getFunction());
////                          Create a Row
//						Row columnsRow = sheet.createRow(1);
//						columnsRow.createCell(0).setCellValue("#");
//						columnsRow.createCell(1).setCellValue("P");
//						columnsRow.createCell(2).setCellValue("PARAMETER");
//						columnsRow.createCell(3).setCellValue("TABNAME");
//						columnsRow.createCell(3).setCellValue("FIELDNAME");
//						columnsRow.createCell(4).setCellValue("E");
//						columnsRow.createCell(5).setCellValue("POSITION");
//						columnsRow.createCell(6).setCellValue("OFFSET");
//						columnsRow.createCell(7).setCellValue("INTLENGTH");
//						columnsRow.createCell(8).setCellValue("DECIMALS");
//						columnsRow.createCell(9).setCellValue("DEFAULT");
//						columnsRow.createCell(10).setCellValue("PARAMTEXT");
//						columnsRow.createCell(9).setCellValue("OPTIONAL");
//						System.out.println("completed column creation ");
//						System.out.println(rowNum);
//						for (int iRow = 0; iRow < rowNum; iRow++) {
//							tableName.setRow(iRow);
//							String paramValue = tableName.getString("PARAMCLASS");
////								System.out.println(paramValue);
//							if (paramValue.equalsIgnoreCase("I")) {
////									System.out.println("entering into p");
//////									String smessge_0 = tableName.getString("P");
////									char message1 =tableName.getChar('P');	
////									String smessge_0=String.valueOf(message1);
////									System.out.println(smessge_0);
////									String smessage_0=String.valueOf(message);
//								String smessge_0 = tableName.getString("PARAMCLASS");
////									System.out.println(smessge_0);
////								if(smessge_0.equalsIgnoreCase("I")) {
//								System.out.println("entered in to loop");
//								String smessge_1 = tableName.getString("PARAMETER");
////										System.out.println(smessge_1);
//								String smessge_2 = tableName.getString("TABNAME");
////										System.out.println(smessge_2);
//								String smessge_3 = tableName.getString("FIELDNAME");
////										System.out.println(smessge_3);
////									    char message1 =tableName.getChar('E');	
////									    String smessge_4=String.valueOf(message1);
//								String smessge_4 = tableName.getString("EXID");
////									    System.out.println(smessge_4);
//								String smessge_5 = tableName.getString("POSITION");
////									    System.out.println(smessge_5);
//								String smessge_6 = tableName.getString("OFFSET");
////									    System.out.println(smessge_6);
//								String smessge_7 = tableName.getString("INTLENGTH");
////									    System.out.println(smessge_7);
//								String smessge_8 = tableName.getString("DECIMALS");
////									    System.out.println(smessge_8);
//								String smessge_9 = tableName.getString("DEFAULT");
////									    System.out.println(smessge_9);
//								String smessge_10 = tableName.getString("PARAMTEXT");
////    								    System.out.println(smessge_10);
////    								    System.out.println("entered paramtext");
//								String smessge_11 = tableName.getString("OPTIONAL");
//								Row row = sheet.createRow(iRow + 2);
//								String[] data_msg = new String[] { smessge_0, smessge_1, smessge_2, smessge_3,
//										smessge_4, smessge_5, smessge_6, smessge_7, smessge_8, smessge_9, smessge_10,
//										smessge_11 };
////     								    data_msg=(String[])resizeArray(data_msg,10000);
////     								    String[] data_msg=Arrays.copyOf(data_msg1, 10000);
////     								    System.out.println(data_msg.length);
//								System.out.println(smessge_0);
//								System.out.println(smessge_1);
//								System.out.println(smessge_2);
//								System.out.println(smessge_3);
//								System.out.println(smessge_4);
//								System.out.println(smessge_5);
//								System.out.println(smessge_6);
//								System.out.println(smessge_7);
//								System.out.println(smessge_8);
//								System.out.println(smessge_9);
//								System.out.println(smessge_10);
//								System.out.println(smessge_11);
//								row.createCell(0).setCellValue(iRow + 1);
//
//								for (int col = 0; col < data_msg.length - 1; col++) {
////									    	System.out.println(data_msg[col]);
//									row.createCell(col + 1).setCellValue(data_msg[col]);
//								}
//							}
//						}
//
////								System.out.println(sMessage);
//					}
//
////					}
//
//				} catch (JCoException e) {
//					e.printStackTrace();
//				}
//				workbook.write(bos);
//				byte[] bytes = bos.toByteArray();
//				InputStream is = new ByteArrayInputStream(bytes);
//				InputStreamResource resource = new InputStreamResource(is);
//				response = ResponseEntity.ok().headers(headers).contentLength(bytes.length)
//						.contentType(MediaType.parseMediaType("application/octet-stream")).body(resource);
//			}
//		} catch (JCoException e) {
//			e.printStackTrace();
//		} finally {
//			workbook.close();
//			bos.close();
//		}
////		System.out.println(bos);
//		return response;
//	}

 
