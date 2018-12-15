package User;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.io.IOException;

public class UserWorkerUDP extends Thread{
    
    DatagramSocket ds;
    DatagramPacket pkt;
    
    public UserWorkerUDP(DatagramSocket ds, DatagramPacket pkt){
            this.ds = ds;
            this.pkt = pkt;  
    }
    
    @Override
    public void run(){
        try{
            while (true){
                System.out.println("entramos");
                ds.receive(pkt);
                System.out.println("Recibido");
                String msg = new String(pkt.getData(), 0, pkt.getLength());
                System.out.println("Pillado");
                System.out.println(msg);
                System.out.println("un loop");
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        
    }
    
}
