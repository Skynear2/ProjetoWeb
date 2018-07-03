/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projetoweb;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Main {
  public static void main(String[] args) {
    try {
        int port = 5555;
        Integer conexoes = 0;
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        long a = System.currentTimeMillis();
      // Instancia o ServerSocket ouvindo a porta 12345
      ServerSocket servidor = new ServerSocket(port);
      System.out.println("Servidor ouvindo a porta " + port + ":");
      while(true) {
        
        Socket cliente = servidor.accept();
        System.out.println("Cliente conectado: " + cliente.getInetAddress().toString());
        conexoes++;
        Worker Cliente = new Worker(cliente, conexoes, sdf.format(d), a); 
        
      }  
    }   
    catch(Exception e) {
       System.out.println("Erro: " + e.getMessage());
    }
    //finally {...}  
  }     

}

