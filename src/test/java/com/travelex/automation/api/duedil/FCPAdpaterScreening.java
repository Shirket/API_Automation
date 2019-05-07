package com.travelex.automation.api.duedil;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
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
import com.travelex.automation.api.methods.ExecuteApiMethods_FCP;

public class FCPAdpaterScreening {

	private static final Logger LOG = LoggerFactory.getLogger(RTSScreeningAPI.class);
	private static Report report;
	private String url, username, password, org_code;
	private final String contentType = "text/xml; charset=UTF-8;";
	private static final String configFile = "Configuration.properties";

	@BeforeClass
	public void beforeClass() {
		report = Report.getInstance("RTS Adaptor Screening Api Testing Report");
		// Getting credentials from properties file.
		url = CommonFunctions.getKeyValue(configFile, "ADAPTER_URL");
		username = CommonFunctions.getKeyValue(configFile, "USERNAME");
		password = CommonFunctions.getKeyValue(configFile, "PASSWORD");
		org_code = CommonFunctions.getKeyValue(configFile, "ORIG_CODE");
	}

	@Test(invocationCount = 1, dataProvider = "DuedilScreeningApiDataProvider", dataProviderClass = DataReader.class)
	public void duedilScreeningApiTest(String listEntryKey, String fullname, String street, String city, String state,  
			String country, String dob, String postalcode, String SearchDefinition,String hitPresent, String actulName) {
		LOG.info("API testing of '" + listEntryKey + "' started");
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
			HashMap<String, String> headerMap = new HashMap<String, String>();
			headerMap.put("Accept", "application/xml");

			// converting the CDATA template xml to document for data manipulation
			Document xmlInputDoc = null;
			try {
				xmlInputDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
						FCPAdpaterScreening.class.getClassLoader().getResource(
								CommonFunctions.getKeyValue(configFile, "RequestXml")).getFile());
			} catch (SAXException | ParserConfigurationException | IOException | FactoryConfigurationError e) {
				e.printStackTrace();
			}

			Element xmlInputRoot = xmlInputDoc.getDocumentElement();

			Node xmlInputNode = xmlInputRoot.getElementsByTagName("ORGANIZATION").item(0);
			xmlInputNode.setTextContent("Travelex");

			xmlInputNode = xmlInputRoot.getElementsByTagName("FULLNAME").item(0);
			xmlInputNode.setTextContent(fullname);

			xmlInputNode = xmlInputRoot.getElementsByTagName("STREET").item(0);
			xmlInputNode.setTextContent(street);

			xmlInputNode = xmlInputRoot.getElementsByTagName("STATE").item(0);
			xmlInputNode.setTextContent(street);

			xmlInputNode = xmlInputRoot.getElementsByTagName("CITY").item(0);
			xmlInputNode.setTextContent(city);

			xmlInputNode = xmlInputRoot.getElementsByTagName("COUNTRY").item(0);
			xmlInputNode.setTextContent(country);

			xmlInputNode = xmlInputRoot.getElementsByTagName("DATEOFBIRTH").item(0);
			xmlInputNode.setTextContent(newDate);

			xmlInputNode = xmlInputRoot.getElementsByTagName("FILEIMG").item(0);
			xmlInputNode.setTextContent(SearchDefinition);

			DOMSource domSource = new DOMSource(xmlInputDoc);
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();

			try {
				Transformer transformer = tf.newTransformer();
				transformer.transform(domSource, result);
			} catch (TransformerException e) {
				e.printStackTrace();
			}

			// after transforming, converting to string
			String request = writer.toString();
			// after conversion from document to string, remove the xml header.
			request = request.replace("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>", "");
			// append the inner XML in the CDATA section
			String finalInput = "<s:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ofac=\"http://www.primeassociates.com/ComplianceManager/OFACReporter/\">   <s:Header/>   <s:Body>      <getSearchResults>         <username>" + username + "</username>         <password>" + password + "</password>         <orgcode>" + org_code + "</orgcode>         <xmlinput>            <![CDATA[" + request + "]]>         </xmlinput>      </getSearchResults>   </s:Body> </s:Envelope>";

			LOG.info("******** REQUEST ***********:\n" + finalInput);

			boolean hit = true;

			if (hitPresent.toLowerCase().contains("no hit")) {
				hit = false;
			}
			String sheet = CommonFunctions.getKeyValue(configFile, "DataSheet");
			ExecuteApiMethods_FCP.executePostMethodForFCPAdapter(sheet, listEntryKey, headerMap, contentType, finalInput, url, report, hit, actulName);

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