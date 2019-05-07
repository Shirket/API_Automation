package com.travelex.automation.api.common;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.XMLUnit;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.codoid.products.exception.FilloException;
import com.codoid.products.fillo.Connection;
import com.codoid.products.fillo.Fillo;


public class Utility {
    
    static Connection connection = null;
	
	public static HashMap<String, String> convertStringToHashMap(String input){
		HashMap<String, String> inputMap = new HashMap<String, String>();
		String[] inputArray = input.split("\\|");
		for(int i = 0; i < inputArray.length; i++){
			String[] inputKeyValue = inputArray[i].split("\\:");
			inputMap.put(inputKeyValue[0], inputKeyValue[1]);
		}
		return inputMap;
	}
	
	public static boolean compareXML(String expectedXML, String actualXML, Report report )throws Exception{
		boolean result = true;
		XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setIgnoreAttributeOrder(true);
        
        DetailedDiff diff = new DetailedDiff(XMLUnit.compareXML(expectedXML, actualXML));     

        List<?> allDifferences = diff.getAllDifferences();            
      
      
        if(allDifferences.size() > 0){
        	report.addDataNew("Difference in the actual and expected xml is : "+diff.toString());
        	result = false;
        }
       
		return result;
	}
	
	// Method to replace the text between tag of request XML
	public static String ReplaceTextBetweenTagOfXML(String bodyString, String tagName, String workplaceValue){
		String t = null;
		Pattern p = Pattern.compile("<"+tagName+">(.*?)</"+tagName+">",Pattern.DOTALL);
		Matcher m = p.matcher(bodyString);
		String regexPattern = "(?<=<"+tagName+">).+?(?=<\\/"+tagName+">)";
		
		if (m.find()){			     
	         t = bodyString.replaceAll(regexPattern, workplaceValue);
	        System.out.println(t);
	    }
	    else {
	        System.out.println("No matches found");
	    }		
		return t;
	}
	
	// Method to find out the text between tags of response xml
	public static String RetrieveTextBetweenTagOfXML(String bodyString, String tagName){
		String actualValue = null;
		Pattern p = Pattern.compile("<"+tagName+">(.*?)</"+tagName+">",Pattern.DOTALL);
		Matcher m = p.matcher(bodyString);
	
		if (m.find()){
			String tagValue = m.group(0);
			String trimmedValue = tagValue.split(">")[1];
			String tagTrimmedValue = trimmedValue.split("<")[0];
			//actualValue = tagTrimmedValue.substring(1, tagTrimmedValue.length());
			actualValue = tagTrimmedValue;
        }
	    else {
	        System.out.println("No matches found");
	    }		
		return actualValue;
	}
	
	
	/*public static void UpdateDataInExcel()throws IOException{
		String excelFilePath = "";
		InputStream is = new FileInputStream(new File(excelFilePath));
		Workbook workBook = StreamingReader.builder().rowCacheSize(100).bufferSize(4096).open(is);
		
	}*/
  public static Connection getConnection() {
		
	//	ConfigurationProperties configurationProperties = new ConfigurationProperties();
	//	 String fileN = configurationProperties.getProperty(ConfigurationProperties.Test_Data_Folder_Path);
	//	 fileN = System.getProperty("user.dir") + fileN;			
      String configFile = "Configuration.properties";
		Fillo fil = new Fillo();
		if(connection == null) {
		    System.out.println("creating new connection");
    		try{
    		    String file = CommonFunctions.getKeyValue(configFile, "ScreeningFilePath");
    		    /*ClassLoader classLoader = new Utility().getClass().getClassLoader();
                String path1 = URLDecoder.decode(classLoader.getResource(file).getPath().replaceFirst("/", "").replaceAll("/", "\\\\"), "UTF-8");*/
    		  //String path1 = Utility.class.getClassLoader().getResource(file).getFile();
    		 // path1 = path1.substring(1);
    		//  path1.replaceAll("\\", "\\");
    		    String path = "C:\\Travelex Projects\\APIAutomation\\WNP-API\\src\\main\\resources\\"+file;
    			connection = fil.getConnection(path);
    		}catch(FilloException e){
    			e.printStackTrace();
    		}
		}
		return connection;
	}
		
	public static void updateDataInExcel(String sheetName, String colName, String textTobeUpdated, String apiNo) throws FilloException{
		/*if(textTobeUpdated==null || textTobeUpdated=="" || textTobeUpdated.contains("NA")){
			return;
		}*/
		String updateQuery = "Update "+sheetName+" set "+colName+"='"+textTobeUpdated+"' where Line_Number = '"+apiNo+"'";
		getConnection().executeUpdate(updateQuery);
	}
	
	
	public static Document updateXmlValue(Document doc, String tagName, String value) { 
    	
    	Element root = doc.getDocumentElement();  
    	//root.getElementsByTagName(tagName).item(0).getNodeValue();  
    	Node node = root.getElementsByTagName(tagName).item(0); 
    	//NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();   
      //  Node node = nodes.item(0); 
        node.setTextContent(value);
    //	node.setNodeValue(value);
    	//System.out.println("Using getAttribute        date: " + root.getAttribute("date"));
    	
    	return doc;
	}
}
