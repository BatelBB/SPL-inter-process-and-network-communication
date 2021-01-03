package bgu.spl.net.api;

import bgu.spl.net.Database;

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

        }
        if (splitmsg[0].equals("COURSESTAT")) {

        }
        if (splitmsg[0].equals("STUDENTSTAT")) {

        }
        if (splitmsg[0].equals("ISREGISTERED")) {

        }
        if (splitmsg[0].equals("UNREGISTER")) {

        }
        if (splitmsg[0].equals("MYCOURSES")) {

        }
        if (splitmsg[0].equals("ACK")) {

        }
        if (splitmsg[0].equals("ERR")) {

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
