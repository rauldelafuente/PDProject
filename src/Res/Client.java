package Res;

import java.net.*;
import java.io.*;

public class Client implements Serializable {

    String username;
    Socket s;
    ObjectInputStream in;
    ObjectOutputStream out;

    public Client(Socket s, ObjectInputStream in, ObjectOutputStream out) {
        this.s = s;
        this.in = in;
        this.out = out;
    }

    public Client(String username, Socket s, ObjectInputStream in, ObjectOutputStream out) {
        this.username = username;
        this.s = s;
        this.in = in;
        this.out = out;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public Socket getSocket() {
        return s;
    }

    public ObjectInputStream getObjectInputStream() {
        return in;
    }

    public ObjectOutputStream getObjectoutputStream() {
        return out;
    }
}
