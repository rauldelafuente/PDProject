package User;

import java.util.Scanner;

import Res.LogIn;
import Res.NewUser;
import Server.ServerInterface;

import java.net.*;
import java.io.*;

import static java.lang.System.exit;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class User extends UnicastRemoteObject implements UserInterface {
    
    public User () throws java.rmi.RemoteException {}

    private static Scanner sc = new Scanner(System.in);
    private static Socket s = null;
    private static ObjectInputStream in = null;
    private static ObjectOutputStream out = null;
    private static String user = null;
    private static String msg = null;
    private static boolean finish = false;

    public static boolean logIn() {
        //ask for user and password to the user and send it to the server 
        try {
            System.out.println("Eneter your username:");
            user = sc.next();
            System.out.println("Enter your password:");
            String password = sc.next();

            LogIn u1 = new LogIn(user, password);
            out.writeObject(u1);
            out.flush();
            Object o = in.readObject();

            return (boolean) o;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean signUp() {
        //ask to the user for all the data and send it to the server so sign up
        try {
            System.out.println("Eneter your name:");
            sc.nextLine();
            String name = sc.nextLine();
            System.out.println("Enter your username:");
            user = sc.next();
            System.out.println("Eneter your password:");
            String password = sc.next();
            System.out.println("Enter your UDP port");
            int udp = sc.nextInt();
            System.out.println("Enter your TCP port");
            int tcp = sc.nextInt();
            InetAddress address;

            address = InetAddress.getLocalHost();

            String ip = address.getHostAddress();

            NewUser nu = new NewUser(name, user, password, udp, tcp, ip);
            out.writeObject(nu);
            out.flush();
            Object o = in.readObject();
            return (boolean) o;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;

    }

    public static void main(String[] args) throws NotBoundException {
        try {
            InetAddress addr = InetAddress.getByName(args[0]);
            int port = Integer.parseInt(args[1]);
            //Set up the Socket and the serialized objects
            s = new Socket(addr, port);
            in = new ObjectInputStream(s.getInputStream());
            out = new ObjectOutputStream(s.getOutputStream());
            int choose;
            boolean accept = false;
            
            String path = "rmi://"+args[0]+"/ServerChat";
            
            System.out.println("Hello user, what do you want to do?");

            while (!accept) {
                //Choose if Log in or Sing up
                System.out.println("1 - Log In");
                System.out.println("2 - Sign Up");
                choose = sc.nextInt();
                if (choose == 1) {
                    //Log in in the server
                    accept = logIn();
                    if (accept) {
                        System.out.println("Welcome to the Server " + user);
                    } else {
                        System.out.println("User or password incorrect");
                    }
                }
                if (choose == 2) {
                    //Sign up in the server database
                    accept = signUp();
                    if (accept) {
                        System.out.println("Welcome to the Server " + user);
                    } else {
                        System.out.println("Error inserting into database");
                    }
                }
            }

            //Remote service = LocateRegistry.createRegistry(Registry.REGISTRY_PORT).lookup(path);
            //service = (ServerInterface) service;
            
            UserWorkerTCP workerTCP = new UserWorkerTCP(in);
            workerTCP.start();
            
            System.out.println("Enter your UDP port");
            int udp2 = sc.nextInt();
            
            DatagramSocket ds = new DatagramSocket(udp2);
            DatagramPacket pkg = new DatagramPacket(new byte [255], 255);
            
            UserWorkerUDP workerUDP = new UserWorkerUDP(ds, pkg);
            workerUDP.start();
            
            while (true) {
                //loop for write and send messagges
                msg = sc.nextLine();
                out.writeObject(msg);
                if ("exit".equals(msg)) {
                    exit(0);
                }
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void notifyNewUsers(String name) throws RemoteException {
        System.out.println("--- User "+ name +" join the chat ---");
    }

    @Override
    public void notifyExitUsers(String name) throws RemoteException {
        System.out.println("--- User "+ name +" has left the chat ---");
    }

}