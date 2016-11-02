/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientesvm;

import RMI.IReplicacion;
import clases.Archivo;
import clases.Proyecto;
import clases.Servidor;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
 * @author ASUS
 */
public class ClienteSVM {
    
    private static IReplicacion replicacion;
    private static String host;
    
    public static int menu(){
        int opc = 0;
        System.out.println("1. Crear Proyecto");
        System.out.println("2. Asociar archivo a proyecto");
        System.out.println("3. Consultar Proyecto");
        System.out.println("4. Checkout");
        System.out.println("5. Commit");
        System.out.println("6. Salir");
        System.out.print("Opcion: ");
        Scanner sc = new Scanner(System.in);
        opc = sc.nextInt();
        return opc;
    }
    
    public static void init() throws Exception{
        host = Persistencia.leerHost("host.txt");
        Registry remote = LocateRegistry.getRegistry(host,1099);
        replicacion = (IReplicacion)remote.lookup("rmi://" + host +"/Replicacion");
    }
    
    public void listaServidores() throws Exception{
        System.out.println("Servidores disponibles para almacenar su proyecto");
        List<Servidor> list = new ArrayList<>();
        int serv = 0;
        list = replicacion.getServidores();
        for (int i = 0; i < list.size();i++) {
            System.out.println("S"+i);
        }
        System.out.print("Digite el numero del servidor: ");
        Scanner sc = new Scanner(System.in);
        serv = sc.nextInt();
        Registry remote = LocateRegistry.getRegistry(list.get(serv).getIp(),1099);
        replicacion = (IReplicacion)remote.lookup("rmi://"+list.get(serv).getIp()+"/Replicacion");
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try{
            // escribir la ip del servidor.
            
            int opc = -1;
            Proyecto p;
            Archivo file = new Archivo();
            Scanner sc = new Scanner(System.in);
            String nombreProyecto;
            opc = menu();
            while(opc != 6){
                switch (opc){
                    case 1:
                        System.out.print("Escriba el nombre del proyecto: ");  
                        p = new Proyecto();
                        p.setNombre(sc.next());
                        p.setPropietario(System.getProperty("user.name"));
                        p.setFechaCreacion(new Date().toString());
                        replicacion.agregarProyecto(p);
                        break;
                    case 2:
                        System.out.print("Escriba el nombre del proyecto al cual pertenece el archivo: ");
                        nombreProyecto = sc.next();
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
                        replicacion.asociarArchivo(file, nombreProyecto);
                            // asociar archivo al proyecto
                            /*Registry R = LocateRegistry.getRegistry("192.168.43.42",1099);
                            IManejador manejador = (IManejador)R.lookup("rmi://192.168.43.42/Manejador");
                            manejador.prueba(file);*/
                        break;
                    default:
                        break;
                }
                opc = menu();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
