package bgu.spl.net.api;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class MessageEncoderDecoderImpl implements MessageEncoderDecoder<String> {
    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private int len = 0;
    private String typeOfMessage;

    /**
     * add the next byte to the decoding process
     *
     * @param nextByte the next byte to consider for the currently decoded
     *                 message
     * @return a message if this byte completes one or null if it doesnt.
     */
    @Override
    public String decodeNextByte(byte nextByte) {
        if (len >= 2) {
            short opcode = bytesToShort(new byte[]{bytes[0], bytes[1]});
            if (opcode == 1 || opcode == 2 || opcode == 3) {
                int zeroByteCounter = 0;
                for (int i = 0; i < len && zeroByteCounter < 2; i++)
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
                if (len == 5) {
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
                }
                return popString();

            } else if (opcode == 4 || opcode == 11) {
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
                for (int i = 0; i < len; i++) {
                    if (bytes[i] == '\0') // termination condition
                        typeOfMessage = "STUDENTSTAT";
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
        String result = typeOfMessage + " " + new String(bytes, 2, len, StandardCharsets.UTF_8);
        len = 0;
        return result;
    }
}
