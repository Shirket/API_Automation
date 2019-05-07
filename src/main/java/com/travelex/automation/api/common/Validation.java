package com.travelex.automation.api.common;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.response.Response;

/**
 * This class represents the methods pertaining to Validation of different API
 * methods type
 * 
 * @author
 */
public class Validation {
	
	/**
	 * Validates the Responses of POST API method with the Expected Result
	 * 
	 * @param resp
	 *            - Response of the API method
	 * @param map
	 *            - Map
	 * @return True if validation passes, false if it fails
	 */
	public static <K, V> boolean validateResponsePOST(Response resp, Map<K, V> expectedop) {
		Boolean flag = false;
		int status = Integer.parseInt((String) (expectedop.get("StatusCode")));
		if (status == 200 || status == 401 || status == 404) {
			flag = validateResponseAsHashMap(resp, expectedop);
		}
		
		return flag;
	}
	
	
	/**
	 * Validates the Responses of GET API method with the Expected Result
	 * 
	 * @param querymap
	 * @param outputjsonmap
	 * @return Output String
	 */
	public static String validateResponseGET(HashMap<String, String> querymap,
			HashMap<String, String> outputjsonmap) {
		String output = null;
		String incorrectkeys = null;
		String incorrectkeyvaluepair = null;
	
		Iterator<Map.Entry<String, String>> it = outputjsonmap.entrySet().iterator();
	
		while (it.hasNext()) {
			Map.Entry<String, String> pairs = (Map.Entry<String, String>) it.next();
			if (querymap.containsKey(pairs.getKey())) {
				if (!(querymap.get(pairs.getKey())).equals(pairs.getKey())) {
					incorrectkeyvaluepair = incorrectkeyvaluepair
						+ pairs.getKey() + "=" + pairs.getValue() + "&"
							+ pairs.getKey() + "="	+ querymap.get(pairs.getKey()) + ", ";
				}
			} else {
				incorrectkeys = incorrectkeys + pairs.getKey() + ", ";
			}
		}
		
		if (incorrectkeys != null) {
			output = "The attributes could not be matched with the Database output "
				+ incorrectkeys + "-----";
		}
		
		if (incorrectkeyvaluepair != null) {
			output = output + "The following key value pair are mismatched"	+ incorrectkeyvaluepair;
		}
		return output;
	}
	
	/**
	 * Validates the Responses of PUT API method with the Expected Result It
	 * converts the expected output and actual output into HashMaps and compare
	 * those two HashMaps
	 * 
	 * @param resp
	 * @param map
	 * @return True if validation passes, false if it fails
	 */
	public static <K, V> boolean validateResponsePUT(Response resp,	Map<K, V> expectedop) {
		return validateResponseAsHashMap(resp, expectedop);
	}
		
	/**
	 * Validates the Responses of DELETE API method with the Expected Result It
	 * converts the expected output and actual output into HashMaps and compare
	 * those two HashMaps
	 * 
	 * @param resp
	 * @param map
	 * @return True if validation passes, false if it fails
	 */
	public static <K, V> boolean validateResponseDELETE(Response resp,	Map<K, V> expectedop) {
		return validateResponseAsHashMap(resp, expectedop);
	}
		
	/**
	 * This function first converts the two input Jason into HashMap and then
	 * compare those two
	 * 
	 * @param resp
	 * @param expectedop
	 * @return If HashMaps matched then returns true else returns false
	 */
	public static <K, V> boolean validateResponseAsHashMap(Response resp, Map<K, V> expectedop) {
		boolean flag = false;
		HashMap<String, String> responsemap = Json.jsonToHashMap(resp.body().asString());
		HashMap<String, String> expectedopmap = Json.jsonToHashMap(expectedop
				.get("Expected OUTPUT").toString());
		if (expectedopmap.equals(responsemap)) {
			flag = true;
		}
		return flag;
	}
	
	public static boolean bodyMsgContntValidation(Response rsp,	String expectedmsg) {
		return rsp.getBody().asString().contains(expectedmsg);
	}

	public static boolean compareJson(String actual, String expected)
			throws JsonProcessingException, IOException {
		boolean result = true;
		ObjectMapper mapper = new ObjectMapper();
		JsonNode tree1 = mapper.readTree(actual);
		JsonNode tree2 = mapper.readTree(expected);
		if (!tree1.equals(tree2)) {
			result = false;
		}
		return result;
	}
		
	/**
	 * 
	 * @param type
	 * @param query
	 * @param inputJson
	 * @return
	 * @throws Exception
	 */
	public static boolean compareExpectedJson(String type, String query,
			String inputJson) throws Exception {
		boolean result = true;
		return result;
	}

}
