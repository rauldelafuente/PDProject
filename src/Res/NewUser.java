package Res;

import java.io.Serializable;

public class NewUser implements Serializable{
    private String name;
    private String user;
    private String password;
    private int portUDP;
    private int portTCP;
    private String ip;

    public NewUser(String name, String user, String password, int portUDP, int portTCP,  String ip){
        this.name = name;
        this.user = user;
        this.password = password;
        this.portUDP = portUDP;
        this.portTCP = portTCP;
        this.ip = ip;
    }

    public String getName(){
       return name; 
    }

    public String getUser(){
        return user;
    }

    public String getPassword(){
        return password;
    }

    public int getPortUDP(){
        return portUDP;
    }

    public int getPortTCP(){
        return portTCP;
    }

    public String getIP(){
        return ip;
    }

}