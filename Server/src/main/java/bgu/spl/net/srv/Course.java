package bgu.spl.net.srv;

import java.util.List;

public class Course {
    private int courseNum;
    private String courseName;
    private int[] KdamCoursesList;
    private int numOfMaxStudents;

    public Course(String[] splitCourse) {
        //Add the split parts to the appropriate list
        courseNum = (Integer.parseInt(splitCourse[0]));
        courseName = (splitCourse[1]);

        //spliting the string in to a string array
        String[] splitKdamString = splitCourse[2].replaceAll("\\[", "").replaceAll("]", "").replaceAll("\\s", "").split(",");
        int[] splitKdamInteger = new int[splitKdamString.length];

        //Take the string array and convert it in to an Integer array
        for (int i = 0; i < splitKdamString.length; i++) {
            try {
                splitKdamInteger[i] = Integer.parseInt(splitKdamString[i]);
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            };
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

    public int[] getKdamCoursesList() {
        return KdamCoursesList;
    }
}
