/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RMI;

import clases.Archivo;
import clases.Proyecto;
import clases.Servidor;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import persistencia.Persistencia;

/**
 *
 * @author ASUS
 */
public class Replicacion extends UnicastRemoteObject implements IReplicacion{
    
    private Servidor thisServer;
    private IManejador manejador;
    private String host;
    private int id;
    private int k;
    
    public Replicacion(Servidor server , String host) throws RemoteException,
            MalformedURLException, NotBoundException{
        super();
        this.thisServer = server;
        this.host = host;
        Registry remote = LocateRegistry.getRegistry(host,1099);
        manejador = (IManejador)remote.lookup("rmi://" + host +"/Manejador");
    }
    
    public void registro() throws Exception{
        List<Integer> l = manejador.registrar(thisServer);
        id = l.get(0);
        k = l.get(1);
    }
    
    // solo se llama desde el master
    public void actualizar(Map<Integer,Servidor> servidores,Archivo archivo) throws Exception{
        Set<Integer> set = servidores.keySet();
        for (Integer i : set) {
            for(int in = 0;in<servidores.get(i).getReplicas().size();in++){
                if(servidores.get(i).getReplicas().get(in).equals(archivo.getNombre())){
                   manejador.agregarArchivo(archivo,i);
                }
            }
        }
    }
    
    // Replicas a un servidor
    public void agregarArchivo(Archivo archivo) throws RemoteException{
        manejador.agregarArchivo(archivo, id);
    }
    
    public void agregarProyecto(Proyecto proyecto) throws RemoteException{
        manejador.agregarProyecto(proyecto, id);
    }
    
    public boolean asociarArchivo(Archivo file, String proyectoName) throws Exception{
        return manejador.asociarArchivo(file, proyectoName, id);
    }

    public Map<Integer,Servidor> getServidores() throws RemoteException{
        return manejador.getServidores();
    }
    
    public boolean checkout(String nombrePro,String nombreArch) throws Exception{
        Archivo arch = manejador.checkout(nombrePro,nombreArch,id);
        if(arch == null)
            return false;
        FileOutputStream out = new FileOutputStream(nombreArch);
        OutputStreamWriter wr = new OutputStreamWriter(out);
        char[] aux = new char[arch.getFile().length];
        for(int i = 0; i < arch.getFile().length;i++){
            aux[i] = (char)arch.getFile()[i];
        }
        wr.write(aux);
        wr.close();
        return true;
    }
    
    public boolean twoPhaseCommit() throws Exception{
        Servidor s = manejador.getServidores().get(id);
        for(Archivo arch : s.getReplicas()){
            if(arch.isDespliegue())
                return false;
        }
        for(Proyecto p : s.getProyectos()){
            for (Archivo a : p.getArchivos()) {
                if(a.isDespliegue())
                    return false;
            }
        }
        return true;
    }
    
    public void recibirAviso(String text){
        System.out.println(text);
    }
    
    public void avisoTodos(Archivo a) throws Exception{
        Map<Integer,Servidor> servidores = manejador.getServidores();
        Set<Integer> set = servidores.keySet();
        for (Integer i : set) {
            for(int ii = 0;ii<servidores.get(i).getReplicas().size();ii++){
                if(servidores.get(i).getReplicas().get(ii).getNombre().equals(a.getNombre())){
                    Registry r = LocateRegistry.getRegistry(servidores.get(i).getIp(),1099);
                    IReplicacion rep = (IReplicacion)r.lookup("rmi://"+servidores.get(i).getIp()+"/Replicacion");
                    rep.recibirAviso("El archivo "+a.getNombre()+" se ha actualizado.");
                }
            }
        }
    }
    
    public boolean commit(String archivo,byte buf[]) throws Exception{
        Map<Integer,Servidor> servidores = manejador.getServidores();
        List<Integer> noContestaron = new ArrayList<>();
        Archivo archi = new Archivo();
        int cont = 0;
        Set<Integer> set = servidores.keySet();
        for (Integer s : set) {
            if(!servidores.get(s).getIp().equals(servidores.get(id).getIp())){
                Registry r = LocateRegistry.getRegistry(servidores.get(s).getIp(),1099);
                IReplicacion rep = (IReplicacion)r.lookup("rmi://"+servidores.get(s).getIp()+"/Replicacion");
                if(rep.twoPhaseCommit())
                    cont++;
                else
                    noContestaron.add(s);
                
            }
        }
        if(cont == servidores.size()-1){
            Servidor s = manejador.getServidores().get(id);
            for(Archivo arch : s.getReplicas()){
                if(arch.isDespliegue())
                    archi = arch;
            }
            for(Proyecto p : s.getProyectos()){
                for (Archivo a : p.getArchivos()) {
                    if(a.isDespliegue())
                        archi = a;
                }
            }
            Archivo newArchivo = new Archivo();
            newArchivo.setDespliegue(false);
            newArchivo.setFile(buf);
            newArchivo.setNombre(archi.getNombre());
            newArchivo.setTimeStamp((new Timestamp(((Date)Calendar.getInstance().getTime()).getTime())));
            avisoTodos(newArchivo);
            return true;
        }
        for (Integer inte : noContestaron) {
            Registry r = LocateRegistry.getRegistry(servidores.get(inte).getIp(),1099);
            IReplicacion rep = (IReplicacion)r.lookup("rmi://"+servidores.get(inte).getIp()+"/Replicacion");
            if(rep.invalidar(archivo)){
                Archivo a = buscarArchivo(archivo);
                manejador.invalidar(a.getTimeStamp(), archivo, id);
                rep.recibirAviso("El archivo " + archivo + " se ha invalidado.");
            }
        }
        
        return false;
    }
    
    public Archivo buscarArchivo(String nombre) throws Exception{
        Map<Integer,Servidor> servidores = manejador.getServidores();
        for (Archivo a : servidores.get(id).getReplicas()) {
            if(a.getNombre().equalsIgnoreCase(nombre) && a.isDespliegue())
                return a;
        }
        for(Proyecto p: servidores.get(id).getProyectos() )
            for(Archivo a : p.getArchivos())
                if(a.getNombre().equalsIgnoreCase(nombre) && a.isDespliegue())
                    return a;
        return null;
    }
    
    public boolean invalidar(String archivo)throws Exception{
        File fichero = new File(archivo);
        return fichero.delete();
    }
}
