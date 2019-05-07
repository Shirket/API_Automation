package com.travelex.automation.api.duedil;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.travelex.automation.api.common.CommonFunctions;
import com.travelex.automation.api.common.DataReader;
import com.travelex.automation.api.common.Report;
import com.travelex.automation.api.methods.ExecuteApiMethods;
public class RTSScreeningAPI {
	private static final Logger LOG = LoggerFactory.getLogger(RTSScreeningAPI.class);

	private static Report report;
	//uat = http://gb-pb-rtc-vt2/RTSAdapter/SanctionCheck.asmx;
	//local = http://10.236.22.225/RTSAdapter/SanctionCheck.asmx
	private final String url = "http://gb-pb-rtc-vt2/RTSAdapter/SanctionCheck.asmx";
	private final String contentType ="text/xml; charset=UTF-8;";
	private static final String configFile = "Configuration.properties";

	@BeforeClass
	public void beforeClass(){
		report = Report.getInstance("RTS Adaptor Screening Api Testing Report");
	}

	@Test(dataProvider = "DuedilScreeningApiDataProvider", dataProviderClass = DataReader.class)
	public void duedilScreeningApiTest(String listEntryKey, String fullname, String street, String city, String state,  
			String country, String dob, String postalcode, String SearchDefinition,String hitPresent, String actulName){
		LOG.info("API testing of '"+listEntryKey+"' started");
		try {
			report.startAPIMethod(url , "Result", "Output");
			report.addDataNew("List Entry Key", listEntryKey);

			// Excel has 'NULL' in the column, replacing it with empty string.
			listEntryKey = checkNull(listEntryKey);
			fullname = checkNull(fullname);
			city = checkNull(city);
			state = checkNull(state);
			country = checkNull(country);
			dob = checkNull(dob);
			postalcode = checkNull(postalcode);
			SearchDefinition = checkNull(SearchDefinition);
			hitPresent = checkNull(hitPresent);
			actulName = checkNull(actulName);

			String newDate = dob;
			System.out.println(" newDOB = =="+newDate);
			HashMap<String, String> headerMap = new HashMap<String, String>();
			headerMap.put("Accept", "application/xml");

			Document doc = null;
			try {
				doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
						RTSScreeningAPI.class.getClassLoader().getResource(
								CommonFunctions.getKeyValue(configFile, "RTSRequest")).getFile());
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (FactoryConfigurationError e) {
				e.printStackTrace();
			}
			
			Element root = doc.getDocumentElement();  

			Node node = root.getElementsByTagName("FirstName").item(0); 
			node.setTextContent(fullname);
			node = root.getElementsByTagName("Street").item(0); 
			node.setTextContent(street);
			node = root.getElementsByTagName("City").item(0); 
			node.setTextContent(city);
			node = root.getElementsByTagName("Country").item(0); 
			node.setTextContent(country);
			node = root.getElementsByTagName("DateOfBirth").item(0); 
			node.setTextContent(newDate);
			node = root.getElementsByTagName("SanctionRule").item(0);
			node.setTextContent(SearchDefinition);

			DOMSource domSource = new DOMSource(doc);
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = null;
			try {
				transformer = tf.newTransformer();
			} catch (TransformerConfigurationException e) {
				e.printStackTrace();
			}
			try {
				transformer.transform(domSource, result);
			} catch (TransformerException e) {
				e.printStackTrace();
			}
			String request = writer.toString();

			LOG.info("input : "+request);
			LOG.info("headers : "+headerMap.toString());
			boolean hit = true;
			
			if (hitPresent.toLowerCase().contains("no hit")) {
				hit = false;
			}
			
			String sheet = CommonFunctions.getKeyValue(configFile, "DataSheet");
			ExecuteApiMethods.executePostMethodForRTSAdapter(sheet, listEntryKey, headerMap, contentType, request, url, report, hit, actulName);

			report.endAPIMethod();
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		LOG.info("API testing of Duedil complete");
	}
	
	public String checkNull(String value) {
		if ("NULL".equalsIgnoreCase(value) || value == null) {
			value = "";
		}
		return value;
	}
}
