package bgu.spl.net.api;

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
        if (msg.equals("ADMINREG")){

        }
        if(msg.equals("STUDENTREG")){

        }
        if (msg.equals("LOGIN")){

        }
        if (msg.equals("LOGOUT")){

        }
        if (msg.equals("COURSEREG")){

        }
        if (msg.equals("KDAMCHECK")){

        }
        if (msg.equals("COURSESTAT")){

        }
        if (msg.equals("STUDENTSTAT")){

        }
        if (msg.equals("ISREGISTERED")){

        }
        if (msg.equals("UNREGISTER")){

        }
        if (msg.equals("MYCOURSES")){

        }
        if (msg.equals("ACK")){

        }
        if (msg.equals("ERR")){

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
