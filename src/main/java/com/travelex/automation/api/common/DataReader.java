package com.travelex.automation.api.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.DataProvider;


public class DataReader {
	
	private static final Logger logger = LoggerFactory.getLogger(DataReader.class);
	private static final String configFile = "Configuration.properties";
	
	@DataProvider(name="DuedilScreeningApiDataProvider")
    public static Object[][] readduedilScreeningApiData(){
        logger.info("reading the data");
        String file = CommonFunctions.getKeyValue(configFile, "ScreeningFilePath");
        logger.info("File : "+file);
        String sheet = CommonFunctions.getKeyValue(configFile, "DataSheet");
        logger.info("sheet : "+sheet);
        Object[][] apiData = null;
        try{
            if(!file.isEmpty() && !sheet.isEmpty()){
                apiData = readTestData(file, sheet);
            }else{
                logger.info("File : "+file);
                logger.info("sheet : "+sheet);
            }
        }catch(Exception e){
            logger.error("Error occurred while reading the api excel : " + file );
            e.printStackTrace();
        }
        
        return apiData;
    }
	
	/**
     * Fetch the test data for a test case based on test case ID
     * 
     * @param workBook
     *            name
     * @param sheetName
     *            name
     * @return testData data
     * @throws IOException
     */
    public static String[][] readTestData(String workBook, String sheetName) throws IOException {
            
        // Establish connection to work sheet
        XSSFWorkbook wb = new XSSFWorkbook(DataReader.class.getClassLoader().getResourceAsStream(workBook));
        XSSFSheet sheet = wb.getSheet(sheetName);
        
        Hashtable<String, Integer> excelrRowColumnCount = new Hashtable<String, Integer>();
        excelrRowColumnCount = findRowColumnCount(sheet, excelrRowColumnCount);
        int rows  = excelrRowColumnCount.get("RowCount");
       int columns=11;//Changed as per 11columns required in excel file
        //int columns = 10;//excelrRowColumnCount.get("ColumnCount");
        String[][] apiData = new String[rows-1][columns];
                
        for (int r = 1; r < rows; r++) {
            XSSFRow row = sheet.getRow(r);
            if (row != null) {
                for (int c = 0; c < columns; c++) {
                    String temp = convertXSSFCellToString(row.getCell(c));
                    apiData[r-1][c] = temp;
                }
                
            }
        }
        logger.info("api data size = "+apiData.length);
    
        return apiData;
    }
    
    /**
     * findRowColumnCount method to get total no of row and column count in a
     * excel work sheet
     * 
     * @param sheet
     *            name
     * @param rowColumnCount
     *            as Hashtable
     * @return Hashtable (returns row count and column count)
     */

    public static Hashtable<String, Integer> findRowColumnCount(XSSFSheet sheet, Hashtable<String, Integer> rowColumnCount) {

        XSSFRow row = null;
        int rows;
        rows = sheet.getPhysicalNumberOfRows();
        int cols = 0;
        int tmp = 0;
        int counter = 0;
        String temp = null;

        for (int i = 0; i < 10 || i < rows; i++) {
            row = sheet.getRow(i);
            if (row != null) {
                temp = convertXSSFCellToString(row.getCell(0));
                if (!temp.isEmpty()) {
                    counter++;
                }
                tmp = sheet.getRow(i).getPhysicalNumberOfCells();
                if (tmp > cols) {
                    cols = tmp;
                }
            }
        }

        rowColumnCount.put("RowCount", counter);
        rowColumnCount.put("ColumnCount", cols);
        
        logger.info("RowCount : "+counter + ", cols : "+cols);
        
        return rowColumnCount;
    }
    
    /**
     * convertHSSFCellToString method to convert the HSSFCell value to its
     * equivalent string value
     * 
     * @param cell
     *            value
     * @return String cellValue
     */
    public static String convertXSSFCellToString(XSSFCell cell) {
        String cellValue = null;
        if (cell != null) {
            cellValue = cell.toString();
            cellValue = cellValue.trim();
        } else {
            cellValue = "";
        }
        return cellValue;
    }
	
	public static String readDataFile(String file) throws IOException{
		String soapString = null;
		BufferedReader br = null;
		try{
			
			//RTSRequest.xml
			File fileObj = new File(DataReader.class.getClassLoader().getResource(file).getFile());
			br = new BufferedReader(new FileReader(fileObj));
			String line;
			StringBuilder sb = new StringBuilder();
			
	
			while((line=br.readLine())!= null){
			    sb.append(line.trim());
			}
			soapString = sb.toString();
			
		//	StringBuffer sb1 = new StringBuffer();			
		}catch(IOException io){
		    io.printStackTrace();
		}catch(Exception e){
			
			logger.error("Error occurred while reading the " + file );
			e.printStackTrace();
		}
		
		return soapString;
		
	}
	
	
	
	
	
	
	
	
	

}
