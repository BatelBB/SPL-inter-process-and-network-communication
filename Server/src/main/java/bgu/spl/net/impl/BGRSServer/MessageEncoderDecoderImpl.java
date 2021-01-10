package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.api.MessageEncoderDecoder;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessageEncoderDecoderImpl implements MessageEncoderDecoder<String> {
    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private int len = 0;
    private int lenEncode = 0;
    private String typeOfMessage;
    private short localOpcode;
    private byte[] ackOptionalMsg = new byte[1 << 10];

    /**
     * add the next byte to the decoding process
     *
     * @param nextByte the next byte to consider for the currently decoded
     *                 message
     * @return a message if this byte completes one or null if it doesnt.
     */
    @Override
    public String decodeNextByte(byte nextByte) {
        pushByte(nextByte);
        if (len >= 2) {
            short opcode = bytesToShort(new byte[]{bytes[0], bytes[1]});
            localOpcode = opcode;
            if (opcode == 1 || opcode == 2 || opcode == 3) {
                int zeroByteCounter = 0;
                for (int i = 2; i < len && zeroByteCounter < 2; i++)
                    if (bytes[i] == '\0')
                        zeroByteCounter++;
                if (zeroByteCounter == 2) {
                    switch (opcode) {
                        case 1://ADMINREG
                            typeOfMessage = "ADMINREG";
                            break;
                        case 2://STUDENTREG
                            typeOfMessage = "STUDENTREG";
                            break;
                        case 3: //LOGIN
                            typeOfMessage = "LOGIN";
                            break;
                    }
                    return popString();
                }
            } else if (opcode == 5 || opcode == 6 || opcode == 7 || opcode == 9 || opcode == 10) {
                if (len == 4) {
                    switch (opcode) {
                        case 5: //COURSEREG
                            typeOfMessage = "COURSEREG";
                            break;
                        case 6: // KDAMCHECK
                            typeOfMessage = "KDAMCHECK";
                            break;
                        case 7: //COURSESTAT
                            typeOfMessage = "COURSESTAT";
                            break;
                        case 9: //ISREGISTERED
                            typeOfMessage = "ISREGISTERED";
                            break;
                        case 10: //UNREGISTER
                            typeOfMessage = "UNREGISTER";
                            break;
                    }
                    return popString();
                }
            } else if (opcode == 4 || opcode == 11 ) {
                switch (opcode) {
                    case 4: //LOGOUT
                        typeOfMessage = "LOGOUT";
                        break;
                    case 11: //MYCOURSES
                        typeOfMessage = "MYCOURSES";
                        break;

                }
                return popString();
            } else if (opcode == 8) { //STUDENTSTAT
                for (int i = 2; i < len; i++) {
                    if (bytes[i] == '\0') { // termination condition
                        typeOfMessage = "STUDENTSTAT";
                        return popString();
                    }
                }
            }
        }
        return null; //not a line yet
    }

    private short bytesToShort(byte[] bytes) {
        short shortArray = 0;

        for (int index = 0; index < bytes.length; index++)
            shortArray = (short) (shortArray + (short) bytes[index]);
        return shortArray;
    }

    public byte[] shortToBytes(short num) {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte) ((num >> 8) & 0xFF);
        bytesArr[1] = (byte) (num & 0xFF);
        return bytesArr;
    }

    /**
     * encodes the given message to bytes array
     *
     * @param message the message to encode
     * @return the encoded bytes
     */
    @Override
    public byte[] encode(String message) {

        byte[] byteResultArr = null; //eventually will return this byte array
        byte[] opcodeByteArr = shortToBytes(localOpcode); //takes the opcode that was saved from the decoding process

        List<byte[]> list = new ArrayList<>();
        String[] splitmsg = message.split(" ");
        int numOfOptionalBytes = 0;
        if (splitmsg[0].equals("ACK")) {
            byte[] ackByte = shortToBytes((short)12);
            for (int i = 1; i < splitmsg.length; i++) {//gets the optional part of the ACK message
                list.add(splitmsg[i].getBytes());
            }
            int byteResultArrLen = opcodeByteArr.length + ackByte.length;
            if (splitmsg.length > 1) {
                lenEncode = 1;
                for (int i = 0; i < lenEncode && i<list.size(); i++) {
                    for (int j = 0; j < list.get(i).length; j++) {
                        pushEncodeByte(list.get(i)[j]);
                        numOfOptionalBytes++;
                    }
                    //list.remove(0);
                }
            }
            byteResultArr = new byte[byteResultArrLen+1+numOfOptionalBytes];
            System.arraycopy(ackByte, 0, byteResultArr, 0, 2);
            System.arraycopy(opcodeByteArr, 0, byteResultArr, 2, 2);
            if (splitmsg.length > 1)//checks if there is an optional message
                System.arraycopy(ackOptionalMsg, 1, byteResultArr, 4, numOfOptionalBytes);
            byteResultArr[byteResultArr.length-1] = '\0';
        } else {//ERROR message
            byte[] errorByte = shortToBytes((short) 13);
            byteResultArr = new byte[4];
            System.arraycopy(errorByte, 0, byteResultArr, 0, 2);
            System.arraycopy(opcodeByteArr, 0, byteResultArr, 2, 2);
        }

        return byteResultArr;
    }

    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }
        bytes[len++] = nextByte;
    }

    private void pushEncodeByte(byte nextByte) {
        if (lenEncode >= ackOptionalMsg.length) {
            ackOptionalMsg = Arrays.copyOf(ackOptionalMsg, lenEncode * 2);
        }
        ackOptionalMsg[lenEncode++] = nextByte;
    }

    private String popString() {
        String result = "";
        if (typeOfMessage.equals("ADMINREG") || typeOfMessage.equals("STUDENTREG") || typeOfMessage.equals("LOGIN") ||
                typeOfMessage.equals("STUDENTSTAT") || typeOfMessage.equals("LOGOUT") || typeOfMessage.equals("MYCOURSES")) {
            ArrayList<String> decodedMessage = new ArrayList<>();
            for (int i = 2, messageStart = 2; i < len; i++) {
                if (bytes[i] == '\0') {
                    decodedMessage.add(new String(bytes, messageStart, i - messageStart, StandardCharsets.UTF_8));
                    messageStart = i + 1;
                }
            }
            result = typeOfMessage + " " + decodedMessage;
        } else if (typeOfMessage.equals("COURSEREG") || typeOfMessage.equals("KDAMCHECK") || typeOfMessage.equals("COURSESTAT")
                || typeOfMessage.equals("ISREGISTERED") || typeOfMessage.equals("UNREGISTER")) {
            short courseNum = bytesToShort(new byte[]{bytes[2], bytes[3]});
            if(courseNum < 0)
                courseNum++;
            result = typeOfMessage + " " + courseNum;
        }
        len = 0;
        bytes = new byte[1 << 10];
        return result;
    }
}

