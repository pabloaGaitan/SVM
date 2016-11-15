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
    
    /**
     * Se encarga de recibir las peticiones de registro de los servidores
     * de la red.
     * @param servidor
     * @return la lista de los servidores actuales.
     * @throws RemoteException 
     */
    public List<Integer> registrar(Servidor servidor) throws RemoteException;
    
    /**
     * Obtiene el mapa con los servidores actuales.
     * @return
     * @throws RemoteException 
     */
    public Map<Integer,Servidor> getServidores() throws RemoteException;
    
    /**
     * Cuando se crea un archivo, es necesario asociarlo a un proyecto, aquí
     * se asocia dicho archivo al proyecto destino.
     * @param file
     * @param proyectoName
     * @param id
     * @param op
     * @return verdadero si pudo hacer la asociación, de lo contrario retornará
     * falso.
     * @throws Exception 
     */
    public boolean asociarArchivo(Archivo file, String proyectoName,int id,int op) throws Exception;
    
    /**
     * Este es llamado para asociar réplicas a un servidor especificado por su id.
     * @param archivo
     * @param id
     * @throws RemoteException 
     */
    public void agregarArchivo(Archivo archivo,int id) throws RemoteException;
    
    /**
     * Cuando un proyecto es creado, se asocia el proyecto al servidor que lo
     * solicita.
     * @param proyecto
     * @param id
     * @throws RemoteException 
     */
    public void agregarProyecto(Proyecto proyecto,int id) throws RemoteException;
    
    /**
     * Se encarga de retornar la copia con la versión más reciente, para ello
     * compara el timestamp para saber cuál es la versión más reciente
     * @param nombrePro
     * @param nombreArch
     * @param id
     * @return
     * @throws RemoteException 
     */
    public Archivo checkout(String nombrePro, String nombreArch,int id) throws RemoteException;
    
    /**
     * En el momento que se hace commit, si hay archivos que no permiten la operación,
     * debe hacerse una invalidación, por lo que este se encarga de invalidar
     * el archivo especificado.
     * @param t
     * @param arch
     * @param id
     * @throws Exception 
     */
    public void invalidar(Timestamp t,String arch,int id)throws Exception;
    
    /**
     * Borra un servidor del mapa de servidores, esto ocurre cuando un servidor
     * se cae.
     * @param id
     * @throws Exception 
     */
    public void delete(int id) throws Exception;
}
