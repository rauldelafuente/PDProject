package Server;

import java.net.*;
import java.util.*;
import java.io.*;

import Res.Client;
import User.User;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.*;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server extends UnicastRemoteObject implements ServerInterface{

    static ServerSocket ss;
    static ObjectInputStream in;
    static ObjectOutputStream out;
    static Socket clientSocket;
    private static ArrayList<Client> list = new ArrayList<Client>();
    private static ArrayList<User> listeners = new ArrayList<User>();
    
    public Server() throws java.rmi.RemoteException{}

    public Server(String args[]) throws java.rmi.RemoteException{
        int listeningPort;

        ss = null;

        try {

            listeningPort = Integer.parseInt(args[0]);
            ss = new ServerSocket(listeningPort);

        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            ss = null;
        }
    }

    public final void processRequests() {
        if (ss == null) {
            return;
        }

        System.out.println("TCP Serialized Server started in port " + ss.getLocalPort() + " ...");

        while (true) {

            try {
                clientSocket = ss.accept();

                out = new ObjectOutputStream(clientSocket.getOutputStream());
                in = new ObjectInputStream(clientSocket.getInputStream());

                Client client = new Client(clientSocket, in, out);

                ServerWorker wroker = new ServerWorker(clientSocket, client);
                wroker.start();

            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    public static ArrayList<Client> getList() {
        return list;
    }

    public static void addList(Client client) {
        list.add(client);
    }

    public static void removeItemList(String user) {
        for (int i = 0; i < list.size(); i++) {
            if ((list.get(i).getUsername()).equals(user)) {
                list.remove(i);
            }
        }
    }
    
    public static boolean isInList(String user){
        for (int i = 0; i < list.size(); i++) {
            if ((list.get(i).getUsername()).equals(user)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void addListener(User user) throws java.rmi.RemoteException{
        listeners.add(user);
    }
    
    @Override
    public void removeListener(User user) throws java.rmi.RemoteException{
        listeners.remove(user);
    }
    
    @Override
    public String[] currentUsers() throws java.rmi.RemoteException{
        return null;
    }
    
    public static void notifyNewUser(String user){
        for (int i = 0; i<listeners.size(); i++){
            try {
                listeners.get(i).notifyNewUsers(user);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void notifyExitUser(String user){
        for (int i = 0; i<listeners.size(); i++){
            try {
                listeners.get(i).notifyExitUsers(user);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
    

    public static void main(String[] args) {
        Server server;
        
        if (args.length != 1) {
            System.out.println("Sintaxe: java Server listeningPort");
            return;
        }
        
        try {
            
            //String path = "rmi://192.168.1.68/ServerChat";
            //Server serverRMI = new Server();
            //LocateRegistry.createRegistry(Registry.REGISTRY_PORT).rebind(path, serverRMI);
            
            server = new Server(args);
            server.processRequests();
            
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
