package bgu.spl.net.srv;

import java.util.ArrayList;
import java.util.List;

public class User {
    String UserName;
    int password;


    public User(String Name, int Pass) {
        UserName = Name;
        password = Pass;

    }

    public String getUserName() {
        return UserName;
    }

    public boolean PasswordCheck(int Pass) {
        return password == Pass;
    }
}
