/*package com.travelex.automation.api.duedil;


import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.SSLConfig;
import com.travelex.automation.api.common.CommonFunctions;
import com.travelex.automation.api.common.DataReader;
import com.travelex.automation.api.common.Report;
import com.travelex.automation.api.common.Utility;

public class DuedilScreeningAPI {
	private static final Logger LOG = LoggerFactory.getLogger(DuedilScreeningAPI.class);
	
	private static Report report;
	private final String url = "https://payments.uat.digital.travelex.net/v1/duedil/screening";
	private final String organization = "Europe";
    private final String businessUnit = "1_Europe";
    private final String region = "UK";
    private final String branch = "RTS";
    private final String country = "UK";
    private final String applicationId = "R03";
    private final String source = "RTS";
    private final String sanctionSearchDefinitionId = "CSRTSAPACSD";
    private final String pepSearchDefinitionId = "PEPSD";
    private final boolean generateTicket = true;
    private final boolean enableSuppression = false; 
    private final String contentType ="application/json";
    
    private final ObjectMapper objectMapper = new ObjectMapper();
	
	private static final String configFile = "Configuration.properties";
	
	static {
        KeyStore keyStore = null;
        SSLConfig config = null;
        FileInputStream privateKeyStoreLocation;
        try {
            privateKeyStoreLocation = new FileInputStream(new File(
                            "C:\\Travelex Projects\\FCP\\platform-setup\\rtsofacadapter_uat\\rtsofacadapter-uat-keystore.jks"));
            keyStore = KeyStore.getInstance("jks");
            keyStore.load(privateKeyStoreLocation, "password".toCharArray());
            if (keyStore != null) {

                org.apache.http.conn.ssl.SSLSocketFactory clientAuthFactory =
                                new org.apache.http.conn.ssl.SSLSocketFactory(keyStore, "password");

                // set the config in rest assured
                config = new SSLConfig().with().sslSocketFactory(clientAuthFactory).and()
                                .allowAllHostnames();

                RestAssured.config = RestAssured.config().sslConfig(config);

            }

        } catch (Exception ex) {
            System.out.println("Error while loading keystore >>>>>>>>>");
            ex.printStackTrace();
        }


    }
	
	@BeforeClass
	public void beforeClass(){
		report = Report.getInstance("Duedil Screening Api Testing Report");
		
	}
	
	@Test(dataProvider = "DuedilScreeningApiDataProvider", dataProviderClass = DataReader.class)
	public void duedilScreeningApiTest(String listEntryKey, String fullname, String street, String city, String state,  
			String country, String dob, String postalcode, String hitPresent, String actualName){
		LOG.info("API testing of '"+listEntryKey+"' started");
		try {
			report.startAPIMethod(url , "Result", "Output");
			
			if(street.equalsIgnoreCase("NULL")) {
			    street = "";
			}
			String newDate = dob.split("/")[1]+"/"+dob.split("/")[0]+"/"+dob.split("/")[2];
			
			HashMap<String, String> headerMap = new HashMap<String, String>();
			headerMap.put("Accept", "application/json");
			headerMap.put("X-Auth", "Basic Y3NjLXRlc3RzOmlhNHVWMUVlS2FpdA==");
			
			ObjectNode inputDetails = createInputDetails(fullname, newDate, street, city, state, country, postalcode, "PEP");
			
			//LOG.info("input : "+inputDetails.toString());
			//LOG.info("headers : "+headerMap.toString());
			boolean hit = true;
			if(hitPresent.contains("No Hit")) {
			    hit = false;
			}
			String response = "";//ExecuteApiMethods.executePostMethodForDuedilScreening(headerMap, contentType, inputDetails.toString(), url, report, hit, fullname);
			//response = response.replace('"',' ');
			response = response.replaceAll("'", "");
			//System.out.println( "new response : "+response);
			Utility.updateDataInExcel(CommonFunctions.getKeyValue(configFile, "DataSheet"), "Actual_Result", response, listEntryKey);
			//report.addDataNew("response : "+response); 
			report.endAPIMethod();
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
			
		LOG.info("API testing of Duedil complete");
	}
	
	public ObjectNode createInputDetails(String fullname, String birthDate, String street, 
                    String city, String state, String country, String postalcode, String screeningType) {
       
        ObjectNode sourceDetails = createSourceDetails();
        
        ObjectNode inputDetails = objectMapper.createObjectNode();
        
        ObjectNode searchAttributes = createSearchAttributes(screeningType);
        ObjectNode partyDetails = createPartyDetails(fullname, birthDate, street, city, state, country, postalcode);
        
        inputDetails.set("sourceDetail", sourceDetails);
        inputDetails.set("searchAttributes", searchAttributes);
        inputDetails.set("partyDetail", partyDetails);

        return inputDetails;
    }
	
	*//**
     * Populates a JSON object with source application details for RTS.
     * 
     * @return - body with required details(org, BU, region, branch, country, appId)
     *//*
    public ObjectNode createSourceDetails() {
        ObjectNode sourceDetails = objectMapper.createObjectNode();
        sourceDetails.put("organization", organization);
        sourceDetails.put("businessUnit", businessUnit);
        sourceDetails.put("region", region);
        sourceDetails.put("branch", branch);
        sourceDetails.put("country", country);
        sourceDetails.put("applicationId", applicationId);
        sourceDetails.put("source", source);
        sourceDetails.put("orderId", "");
        
        return sourceDetails;
    }
    
    *//**
     * Populates a JSON object with search attributes details.
     * 
     * @param screeningType - The type of screening(SANCTION/PEP)
     * @return - body with required details(searchDefinition, screeningType, generateTicket etc)
     *//*
    public ObjectNode createSearchAttributes(String screeningType) {
        ObjectNode searchAttributes = objectMapper.createObjectNode();
        if(screeningType.toUpperCase().equals("SANCTION")) {
            searchAttributes.put("searchDefinition", sanctionSearchDefinitionId);
        }else {
            searchAttributes.put("searchDefinition", pepSearchDefinitionId);
        }
        
        searchAttributes.put("screeningType", screeningType.toUpperCase());
        searchAttributes.put("generateTicket", generateTicket);
        searchAttributes.put("enableSuppression", enableSuppression);
        searchAttributes.put("applyMisspellingRule", true);
        searchAttributes.put("mustMatchFields", "");

        return searchAttributes;
    }
    
    private ObjectNode createPartyDetails(String fullName, String birthDate, String street, 
                    String city, String state, String country, String postalcode) {
        ObjectNode address = objectMapper.createObjectNode().put("address1", "")
                        .put("address2", "").put("street", street)
                        .put("city", city).put("state", state)
                        .put("country", country).put("postalCode", postalcode);

        ObjectNode identificationDetails = objectMapper.createObjectNode().put("issueCountry", "")
                        .put("type", "").put("value", "");

        ObjectNode partyDetails = objectMapper.createObjectNode().put("partyId", "123")
                        .put("partyType", "INDIVIDUAL").put("fullName", fullName)
                        .put("firstName", "").put("middleName", "").put("lastName", "")
                        .put("dateOfBirth", birthDate);

        partyDetails.set("address", address);

        ArrayNode identificationArray = partyDetails.putArray("identificationDetails");

        identificationArray.add(identificationDetails);

        return partyDetails;
    }


}
*/