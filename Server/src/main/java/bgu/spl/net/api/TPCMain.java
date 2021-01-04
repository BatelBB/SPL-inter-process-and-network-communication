package bgu.spl.net.api;

import bgu.spl.net.srv.Server;

public class TPCMain {
    public static void main(String[] args) {
        if(args.length <1){
            System.out.println("Type port number");
            return;
        }
        Server<String> TPC = Server.threadPerClient(Integer.parseInt(args[1]), ()->new MessagingProtocolImpl(),()->new MessageEncoderDecoderImpl());
        TPC.serve();
    }
}
