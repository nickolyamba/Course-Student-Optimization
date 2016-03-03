package edu.gatech.cs6310.projectOne;

// Sources used: http://galaxy.lamar.edu/~sandrei/CPSC-4360-01/
public class Demand {
	private Course course;
	private PersonSchedule personSchedule; 
	
	public Demand(PersonSchedule personSchedule, Course course) {
		this.course = course;
		this.personSchedule = personSchedule;
	}

	/**
	 * @return the student
	 */
	public PersonSchedule getStudent() {
		return personSchedule;
	}

	/**
	 * @return the course
	 */
	public Course getCourse() {
		return course;
	}

}
