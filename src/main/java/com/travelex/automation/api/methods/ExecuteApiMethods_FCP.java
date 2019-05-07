package com.travelex.automation.api.methods;

import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.jayway.restassured.response.Response;
import com.travelex.automation.api.common.Report;

/**
 * This class represents the Methods pertaining to execution of API Methods  
 * @author 
 */
public class ExecuteApiMethods_FCP extends APIMethods{

	private static final Logger LOG = LoggerFactory.getLogger(APIMethods.class);

	@SuppressWarnings("deprecation")
	public static void executePostMethodForFCPAdapter(String sheetName, String listEntryKey, HashMap<String, String> headers, String contentType,
			String body, String url, Report report, boolean hit, String expectedResponseString) {
		String res = null;

		try {
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");  
		    Date date = new Date();  
		    String startTime = formatter.format(date);
			
		    Response response = POST(headers, contentType, body, url);
		    
		    date = new Date();  
		    String endTime = formatter.format(date);
		    
		    res = response.prettyPrint();
		    LOG.info("******** RESPONSE ***********:\n" + res);

			res = res.replaceAll("&lt;", "<");
			res = res.replaceAll("&gt;", ">");

			report.addDataNew("REQUEST :\n" + startTime, printInHtml(body));
			report.addDataNew("RESPONSE :\n" + endTime, printInHtml(res));

			//Converting Response Into Document
			Document doc = null;
			try {
				doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(response.asInputStream());
			} catch (SAXException | IOException | ParserConfigurationException | FactoryConfigurationError e) {
				e.printStackTrace();
			}

			//Converting Document into element Node
			Element root = doc.getDocumentElement(); 

			// NodeList nn=root.getChildNodes();
			Node node = root.getElementsByTagName("getSearchResultsResult").item(0); 
			String CDATA = node.getTextContent();

			if (CDATA.contains("&")) {
				CDATA = CDATA.replace("&", "&amp;");
			}

			//Converting String i.e CDATA into Document again
			Document xmldoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(CDATA)));

			//Converting Document to Element and Node format
			Element xmlroot = xmldoc.getDocumentElement();

			//Fetching the TransactionID
			String AlertId = "";
			try {
				Node txnId = xmlroot.getElementsByTagName("n:TRANSACTIONID").item(0);
				AlertId = txnId.getTextContent();
			} catch (Exception ex) {
				
			}

			LOG.info("Transaction ID is :- " + AlertId);
			if (hit) {
				if (AlertId.contains("WLF_NO HIT")) {
					report.addDataNew("Response for Mismatch is ", printInHtml(res));
					report.addDataNew("FAIL", "Hit was expected but NO HIT FOUND");
					assertTrue(false, "Hit was expected but NO HIT FOUND");
				} else {
					if (!AlertId.isEmpty()) {
						//Fetching the Match Contents
						NodeList xmlnode = xmlroot.getElementsByTagName("n:MATCH");

						Node xmlmatchcount = null;
						String ResultContent = "";
						String R1 = "";

						for (int i=0; i<xmlnode.getLength(); i++) {
							xmlmatchcount = xmlnode.item(i);
							Node xmlmatchnode = xmlroot.getElementsByTagName("n:MATCHNAME").item(i);
							R1=xmlmatchnode.getTextContent();
							LOG.info("Match name from response is :- " + R1);

							ResultContent = xmlmatchcount.getTextContent();
							R1 = R1 + ResultContent;
						} 

						report.addDataNew("Response ", printInHtml(R1));

						if (R1.contains(StringEscapeUtils.escapeXml(expectedResponseString))) {
							report.addDataNew(  "PASS", expectedResponseString + " found in the hit response object");
							LOG.info("PASS", expectedResponseString + " found in the hit response object");
							System.out.println("Actual  Name is present in Hit Result");
						} else {
							report.addDataNew(  "FAIL", expectedResponseString + " not found in the hit response object");
							LOG.info("FAIL", expectedResponseString + " not found in the hit response object");
							System.out.println("Actual  Name is Missing from Hit Result");
							assertTrue(false, expectedResponseString + " not found in the hit response object");
						}
					}
				}
			} else {
				if (AlertId.contains("WLF_NO HIT")) {
					report.addDataNew(  "PASS", "There is no hit for this customer");
					LOG.info("PASS", "There is no hit for this customer");
				} else {
					report.addDataNew("Response ", printInHtml(res));
					report.addDataNew("FAIL", "No Hit was expected but HIT FOUND");
					assertTrue(false, "No Hit was expected but HIT FOUND");
				}
			}
		} catch (Exception e) {
			try {
				report.addDataNew("FAIL", "Error Occurred : " + e.getMessage());

			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
			LOG.error(e.getMessage());
			LOG.error("Not able to process POST method");
		}
	}

	public static String printInHtml(String value) {
		return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
	}
}

