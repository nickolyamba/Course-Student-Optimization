package edu.gatech.cs6310.projectOne;

public class ProjectOne {

	public static void main(String[] args) {
		
		//get path from command line args
		String pathToStudData = getPath(args);
		
		// call scheduler
		Project1Scheduler scheduler = new Project1Scheduler();
		
		// call the calculateSchedule method where gurobi
		scheduler.calculateSchedule(pathToStudData);
	}//main

	/******************************************************************************
	Function: getPath
	
	Purpose:  Get path to student_demand.csv from command line args
	
	Receives: String[] args
	
	Returns:  String path to student_demand
	
	Pre:      None
	
	Post:     path to student_demand.csv is returned as a String
	******************************************************************************/
	private static String getPath(String[] args)
	{
		String path = "";
		/*
		for(int i = 0; i < args.length; i++)
		{
			
			System.out.println(args[i]);
		}
		System.out.println("\nLength = "+args.length+"\n");
		*/
		
		// read command line args
		if(args.length > 1)
			path = args[1].replaceAll(" ", "\\ ");
	
		
		else
			System.out.println("Command Line Error! " +
					"Correct args usage: ProjectOne.java -i PATH/TO/STUDENT");
		
		return path;
	}//getPath()
	
	/*GRBLinExpr PrereqConstrLess, PrereqConstrMore;
	for(int i = 0; i < NUM_STUD; i++)
	{
		for(int j = 0; j < NUM_COURSES; j++)
		{
			// check if for Course ID[j+1] there is a prerequisite Course
			Set<Integer> courseSet = courses.get(j).getPrereqIDSet();
			if(!courseSet.isEmpty())
			{
				// in case there are more than 1 prerequisite iterate through set
				for(int prereqID : courseSet) //prereqID is ID of prereq Course
				{
					PrereqConstrLess = new GRBLinExpr();
					PrereqConstrMore = new GRBLinExpr();
					
					for(int k = 0; k < NUM_SEM-1; k++)
					{
						// Set the constraint
						PrereqConstrLess.addTerm(1, yijk[i][j][k+1]);
						PrereqConstrMore.addTerm(1, yijk[i][prereqID-1][k]);
					}
					constrName = "Course_" + (prereqID) + " PREREQ_TO" + " Course_" + (j+1);
					model.addConstr(PrereqConstrLess, GRB.EQUAL, PrereqConstrMore, constrName);	
				}//for courseID
			}//if
		}//for j
	}//for i*/
	
}//ProjectOne