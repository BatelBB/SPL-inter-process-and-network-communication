package bgu.spl.net.api;

import bgu.spl.net.srv.Server;

public class Main {
    public static void main(String[] args) {
        Server<String> reactor = Server.reactor(2,7777,()->new MessagingProtocolImpl(),()->new MessageEncoderDecoderImpl());
        reactor.serve();
    }
}
