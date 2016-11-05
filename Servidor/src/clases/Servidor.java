/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clases;

import java.io.Serializable;
import java.rmi.Remote;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author HP
 */
public class Servidor implements Serializable{
    
    private String ip;
    private List<Proyecto> proyectos;
    private List<Archivo> replicas;
    
    public Servidor(){
        replicas = new ArrayList<>();
        proyectos = new ArrayList<>();
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public List<Proyecto> getProyectos() {
        return proyectos;
    }

    public void setProyectos(List<Proyecto> proyectos) {
        this.proyectos = proyectos;
    }

    public List<Archivo> getReplicas() {
        return replicas;
    }

    public void setReplicas(List<Archivo> replicas) {
        this.replicas = replicas;
    }
}
