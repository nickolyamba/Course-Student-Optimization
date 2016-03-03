package edu.gatech.cs6310.projectOne;

public class Student{
	private int stud_ID;
	private PersonSchedule schedule;
	
	//Constructor
	// @param
	// int stud_ID, PersonSchedule schedule
	public Student(int stud_ID, PersonSchedule schedule) {
		this.stud_ID = stud_ID;
		this.schedule = schedule;
	}
	
	/**
	 * @return the stud_ID
	 */
	public int getStud_ID() {
		return stud_ID;
	}

	/**
	 * @return the schedule
	 */
	public PersonSchedule getSchedule() {
		return schedule;
	}

}
