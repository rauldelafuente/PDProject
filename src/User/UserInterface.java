package User;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface UserInterface extends Remote{
    
    public void notifyNewUsers(String name) throws RemoteException; 
    public void notifyExitUsers(String name) throws RemoteException; 

}
