/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientesvm;

import clases.Archivo;
import clases.Proyecto;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

/**
 *
 * @author ASUS
 */
public class ClienteSVM {
    
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
                        // creación de proyecto
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
