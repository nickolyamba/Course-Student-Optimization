package edu.gatech.cs6310.projectOne;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Set;

import gurobi.GRB;
import gurobi.GRBException;
import gurobi.GRBVar;

public class TestConstraints {
	
	// default constructor
	public TestConstraints() {
		
	}
	
	public void testDemand(GRBVar [][][] yijk, Project1Scheduler.Limits limit) 
			throws FileNotFoundException, UnsupportedEncodingException, GRBException{
		// -------------------- TESTING DEMAND ------------------------//
	    // DEMAND
	    //PrintWriter writer = new PrintWriter("10pre.csv", "UTF-8");
	    //writer.println("student_ID,course_ID");
	    double sum=0;
	    for(int i = 0; i < limit.stud_num; i++)
		{			
	    	for(int j = 0; j < limit.course_num; j++)
			{
	    		sum = 0;
	    		for(int k = 0; k < limit.semester_num; k++)
				{
	    			
	    			if(yijk[i][j][k].get(GRB.DoubleAttr.X) > 0)
	    			{
	    				sum = sum + yijk[i][j][k].get(GRB.DoubleAttr.X);
	    				//System.out.println(yijk[i][j][k].get(GRB.StringAttr.VarName)
			            //        + ", " + yijk[i][j][k].get(GRB.DoubleAttr.X));
	    			}	
				}
	    		if(sum >0)
	    		{
	    			//String line = i+1 + "," + (j+1);
	    			//System.out.println(line);
	    			//writer.println(line);
	    		}
			}
		}//writer.close();
	}//testDemand
	
	public void testPrereq(GRBVar [][][] yijk, Project1Scheduler.Limits limit, ArrayList<Course> courses) 
			throws FileNotFoundException, UnsupportedEncodingException, GRBException{
		// -------------------- TESTING PREREQUSITE ------------------------//
	    //PrintWriter writer = new PrintWriter("10pre.csv", "UTF-8");
	    //writer.println("student_ID,course_ID");
	    String line="None";
	    System.out.println("Stud\t" + "Course\t\t" + "Prereq\t" + "Result");
	    for(int i = 0; i < limit.stud_num; i++)
		{			
	    	for(int j = 0; j < limit.course_num; j++)
			{
	    		Set<Integer> courseSet = courses.get(j).getPrereqIDSet();
				if(!courseSet.isEmpty())
				{
					for(int prereqID : courseSet)
					{
						for(int k = 0; k < limit.semester_num; k++)
						{
		    				if(yijk[i][j][k].get(GRB.DoubleAttr.X) > 0)
		    				{
		    					line = (i+1) + "\t" +(j+1) + " (" + (k+1) + ")"+"\t\t";
		    					
		    					
		    					for(int z = 0; z < limit.semester_num; z++)
		    					{
		    						if(yijk[i][prereqID-1][z].get(GRB.DoubleAttr.X) > 0)
		    						{
		    							String result = (z < k) ? "+" : "-";
		    							if(result == "-")
		    								System.out.println(line + prereqID + " (" + (z+1) + ")" + "\t\t" + result);
		    						}	
		    					}//for z
		    				}//if
						}//for k
					}
				}
			}
		}
	}//testPrereq
	
	
	public void testCapacity(GRBVar [][][] yijk, Project1Scheduler.Limits limit) 
			throws FileNotFoundException, UnsupportedEncodingException, GRBException{
		// -------------------- TESTING DEMAND ------------------------//
	    // DEMAND
	    PrintWriter writer = new PrintWriter("testCapacity.csv", "UTF-8");
	    writer.println("student_ID,course_ID");
	    double sum=0;
	    for(int i = 0; i < limit.stud_num; i++)
		{			
	    	for(int j = 0; j < limit.course_num; j++)
			{
	    		sum = 0;
	    		for(int k = 0; k < limit.semester_num; k++)
				{
	    			
	    			if(yijk[i][j][k].get(GRB.DoubleAttr.X) > 0)
	    			{
	    				sum = sum + yijk[i][j][k].get(GRB.DoubleAttr.X);
	    				//System.out.println(yijk[i][j][k].get(GRB.StringAttr.VarName)
			            //        + ", " + yijk[i][j][k].get(GRB.DoubleAttr.X));
	    			}	
				}
	    		if(sum >0)
	    		{
	    			String line = i+1 + "," + (j+1);
	    			//System.out.println(line);
	    			writer.println(line);
	    		}
			}
		}writer.close();
	}//testDemand
}


/********************************************************************************
// Constraint #5: Courses Prerequisites
// Some tests failed
*********************************************************************************/
/*GRBLinExpr PrereqConstrLess, PrereqConstrMore;
for(int i = 0; i < NUM_STUD; i++)
{
	for(int j = 0; j < NUM_COURSES; j++)
	{	
		PrereqConstrLess = new GRBLinExpr();
		PrereqConstrMore = new GRBLinExpr();
		// check if for Course ID[j+1] there is a prerequisite Course
		Set<Integer> courseSet = courses.get(j).getPrereqIDSet();
		if(!courseSet.isEmpty())
		{
			int prereqTemp=-1;
			// if there are more than 1 prerequisites, iterate through set
			for(int prereqID : courseSet) //prereqID is ID of prereq Course
			{
				
				for(int k1 = 0; k1 < NUM_SEM-1; k1++)
				{
					prereqTemp = prereqID;
					for(int k = 0; k <= k1; k++)
					{
						// Set the constraint
						PrereqConstrLess.addTerm(1, yijk[i][j][k+1]);
						PrereqConstrMore.addTerm(1, yijk[i][prereqID-1][k]);
					}//for k
				
					constrName = "STUD_"+(i+1)+"_Course_" + (prereqTemp) + "_PREREQ_TO" + "_Course_" + (j+1) + " " + k1;
					model.addConstr(PrereqConstrLess, GRB.LESS_EQUAL, PrereqConstrMore, constrName);	
				}//for k1
			}//for courseID
		}//if
	}//for j
}//for i*/
	    

