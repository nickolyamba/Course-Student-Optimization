package edu.gatech.cs6310.projectOne;

import java.util.ArrayList;

public interface Scheduler {

	public void calculateSchedule(String dataFolder);
	
	public void initStudents(ArrayList<Student> students, ArrayList<Course> courses, String dataFolder);

	public void initCourses(ArrayList<Course> courses);
	
	public void initSemesters(ArrayList<Semester> sems);
}