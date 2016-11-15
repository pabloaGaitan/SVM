/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidormaestro;

import RMI.IManejador;
import RMI.IReplicacion;
import clases.Servidor;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sala-a
 */
public class ControlThread extends Thread implements Runnable{
    
    private int id;
    
    /**
     * Este hilo se encarga de hacer ping a los servidores cada minuto,
     * esto con el fin de saber si están activos o no.
     */
    public void run(){
        IManejador manejador = null;
        try{
            long currentTime = 0;
            long startTime = System.nanoTime();
            Registry R = LocateRegistry.getRegistry(InetAddress.getLocalHost().getHostAddress(),1099);
            manejador = (IManejador)R.lookup("rmi://"+InetAddress.getLocalHost().getHostAddress() +"/Manejador");
            long tiempoTotal = 1;
            while(true){
                currentTime = (System.nanoTime() - startTime)/1000000000;
                if(currentTime%60 > 56 && currentTime%60 < 59){
                    Map<Integer,Servidor> m = manejador.getServidores();
                    Set<Integer> s = m.keySet();
                    for (Integer i : s) {
                        try{
                            Registry l = LocateRegistry.getRegistry(m.get(i).getIp(),1099);
                            IReplicacion r = (IReplicacion)l.lookup("rmi://"+ m.get(i).getIp() + "/Replicacion");
                            r.ping();
                        }catch(ConnectException ex){
                            System.out.println("delete "+i);
                            manejador.delete(i);
                        }
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
       
    }
}
