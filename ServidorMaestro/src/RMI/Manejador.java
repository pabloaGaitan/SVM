/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RMI;

import clases.Archivo;
import clases.Servidor;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.nio.Buffer;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author HP
 */
public class Manejador extends UnicastRemoteObject implements IManejador {
    
    private List<Servidor> servidores;
    
    public Manejador() throws RemoteException, MalformedURLException{
        super();
        servidores = new ArrayList<>();
    }
    
    public List<Servidor> registrar(Servidor servidor) throws RemoteException{
        servidores.add(servidor);
        System.out.println(servidor.getIp());
        return servidores;
    }
    
    public void actualizar(Servidor servidor) throws RemoteException{
        for (Servidor s : servidores) {
            if(s.equals(servidor.getIp())){
                s.setReplicas(servidor.getReplicas());
                break;
            }
        }
    }
    
    public List<Servidor> getServidores()throws RemoteException{
        return this.servidores;
    }
}
