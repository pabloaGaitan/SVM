/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RMI;

import clases.Archivo;
import clases.Proyecto;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author HP
 */
public class Manejador extends UnicastRemoteObject implements IManejador {
    
    private int contador;
    private Map<Integer,Servidor> servidores;
    private int k;
    
    public Manejador(int k) throws RemoteException, MalformedURLException{
        super();
        contador = 0;
        servidores = new HashMap<>();
        this.k = k;
    }
    
    public List<Integer> registrar(Servidor servidor) throws RemoteException{
        List<Integer> l = new ArrayList<>();
        Integer co = contador++;
        servidores.put(co,servidor);
        l.add(co);
        l.add(k);
        System.out.println(servidor.getIp());
        return l;
    }
    
    /*public void actualizar(Servidor servidor, int id) throws RemoteException{
        for (Servidor s : servidores) {
            if(s.equals(servidor.getIp())){
                s.setReplicas(servidor.getReplicas());
                break;
            }
        }
    }*/
    
    public Map<Integer,Servidor> getServidores()throws RemoteException{
        return this.servidores;
    }
    
    // solo se llama desde el master
    public void actualizar(Map<Integer,Servidor> servidores) throws RemoteException{
        
    }
    
    // Replicas a un servidor
    public void agregarArchivo(Archivo archivo ,int id) throws RemoteException{
        servidores.get(id).getReplicas().add(archivo);
        System.out.println("Se agrego archivo");
    }
    
    public void agregarProyecto(Proyecto proyecto,int id) throws RemoteException{
        servidores.get(id).getProyectos().add(proyecto);
        System.out.println("Se agrego proyecto "+proyecto.getNombre()+" "+id);
        
    }
    
    public boolean asociarArchivo(Archivo file, String proyectoName, int id) throws Exception{
        Proyecto p = buscarProyecto(proyectoName,id);
        if(proyectoName != null){
            p.getArchivos().add(file);
            replicar(id,file);
            return true;
        }
        return false;
    }
    
    public Proyecto buscarProyecto(String nombre,int id){
        for (Proyecto proyecto : servidores.get(id).getProyectos()) {
            if(proyecto.getNombre().equalsIgnoreCase(nombre))
                return proyecto;
        }
        return null;
    }
    
    public void replicar(int id,Archivo archivo) throws Exception{
        int menor = 1000;
        Servidor s = null;
        Set<Integer> set = servidores.keySet();
        for (int i = 0 ; i< k;i++) {
            for (Integer in : set) {
                if(servidores.get(in).getReplicas().size()<menor && !servidores.get(id).getIp().equalsIgnoreCase(servidores.get(in).getIp())){
                    menor = servidores.get(in).getReplicas().size();
                    s = servidores.get(in);
                }
            }
        }
        agregarArchivo(archivo,buscarID(s.getIp()));
    }
    
    public int buscarID(String ip){
        int ret = 0;
        Set<Integer> set = servidores.keySet();
        for (Integer i:set) {
            if(servidores.get(i).getIp().equals(ip)){
                return ret;
            }
            ret++;
        }
        return -1;
    }
    
    public Archivo checkout(String nombrePro, String nombreArch,int id){
        for (Archivo a : servidores.get(id).getReplicas()) {
            if(a.getNombre().equalsIgnoreCase(nombreArch)){
                a.setDespliegue(true);
                return a;
            }
        }
        for (Proyecto p : servidores.get(id).getProyectos()) {
            if(p.getNombre().equalsIgnoreCase(nombrePro)){
                for(Archivo ar : p.getArchivos()){
                    if(ar.getNombre().equalsIgnoreCase(nombreArch)){
                        ar.setDespliegue(true);
                        return ar;
                    }
                }
            }
        }
        return null;
    }
    
}
