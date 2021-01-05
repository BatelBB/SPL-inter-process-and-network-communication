package bgu.spl.net;


import bgu.spl.net.api.Course;
import bgu.spl.net.api.User;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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
    private Map<String, List<User>> UsersMap;
    private Map<Integer, List<String>> CourseMap; // contains the names of the students that take this course
    private List<Course> CourseList;//contains the course number with all its data


    private static class databaseSingletonHolder {
        private static final Database database = new Database();
    }

    //to prevent user from creating new bgu.spl.net.Database
    private Database() {
        UsersMap = new ConcurrentHashMap<>();
        CourseMap = new ConcurrentHashMap<>();
        CourseList = new ArrayList<>();

        UsersMap.put("Admin", new ArrayList<>());
        UsersMap.put("Student", new ArrayList<>());

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
                String course = CoursesFile.nextLine();
                String[] splitCourse = course.split("|");
                //takes the string[] of courses and creates a new Course
                Course CurrentCourse = new Course(splitCourse);
                CourseList.add(CurrentCourse);
                CourseMap.put(CurrentCourse.getCourseNum(), new ArrayList<>());

            }
            CoursesFile.close();

        } catch (FileNotFoundException e) {
            return false;
        };
        return true;
    }

    //create a new user in the map if there is no user with the same username
    public boolean setNewUser(String UserType, String Name, int Password) {
        if (IsRegistered(Name) != null) {
            User newUser = new User(Name, Password, false);
            UsersMap.get(UserType).add(newUser);
            //if the user was added
            return true;
        }
        //if the user failed to be added
        return false;
    }

    //checks if the username is already exists in the map
    public String IsRegistered(String Name) {
        //checks if its in students
        for (int i = 0; i < UsersMap.get("Student").size(); i++) {
            if (Name.equals(UsersMap.get("Student").get(i).getUserName()))
                return "Student";
        }
        //checks if it's an Admin
        for (int i = 0; i < UsersMap.get("Admin").size(); i++) {
            if (Name.equals(UsersMap.get("Admin").get(i).getUserName()))
                return "Admin";
        }
        //return false if the name wasn't found registered
        return null;
    }

    //checks if the course is full
    public boolean IsCourseFull(int Course) {
        for (int i = 0; i < CourseList.size(); i++) {
            if (CourseList.get(i).getCourseNum() == Course) {
                if (CourseList.get(i).getNumOfStudents() != CourseMap.get(Course).size()) {
                    //false if its not full
                    return false;
                } else {
                    //true if its full
                    return true;
                }
            }
        }
        //true if the curse wasn't found
        return true;
    }

    //returns an array of the pre required for this course
    public int[] KdamCourses(int Course, String name) {
        if (IsRegistered(name).equals("Student")) {
            for (int i = 0; i < CourseList.size(); i++) {
                if (CourseList.get(i).getCourseNum() == Course) {
                    return CourseList.get(i).getKdamCoursesList();
                }
            }
        }
        //returns null if the course wasn't found
        return null;
    }

    //returns the names of the students that are registered for this course
    public List<String> CourseStat(int Course) {
        return CourseMap.get(Course);
    }
    //return a string of an array of the students that registered to the course required
    public String studentRegisteredArr(int course){
        String studentsArr = "[" + CourseStat(course) + "]";
        return studentsArr.replaceAll(" ", ",");
    }
    //returns a string of the an array of the courses that the student registered to
    public String coursesRegisteredArr(String name){
        String coursesArr = "[" + StudentCourses(name) + "]";
        return coursesArr.replaceAll(" ", ",");
    }
    //return a string of the course name if it's exist in the CourseList, if it's not returns null
    public String courseName(int course) {
        for (int i = 0; i < CourseList.size(); i++) {
            if (CourseList.get(i).getCourseNum() == course)
                return CourseList.get(i).getCourseName();
        }
        return null;
    }

    //returns the number of students that can be in one course that is required
    public int numOfStudentsInCourse(int course) {
        int numOfStudents = 0;
        for (int i = 0; i < CourseList.size(); i++) {
            if (CourseList.get(i).getCourseNum() == course) {
                numOfStudents = CourseList.get(i).getNumOfStudents();
                break;
            }
        }
        return numOfStudents;
    }

    //returns a list of courses that the student is registered in
    public List<Integer> StudentCourses(String StudentName) {
        List<Integer> Courses = new ArrayList<>();
        for (int i = 0; i < CourseList.size(); i++) {
            if (CourseStat(CourseList.get(i).getCourseNum()).contains(StudentName))
                Courses.add(CourseList.get(i).getCourseNum());
        }
        return Courses;
    }
    //returns true if it registered the student to the course, false if it couldn't register
    public boolean registerStudentToCourse(String StudentName, int Course) {
        boolean exist = false;
        //checks if the course exists, if the course isn't full, if the user loggedIn and if the student is registered
        if (CourseMap.containsKey(Course) && !IsCourseFull(Course) && isUserLoggedIn(StudentName) && IsRegistered(StudentName).equals("Student")) {
            exist = true;
            int[] kdam = KdamCourses(Course, StudentName);
            List<Integer> studentKdam = StudentCourses(StudentName);
            for (int i = 0; i < kdam.length; i++) {
                if (!studentKdam.contains(kdam[i])) {
                    exist = false;
                    break;
                }
            }
            //if exist stayed true than it adds the student to the courseMap
            if (exist) {
                CourseMap.get(Course).add(StudentName);
            }
        }
        //if it stayed true it registered and returns true, if it didn't find the kdam in the students kdam list then it's false
        return exist;
    }

    //returns whether or not the student is registered to the course
    public boolean IsRegisteredStudent(String StudentName, int Course) {
        return CourseMap.get(Course).contains(StudentName);
    }

    //unregisters the student from the course
    public boolean Unregister(String StudentName, int Course) {
        if (IsRegisteredStudent(StudentName, Course)) {
            CourseMap.get(Course).remove(StudentName);
            //returns true if the student was unregistered
            return true;
        }
        //returns falls if the student wasn't found in the course to begin with
        return false;
    }
    //returns true if the password is the same, false if the password isn't the same.
    public boolean isPassTheSame(String Name, int Password) {
        boolean isPassTheSame;
        if (IsRegistered(Name) != null) {
            for (int i = 0; i < UsersMap.get(IsRegistered(Name)).size(); i++) {
                if (UsersMap.get(IsRegistered(Name)).get(i).getUserName().equals(Name)) {
                    //returns true if the password is the same as the one the user registered with
                    isPassTheSame = UsersMap.get(IsRegistered(Name)).get(i).getUserPassword() == Password;
                    //if the password is the same, sets the user to be logged in
                    UsersMap.get(IsRegistered(Name)).get(i).setLoggedIn(isPassTheSame);
                    return isPassTheSame;
                }
            }
        }
        //if the user didn't register
        return false;
    }
    //returns true if the user is loggedIn, false if the user isn't loggedIn.
    public boolean isUserLoggedIn(String Name) {
        if (IsRegistered(Name) != null) {
            for (int i = 0; i < UsersMap.get(IsRegistered(Name)).size(); i++) {
                //checks if the userMap contains the user's username.
                if (UsersMap.get(IsRegistered(Name)).get(i).getUserName().equals(Name)) {
                    //returns true if the user is loggedIn
                    return UsersMap.get(IsRegistered(Name)).get(i).getLoggedIn();
                }
            }
        }
        //returns false if the user isn't registered.
        return false;
    }
    //sets the user to be logged out.
    public void setLogOut(String Name) {
        for (int i = 0; i < UsersMap.get(IsRegistered(Name)).size(); i++) {
            if (UsersMap.get(IsRegistered(Name)).get(i).getUserName().equals(Name)) {
                UsersMap.get(IsRegistered(Name)).get(i).setLoggedIn(false);
            }
        }
    }

}
