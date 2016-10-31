/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RMI;

import clases.Servidor;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author HP
 */
public interface IManejador extends Remote{
    
    public void registrar(Servidor servidor) throws RemoteException;
    public void actualizar(Servidor servidor) throws RemoteException;
    
}