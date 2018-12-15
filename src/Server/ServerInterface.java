package Server;

import User.User;

public interface ServerInterface extends java.rmi.Remote{
    
    public void addListener(User listener) throws java.rmi.RemoteException;
    public void removeListener(User listener) throws java.rmi.RemoteException;
    public String[] currentUsers() throws java.rmi.RemoteException;
    
}
