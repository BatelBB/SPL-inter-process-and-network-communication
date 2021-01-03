package bgu.spl.net.api;

import bgu.spl.net.Database;

public class MessagingProtocolImpl implements MessagingProtocol<String> {
    private boolean shouldTerminate = false;
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

        if (splitmsg[0].equals("ADMINREG") || splitmsg[0].equals("STUDENTREG")){
            if(Data.setNewUser(splitmsg[0],splitmsg[1],Integer.parseInt(splitmsg[2]))) {
                if (splitmsg[0].equals("ADMINREG")) {
                    return "ACK 1";
                } else { return "ACK 2"; }
            }else{
                if (splitmsg[0].equals("ADMINREG")) {
                    return "ERROR 1";
                } else { return "ERROR 2"; }
            }
        }
        if (splitmsg[0].equals("LOGIN")){

        }
        if (splitmsg[0].equals("LOGOUT")){

        }
        if (splitmsg[0].equals("COURSEREG")){

        }
        if (splitmsg[0].equals("KDAMCHECK")){

        }
        if (splitmsg[0].equals("COURSESTAT")){

        }
        if (splitmsg[0].equals("STUDENTSTAT")){

        }
        if (splitmsg[0].equals("ISREGISTERED")){

        }
        if (splitmsg[0].equals("UNREGISTER")){

        }
        if (splitmsg[0].equals("MYCOURSES")){

        }
        if (splitmsg[0].equals("ACK")){

        }
        if (splitmsg[0].equals("ERR")){

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
