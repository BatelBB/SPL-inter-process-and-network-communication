package bgu.spl.net.api;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class MessageEncoderDecoderImpl implements MessageEncoderDecoder<String> {
    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private int len = 0;
    private String typeOfMassag;

    /**
     * add the next byte to the decoding process
     *
     * @param nextByte the next byte to consider for the currently decoded
     *                 message
     * @return a message if this byte completes one or null if it doesnt.
     */
    @Override
    public String decodeNextByte(byte nextByte) {
        if(len>=2){ //check op code to determine pop condition
            short opCode = bytesToShort(new byte[]{bytes[0], bytes[1]}); //get op code
            if(opCode == 1 || opCode == 2 || opCode == 3) {
                int zeroByte = 0;
                for (int i = 0; i < len && zeroByte < 2; i++)
                    if (bytes[i] == '\0')
                        zeroByte++;
                if (zeroByte == 2) { // termination condition
                    switch (opCode) {
                        case 1://ADMINREG
                            typeOfMassag = "ADMINREG";
                            break;
                        case 2://STUDENTREG
                            typeOfMassag = "STUDENTREG";
                            break;
                        case 3: //LOGIN
                            typeOfMassag = "LOGIN";
                            break;
                    }
                    return popString();
                }
            }else if(opCode == 5 || opCode == 6 || opCode == 7 || opCode == 9 || opCode == 10){
                if(len == 5){ // termination condition
                    switch (opCode) {
                        case 5: //COURSEREG
                            typeOfMassag = "COURSEREG";
                            break;
                        case 6: // KDAMCHECK
                            typeOfMassag = "KDAMCHECK";
                            break;
                        case 7: //COURSESTAT
                            typeOfMassag = "COURSESTAT";
                            break;
                        case 9: //ISREGISTERED
                            typeOfMassag = "ISREGISTERED";
                            break;
                        case 10: //UNREGISTER
                            typeOfMassag = "UNREGISTER";
                            break;
                    }
                }
                    return popString();

            }else if(opCode == 4 || opCode == 11) {
                switch (opCode) {
                    case 4: //LOGOUT
                        typeOfMassag = "LOGOUT";
                        break;
                    case 11: //MYCOURSES
                        typeOfMassag = "MYCOURSES";
                        break;
                }
                return popString();

            }else if(opCode == 8){ //STUDENTSTAT
                for(int i=0;i<len;i++) {
                    if (bytes[i] == '\0') // termination condition
                        typeOfMassag = "STUDENTSTAT";
                        return popString();
                }
            }
        }
        pushByte(nextByte);
        return null; //not a line yet
    }

    private short bytesToShort(byte[] bytes) {
        short shortArray = 0;

        for (int index = 0; index < bytes.length; index++)
            shortArray = (short) (shortArray + (short) bytes[index]);
        return shortArray;
    }


    /**
     * encodes the given message to bytes array
     *
     * @param message the message to encode
     * @return the encoded bytes
     */
    @Override
    public byte[] encode(String message) {
        return new byte[0];
    }

    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }
        bytes[len++] = nextByte;
    }
    private String popString() {
        String result = typeOfMassag + " " + new String(bytes, 2, len, StandardCharsets.UTF_8);
        len = 0;
        return result;
    }
}
