package bgu.spl.net;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Passive object representing the bgu.spl.net.Database where all courses and users are stored.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add private fields and methods to this class as you see fit.
 */
//we can add public methods
	//https://www.cs.bgu.ac.il/~spl211/Assignments/Assignment_3?action=show-thread&id=02259a78b260b7e58bd05ca5232a3c8e
public class Database {
	private List<Integer> courseNum;
	private List<String> courseName;
	private List<Integer[]> KdamCoursesList;
	private List<Integer> numOfMaxStudents;

	private static class databaseSingletonHolder{
		private static final Database database = new Database();
	}
	//to prevent user from creating new bgu.spl.net.Database
	private Database() {
		courseNum = new ArrayList<>();
		courseName = new ArrayList<>();
		KdamCoursesList = new ArrayList<>();
		numOfMaxStudents = new ArrayList<>();
	}

	/**
	 * Retrieves the single instance of this class.
	 */
	public static Database getInstance() {
		return databaseSingletonHolder.database;
	}
	
	/**
	 * loads the courses from the file path specified
	 * into the bgu.spl.net.Database, returns true if successful.
	 */
	boolean initialize(String coursesFilePath) {
		try {
			//Scan the file
			Scanner CoursesFile = new Scanner(new FileReader(coursesFilePath));

			//As long as you have a next line go to it
			while (CoursesFile.hasNextLine()) {

				//Take the next line and split it at all the points there's |
				String Course = CoursesFile.nextLine();
				String[] splitCourse = Course.split("|");

				//Add the split parts to the appropriate list
				courseNum.add(Integer.parseInt(splitCourse[0]));
				courseName.add(splitCourse[1]);

				//spliting the string in to a string array
				String[] splitKdamString = splitCourse[2].replaceAll("\\[", "").replaceAll("]", "").replaceAll("\\s", "").split(",");
				Integer[] splitKdamInteger = new Integer[splitKdamString.length];

				//Take the string array and cunvert it in to an Integer array
				for (int i = 0; i < splitKdamString.length; i++) {
					try {
						splitKdamInteger[i] = Integer.parseInt(splitKdamString[i]);
					} catch (NumberFormatException nfe) {
						nfe.printStackTrace();
					}
					;
				}


				KdamCoursesList.add(splitKdamInteger);
				numOfMaxStudents.add(Integer.parseInt(splitCourse[3]));
			}
			CoursesFile.close();

		} catch (FileNotFoundException e){
			return false;
		};
		return true;
	}
}
