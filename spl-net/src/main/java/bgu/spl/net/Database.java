package bgu.spl.net;


/**
 * Passive object representing the bgu.spl.net.Database where all courses and users are stored.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add private fields and methods to this class as you see fit.
 */
public class Database {

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
	boolean initialize(String coursesFilePath) {
		// TODO: implement
		return false;
	}


}
