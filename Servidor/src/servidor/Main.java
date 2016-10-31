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

/**
 *
 * @author HP
 */
public class Main {
    
    private static List<Proyecto> proyectos = new ArrayList<>();
    private static List<Servidor> servidores = new ArrayList<>();
    private static Servidor server;
    private static Registry remote;
    private static Registry rServers;
    private static IManejador manejador;
    private static IReplicacion replicacion;
    
    public static void init(String host) throws Exception{
        rServers = LocateRegistry.createRegistry(1099);
        rServers.rebind("rmi://"+InetAddress.getLocalHost().getHostAddress()+"/Replicacion", new Replicacion());
        remote = LocateRegistry.getRegistry(host,1099);
        manejador = (IManejador)remote.lookup("rmi://"+host+"/Manejador");
    }
    
    public static int menu(){
        int opc = 0;
        System.out.println("1. Crear Proyecto");
        System.out.println("2. Asociar archivo a proyecto");
        System.out.println("3. Consultar Proyecto");
        System.out.println("4. Checkout");
        System.out.println("5. Commit");
        System.out.print("Opcion: ");
        Scanner sc = new Scanner(System.in);
        opc = sc.nextInt();
        return opc;
    }
    
    public static void registro() throws Exception{
        servidores = manejador.registrar(server);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try{
            init("192.168.43.42");
            server = new Servidor();
            server.setIp(InetAddress.getLocalHost().getHostAddress());
            registro(); // en un archivo
            System.out.print("Digite el numero de replicas de archivos: ");
            Scanner sc = new Scanner(System.in);
            int k = sc.nextInt(),opc = -1;
            Proyecto p = new Proyecto();
            Archivo file = new Archivo();
            while(opc != 6){
                opc = menu();
                switch (opc){
                    case 1:
                        System.out.print("Escriba el nombre del proyecto: ");  
                        p = new Proyecto();
                        p.setNombre(sc.next());
                        p.setPropietario(System.getProperty("user.name"));
                        p.setFechaCreacion(new Date().toString());
                        proyectos.add(p);
                        break;
                    case 2:
                        System.out.println("Escriba el nombre del proyecto al cual pertenece el archivo: ");
                        p = buscarProyecto(sc.next());
                        if(p == null)
                            System.out.println("Proyecto no existe");
                        else{
                            System.out.print("Escriba el nombre del archivo: ");
                            file.setTimeStamp((new Timestamp(((Date)Calendar.getInstance().getTime()).getTime())));
                            file.setNombre(sc.next());
                            File fi = new File(file.getNombre());
                            BufferedInputStream bf = new BufferedInputStream(new FileInputStream(file.getNombre()));
                            byte buffer[] = new byte[(int)fi.length()];
                            bf.read(buffer,0,buffer.length);
                            bf.close();
                            file.setFile(buffer);
                            System.out.print("Locacion: ");
                            file.setLocacion(sc.next());
                            p.getArchivos().add(file);
                            /*Registry R = LocateRegistry.getRegistry("192.168.43.42",1099);
                            IManejador manejador = (IManejador)R.lookup("rmi://192.168.43.42/Manejador");
                            manejador.prueba(file);*/
                        }
                        break;
                    default:
                        break;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public static Proyecto buscarProyecto(String nombre){
        for (Proyecto proyecto : proyectos) {
            if(proyecto.getNombre().equalsIgnoreCase(nombre))
                return proyecto;
        }
        return null;
    }
    
    public static void replicar(Archivo file) throws Exception{
        int menor = 1000;
        Servidor s = null;
        for (Servidor server : servidores) {
            if(server.getReplicas().size() < menor){
                menor = server.getReplicas().size();
                s = server;
            }
        }
        Registry r = LocateRegistry.getRegistry(s.getIp(),1099);
        replicacion = (IReplicacion)r.lookup("rmi://"+s.getIp()+"/Replicacion");
    }
}
