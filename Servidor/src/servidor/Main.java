/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;

import RMI.IManejador;
import RMI.IReplicacion;
import RMI.Replicacion;
import clases.Archivo;
import clases.Proyecto;
import clases.Servidor;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import persistencia.Persistencia;

/**
 *
 * @author HP
 */
public class Main {

    private static List<Proyecto> proyectos = new ArrayList<>();
    private static IReplicacion replicacion;
    private static Servidor server;
    private static Registry rServers;
    private static int id;
    
    public static void init() throws Exception{
        server = new Servidor();
        server.setIp(InetAddress.getLocalHost().getHostAddress());
        replicacion = new Replicacion(server,Persistencia.leerHost("host.txt"));
        replicacion.registro();
        rServers = LocateRegistry.createRegistry(1099);
        rServers.rebind("rmi://"+InetAddress.getLocalHost().getHostAddress()+"/Replicacion", replicacion);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try{
            init();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
