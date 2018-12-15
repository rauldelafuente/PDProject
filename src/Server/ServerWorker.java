package Server;

import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import Res.LogIn;
import Res.NewUser;
import Res.Client;
import User.User;

import java.io.*;

public class ServerWorker extends Thread {

    // JDBC driver name and database URL
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://192.168.1.68/prueba";
    // Database credentials
    private static final String USER = "user";
    private static final String PASS = "root";

    private static Connection conn = null;
    private static Statement stmt = null;

    private Socket clietnSocket;
    private Client client;
    private static ArrayList<Client> list = new ArrayList<Client>();
    private static ArrayList<User> listeners = new ArrayList<User>();

    public ServerWorker(Socket clientSocket, Client client) {

        this.clietnSocket = clientSocket;
        this.client = client;

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static boolean logIn(String username, String password) {
        try {
            String res = null;
            stmt = conn.createStatement();
            String sql = "SELECT * FROM users WHERE user='" + username + "';";
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                res = rs.getString("password");
            }
            if (res.equals(password)) {
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean SignUp(String name, String username, String password, int portTCP, int portUDP, String ip) {
        try {
            stmt = conn.createStatement();
            String sql = "SELECT * FROM users WHERE user='" + username + "';";
            ResultSet rs = stmt.executeQuery(sql);
            if (!rs.next()) {
                sql = "INSERT INTO users (name, user, password, portUDP, portTCP, ipAddress, state) VALUES ('" + name + "', '" + username + "', '" + password + "', " + portUDP + ", " + portTCP + ", '" + ip + "', "+1+");";
                stmt.executeUpdate(sql);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void SendUDP(String msg) {
        try {
            System.out.println("Entrandooooo");
            stmt = conn.createStatement();
            String sql = "SELECT * FROM users WHERE state=" + 1 + ";";
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                if(!Server.isInList(rs.getString("user"))){
                    DatagramSocket ds = new DatagramSocket();
                    byte [] data = msg.getBytes();
                    System.out.println(rs.getString("user")+" "+rs.getString("ipAddress") +" "+rs.getInt("portUDP"));
                    DatagramPacket pkt = new DatagramPacket(data, data.length, InetAddress.getByName(rs.getString("ipAddress")), rs.getInt("portUDP"));
                    ds.send(pkt);
                    ds.close();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (SocketException e){
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void StateOn(Client client) {
        try {
            stmt = conn.createStatement();
            String sql = "update users set state=" + 1 + " where user='" + client.getUsername() + "';";
            stmt.executeUpdate(sql);
            //Here maybe we should put something for the callback
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void StateOff(Client client) {
        try {
            stmt = conn.createStatement();
            String sql = "update users set state=" + 0 + " where user='" + client.getUsername() + "';";
            stmt.executeUpdate(sql);
            //Here maybe we should put something for the callback
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void run() {

        doRequest(clietnSocket, client);

    }

    private void doRequest(Socket s, Client client) {
        try {
            ObjectOutputStream out = client.getObjectoutputStream();
            ObjectInputStream in = client.getObjectInputStream();
            list = Server.getList();
            while (true) {
                Object o = in.readObject();
                if (o instanceof String) {
                    String line = (String) o;
                    if ("exit".equals(line)) {
                        StateOff(client);
                        Server.notifyExitUser(client.getUsername());
                        Server.removeItemList(client.getUsername());
                        break;
                    }
                    if ("".equals(line)) {
                    } else {
                        String msg = client.getUsername() + ": " + line;
                        for (int i = 0; i < list.size(); i++) {
                            if (!(list.get(i).getUsername()).equals(client.getUsername())) {
                                (list.get(i).getObjectoutputStream()).writeObject(msg);
                                out.flush();
                            }
                        }
                        SendUDP(msg);
                    }
                }

                if (o instanceof LogIn) {
                    LogIn log = (LogIn) o;
                    boolean accept = logIn(log.getUser(), log.getPassword());
                    if (accept) {
                        client.setUsername(log.getUser());
                        StateOn(client);
                        Server.addList(client);
                        Server.notifyNewUser(client.getUsername());
                    }
                    out.writeObject(accept);
                }

                if (o instanceof NewUser) {
                    NewUser newU = (NewUser) o;
                    boolean accept = SignUp(newU.getName(), newU.getUser(), newU.getPassword(), newU.getPortTCP(), newU.getPortUDP(), newU.getIP());
                    if (accept) {
                        client.setUsername(newU.getUser());
                        StateOn(client);
                        Server.addList(client);
                        Server.notifyNewUser(client.getUsername());
                    }
                    out.writeObject(accept);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

}
