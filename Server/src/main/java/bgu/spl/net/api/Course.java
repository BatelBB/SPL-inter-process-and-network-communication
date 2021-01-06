package bgu.spl.net.api;

import java.util.ArrayList;
import java.util.List;

public class Course {
    private int courseNum;
    private String courseName;
    private ArrayList<Integer> KdamCoursesList;
    private int numOfMaxStudents;

    public Course(String[] splitCourse) {
        //Add the split parts to the appropriate list
        courseNum = (Integer.parseInt(splitCourse[0]));
        courseName = (splitCourse[1]);

        //spliting the string in to a string array
        String[] splitKdamString = splitCourse[2].replaceAll("\\[", "").replaceAll("]", "").replaceAll("\\s", "").split(",");
        ArrayList<Integer> splitKdamInteger = new ArrayList<>();

        //Take the string array and convert it in to an Integer array
        for (int i = 0; i < splitKdamString.length; i++) {
            if (!splitKdamString[i].equals(""))
                try {
                    splitKdamInteger.add(Integer.parseInt(splitKdamString[i]));
                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }
            ;
        }

        KdamCoursesList = splitKdamInteger;
        numOfMaxStudents = (Integer.parseInt(splitCourse[3]));
    }

    public int getNumOfStudents() {
        return numOfMaxStudents;
    }

    public String getCourseName() {
        return courseName;
    }

    public int getCourseNum() {
        return courseNum;
    }

    public ArrayList<Integer> getKdamCoursesList() {
        return KdamCoursesList;
    }
}
