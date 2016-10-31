/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RMI;

import clases.Archivo;
import clases.Servidor;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

/**
 *
 * @author ASUS
 */
public class Replicacion extends UnicastRemoteObject implements IReplicacion{
    
    public Replicacion() throws RemoteException,MalformedURLException{
        super();
    }
    
    public void replicar(Archivo file) throws RemoteException{
        // crear file
    }
}
