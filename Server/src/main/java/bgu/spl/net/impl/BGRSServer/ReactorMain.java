package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.srv.Server;

public class ReactorMain {
    public static void main(String[] args) {
        if(args.length <2){
            System.out.println("Type port AND number of threads");
            return;
        }
        Server<String> reactor = Server.reactor(Integer.parseInt(args[1]),Integer.parseInt(args[0]), ()->new MessagingProtocolImpl(),()->new MessageEncoderDecoderImpl());
        reactor.serve();
    }
}
