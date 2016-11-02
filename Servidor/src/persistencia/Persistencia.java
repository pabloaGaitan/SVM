/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistencia;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sala-a
 */
public class Persistencia {
    
    public static String leerHost(String arch) throws IOException{
        String host = new String();
        FileInputStream in = new FileInputStream(arch);
        InputStreamReader inp = new InputStreamReader(in);
        BufferedReader bf = new BufferedReader(inp);
        host = bf.readLine();
        in.close();
        return host;
    }
    
}
