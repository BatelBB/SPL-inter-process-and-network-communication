package bgu.spl.net.impl;

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
            if (Data.setNewUser(splitmsg[0], splitmsg[1], Integer.parseInt(splitmsg[2]))) {//needs to check that there is a " " after the opcode
                if (splitmsg[0].equals("Admin")) {
                    return "ACK 1";
                } else {
                    return "ACK 2";
                }
            } else {
                if (splitmsg[0].equals("Admin")) {
                    return "ERROR 1";
                } else {
                    return "ERROR 2";
                }
            }
        }
        if (splitmsg[0].equals("LOGIN")) {
            if(Data.isPassTheSame(splitmsg[1],Integer.parseInt(splitmsg[2]))) {
                userName = splitmsg[1];
                return "ACK 3";
            }
            return "ERROR 3";
        }
        if (splitmsg[0].equals("LOGOUT")) {
            if(userName!=null)
                return "ACK 4";
            return "ERROR 4";
        }

        if(splitmsg[0].equals("TERMINATE")) {
            Data.setLogOut(userName);
            userName=null;
            shouldTerminate = true;
        }

        if (splitmsg[0].equals("COURSEREG")) {
            if(Data.registerStudentToCourse(userName,Integer.parseInt(splitmsg[1])))
                return "ACK 5";
            return "ERROR 5";
        }
        if (splitmsg[0].equals("KDAMCHECK")) {
            if(Data.KdamCourses(Integer.parseInt(splitmsg[1]),userName)!=null){
                return ("ACK 6 " + Arrays.toString(Data.KdamCourses(Integer.parseInt(splitmsg[1]),userName)));
            }
            return "ERROR 6";
        }
        if (splitmsg[0].equals("COURSESTAT")) {
            String returnMessage = "Course: (" + splitmsg[1] +") ";
            if(Data.IsRegistered(userName).equals("Student")||Data.courseName(Integer.parseInt(splitmsg[1]))==null)
                return "ERROR 7";
            returnMessage += Data.courseName(Integer.parseInt(splitmsg[1]))+"\n";
            int registeredStudent = Data.CourseStat(Integer.parseInt(splitmsg[1])).size();
            int courseSize = Data.numOfStudentsInCourse(Integer.parseInt(splitmsg[1]));
            returnMessage += "Seats Available: " + registeredStudent + "/" + courseSize+"\n";
            String studentRegistered = Data.studentRegisteredArr(Integer.parseInt(splitmsg[1]));
            returnMessage += "Students Registered: " + studentRegistered;
            return "ACK 7 " + returnMessage;
        }
        if (splitmsg[0].equals("STUDENTSTAT")) {
            if(Data.IsRegistered(userName).equals("Student")||!Data.IsRegistered(splitmsg[1]).equals("Student"))
                return "ERROR 7";
            String returnMessage = "Student: " + splitmsg[1]+"\n";
            String courseRegistered = Data.coursesRegisteredArr(splitmsg[1]);
            returnMessage += "Courses: " + courseRegistered;
            return "ACK 8 " + returnMessage;
        }
        if (splitmsg[0].equals("ISREGISTERED")) {
            if(Data.IsRegisteredStudent(userName,Integer.parseInt(splitmsg[1]))){
                return "ACK 9 REGISTERED";
            }
            return "ACK 9 NOT REGISTERED";
        }
        if (splitmsg[0].equals("UNREGISTER")) {
            if(Data.Unregister(userName,Integer.parseInt(splitmsg[1]))){
                return "ACK 10";
            }
            return "ERROR 10";
        }
        if (splitmsg[0].equals("MYCOURSES")) {
            return "ACK 11 " + Data.coursesRegisteredArr(userName);
        }
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
