#include <connectionHandler.h>

using boost::asio::ip::tcp;

using std::cin;
using std::cout;
using std::cerr;
using std::endl;
using std::string;

ConnectionHandler::ConnectionHandler(string host, short port) : host_(host), port_(port), io_service_(),
                                                                socket_(io_service_) {}

ConnectionHandler::~ConnectionHandler() {
    close();
}

bool ConnectionHandler::connect() {
   std::cout << "Starting connect to "
              << host_ << ":" << port_ << std::endl;
    try {
        tcp::endpoint endpoint(boost::asio::ip::address::from_string(host_), port_); // the server endpoint
        boost::system::error_code error;
        socket_.connect(endpoint, error);
        if (error)
            throw boost::system::system_error(error);
    }
    catch (std::exception &e) {
        std::cerr << "Connection failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

bool ConnectionHandler::getBytes(char bytes[], unsigned int bytesToRead) {
    size_t tmp = 0;
    boost::system::error_code error;
    try {
        while (!error && bytesToRead > tmp) {
            tmp += socket_.read_some(boost::asio::buffer(bytes + tmp, bytesToRead - tmp), error);
        }
        if (error)
            throw boost::system::system_error(error);
    } catch (std::exception &e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

bool ConnectionHandler::sendBytes(const char bytes[], int bytesToWrite) {
    int tmp = 0;
    boost::system::error_code error;
    try {
        while (!error && bytesToWrite > tmp) {
            tmp += socket_.write_some(boost::asio::buffer(bytes + tmp, bytesToWrite - tmp), error);
        }
        if (error)
            throw boost::system::system_error(error);
    } catch (std::exception &e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

bool ConnectionHandler::getLine(std::string& line, bool& terminate) {
    char ch;
    vector<char> vecBytes;
    char opcodeArr[2];
    char optionalMessageBytes[2];
    short opcode = 0;
    short optionalMessage = 0;
    try {
        do {
            if (!getBytes(&ch, 1)) {
                return false;
            }
            vecBytes.push_back(ch);

            if (vecBytes.size() == 4) {
                opcodeArr[0] = vecBytes[0];
                opcodeArr[1] = vecBytes[1];
                optionalMessageBytes[0] = vecBytes[2];
                optionalMessageBytes[1] = vecBytes[3];
                opcode = bytesToShort(opcodeArr);
                optionalMessage = bytesToShort(optionalMessageBytes);
                if(opcode == 13){
                    break;
                }
            }
        } while ('\0' != ch || vecBytes.size()<=4);
    } catch (std::exception &e) {
        std::cerr << "recv failed2 (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    if(opcode != 12 && opcode != 13){
        return false;
    }
    if (opcode == 12) { //ACK reply message
         line = "ACK " + std::to_string(optionalMessage);

        //ACK message after logout
        if (optionalMessage == 4) {
            terminate = true;
        }
        if(vecBytes.size()>5){
            line += "\n";
            for (unsigned i = 4; i < vecBytes.size(); i++) {
                line.append(1, vecBytes[i]);//takes the <optional> part of the ack message and turns it into a string
            }
        }

    } else if (opcode == 13) {
        line = "ERROR " + std::to_string(optionalMessage);
    }
    return true;
}

bool ConnectionHandler::sendLine(std::string &line) {
    unsigned int opcodeIndex = line.find(" ");
    string opcodeString = line;

    //if opcodeIndex found a space, takes the opcode from the rest of the sentence
    if (opcodeIndex != string::npos) {
        opcodeString = line.substr(0, opcodeIndex);
        line = line.substr(opcodeIndex + 1, line.length() - opcodeIndex);
    }
    short opcode = stringToOpcode(opcodeString);
    if (opcode == 0) {
        return false;
    }


    //string messages
    if ((opcode == 1) | (opcode == 2) | (opcode == 3) | (opcode == 8)) {
        int lineLength = line.length()+1;
        std::replace(line.begin(), line.end(), ' ', '\0');
        line += '\0';

        const char* bytesString = line.c_str();
        unsigned mLength = 2+lineLength;
        char mBytes[mLength];
        shortToBytes(opcode,mBytes);
        for(unsigned int i=2;i<mLength;i++){
              mBytes[i]=bytesString[i-2];
        }
        bool resultBytes=sendBytes(mBytes,mLength);
        if (resultBytes == false) {
                return false;
       }

        //course messages
    } else if ((opcode == 5) | (opcode == 6) | (opcode == 7) | (opcode == 9) | (opcode == 10)) {
        short courseShort = short(atoi(line.c_str()));
        char bytesArray[2];
        shortToBytes(courseShort, bytesArray);
        int mLength = 4;
        char mBytes[mLength];
        shortToBytes(opcode,mBytes);
        mBytes[2]=bytesArray[0];
        mBytes[3]=bytesArray[1];
        bool resultBytes = sendBytes(mBytes, mLength);
        if (resultBytes == false) {
            return false;
        }
    }
    else if((opcode == 11) | (opcode == 4)){
        char opcodeChar[2];
        shortToBytes(opcode,opcodeChar);
        bool resultBytes=sendBytes(opcodeChar,2);
        if (resultBytes == false) {
             return false;
        }
    }
    return true;
}


// Close down the connection properly.
void ConnectionHandler::close() {
    try {
        socket_.close();
    } catch (...) {
        std::cout << "closing failed: connection already closed" << std::endl;
    }
}

short ConnectionHandler::stringToOpcode(const std::string &opcode) {
    if (opcode == "ADMINREG") return 1;
    if (opcode == "STUDENTREG") return 2;
    if (opcode == "LOGIN") return 3;
    if (opcode == "LOGOUT") return 4;
    if (opcode == "COURSEREG") return 5;
    if (opcode == "KDAMCHECK") return 6;
    if (opcode == "COURSESTAT") return 7;
    if (opcode == "STUDENTSTAT") return 8;
    if (opcode == "ISREGISTERED") return 9;
    if (opcode == "UNREGISTER") return 10;
    if (opcode == "MYCOURSES") return 11;
    return 0;
}

short ConnectionHandler::bytesToShort(const char* bytesArr)
{
    short result = (short)((bytesArr[0] & 0xff) << 8);
    result += (short)(bytesArr[1] & 0xff);
    return result;
}

void ConnectionHandler::shortToBytes(short num, char* bytesArr)
{
    bytesArr[0] = ((num >> 8) & 0xFF);
    bytesArr[1] = (num & 0xFF);
}


