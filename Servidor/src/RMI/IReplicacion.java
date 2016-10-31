/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RMI;

import clases.Archivo;
import clases.Servidor;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 *
 * @author ASUS
 */
public interface IReplicacion extends Remote{
    
    public void replicar(Archivo file) throws RemoteException;
    
}
