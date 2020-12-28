package bgu.spl.net;


import java.io.FileNotFoundException;
import java.io.FileReader;
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
	private List<String[]> courses;

	private static class databaseSingletonHolder{
		private static final Database database = new Database();
	}
	//to prevent user from creating new bgu.spl.net.Database
	private Database() {
		// TODO: implement
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
	boolean initialize(String coursesFilePath) throws FileNotFoundException {
		// TODO: implement
		Scanner CoursesFile = new Scanner(new FileReader(coursesFilePath));
		while (CoursesFile.hasNextLine()) {
			String Course = CoursesFile.nextLine();
			courses.add(Course.split("|"));
		}
		CoursesFile.close();
		return false;
	}


}
