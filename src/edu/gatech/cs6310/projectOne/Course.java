package edu.gatech.cs6310.projectOne;

import java.util.HashSet;
import java.util.Set;

public class Course {
	private int course_ID;
	
	// sets of CourseSem and semesterIDs
	// associated with the given course
	private Set<CourseSem> semCourseSet;
	private Set<Integer> semesterIDSet;
	
	// Prerequisite sets of Courses and IDs
	private Set<Course> prereqSet; 
	private Set<Integer> prereqIDSet;
	
	// constructor 
	// @param course_ID  to initialize
	public Course(int course_ID){
		this.course_ID = course_ID;
		semCourseSet = new HashSet<CourseSem>();
		semesterIDSet = new HashSet<Integer>();

		prereqSet = new HashSet<Course>();
		prereqIDSet = new HashSet<Integer>();
	}
	
	public void addSemester(Semester semester){
		this.semCourseSet.add(new CourseSem(this, semester));
		this.semesterIDSet.add(semester.getSemester_ID());
	}
	
	public void addPrereq(Course course)
	{
		this.prereqSet.add(course);
		this.prereqIDSet.add(course.getCourse_ID());
	}

	/**
	 * @return the course_ID
	 */
	public int getCourse_ID() {
		return course_ID;
	}
	
	/**
	 * @return the semesterSet
	 */
	public Set<Semester> getSemesterSet() {
		Set<Semester> semesters = new HashSet<>();
		
		//get set of Semester obj from semesterSet
		for(CourseSem sem : semCourseSet)
			semesters.add(sem.getSemester());
		
		return semesters;
	}

	/**
	 * @return the semesterIdSet
	 */
	public Set<Integer> getSemesterIDSet() {
		return semesterIDSet;
	}

	/**
	 * @return the prereqSet
	 */
	public Set<Course> getPrereqSet() {
		return prereqSet;
	}

	/**
	 * @return the prereqIDSet
	 */
	public Set<Integer> getPrereqIDSet() {
		return prereqIDSet;
	}

}//Course
