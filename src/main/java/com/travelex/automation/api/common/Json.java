package com.travelex.automation.api.common;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents the methods required for operation specific to json
 * 
 * @author 
 */
public class Json {
	private static final Logger LOG = LoggerFactory.getLogger(Json.class);
	static String keyr = new String();
	static String value = new String();
	static String replacedjson = new String();
	static Iterator<Entry<String, String>> iterator;
	static byte[] encoded;

	public static void setEncoded(byte[] encoded) {
		Json.encoded = encoded;
	}

	public static void setIterator(Iterator<Entry<String, String>> iterator) {
		Json.iterator = iterator;
	}

	public static void setReplacedjson(String replacedjson) {
		Json.replacedjson = replacedjson;
	}

	/**
	 * This method takes a Json string as an input and returns a Hashmap. (STUB)
	 * This method is specific to output json we get of GET API
	 * 
	 * @param json
	 * @return
	 */
	public static HashMap<String, String> jsonToHashMap(String json) {
		String jsontohreplace = new String();
		jsontohreplace = json;
		HashMap<String, String> jsontomap = new HashMap<String, String>();
		for (int counter = jsontohreplace.indexOf(":"); counter != -1; counter = jsontohreplace.indexOf(":")) {
			if (jsontohreplace.charAt(counter + 1) != '['
					&& jsontohreplace.charAt(counter + 1) != '{'
					&& jsontohreplace.charAt(counter - 1) == '\"') {
				for (int i = 0; jsontohreplace.charAt(counter + 1 + i) != ','
						&& jsontohreplace.charAt(counter + 1 + i) != '}'
						&& jsontohreplace.charAt(counter + 1 + i) != ']'; i++) {
					if (jsontohreplace.charAt(counter + 1 + i) != '\"')
						value = value + jsontohreplace.charAt(counter + 1 + i);
				}
				for (int i = 2; jsontohreplace.charAt(counter - i) != '\"'; i++) {
					keyr = keyr + jsontohreplace.charAt(counter - i);
				}
				String key = new StringBuffer(keyr).reverse().toString();
				jsontomap.put(key, value);
				keyr = "";
				key = "";
				value = "";
			}
			jsontohreplace = jsontohreplace.substring(counter + 1);
		}
		if (jsontomap.isEmpty()) {
			LOG.info("The given json is either empty or of invalid format"); 
		}
		return jsontomap;
	}
	
	/***
	 * @param path
	 *            - Takes as input the file path
	 * @param encoding
	 * @return return a string out of the given file by removing "enter" and
	 *         extra spaces
	 * @throws IOException
	 */
	public static String readFile(String path, Charset encoding) {
		try {
			setEncoded(Files.readAllBytes(Paths.get(path)));
		} catch (IOException ioe) {
			LOG.info("The file does not exist at the given path " + path);
		}
		return new String(encoded, encoding).replaceAll("(\\r|\\n)", "")
				.replaceAll(": \"", ":\"").trim();
	}
	
	/***
	 * This function takes as an input a Hashmap containing data from Excel and
	 * input json Then it replaces the value from Hashmap to json then it
	 * returns the replaced json assumption there is no extra space near : or "
	 */
	public static String replaceHashmapToJson(HashMap<String, String> inputExcelH, String json) {
		setReplacedjson(json.replaceAll("(\\r|\\n)", "").replaceAll(": \"", ":\"").trim());
		setIterator(inputExcelH.entrySet().iterator());
		while (iterator.hasNext()) {
			Entry<String, String> pairs = iterator.next();
			if (replacedjson.indexOf(pairs.getKey() + "\":\"\"") != -1) {
				replacedjson = replacedjson.replace(pairs.getKey() + "\":\"\"",
					pairs.getKey() + "\":\"" + pairs.getValue() + "\"");
			} else if (replacedjson.indexOf(pairs.getKey() + "\":[\"\"]") != -1) {
				replacedjson = replacedjson.replace(pairs.getKey() + "\":[\"\"]",
					pairs.getKey() + "\":[\"" + pairs.getValue() + "\"]");
			} else if (replacedjson.indexOf(pairs.getKey() + "\":,") != -1) {
				replacedjson = replacedjson.replace(pairs.getKey() + "\":,",
					pairs.getKey() + "\":" + pairs.getValue() + ",");
			} else if (replacedjson.indexOf(pairs.getKey() + "\":}") != -1) {
				replacedjson = replacedjson.replace(pairs.getKey() + "\":}",
					pairs.getKey() + "\":" + pairs.getValue() + "}");
			} else {
				System.out.println("Attribute " + pairs.getKey() + " is missing from json");
			}
		}
		return replacedjson;
	}
		
	/**
	 * @param arraylist
	 * @return
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static ArrayList<String> arraylistFormbodyContentTypeJson(
			ArrayList<HashMap<String, String>> arraylist, String jsonName)
			throws IOException {
		ArrayList<String> bodyarray = new ArrayList<String>();
		try {
			for (int i = 0; i < arraylist.size(); i++) {
				
			}
		} catch (Exception e) {
			System.out.println("Exception : " + e.getMessage());
		}
		return bodyarray;
	}

}