package edu.gatech.cs6310.projectOne;

public class Semester {
	private int semester_ID;
	private String season;
	
	public Semester(int semester_ID) {
		this.semester_ID = semester_ID;
		season = calcSeason(semester_ID);
	}
	

	/**Calculate Season
	 * @param int  semester_ID
	 * @return the season
	 */
	private String calcSeason(int semID){
		String season = "";
		
		//seen switch here: https://piazza.com/class/ij4blvpmdri3ou?cid=206
		switch(semID % 3)
		{
			case 0: return "Summer";
			case 1: return "Fall";
			case 2: return "Spring";
		}
		
		return season;
	}//calcSeason

	/**
	 * @return the semester_ID
	 */
	public int getSemester_ID() {
		return semester_ID;
	}
	
	/**
	 * @return the season
	 */
	public String getSeason() {
		return season;
	}
}//Semester
