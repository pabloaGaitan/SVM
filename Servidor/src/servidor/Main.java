/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;

import RMI.IManejador;
import clases.Archivo;
import clases.Proyecto;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author HP
 */
public class Main {
    
    private static List<Proyecto> proyectos;
    
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
    
    public static void registro(String host) throws Exception{
        IManejador manejador = (IManejador)Naming.lookup("rmi://" + host +":1099/Manejador");
        Servidor s =
        manejador.registrar(servidor);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.print("Digite el numero de replicas de archivos: ");
        Scanner sc = new Scanner(System.in);
        int k = sc.nextInt();
        int opc = -1;
        Proyecto p = new Proyecto();
        Archivo file = new Archivo();
        while(opc != 6){
            opc = menu();
            switch (opc){
                case 1:
                    System.out.print("Escriba el nombre del proyecto: ");
                    sc.next();          
                    p = new Proyecto();
                    p.setNombre(sc.next());
                    p.setPropietario(System.getProperty("user.name"));
                    p.setFechaCreacion(new Date().toString());
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
                        System.out.print("Locacion: ");
                        file.setLocacion(sc.next());
                    }
            }
        }
    }
    
    public static Proyecto buscarProyecto(String nombre){
        for (Proyecto proyecto : proyectos) {
            if(proyecto.getNombre().equalsIgnoreCase(nombre))
                return proyecto;
        }
        return null;
    }
    
}
