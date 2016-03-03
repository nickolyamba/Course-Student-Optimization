package edu.gatech.cs6310.projectOne;

import java.util.HashSet;
import java.util.Set;

public class PersonSchedule{
	private Set<Demand> courseStudSet;
	private Set<Integer> coursesIDSet;
	
	// constructor
	// @param: none
	public PersonSchedule() {
		courseStudSet = new HashSet<Demand>();
		coursesIDSet = new HashSet<Integer>();
	}
	
	public void addCourse(Course course){
		this.courseStudSet.add(new Demand(this, course));
		this.coursesIDSet.add(course.getCourse_ID());
	}
	
	/**
	 * @return a set of courses contained in coursesSet
	 */
	public Set<Course> getCoursesSet() {
		Set<Course> courses = new HashSet<Course>();
		// add course obj to Set courses to return
		for(Demand dem: courseStudSet)
			courses.add(dem.getCourse());
		
		return courses;
	}

	/**
	 * @return the coursesIDSet
	 */
	public Set<Integer> getCoursesIDSet() {
		return coursesIDSet;
	}

}//PersonSchedule
