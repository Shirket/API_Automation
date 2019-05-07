package com.travelex.automation.api.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This Class represents generic methods which can be utilized across projects
 * 
 * @author 
 */
public class CommonFunctions {
	
	private static final Logger LOG = LoggerFactory.getLogger(CommonFunctions.class);

	/**
	 * This Method sets current working directory to static variable PROJECT_DIR
	 */
	public static String PROJECT_DIR ;
	
	static {
		PROJECT_DIR = System.getProperty("user.dir") + "\\";
		LOG.info("------------------ <" + PROJECT_DIR + "> --------------------");
	}
	
	/**
	 * This Method compares the Key in the given two Maps
	 * 
	 * @param map1
	 * @param map2
	 * @return Returns true if match found else returns false
	 */
	public static <V, K> boolean compareKeys(HashMap<K, V> json, HashMap<K, V> excel) {
		boolean x = true;
		for (Entry<K, V> entry : json.entrySet()) {
			if (!excel.containsKey(entry.getKey())) {
				LOG.info(">>>>>>>>>>>>>>>>>" + entry.getKey());
				x = false;
			}
		}
		return x;
	}
	
	/**
	 * This Method takes as input the Config file path and Key and returns the
	 * respective value.
	 * 
	 * @param filename
	 * @param key
	 * @return Value for the given Key and file
	 */
	public static String getKeyValue(String filename, String key) {
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(PROJECT_DIR + filename));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		LOG.info(filename + " is loaded in properties object" + prop);
		LOG.info(prop.getProperty(key));
		return prop.getProperty(key);
	}

	/**
	 * The function is typically used by POST API Methods where ContentType is Json. 
	 * This function replaces the values from the ArrayList of HashMap into the Json
	 * and returns the ArrayList of Json
	 * 
	 * @param arraylist
	 * @param json
	 * @return ArrayList
	 */
	public static ArrayList<String> arraylistFormbodyContentTypeJson(
			ArrayList<HashMap<String, String>> arraylist, String json) {
		ArrayList<String> bodyarray = new ArrayList<String>();
		for (int i = 0; i < arraylist.size(); i++) {
			bodyarray.add(Json.replaceHashmapToJson(arraylist.get(i), json));
		}
		return bodyarray;
		
	}
	
	/**
	 * This Method replaces the value of the HashMap in the ArrayList if the
	 * input HashMap keys matches any of the value This is used to parameterize
	 * the data
	 * 
	 * @param arraylist
	 * @param map
	 * @return
	 */
	public static ArrayList<HashMap<String, String>> replaceListValfromHashMapKeys(
		ArrayList<HashMap<String, String>> arraylist,
		HashMap<String, String> map) {
		for (int i = 0; i < arraylist.size(); i++) {
			for (Map.Entry<String, String> entry : arraylist.get(i).entrySet()) {
				for (Map.Entry<String, String> entry2 : map.entrySet()) {
					if (entry.getValue().contains(entry2.getKey())) {
						entry.setValue(entry.getValue().replace(entry2.getKey(), entry2.getValue()));
					}
				}
			}
		}
		return arraylist;
	}
	
	/**
	 * This method take ArrayList of HashMap and URL as Input and replaces the
	 * URL variable 
	 * 
	 * @param arraylisth
	 * @param url
	 * @return Returns the ArrayList of Formed URLs
	 */
	public static ArrayList<String> formURLArrayList(
			ArrayList<HashMap<String, String>> arraylisth, String url) {
		ArrayList<String> list = new ArrayList<String>();
		String temp = new String();
		for (int i = 0; i < arraylisth.size(); i++) {
			String tempurl = url;
			for (Map.Entry<String, String> entry : arraylisth.get(i).entrySet()) {
				System.out.println(entry);
				if (entry.getValue().equals("<missing>")) {
					temp = "{" + entry.getKey().trim() + "}";
					tempurl = tempurl.replace("/" + temp, "");
					temp = "{" + entry.getKey().trim() + "=}";
					tempurl = tempurl.replace("&" + temp, "");
					temp = "{" + entry.getKey().trim() + "=}";
					if (tempurl.contains("?" + temp)) {
						if (tempurl.contains("?" + temp + "&")) {
							tempurl = tempurl.replace(temp + "&", "");
						} else {
							tempurl = tempurl.replace("?" + temp, "");
						}
					}
				} else {
					temp = "{" + entry.getKey().trim() + "}";
					tempurl = tempurl.replace(temp, entry.getValue().trim());
					temp = "{" + entry.getKey().trim() + "=}";
					tempurl = tempurl.replace(temp, entry.getKey().trim() + "="
						+ entry.getValue().trim());
				}
			}
			list.add(tempurl);
		}
		return list;
	}

	/**
	 * Compare two json strings
	 * 
	 * @param jsonString1
	 * @param jsonString2
	 */
	public static boolean compareJson(String jsonString1, String jsonString2) {
		boolean result = false; 
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode tree1 = mapper.readTree(jsonString1);
			JsonNode tree2 = mapper.readTree(jsonString2);
			if (tree1.equals(tree2)) {
				LOG.info("yes, contents are equal");
				result = true;
			} else {
				LOG.info("not equal");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;

	}

	public static String readFile(String filename) {
		String content = null;
		File file = new File(filename); 
		try {
			FileReader reader = new FileReader(file);
			char[] chars = new char[(int) file.length()];
			reader.read(chars);
			content = new String(chars);
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}
	
}
