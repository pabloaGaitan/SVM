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
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 *
 * @author HP
 */
public interface IManejador extends Remote{
    
    public List<Integer> registrar(Servidor servidor) throws RemoteException;
    //public void actualizar(Servidor servidor) throws RemoteException;
    public Map<Integer,Servidor> getServidores() throws RemoteException;
    public boolean asociarArchivo(Archivo file, String proyectoName, int id) throws Exception;
    //public void actualizar()
    public void agregarArchivo(Archivo archivo, int id) throws RemoteException;
    public void agregarProyecto(Proyecto proyecto, int id) throws RemoteException;
    public Archivo checkout(String nombrePro, String nombreArch, int id) throws RemoteException;
    public void invalidar(Timestamp t, String arch, int id) throws Exception;
}
