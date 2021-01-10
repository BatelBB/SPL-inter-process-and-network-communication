package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.Database;
import bgu.spl.net.api.MessagingProtocol;


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
        for (int i=0; i < splitmsg.length; i++) {
            splitmsg[i] = splitmsg[i].replaceAll("\\[", "").replaceAll("]", "").replaceAll("\\s", "").replaceAll(",", "");
        }
        Database Data = Database.getInstance();
        if (splitmsg[0].equals("ADMINREG") || splitmsg[0].equals("STUDENTREG")) {//registration
            if(userName!=null)
                return "ERROR";
            if (splitmsg[0].equals("ADMINREG"))
                splitmsg[0] = "Admin";
            else
                splitmsg[0] = "Student";
            //if the setNewUser succeeded then returns true
            if (Data.setNewUser(splitmsg[0], splitmsg[1], splitmsg[2])) {//needs to check that there is a " " after the opcode
                if (splitmsg[0].equals("Admin")) {
                    return "ACK";
                } else {
                    return "ACK";
                }
            } else {//the setNewUser have not succeeded
                if (splitmsg[0].equals("Admin")) {
                    return "ERROR";
               }
                else {
                    return "ERROR";
                }
            }
        }
        if (splitmsg[0].equals("LOGIN")) {
            //calls the method isPassTheSame with the username ([1]) and the password ([2])
            if(userName==null&&!Data.isUserLoggedIn(splitmsg[1])&&Data.isPassTheSame(splitmsg[1],splitmsg[2])) {
                userName = splitmsg[1];
                return "ACK";
            }
            //if the password isn't the same returns false
            return "ERROR";
        }
        if (splitmsg[0].equals("LOGOUT")) {
            //checks if the userName of the current client isn't null, if it's null then the client isn't logged in.
            if(userName!=null) {
                //sets the current username to be logged out
                Data.setLogOut(userName);
                //sets the userName to be null
                userName = null;
                shouldTerminate = true;
                return "ACK";
            }
            return "ERROR";
        }

        if (splitmsg[0].equals("COURSEREG")) {
            //calls the method registerStudentToCourse with the userName and the password[1]
            if(userName!=null&&Data.registerStudentToCourse(userName,Integer.parseInt(splitmsg[1]))) {
                return "ACK";
            }
            return "ERROR";
        }
        if (splitmsg[0].equals("KDAMCHECK")) {
            //calls the KdamCourse method to check if the number of course ([1]) is in the userName's kdamCourse list
            if(userName!=null&&Data.KdamCourses(Integer.parseInt(splitmsg[1]),userName)!=null){
                //returns the ACK6 and the array of the kdamCourses.
                return ("ACK " + (Data.KdamCourses(Integer.parseInt(splitmsg[1]),userName)));
            }
            return "ERROR";
        }
        if (splitmsg[0].equals("COURSESTAT")) {
            //creates a string with the courses' name ([1]).
            String returnMessage = "Course:_(" + splitmsg[1] +")_";
            //returns ERROR 7 if the userName is registered and that it's a student, and if the course doesn't exists
            if(userName==null||Data.IsRegistered(userName).equals("Student")||Data.courseName(Integer.parseInt(splitmsg[1]))==null) {
                return "ERROR";
            }
            //adds the course number to the string
            returnMessage += Data.courseName(Integer.parseInt(splitmsg[1]))+"\n";
            //gets an int of the number of students that registered to the course
            int registeredStudent = Data.CourseStat(Integer.parseInt(splitmsg[1])).size();
            //gets an int of the course limit size
            int courseSize = Data.numOfStudentsInCourse(Integer.parseInt(splitmsg[1]));
            //adds to the string the ints that were just created
            returnMessage += "Seats_Available:_" + (courseSize - registeredStudent) + "/" + courseSize+"\n";
            //creates a string that contains an array of the registered student of the specific course
            String studentRegistered = Data.studentRegisteredArr(Integer.parseInt(splitmsg[1]));
            //adds to the string the array
            returnMessage += "Students_Registered:_" + studentRegistered;
            //returns the message and the ACK 7
            return "ACK " + returnMessage;
        }
        if (splitmsg[0].equals("STUDENTSTAT")) {
            //returns ERROR 7 if the userName is a student and if the user that required isn't a student
            if (userName == null || Data.IsRegistered(userName).equals("Student") || Data.IsRegistered(splitmsg[1]) == null
                    || !Data.IsRegistered(splitmsg[1]).equals("Student")){
            return "ERROR";
        }
            //adds the name of the student required to the string
            String returnMessage = "Student:_" + splitmsg[1]+"\n";
            //creates a string that contains an array of the courses of the student
            String courseRegistered = Data.coursesRegisteredArr(splitmsg[1]);
            //adds the array to the string
            returnMessage += "Courses:_" + courseRegistered;
            //returns the string with the ACK 8
            return "ACK " + returnMessage;
        }
        if (splitmsg[0].equals("ISREGISTERED")) {
            if(userName==null||!Data.isThereACourse(Integer.parseInt(splitmsg[1]))||Data.IsRegistered(userName).equals("Admin"))
                return "ERROR";
            //calls the IsRegisteredStudent with the userName and the course number ([1])
            if(Data.IsRegisteredStudent(userName,Integer.parseInt(splitmsg[1]))){
                return "ACK REGISTERED";
            }
            return "ACK NOT_REGISTERED";
        }
        if (splitmsg[0].equals("UNREGISTER")) {
            //calls the Unregister method with the userName and the course number ([1]) to remove the student from the courseMap
            if(userName!=null&&Data.isThereACourse(Integer.parseInt(splitmsg[1]))&&Data.Unregister(userName,Integer.parseInt(splitmsg[1]))){
                return "ACK";
            }
            //returns ERROR 10 if the student wasn't in the course
            return "ERROR";
        }
        if (splitmsg[0].equals("MYCOURSES")) {
            if(userName!=null&&Data.IsRegistered(userName).equals("Student"))
            //adds ACK 11 and the array of the courses that the userName is registered to.
                return "ACK " + Data.coursesRegisteredArr(userName);
            return "ERROR";
        }
        //returns this string if the splitmsg[0] isn't correct
        return null;
    }

    /**
     * @return true if the connection should be terminated
     */
    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }
}

