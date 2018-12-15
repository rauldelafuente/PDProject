package User;

import java.io.IOException;
import java.io.ObjectInputStream;

public class UserWorkerTCP extends Thread {

    ObjectInputStream in;

    public UserWorkerTCP(ObjectInputStream in) {
        this.in = in;
    }

    public void run() {
        //Thread to receive messages and print them
        String print;
        try {
            while (true) {
                print = (String) in.readObject();
                System.out.println(print);
            }
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

}
