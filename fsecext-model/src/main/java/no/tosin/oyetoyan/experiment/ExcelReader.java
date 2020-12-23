/**
JiraSecPlugin 
Copyright 2016 Tosin Daniel Oyetoyan

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/


package no.tosin.oyetoyan.experiment;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * @author tosindo
 *
 */
public final class ExcelReader {
	
	//private static final Logger log = LoggerFactory.getLogger(ExcelReader.class);
		private List<Vector<String>> data;
		private List<String> dataAsCSV;
		/**
		 * 
		 */
		public ExcelReader(String filename, String sep) {
			data = new ArrayList<Vector<String>>();
			dataAsCSV = new ArrayList<String>();
			readExcel(filename, sep);
			readExcelAsCSV(filename, sep);
		}
		
		private void readExcel(String filename, String sep){
			 
	        try(FileInputStream fiStream = new FileInputStream(filename);)
	        
	        { 
	        	XSSFWorkbook xssfWorkbook = new XSSFWorkbook(fiStream);
	            XSSFSheet xssSheet = xssfWorkbook.getSheetAt(0);
	            Iterator<Row> rowIt = xssSheet.rowIterator(); 
	 
	            while(rowIt.hasNext()){
	                XSSFRow xssfRow = (XSSFRow) rowIt.next();
	                Iterator<Cell> cellIterator = xssfRow.cellIterator();
	                Vector<String> cellDataVector = new Vector<String>();
	                while(cellIterator.hasNext()){
	                    XSSFCell xssfCell = (XSSFCell) cellIterator.next();
	                    String cellData = null;
	                    try{
	                    	cellData = xssfCell.getStringCellValue().replaceAll(sep, " ");
	                    }catch(IllegalStateException i){

	                    	cellData = String.valueOf(xssfCell.getRawValue()).replaceAll(sep, " ");
	                    }
	                    cellDataVector.addElement(cellData);
	                }
	                data.add(cellDataVector);
	            }
	            
	            xssfWorkbook.close();
	        } catch (IOException e) {
	        	e.printStackTrace();;
	        } 
	    }
		
		private void readExcelAsCSV(String filename, String sep){
			
			//FileInputStream fiStream = null;
			 
	        try(
	        		FileInputStream fiStream = new FileInputStream(filename);
	        		)
	        
	        { //input is from Main.class) {
	        	XSSFWorkbook xssfWorkbook = new XSSFWorkbook(fiStream);
	            XSSFSheet xssSheet = xssfWorkbook.getSheetAt(0);
	            Iterator<Row> rowIt = xssSheet.rowIterator(); 
	 
	            while(rowIt.hasNext()){
	                XSSFRow xssfRow = (XSSFRow) rowIt.next();
	                Iterator<Cell> cellIterator = xssfRow.cellIterator();
	                StringBuffer cellDataVector = new StringBuffer();
	                while(cellIterator.hasNext()){
	                    XSSFCell xssfCell = (XSSFCell) cellIterator.next();
	                    String cellData = null;
	                    try{
	                    	cellData = xssfCell.getStringCellValue().replaceAll(sep, " ");
	                    }catch(IllegalStateException i){
	                    	//System.out.println(xssfCell.getRawValue());
	                    	cellData = String.valueOf(xssfCell.getRawValue().replaceAll(sep, " "));
	                    }
	                    cellDataVector.append(cellData+sep);
	                }
	                dataAsCSV.add(cellDataVector.toString());
	            }
	            
	            xssfWorkbook.close();
	        } catch (IOException e) {
	        	e.printStackTrace();;
	        } 
	    }

		public List<Vector<String>> getData() {
			return data;
		}
		
		public List<String> getDataAsCSV(){
			return dataAsCSV;
		}

}
