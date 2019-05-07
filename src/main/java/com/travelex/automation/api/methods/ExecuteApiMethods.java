package com.travelex.automation.api.methods;

import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.jayway.restassured.response.Response;
import com.travelex.automation.api.common.Report;

/**
 * This class represents the Methods pertaining to execution of API Methods  
 * @author 
 */
public class ExecuteApiMethods extends APIMethods{
	
	private static final Logger LOG = LoggerFactory.getLogger(APIMethods.class);
	
	public static void executePostMethodForRTSAdapter(String sheetName, String listEntryKey, HashMap<String, String> headers, String contentType,
	                String body, String url, Report report, boolean hit, String expectedResponseString) {
	    @SuppressWarnings("unused")
		String res = null;
        
        try {
            Response response = POST(headers, contentType, body, url);
            System.out.println("response : "+response.getBody().asString());
            res = response.prettyPrint();
            //report.addDataNew("Input ", body);
            
            report.addDataNew("Request ", body);
            report.addDataNew("Response ", response.getBody().asString());
           // Utility.updateDataInExcel(sheetName, "Actual_Result", res, listEntryKey);
            Document doc = null;
            try {
                doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(response.asInputStream());
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
            Node node = root.getElementsByTagName("Outcome").item(0); 
            String matches = node.getTextContent();
            
          
            if (hit) {
             
            	
                if(!(matches.equals("NoMatches"))) {
                    if(response.prettyPrint().contains(expectedResponseString)){
                        report.addDataNew(  "PASS", expectedResponseString + " found in the hit response object");
                        LOG.info("PASS", expectedResponseString + " found in the hit response object");
                    }else {
                        report.addDataNew(  "FAIL", expectedResponseString + " not found in the hit response object");
                        LOG.info("FAIL", expectedResponseString + " not found in the hit response object");
                        assertTrue(false,expectedResponseString + " not found in the hit response object");
                    }
                }else {
                    report.addDataNew("FAIL", "Hit was expected but NO HIT FOUND");
                    assertTrue(false, "Hit was expected but NO HIT FOUND");
                }
            } else {
             //   expectedHttpStatus = HttpStatus.SC_OK;
                if(matches.equals("NoMatches")) {
                    report.addDataNew(  "PASS", "There is no hit for this customer");
                    LOG.info("PASS", "There is no hit for this customer");
                }else {
                    report.addDataNew("FAIL", "No Hit was expected but HIT FOUND");
                    assertTrue(false, "No Hit was expected but HIT FOUND");
                }
            }
        } catch (Exception e) {
            try {
                report.addDataNew("FAIL", "Error Occurred : "+e.getMessage());
                
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
            LOG.error(e.getMessage());
            LOG.error("Not able to process POST method");
        }
       
	}
	
	
	/**
     * The method is used for iterative execution of POST API Methods.
     * The Method also calls the for report writing of the execution result.
     * 
     * @param arraylist
     * @param contentType
     * @param arraybody
     * @param url
     * @param report
     */
    /*public static String executePostMethodForDuedilScreening(HashMap<String, String> headers, String contentType,
            String body, String url, Report report, boolean hit, String expectedResponseString) {
        String res = null;
      //  int expectedHttpStatus;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            final Response response = POST(headers, contentType, body, url);
            res = response.prettyPrint();
            report.addDataNew("Input ", body);
            report.addDataNew("Response ", response.getBody().asString());
            if (hit) {
            //    expectedHttpStatus = HttpStatus.SC_ACCEPTED;
                if(Integer.parseInt(
                                objectMapper.readTree(response.getBody().asString()).get("matchCount").asText()
                                ) > 0) {
                    if(response.prettyPrint().contains(expectedResponseString)){
                        report.addDataNew(  "PASS", expectedResponseString + " found in the hit response object");
                        LOG.info("PASS", expectedResponseString + " found in the hit response object");
                    }else {
                        report.addDataNew(  "FAIL", expectedResponseString + " not found in the hit response object");
                        LOG.info("FAIL", expectedResponseString + " not found in the hit response object");
                    }
                }
            } else {
            //    expectedHttpStatus = HttpStatus.SC_OK;
                if(Integer.parseInt(
                                objectMapper.readTree(response.getBody().asString()).get("matchCount").asText()
                                ) == 0) {
                    report.addDataNew(  "PASS", "There is no hit for this customer");
                    LOG.info("PASS", "There is no hit for this customer");
                }
            }
        } catch (Exception e) {
            try {
                report.addDataNew("FAIL", "Error Occurred : "+e.getMessage());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            LOG.error(e.getMessage());
            LOG.error("Not able to execute POST method");
        }
        return res;
    }*/
    
    /*public static String executePostMethodForActimizeWLF(HashMap<String, String> headers, String contentType,
                    String body, String url, Report report, boolean hit, String expectedResponseString) {
                String res = null;
              //  int expectedHttpStatus;
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    final Response response = POST(headers, contentType, body, url);
                    res = response.prettyPrint();
                    report.addDataNew("Input ", body);
                    report.addDataNew("Response ", response.getBody().asString());
                    
                    int returnCode = Integer.parseInt(
                                    objectMapper.readTree(response.getBody().asString()).get("matchCount").asText());
                    String message = objectMapper.readTree(response.getBody().asString()).get("message").asText();
                    if(returnCode == 0) {
                    
                        if (hit) {
                           if(Boolean.parseBoolean(objectMapper.readTree(response.getBody().asString()).get("hasHits").asText())) {
                                if(response.prettyPrint().contains(expectedResponseString)){
                                    report.addDataNew(  "PASS", expectedResponseString + " found in the hit response object");
                                    LOG.info("PASS", expectedResponseString + " found in the hit response object");
                                }else {
                                    report.addDataNew(  "FAIL", expectedResponseString + " not found in the hit response object");
                                    LOG.info("FAIL", expectedResponseString + " not found in the hit response object");
                                }
                            }else {
                                report.addDataNew(  "FAIL", "HIT was expected but NO HIT found");
                                LOG.info("FAIL", "HIT was expected but NO HIT found");
                            }
                        } else {
                        //    expectedHttpStatus = HttpStatus.SC_OK;
                            if(!Boolean.parseBoolean(objectMapper.readTree(response.getBody().asString()).get("hasHits").asText())) {
                                report.addDataNew(  "PASS", "There is no hit for this customer");
                                LOG.info("PASS", "There is no hit for this customer");
                            }else {
                                report.addDataNew(  "FAIL", "NO HIT was expected but HIT found");
                                LOG.info("FAIL", "NO HIT was expected but HIT found");
                            }
                        }
                    }
                    else {
                        report.addDataNew(  "FAIL", "Actimize returned error code : " +  returnCode + " -- with message : " + message);
                        LOG.info("FAIL", "Actimize returned error code : " +  returnCode + " -- with message : " + message);
                    }
                } catch (Exception e) {
                    try {
                        report.addDataNew("FAIL", "Error Occurred : "+e.getMessage());
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    LOG.error(e.getMessage());
                    LOG.error("Not able to execute POST method");
                }
                return res;
            }*/
	
}

