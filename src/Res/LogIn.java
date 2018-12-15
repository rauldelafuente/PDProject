package Res;

import java.io.Serializable;

public class LogIn implements Serializable{
    private String user;
    private String password;

    public LogIn(String user, String password){
        this.user = user;
        this.password = password;
    }

    public String getUser(){
        return user;
    }

    public String getPassword(){
        return password;
    }

}