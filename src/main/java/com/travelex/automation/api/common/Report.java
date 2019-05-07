package com.travelex.automation.api.common;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents the methods pertaining to Reporting
 * 
 * @author 
 */
public class Report {
	
	private static final Logger LOG = LoggerFactory.getLogger(Report.class);
	RandomAccessFile raf;
	RandomAccessFile rafi;
	public String folderName;
	public boolean folderCreated = false;
	String fileName;
	private static Report report = null;
	String testReportPath = CommonFunctions.getKeyValue("Configuration.properties", "OutputReportPath");
	public String text = "<meta http-equiv=Content-Type content=text/html; charset=iso-8859-1><br><b></b>"
			+ "<style>td, th {border: 1px solid black;}"
			+ "table{table-layout: fixed; width: 100%;}"
			+ "</style>"
			+ "<tbody><font size=20 align=center><center>REPORT</center><center>API Automation Report</center></font>";
		
	/**
	 * @return The Method returns a TimeStamp to be used for Report name
	 */
	public String getTimeStamp() {
		Calendar currentDate = Calendar.getInstance();
		SimpleDateFormat timeFormatter = new SimpleDateFormat("HH-mm-ss");
		SimpleDateFormat yearFormatter = new SimpleDateFormat("yyyy");
		SimpleDateFormat monthFormatter = new SimpleDateFormat("MM");
		SimpleDateFormat dayFormatter = new SimpleDateFormat("dd");
		String time = timeFormatter.format(currentDate.getTime());
		String year = yearFormatter.format(currentDate.getTime());
		String month = monthFormatter.format(currentDate.getTime());
		String day = dayFormatter.format(currentDate.getTime());
		String timestamp = day + "-" + month + "-" + year + "_" + time;
		return timestamp;
	}
	
	/**
	 * This method is used for creating report used by report initialization function
	 * 
	 * @param className
	 */
	public void createReport(String className) {
		fileName = className;
		try {
			File file = new File(CommonFunctions.PROJECT_DIR + testReportPath + fileName + ".html");
			raf = new RandomAccessFile(file, "rw");
			if (fileName.equalsIgnoreCase("Index")) {
				raf.writeBytes(text);
			} else {
				raf.writeBytes(text);
			}
		} catch (IOException e) {
			LOG.error("Some error occurred with writing html report file.",	e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method is used to create a new table at the start of the New API
	 * 
	 * @param arrHeader
	 * @throws IOException
	 */
	public void startAPIMethod(String... arrHeader) throws IOException {
		int colLength = arrHeader.length;
		if (colLength > 0) {
			String reportContent2 = new String();
			reportContent2 = "<br><center><b><i>" + arrHeader[0] + "</i></b></center>"
					+ "<table border=1 cellpadding=5 ALIGN=CENTER>"
					+ "<tr bgcolor=#837E7C>";
			int i = 1;
			while (i < arrHeader.length){
				if (arrHeader[i].equals("")) {
					arrHeader[i] = "-";
				}
				if (arrHeader[i].equalsIgnoreCase("Result")) {
					reportContent2 = reportContent2
						+ "<th style=\"width: 6%;\"> <font color=#ffffff>"
						+ arrHeader[i] + "</font></th>";
				} else {
					reportContent2 = reportContent2
						+ "<th style=\"width: 45%;\"><font color=#ffffff>"
						+ arrHeader[i] + "</font></th>";
				}
				raf.writeBytes(reportContent2);
				reportContent2 = "";
				i = i + 1;
			}
			raf.writeBytes("</tr>");
		}
	}

	public void endAPIMethod() throws IOException {
		raf.writeBytes("</table>");
	}
		
	/**
	 * This method is used to create a new table at the start of the New API
	 * 
	 * @param arrHeader
	 * @throws IOException
	 */
	public void writeTitle(String title) throws IOException {
		raf.writeBytes("</br></br></br><font size=5><center><b><u>" + title
				+ "</u></b></center></font></br>");
	}
	
	/**
	 * This Method is used to add new data into the report HTML file
	 * 
	 * @param arrData
	 * @throws IOException
	 */
	public void addDataNew(String... arrData) throws IOException {
		int colLength = arrData.length;
		if (colLength > 0) {
			String reportContent2 = null;
			reportContent2 = "<tr>";
			int i = 0;
			while (i < arrData.length) {
				if (arrData[i].equals("")) {
					arrData[i] = "-";
				}
				if (arrData[i].equalsIgnoreCase("Pass")) {
					reportContent2 = reportContent2
							+ "<td align=center bgcolor=#00CC00>" + "Passed"
							+ "</td>";
				} else if (arrData[i].equalsIgnoreCase("Fail")) {
					reportContent2 = reportContent2
							+ "<td align=center bgcolor=#CC0000>" + "Failed"
							+ "</td>";
				} else if (arrData[i].equalsIgnoreCase("Skipped")) {
					reportContent2 = reportContent2
							+ "<td align=center bgcolor=#CCCC00>" + "Skipped"
							+ "</td>";
				} else {
					arrData[i] = arrData[i].replace("\n", "</br>");
					reportContent2 = reportContent2
							+ "<td style=\"width: 46%;word-wrap: break-word;\">"
							+ arrData[i] + "</td>";
				}
				raf.writeBytes(reportContent2);
				
				reportContent2 = "";
				i = i + 1;
			}
			raf.writeBytes("</tr>");
		}
	}
		
	/**
	 * Close the report (RanDomAcessFile pointer is closed)
	 * 
	 * @throws IOException
	 */
	public void closeReport() throws IOException {
		if (raf != null && rafi != null) {
			raf.close();
			rafi.close();
			LOG.info("Your file has been written");
		}
	}
	
	/**
	 * This function initializes the report (Creating folder and report html file)
	 * 
	 * @param report
	 * @throws IOException
	 */
	public void initializereport() throws IOException {
		createReport("Travelex_API_Execution-" + getTimeStamp());
	}
	
	
	public static Report getInstance(String title) {
		try {
			if (report == null) {
				report = new Report();
				report.initializereport();
			}
			report.writeTitle(title);
			LOG.info("Report instantiated");
		} catch (IOException e) {
			LOG.error(e.getMessage());
		}
		return report;
	}
	
}
