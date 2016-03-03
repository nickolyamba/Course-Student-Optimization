package edu.gatech.cs6310.projectOne;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;

public class Project1Scheduler implements Scheduler{

	public void calculateSchedule(String pathToStudDemand){
		//pathToStudDemand = "I:\\Dropbox\\CS 6310 - Software Architecture and Design\\Project 1\\project1-datasets\\demand12.csv";
		// Declare ArrayLists of Course, Semester and Student objects
		ArrayList<Course> courses = new ArrayList<>();
		ArrayList<Semester> semesters = new ArrayList<>();
		ArrayList<Student> students = new ArrayList<>();
		
		//populate ArrayLists of Course, Semester and Student with objects
		initSemesters(semesters);
		initCourses(courses);
		// add set of Semesters to Courses that Student have to take
		addSemToCourses(courses, semesters);
		initStudents(students, courses, pathToStudDemand); //read from a file
		
		final int NUM_SEM = semesters.size();
		final int NUM_COURSES = courses.size(); 
		final int NUM_STUD = students.size();
		Limits limit = new Limits(NUM_STUD, NUM_COURSES, NUM_SEM);
		
	    /****************************************************************************
	    //			                     GUROBI 
	    *****************************************************************************/
        GRBEnv env;
		try {
			env = new GRBEnv("mip1.log");
			// prevent from outputting gurobi data to console
            env.set(GRB.IntParam.LogToConsole, 0);
			GRBModel model = new GRBModel(env);
			
			//source used: https://www.gurobi.com/documentation/6.5/examples/sudoku_java.html
			GRBVar [][][] yijk = new GRBVar[NUM_STUD][NUM_COURSES][NUM_SEM];
			GRBVar X = model.addVar(0, 1000, 1.0, GRB.INTEGER, "X"); // largest class size
			
		    // populate 3D array of Binary guorobi varibales
			createGvars(yijk, limit, model);
			
			// Integrate new variables
		    model.update();
		    
		    String constrName; //constraint name
		    /****************************************************************************
		    // Constraint #1: Student can take no more than two classes in one semester
		    // Sum_j_(y_ijk) ≤ 2: y111 + y121 + y131 + y141 + ... y1Nj1 ≤ 2 (i, k = const)
		    *****************************************************************************/
		    GRBLinExpr maxCoursesConstraint;
			for(int i = 0; i < NUM_STUD; i++)
			{			
				for(int k = 0; k < NUM_SEM; k++)
				{
					maxCoursesConstraint = new GRBLinExpr();
					
					for(int j = 0; j < NUM_COURSES; j++)
					{
						// Set the constraint
						maxCoursesConstraint.addTerm(1, yijk[i][j][k]);
					}
					
					constrName = "MAXCOURSE_Student_" + (i+1) + "_Semester" + (k+1);
					model.addConstr(maxCoursesConstraint, GRB.LESS_EQUAL, 2, constrName);
				}
			}
			
			/*************************************************************************************
			// Constraint #2: Student Demand Data + constraint that course can be taken only once
		    // 1D: (i, j = const) Sum_k_(y_ijk) = 1: y111 + y112 + y113 + y114 + ... y11k = 1 or 0
			**************************************************************************************/
		    GRBLinExpr mustTakeConstraint;
			for(int i = 0; i < NUM_STUD; i++)
			{
				for(int j = 0; j < NUM_COURSES; j++)
				{
					// check if Student ID[i+1] must take Course ID[j+1] during any of 12 semesters
					Set<Integer> courseSet = students.get(i).getSchedule().getCoursesIDSet();
					if(courseSet.contains(j+1))// course_ID starts from 1. Array from 0
					{
						mustTakeConstraint = new GRBLinExpr();
						
						for(int k = 0; k < NUM_SEM; k++)
						{
							// Set the constraint
							mustTakeConstraint.addTerm(1, yijk[i][j][k]);
						}
						
						constrName = "MUSTTAKE_Student_" + (i+1) + "_Course_" + (j+1);
						model.addConstr(mustTakeConstraint, GRB.EQUAL, 1, constrName);	
					}
					// if Student[i+1] do NOT required take a Course[j+1] during any of 12 semesters
					// leave it unconstrained in case he/she should take a prerequisite course 
				}//for
			}//for
			
			/***********************************************************************************
			// Constraint #3: Courses Prerequisites. Class and its prerequisite can't be taken 
			// at the same time.
			// Yi(j0)k + Yi(j1)k <= 1, for 1D: i, k = const
			// Source of the idea: https://piazza.com/class/ij4blvpmdri3ou?cid=99
			************************************************************************************/
		    GRBLinExpr PrereqSameSemConstr;
			for(int i = 0; i < NUM_STUD; i++)
			{
				for(int k = 0; k < NUM_SEM; k++)
				{	
					for(int j = 0; j < NUM_COURSES; j++)
					{
						// check if for Course ID[j+1] has a prerequisite Course
						Set<Integer> courseIDSet = courses.get(j).getPrereqIDSet();
						if(!courseIDSet.isEmpty())
						{
							PrereqSameSemConstr = new GRBLinExpr();
							for(int prereqID : courseIDSet)
							{
								PrereqSameSemConstr.addTerm(1, yijk[i][j][k]);
								PrereqSameSemConstr.addTerm(1, yijk[i][prereqID-1][k]);
								
								constrName = "STUD_" + (i+1) + "_Course_" + (j+1) + "_Prereq_" + prereqID + "_SEM_" + (k+1);
								model.addConstr(PrereqSameSemConstr, GRB.LESS_EQUAL, 1, constrName);
							}//for
						}//if
					}//for j
				}//for k
			}//for i
			
			/***********************************************************************************
			// Constraint #4: Courses Prerequisites. Class with a prerequisite can't be taken 
			// 1st semester. Source of the idea: https://piazza.com/class/ij4blvpmdri3ou?cid=99
			// Yi(j0)1 = 0
			************************************************************************************/
		    GRBLinExpr PrereqFirstSemConstr;
			for(int i = 0; i < NUM_STUD; i++)
			{
				for(int j = 0; j < NUM_COURSES; j++)
				{
					// check if for Course ID[j+1] has a prerequisite Course
					Set<Integer> courseSet = courses.get(j).getPrereqIDSet();
					if(!courseSet.isEmpty())
					{
						PrereqFirstSemConstr = new GRBLinExpr();
						PrereqFirstSemConstr.addTerm(1, yijk[i][j][0]);

						constrName = "STUD_" + (i+1) +"_Course_" + (j+1) + "_!1stSem";
						model.addConstr(PrereqFirstSemConstr, GRB.EQUAL, 0, constrName);	
					}//if
				}//for j
			}//for i
			
			/********************************************************************************
			// Constraint #5: Courses Prerequisites
		    // Sum_(k): k*Yij(0)k <= Sum_(k): k*Yij(0)k; 1D: (i, j = const)
		    // 1*y1(12)1 + 2*y1(12)2 + ... + m*y1(12)m <= 1*y1(1)1 + 2*y1(1)2 + ...+ m*y1(1)m
			*********************************************************************************/
			GRBLinExpr PrereqConstrLess, PrereqConstrMore;
			for(int i = 0; i < NUM_STUD; i++)
			{
				for(int j = 0; j < NUM_COURSES; j++)
				{
					// If student doesn't take a course that requires a prerequisite, skip
					Set<Integer> courseInStud = students.get(i).getSchedule().getCoursesIDSet();
					if(!courseInStud.contains(j+1))
						continue;
					
					PrereqConstrLess = new GRBLinExpr();
					PrereqConstrMore = new GRBLinExpr();
					// check if for Course ID[j+1] there is a prerequisite Course
					Set<Integer> courseSet = courses.get(j).getPrereqIDSet();
					if(!courseSet.isEmpty())
					{
						int prereqTemp = -1;// to reference in constraint name
						// if there are more than 1 prerequisites, iterate through set
						for(int prereqID : courseSet) //prereqID is ID of prereq Course
						{
							prereqTemp = prereqID;
							for(int k = 1; k <= NUM_SEM; k++)
							{
								// Set the constraint
								PrereqConstrMore.addTerm(k, yijk[i][j][k-1]);// (k-1) because array index starts from 0
								PrereqConstrLess.addTerm(k, yijk[i][prereqID-1][k-1]);
							}//for k
						
							constrName = "STUD_"+(i+1)+"_Course_" + (prereqTemp) + "_PREREQ_TO" + "_Course_" + (j+1);
							model.addConstr(PrereqConstrLess, GRB.LESS_EQUAL, PrereqConstrMore, constrName);	
						}//for courseID
					}//if
				}//for j
			}//for i		
			
			/*******************************************************************
			Constraint #6: Capacity limits for one class at one semester
		    (j, k = const) Sum_i_(y_ijk) ≤ Ac,jk 
			1D: y111 + y211 + y311 + y411 + ... yNi11 ≤ capacity[j][k] 
			*******************************************************************/
		    GRBLinExpr maxCapacityConstraint;
		    for(int j = 0; j < NUM_COURSES; j++)
			{			
		    	for(int k = 0; k < NUM_SEM; k++)
				{
		    		// check if Course[j+1] is offered at Semester[k+1], then Sum of Students <= X
		    		Set<Integer> coursesIDSet = courses.get(j).getSemesterIDSet();
		    		if(coursesIDSet.contains(k+1))// k+1 because course_ID start at 1 vs. Array
		    		{
		    			maxCapacityConstraint = new GRBLinExpr();
						
						for(int i = 0; i < NUM_STUD; i++)
						{
							maxCapacityConstraint.addTerm(1, yijk[i][j][k]);
						}
						// for each course-semester pair, sum(students) <= X
						constrName = "CAPLIM_Course_" + j + "_Semester_" + k;
						model.addConstr(maxCapacityConstraint, GRB.LESS_EQUAL, X, constrName);
		    		}//if
		    		
		    		// if Course[j+1] is NOT offered at Semester[k+1], then Sum of Students = 0
		    		else
		    		{
		    			maxCapacityConstraint = new GRBLinExpr();
						
						for(int i = 0; i < NUM_STUD; i++)
						{
							maxCapacityConstraint.addTerm(1, yijk[i][j][k]);
						}
						// for each course-semester pair, sum(students) = 0
						constrName = "CAPLIM_Course_" + j + "_Semester_" + k;
						model.addConstr(maxCapacityConstraint, GRB.EQUAL, 0, constrName);
		    		}//else
				}//for k
			}//for j
		    
			/*******************************************************************
				Set objective: minimize X (minimize the largest class size)
			*******************************************************************/
		    GRBLinExpr objectiveExpr = new GRBLinExpr();
		    objectiveExpr.addTerm(1.0, X);
		    model.setObjective(objectiveExpr, GRB.MINIMIZE);
			
		    // Optimize the model
            model.optimize();
            
         // INSERT AFTER OPTIMIZATION FOR TESTING THE MODEL
          TestConstraints test = new TestConstraints();
          //test.testCapacity(yijk, limit);
          //test.testDemand(yijk, limit);
          test.testPrereq(yijk, limit, courses);

            // Display the results
            double objectiveValue = model.get(GRB.DoubleAttr.ObjVal);
            System.out.printf("X=%.2f", objectiveValue);          
            
            // Dispose of model and environment
            model.dispose();
            env.dispose();
			
		} catch (GRBException | FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}//end try-catch
	}//calculateSchedule()
	
