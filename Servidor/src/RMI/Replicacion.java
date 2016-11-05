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
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
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
    public void actualizar(List<Servidor> servidores) throws RemoteException{
        
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
    
    // falta mirar el k del sistema
    public void replicar(Archivo file) throws Exception{
        int menor = 1000;
        Servidor s = null;
        Map<Integer,Servidor> m = manejador.getServidores();
        Set<Integer> servidores = m.keySet();
        for (Integer server : servidores) {
            if(m.get(server).getReplicas().size() < menor && !thisServer.getIp().equals(m.get(server).getIp())){
                menor = m.get(server).getReplicas().size();
                s = m.get(server);
            }
        }
        Registry r = LocateRegistry.getRegistry(s.getIp(),1099);
        IReplicacion replicacion = (IReplicacion)r.lookup("rmi://"+s.getIp()+"/Replicacion");
        replicacion.agregarArchivo(file);
    }

    public Map<Integer,Servidor> getServidores() throws RemoteException{
        //manejador.getServidores();
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
    
    public boolean commit(String archivo) throws Exception{
        Map<Integer,Servidor> servidores = manejador.getServidores();
        int cont = 0;
        Set<Integer> set = servidores.keySet();
        for (Integer s : set) {
            if(!servidores.get(s).getIp().equals(servidores.get(id).getIp())){
                Registry r = LocateRegistry.getRegistry(servidores.get(s).getIp(),1099);
                IReplicacion rep = (IReplicacion)r.lookup("rmi://"+servidores.get(s).getIp()+"/Replicacion");
                if(rep.twoPhaseCommit())
                    cont++;
            }
        }
        if(cont == servidores.size()){
            // hacer commit
            return true;
        }
        return false;
    }
}
