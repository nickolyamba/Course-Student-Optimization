package edu.gatech.cs6310.projectOne;

public class CourseSem {
	private Semester semester;
	private Course course;
	
	public CourseSem(Course course, Semester semester) {
		this.semester = semester;
		this.course = course;
	}

	/**
	 * @return the course
	 */
	public Course getCourse() {
		return course;
	}

	/**
	 * @return the semester
	 */
	public Semester getSemester() {
		return semester;
	}

}// CourseSem
