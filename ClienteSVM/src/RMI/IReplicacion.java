/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RMI;

import clases.Archivo;
import clases.Proyecto;
import clases.Servidor;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ASUS
 */
public interface IReplicacion extends Remote{
    
    public void registro() throws Exception;
    public boolean asociarArchivo(Archivo file, String proyectoName) throws Exception;
    public void actualizar(List<Servidor> servidores) throws RemoteException;
    public void agregarArchivo(Archivo archivo) throws RemoteException;
    public void agregarProyecto(Proyecto proyecto) throws RemoteException;
    public Map<Integer,Servidor> getServidores() throws RemoteException;
    public boolean checkout(String nombrePro,String nombreArch) throws Exception;
    public boolean twoPhaseCommit() throws Exception;
    public boolean commit(String archivo) throws Exception;
}
