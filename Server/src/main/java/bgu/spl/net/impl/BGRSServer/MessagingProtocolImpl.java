package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.Database;
import bgu.spl.net.api.MessagingProtocol;

import java.util.Arrays;

public class MessagingProtocolImpl implements MessagingProtocol<String> {
    private boolean shouldTerminate = false;
    private String userName = null;

    /**
     * process the given message
     *
     * @param msg the received message
     * @return the response to send or null if no response is expected by the client
     */
    @Override
    public String process(String msg) {
        String[] splitmsg = msg.split(" ");
        Database Data = Database.getInstance();

        if (splitmsg[0].equals("ADMINREG") || splitmsg[0].equals("STUDENTREG")) {//registration
            if (splitmsg[0].equals("ADMINREG"))
                splitmsg[0] = "Admin";
            else
                splitmsg[0] = "Student";
            //if the setNewUser succeeded then returns true
            if (Data.setNewUser(splitmsg[0], splitmsg[1], Integer.parseInt(splitmsg[2]))) {//needs to check that there is a " " after the opcode
                if (splitmsg[0].equals("Admin")) {
                    return "ACK 1";
                } else {
                    return "ACK 2";
                }
            } else {//the setNewUser have not succeeded
                if (splitmsg[0].equals("Admin")) {
                    return "ERROR 1";
                } else {
                    return "ERROR 2";
                }
            }
        }
        if (splitmsg[0].equals("LOGIN")) {
            //calls the method isPassTheSame with the username ([1]) and the password ([2])
            if(Data.isPassTheSame(splitmsg[1],Integer.parseInt(splitmsg[2]))) {
                userName = splitmsg[1];
                return "ACK 3";
            }
            //if the password isn't the same returns false
            return "ERROR 3";
        }
        if (splitmsg[0].equals("LOGOUT")) {
            //checks if the userName of the current client isn't null, if it's null then the client isn't logged in.
            if(userName!=null)
                return "ACK 4";
            return "ERROR 4";
        }

        if(splitmsg[0].equals("TERMINATE")) {
            //sets the current username to be logged out
            Data.setLogOut(userName);
            //sets the userName to be null
            userName=null;
            shouldTerminate = true;
        }

        if (splitmsg[0].equals("COURSEREG")) {
            //calls the method registerStudentToCourse with the userName and the password[1]
            if(Data.registerStudentToCourse(userName,Integer.parseInt(splitmsg[1])))
                return "ACK 5";
            return "ERROR 5";
        }
        if (splitmsg[0].equals("KDAMCHECK")) {
            //calls the KdamCourse method to check if the number of course ([1]) is in the userName's kdamCourse list
            if(Data.KdamCourses(Integer.parseInt(splitmsg[1]),userName)!=null){
                //returns the ACK6 and the array of the kdamCourses.
                return ("ACK 6 " + Arrays.toString(Data.KdamCourses(Integer.parseInt(splitmsg[1]),userName)));
            }
            return "ERROR 6";
        }
        if (splitmsg[0].equals("COURSESTAT")) {
            //creates a string with the courses' name ([1]).
            String returnMessage = "Course: (" + splitmsg[1] +") ";
            //returns ERROR 7 if the userName is registered and that it's a student, and if the course doesn't exists
            if(Data.IsRegistered(userName).equals("Student")||Data.courseName(Integer.parseInt(splitmsg[1]))==null)
                return "ERROR 7";
            //adds the course number to the string
            returnMessage += Data.courseName(Integer.parseInt(splitmsg[1]))+"\n";
            //gets an int of the number of students that registered to the course
            int registeredStudent = Data.CourseStat(Integer.parseInt(splitmsg[1])).size();
            //gets an int of the course limit size
            int courseSize = Data.numOfStudentsInCourse(Integer.parseInt(splitmsg[1]));
            //adds to the string the ints that were just created
            returnMessage += "Seats Available: " + registeredStudent + "/" + courseSize+"\n";
            //creates a string that contains an array of the registered student of the specific course
            String studentRegistered = Data.studentRegisteredArr(Integer.parseInt(splitmsg[1]));
            //adds to the string the array
            returnMessage += "Students Registered: " + studentRegistered;
            //returns the message and the ACK 7
            return "ACK 7 " + returnMessage;
        }
        if (splitmsg[0].equals("STUDENTSTAT")) {
            //returns ERROR 7 if the userName is a student and if the user that required isn't a student
            if(Data.IsRegistered(userName).equals("Student")||!Data.IsRegistered(splitmsg[1]).equals("Student"))
                return "ERROR 7";
            //adds the name of the student required to the string
            String returnMessage = "Student: " + splitmsg[1]+"\n";
            //creates a string that contains an array of the courses of the student
            String courseRegistered = Data.coursesRegisteredArr(splitmsg[1]);
            //adds the array to the string
            returnMessage += "Courses: " + courseRegistered;
            //returns the string with the ACK 8
            return "ACK 8 " + returnMessage;
        }
        if (splitmsg[0].equals("ISREGISTERED")) {
            //calls the IsRegisteredStudent with the userName and the course number ([1])
            if(Data.IsRegisteredStudent(userName,Integer.parseInt(splitmsg[1]))){
                return "ACK 9 REGISTERED";
            }
            return "ACK 9 NOT REGISTERED";
        }
        if (splitmsg[0].equals("UNREGISTER")) {
            //calls the Unregister method with the userName and the course number ([1]) to remove the student from the courseMap
            if(Data.Unregister(userName,Integer.parseInt(splitmsg[1]))){
                return "ACK 10";
            }
            //returns ERROR 10 if the student wasn't in the course
            return "ERROR 10";
        }
        if (splitmsg[0].equals("MYCOURSES")) {
            //adds ACK 11 and the array of the courses that the userName is registered to.
            return "ACK 11 " + Data.coursesRegisteredArr(userName);
        }
        //returns this string if the splitmsg[0] isn't correct
        return "OPCODE isn't correct";
    }

    /**
     * @return true if the connection should be terminated
     */
    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }
}