	/******************************************************************************
	Function: createGvars
	
	Purpose:  Create 3D array of references to gurobi variables
	
	Receives: 
		- GRBVar [][][] yijk: 3D empty array for GRBVar instances
		- Limits liimit: class that provides num of studs, courses, semesters
	
	Returns:  None
	
	Pre:      limits != null, model != null
	
	Post:     create references to Binary gurobi variables in 3D array 
	******************************************************************************/
	private void createGvars(GRBVar [][][] yijk, Limits limit, GRBModel model) throws GRBException
	{
		// Gurobi var name to reference later
		String gvar_name = "";
		for(int i = 0; i < limit.stud_num; i++)
		{
			for(int j = 0; j < limit.course_num; j++)
			{
				for(int k = 0; k < limit.semester_num; k++)
				{
					gvar_name = "BIN_" + String.valueOf(i+1) + 
								"_" + String.valueOf(j+1) + 
								"_" + String.valueOf(k+1);
					
					// lower bound, upper bound, Objective coefficient=0 (is set later), type, name
					yijk[i][j][k] = model.addVar(0, 1, 0.0, GRB.BINARY, gvar_name);
				}
			}
		}
	}//createGvars
	
	/******************************************************************************
	Function: initSemesters -- hard coded
	
	Purpose:  Initialize ArrayList of Semesters
	
	Receives: 
		- ArrayList<Semester> sems 
		- int NUM_SEM - number of semsters
	
	Returns:  None
	
	Pre:      sems isEmpty
	
	Post:     Initialized ArrayList of Semesters
	******************************************************************************/
	public void initSemesters(ArrayList<Semester> sems)
	{
		final int NUM_SEM = 12;
		
		//semester_id starts from 1
		for(int i = 1; i <= NUM_SEM; i++)
			sems.add(new Semester(i));
	}
	
