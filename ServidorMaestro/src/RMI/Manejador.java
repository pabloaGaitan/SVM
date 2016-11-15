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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Se encarga de la comunicación entre el servidor y el servidor maestro.
 * @author HP
 */
public class Manejador extends UnicastRemoteObject implements IManejador {
    
    private int contador;
    private Map<Integer,Servidor> servidores;
    private int k;
    
    /**
     * Constructor con la inicialización de los atributos
     * @param k
     * @throws RemoteException
     * @throws MalformedURLException 
     */
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
    
    public Map<Integer,Servidor> getServidores()throws RemoteException{
        return this.servidores;
    }
    
    // Replicas a un servidor
    public void agregarArchivo(Archivo archivo ,int id) throws RemoteException{
        servidores.get(id).getReplicas().add(archivo);
    }
    
    public void agregarProyecto(Proyecto proyecto,int id) throws RemoteException{
        servidores.get(id).getProyectos().add(proyecto);
        System.out.println("Se agrego proyecto "+proyecto.getNombre()+" "+id);
        
    }
    
    public boolean asociarArchivo(Archivo file, String proyectoName, int id,int op) throws Exception{
        Proyecto p = buscarProyecto(proyectoName,id);
        if(p != null){
            System.out.println(" --- " + op + " " + id);
            p.getArchivos().add(file);
            if(op == 1)
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
    
    /**
     * Replica el archivo a los demás servidores de forma balanceada.
     * @param id
     * @param archivo
     * @throws Exception 
     */
    public void replicar(int id,Archivo archivo) throws Exception{
        int menor = 1000;
        int s=-1;
        Set<Integer> set = servidores.keySet();
        List<Integer> servs = kMenores(id);
        for (Integer serv : servs) {
            agregarArchivo(archivo,serv);
        }
       
    }
    
    /**
     * Según el k, retorna los servidores cuya carga es menor para poder replicar
     * @param id
     * @return 
     */
    public List<Integer> kMenores(int id){
        List<Integer> ret = new ArrayList<>();
        int menor = 1000000;
        Set<Integer> set = servidores.keySet();
        for(int i = 0; i < k; i++){
            for (Integer in : set) {
                if(!servidores.get(id).getIp().equalsIgnoreCase(servidores.get(in).getIp()) 
                        && servidores.get(in).getReplicas().size() <= menor && !ret.contains(in)){
                    ret.add(in);
                }
            }
        }
        return ret;
    }
    
    public Archivo checkout(String nombrePro, String nombreArch,int id){
        List<Archivo> la = new ArrayList<>();
        boolean pr = false;
        for (Archivo a : servidores.get(id).getReplicas()) {
            if(a.getNombre().equalsIgnoreCase(nombreArch)){
                //a.setDespliegue(true);
                la.add(a);
            }
        }
        if(la.isEmpty()){
            pr = true;
            for (Proyecto p : servidores.get(id).getProyectos()) {
                if(p.getNombre().equalsIgnoreCase(nombrePro)){
                    for(Archivo ar : p.getArchivos()){
                        if(ar.getNombre().equalsIgnoreCase(nombreArch)){
                            //ar.setDespliegue(true);
                            la.add(ar);
                        }
                    }
                }
            }
        }
        Archivo x = la.get(0);
        for (int i = 1; i < la.size();i++) {
            if(x.getTimeStamp().before(la.get(i).getTimeStamp())){
                x = la.get(i);
            }
        }
        setDespliegue(x,pr,id);
        return x;
    }
    
    /**
     * cambia el atributo despliegue al archivo especificado
     * @param x
     * @param pr
     * @param id 
     */
    public void setDespliegue(Archivo x,boolean pr, int id){
        if(pr){
            for (Proyecto p : servidores.get(id).getProyectos()) {
                for(Archivo ar : p.getArchivos()){
                    if(ar.getTimeStamp().equals(x.getTimeStamp())){
                        ar.setDespliegue(true);
                    }
                }
            }
        }else{
            for (Archivo a : servidores.get(id).getReplicas()) {
                if(a.getTimeStamp().equals(x.getTimeStamp())){
                    a.setDespliegue(true);
                }
            }
        }
    }
    
    public void invalidar(Timestamp t,String arch,int id)throws Exception{
        boolean enc = false;
        for (Proyecto p : servidores.get(id).getProyectos()) {
            for(Archivo ar : p.getArchivos()){
                if(ar.getNombre().equalsIgnoreCase(arch)){
                     ar.setDespliegue(false);
                     enc = true;
                }
            }
        }
        for (Archivo arc : servidores.get(id).getReplicas()) {
           if(arc.getNombre().equalsIgnoreCase(arch) && arc.getTimeStamp().equals(t)){
                arc.setDespliegue(false);
            } 
        }
    }
    
    /**
     * Después de que un servidor se ha caido, revisa que servidor puede
     * tomar los proyectos del servidor caido.
     * @param p
     * @param id 
     */
    public void distribuirProyectos(List<Proyecto> p, int id){
        Set<Integer> s = servidores.keySet();
        int menor = 10000,idS=-1;
        for (Proyecto pro : p) {
            menor = 10000;
            for (Integer integer : s) {
                if(servidores.get(integer).getProyectos().size() <= menor && 
                        integer != id){
                    menor = servidores.get(integer).getProyectos().size();
                    idS = integer;
                }
            }
            if(idS != -1)
                servidores.get(idS).getProyectos().add(pro);
        }
    }
    
    public void delete(int id) throws Exception{
        if(servidores.containsKey(id)){
            List<Proyecto> p = servidores.get(id).getProyectos();
            distribuirProyectos(p,id);
            servidores.remove(id);
        }
    }
}
