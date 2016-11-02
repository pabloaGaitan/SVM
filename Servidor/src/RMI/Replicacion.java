/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RMI;

import clases.Archivo;
import clases.Proyecto;
import clases.Servidor;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

/**
 *
 * @author ASUS
 */
public class Replicacion extends UnicastRemoteObject implements IReplicacion{
    
    private Servidor thisServer;
    private IManejador manejador;
    
    public Replicacion(Servidor server) throws RemoteException,MalformedURLException, NotBoundException{
        super();
        this.thisServer = server;
        Registry remote = LocateRegistry.getRegistry("host-ip",1099);
        manejador = (IManejador)remote.lookup("rmi://host-ip/Manejador");
    }
    
    public void registro() throws Exception{
        manejador.registrar(thisServer);
    }
    
    // solo se llama desde el master
    public void actualizar(List<Servidor> servidores) throws RemoteException{
        
    }
    
    // Replicas a un servidor
    public void agregarArchivo(Archivo archivo) throws RemoteException{
        thisServer.getReplicas().add(archivo);
    }
    
    public void agregarProyecto(Proyecto proyecto) throws RemoteException{
        thisServer.getProyectos().add(proyecto);
    }
    
    public boolean asociarArchivo(Archivo file, String proyectoName) throws Exception{
        Proyecto p = buscarProyecto(proyectoName);
        if(proyectoName != null){
            p.getArchivos().add(file);
            replicar(file);
            return true;
        }
        return false;
    }
    
    // falta mirar el k del sistema
    public void replicar(Archivo file) throws Exception{
        int menor = 1000;
        Servidor s = null;
        for (Servidor server : servidores) {
            if(server.getReplicas().size() < menor && !thisServer.getIp().equals(server.getIp())){
                menor = server.getReplicas().size();
                s = server;
            }
        }
        Registry r = LocateRegistry.getRegistry(s.getIp(),1099);
        IReplicacion replicacion = (IReplicacion)r.lookup("rmi://"+s.getIp()+"/Replicacion");
        replicacion.agregarArchivo(file);
    }
    
    public Proyecto buscarProyecto(String nombre){
        for (Proyecto proyecto : thisServer.getProyectos()) {
            if(proyecto.getNombre().equalsIgnoreCase(nombre))
                return proyecto;
        }
        return null;
    }

    public List<Servidor> getServidores() throws RemoteException{
        //manejador.getServidores();
        return servidores;
    }
    
    public Servidor getThisServer() throws RemoteException{
        return this.thisServer;
    }
}