	/******************************************************************************
	Function: initCourses -- hard coded
	
	Purpose:  Initialize ArrayList of Courses
	
	Receives: 
		- ArrayList<Course> courses 
		- int NUM_COURSES - number of courses
	
	Returns:  None
	
	Pre:      courses isEmpty
	
	Post:     Initialized ArrayList of Courses
	******************************************************************************/
	public void initCourses(ArrayList<Course> courses)
	{
		final int NUM_COURSES = 18;
		//course_id starts from 1
		for(int i = 1; i <= NUM_COURSES; i++)
		{
			//Course course = new Course(i);
			courses.add(new Course(i));
		}
		
		// Add prerequisite Courses - hard coded
		for(int i = 1; i <= NUM_COURSES; i++)
		{
			if(i == 16)
				courses.get(i-1).addPrereq(courses.get(3));
			else if(i == 1)
				courses.get(i-1).addPrereq(courses.get(11));
			else if(i == 13)
				courses.get(i-1).addPrereq(courses.get(8));
			else if(i == 7)
				courses.get(i-1).addPrereq(courses.get(2));
		}	
	}//initCourses()
	
	/******************************************************************************
	Function: addSemToCourses
	
	Purpose:  Adds Set of Semesters to each Course object
	
	Receives: 
		- ArrayList<Course> courses 
		- ArrayList<Semester> semesters
	
	Returns:  None
	
	Pre:      None
	
	Post:     Courses have set of semesters when they offered
	******************************************************************************/
	private void addSemToCourses(ArrayList<Course> courses, ArrayList<Semester> semesters)
	{
		int courseID;
		// create set of Fall courses
		Set<Integer> setFall = new HashSet<Integer>();
		setFall.add(1); setFall.add(7); setFall.add(11); setFall.add(15);
		setFall.add(17);
		
		// create set of Spring courses
		Set<Integer> setSpring = new HashSet<Integer>();
		setSpring.add(5); setSpring.add(10); setSpring.add(14); setSpring.add(16);
		setSpring.add(18);
		
		for(Course course : courses)
		{
			courseID = course.getCourse_ID();
			
			// if courseID is in Fall set,
			if(setFall.contains(courseID))
			{
				//add all Fall semesters to the given course
				for(Semester semester : semesters)
				{
					if(semester.getSeason() == "Fall")
						course.addSemester(semester);
				}
					
			}
			
			// if courseID is in Spring set
			else if(setSpring.contains(courseID))
			{
				//add all Spring semesters to the given course
				for(Semester semester : semesters)
				{
					if(semester.getSeason() == "Spring")
						course.addSemester(semester);
				}
			}
			
			// course is offered every semester
			else
			{
				// add all semesters to the given course
				for(Semester semester : semesters)
					course.addSemester(semester);
			}
				
		}//for
	}//addSemToCourses
	
