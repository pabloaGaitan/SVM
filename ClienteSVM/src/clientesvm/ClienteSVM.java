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
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import javax.swing.JOptionPane;
import persistencia.Persistencia;

/**
 *
 * @author ASUS
 */
public class ClienteSVM {
    
    private static IReplicacion replicacion;
    private static String host;
    
    /**
     * Muestra el menu de opciones al usuario y además, retorna la opción
     * digitada por él.
     * @return 
     */
    public static int menu(){
        int opc = 0;
        System.out.println("1. Crear Proyecto");
        System.out.println("2. Asociar archivo a proyecto");
        System.out.println("3. Consultar Proyectos");
        System.out.println("4. Checkout");
        System.out.println("5. Commit");
        System.out.println("6. Salir");
        System.out.print("Opcion: ");
        Scanner sc = new Scanner(System.in);
        opc = sc.nextInt();
        return opc;
    }
    
    /**
     * Inicializa la conexión RMI con el servidor.
     * @throws Exception 
     */
    public static void init() throws Exception{
        host = Persistencia.leerHost("host.txt");
        Registry remote = LocateRegistry.getRegistry(host,1099);
        replicacion = (IReplicacion)remote.lookup("rmi://" + host +"/Replicacion");
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try{
            init();
            int opc = -1;
            Proyecto p;
            Archivo file = new Archivo();
            Scanner sc = new Scanner(System.in);
            String nombreProyecto;
            String continuar;
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
                        try{
                            File fi = new File(file.getNombre());
                            BufferedInputStream bf = new BufferedInputStream(new FileInputStream(file.getNombre()));
                            byte buffer[] = new byte[(int)fi.length()];
                            bf.read(buffer,0, (int) fi.length());
                            bf.close();
                            file.setFile(buffer);
                            replicacion.asociarArchivo(file, nombreProyecto,1);
                        }catch(Exception e){
                            System.out.println("Problemas con el archivo.");
                        }
                        break;
                    case 3:
                        Map<Integer, Servidor> servidores = replicacion.getServidores();
                        int i = 0;
                        Set<Integer> set = servidores.keySet();
                        for (Integer s : set) {
                            System.out.println("S"+(i++));
                            System.out.println("Proyectos: ");
                            for(Proyecto pr : servidores.get(s).getProyectos()){
                                System.out.println(" "+pr.getNombre() + ": ");
                                for(Archivo a : pr.getArchivos()){
                                    System.out.println("  "+a.getNombre());
                                }
                            }
                            System.out.println("Replicas: ");
                            for(String a : getReplicas(servidores.get(s))){
                                System.out.println(" "+a);
                            }
                        }
                        break;
                    case 4:
                        System.out.print("Nombre del proyecto: ");
                        String namePro = sc.next();
                        System.out.print("Nombre del archivo: ");
                        String nameArch = sc.next();
                        if(replicacion.checkout(namePro,nameArch))
                            System.out.println("Se desplegó el archivo");
                        else
                            System.out.println("No se pudo desplegar el archivo, quizas este archivo no esta en este servidor");
                        break;
                    case 5:
                        System.out.print("Nombre del proyecto: ");
                        String nameProo = sc.next();
                        System.out.print("Nombre del archivo: ");
                        String arch = sc.next();
                        try{
                            File fil = new File(arch);
                            BufferedInputStream bfi = new BufferedInputStream(new FileInputStream(arch));
                            byte bufferr[] = new byte[(int)fil.length()];
                            continuar = "s";
                            bfi.read(bufferr,0, (int) fil.length());
                            while(continuar.equalsIgnoreCase("s")){
                                if(replicacion.commit(arch, bufferr,nameProo)){
                                    System.out.println("se hizo commit");
                                    continuar = "n";
                                }else{
                                    System.out.println("No se pudo hacer");
                                    System.out.print("Intentar de nuevo? S/N: ");
                                    continuar = sc.next();
                                }
                            }
                            bfi.close();
                        }catch(Exception e){
                            System.out.println("Problemas con el archivo.");
                        }
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
    
    /**
     * Obtiene las réplicas de un servidor dado.
     * @param s
     * @return 
     */
    public static List<String> getReplicas(Servidor s){
        List<String> list = new ArrayList<>();
        for(Archivo a : s.getReplicas()){
            if(!list.contains(a.getNombre()))
                list.add(a.getNombre());
        }
        return list;
    }
}
