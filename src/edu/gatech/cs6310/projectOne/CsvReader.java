package edu.gatech.cs6310.projectOne;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// Sources used: http://www.mkyong.com/java/how-to-read-and-parse-csv-file-in-java/
public class CsvReader {

	// constructor
	public CsvReader() {
		
	}
	
	public Map<Integer, Set<Integer>> readFile(String pathToFile) 
	{
		//Initialize variables for reading and storing data
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		Map<Integer, Set<Integer>> studentData = new HashMap<Integer, Set<Integer>>();
		//ArrayList<Set<Integer>> studData = new ArrayList<Set<Integer>>();
		int studID, courseID;
		
		try{
			br = new BufferedReader(new FileReader(pathToFile));
			// read first line containing headings
			line = br.readLine();
			// continue reading line by line
			while ((line = br.readLine()) != null) 
			{
			    // use comma as separator and split line to an array of Strings
				String[] dataLine = line.split(cvsSplitBy);
				studID = Integer.parseInt(dataLine[0]);
				courseID = Integer.parseInt(dataLine[1]);
				
				// if studID key doesn't exist, map studID to new HashSet
				if(studentData.get(studID)== null)
					studentData.put(studID, new HashSet<Integer>());
				
				// add courseID to HashSet
				studentData.get(studID).add(courseID);
				
			}//while
		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}//finally
		
		return studentData;
		
	}//readFile
	
}//CsvReader