	/******************************************************************************
	Function: initStudents
	
	Purpose:  Gets list of students from student_demand file and populates
			  ArrayList with Student objects
	
	Receives: 
		- ArrayList<Student> students 
		- ArrayList<Course> courses
		- String pathToStudDemand
	Returns:  None
	
	Pre:      courses isEmpty
	
	Post:     ArrayList is populated with Student objects from a data file
	******************************************************************************/
	public void initStudents(ArrayList<Student> students, ArrayList<Course> courses, String pathToStudDemand)
	{
		// to save data from csv file
		Map<Integer, Set<Integer>> studentData;
		
		// read csv file to map of integer sets
		CsvReader csv = new CsvReader();
		studentData = csv.readFile(pathToStudDemand);
		
		// source: https://dzone.com/articles/the-magic-word-in-java-cafebabe
		// iterate over the map of student demand read from a file 
		for (Map.Entry<Integer, Set<Integer>> entry : studentData.entrySet()) 
		{
			// get key and mapped set of course integers
			int studID = entry.getKey();
			Set<Integer> courseSet = entry.getValue();
			
			// create new PersonSchedule object
			PersonSchedule schedule = new PersonSchedule();
			
			// add courses to PersonSchedule obj of the given Student
			for(Course course : courses)
			{
				if(courseSet.contains(course.getCourse_ID()))
					schedule.addCourse(course);
			}
			
			Student student = new Student(studID, schedule);
			
			// add to ArrayList<Student>
			students.add(student);
		}//for
	}//initStudents

	// Inner class to store and access
	// number of students, semesters, and courses
	protected class Limits
	{
		int stud_num;
		int course_num;
		int semester_num;
		
	    /********************************
	    //         Constructor
		// @param: 
		// int stud = Number of Students
		// int cour = Number of Courses
		// int sem = Number of Semesters
	    *********************************/
		Limits(int stud, int cour, int sem)
		{
			this.stud_num = stud; this.course_num = cour; this.semester_num = sem;
		}
	}//Class Limits
		
}//class Project1Scheduler


// INSERT AFTER OPTIMIZATION FOR TESTING THE MODEL
//TestConstraints test = new TestConstraints();
//test.testCapacity(yijk, limit);
//test.testDemand(yijk, limit);
//test.testPrereq(yijk, limit, courses);

//model.update();
//model.write("constraints.lp"); // constraints

//model.update();
//model.write("solution.sol"); // solution

/*
constrMaxCourseInSem(yijk, limit, model);
constrStudDemand(yijk, limit, model);
constrSameSemPrereq(yijk, limit, model);
constrFirstSemPrereq(yijk, limit, model);
constrPrereq(yijk, limit, model);
constrCapLimit(yijk, limit, model);
 */
