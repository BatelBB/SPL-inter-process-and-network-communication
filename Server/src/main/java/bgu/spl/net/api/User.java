package bgu.spl.net.api;

import java.util.ArrayList;
import java.util.List;

public class User {
    String UserName;
    String password;
    boolean loggedIn;


    public User(String Name, String Pass, boolean loggedIn) {
        this.UserName = Name;
        this.password = Pass;
        this.loggedIn = loggedIn;

    }

    public String getUserName() {
        return this.UserName;
    }

    public String getUserPassword() {
        return this.password;
    }

    public void setLoggedIn(boolean status){
        this.loggedIn = status;
    }

    public boolean getLoggedIn(){
        return this.loggedIn;
    }
}
