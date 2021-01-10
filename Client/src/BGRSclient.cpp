#include <stdlib.h>
#include <connectionHandler.h>
#include <thread>
#include <boost/thread.hpp>

/**
* This code assumes that the server replies the exact text the client sent it (as opposed to the practical session example)
*/

void input(ConnectionHandler& connectionHandler) {
    while (1) {
        try {
            const short bufsize = 124;
            char buf[bufsize];
            std::cin.getline(buf,bufsize);
            std::string line(buf);

            if(!connectionHandler.sendLine(line)){
                break;
            }
        }
        catch(boost::thread_interrupted&){
            break;
        }
    }
}

int main(int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);

    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }
    boost::thread inputWorkingThread(&input, boost::ref(connectionHandler));
    //From here we will see the rest of the ehco client implementation:
    while (1) {
        // We can use one of three options to read data from the server:
        // 1. Read a fixed number of characters
        // 2. Read a line (up to the newline character using the getline() buffered reader
        // 3. Read up to the null character
        std::string answer;
        bool terminate = false;
        // Get back an answer: by using the expected number of bytes (len bytes + newline delimiter)
        // We could also use: connectionHandler.getline(answer) and then get the answer without the newline char at the end
        if (!connectionHandler.getLine(answer,terminate)) {
            inputWorkingThread.interrupt();
            break;
        }
    std::replace( answer.begin(), answer.end(), '_', ' ');
	std::cout << answer << std::endl;
        // A C string must end with a 0 char delimiter.  When we filled the answer buffer from the socket
        // we filled up to the \n char - we must make sure now that a 0 char is also present. So we truncate last character.
        if (terminate) {
            inputWorkingThread.interrupt();
            break;
        }

    }
    inputWorkingThread.join();
    return 0;
}



