package bgu.spl.net.srv;

import java.util.ArrayList;
import java.util.List;

public class User {
    String UserName;
    int password;
    boolean loggedIn;


    public User(String Name, int Pass, boolean loggedIn) {
        this.UserName = Name;
        this.password = Pass;
        this.loggedIn = loggedIn;

    }

    public String getUserName() {
        return this.UserName;
    }

    public int getUserPassword() {
        return this.password;
    }

    public void setLoggedIn(boolean status){
        this.loggedIn = status;
    }

    public boolean getLoggedIn(){
        return this.loggedIn;
    }


//    public boolean PasswordCheck(int Pass) {
//        return password == Pass;
//    }
}
