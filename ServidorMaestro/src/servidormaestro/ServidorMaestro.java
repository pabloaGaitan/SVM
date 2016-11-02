/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidormaestro;

import RMI.IManejador;
import RMI.Manejador;
import clases.Servidor;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author HP
 */
public class ServidorMaestro {
    
    private static IManejador manejador;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)  {
        
        try {
            System.setProperty("java.security.policy","policy.all");
            Registry R = LocateRegistry.createRegistry(1099);
            manejador = new Manejador();
            R.rebind("rmi://"+InetAddress.getLocalHost().getHostAddress() +"/Manejador", manejador);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    
    
}
