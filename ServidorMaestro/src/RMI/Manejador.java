/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RMI;

import clases.Servidor;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author HP
 */
public class Manejador extends UnicastRemoteObject implements IManejador {
    
    private List<Servidor> servidores;
    
    public Manejador() throws RemoteException, MalformedURLException{
        super();
        servidores = new ArrayList<>();
        Naming.rebind("rmi://localhost:1099/Manejador", this);
    }
    
    public void registrar(Servidor servidor) throws RemoteException{
        servidores.add(servidor);
    }
    
    public void actualizar(Servidor servidor) throws RemoteException{
        for (Servidor s : servidores) {
            if(s.equals(servidor.getIp())){
                s.setArchivos(servidor.getArchivos());
                break;
            }
        }
    }
}
