package bgu.spl.net.api;

public class COURSESTAT implements MessagingProtocol<String> {
    private boolean shouldTerminate = false;
    /**
     * process the given message
     *
     * @param msg the received message
     * @return the response to send or null if no response is expected by the client
     */
    @Override
    public String process(String msg) {
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
