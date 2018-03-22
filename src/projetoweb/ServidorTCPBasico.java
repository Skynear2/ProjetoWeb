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
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ServidorTCPBasico {
  public static void main(String[] args) {
    try {
        int port = 5555;
      // Instancia o ServerSocket ouvindo a porta 12345
      ServerSocket servidor = new ServerSocket(port);
      System.out.println("Servidor ouvindo a porta " + port + ":");
      while(true) {
        
        Socket cliente = servidor.accept();
        System.out.println("Cliente conectado: " + cliente.getInetAddress().toString());
        ThreadCliente Cliente = new ThreadCliente(cliente); 
        
      }  
    }   
    catch(Exception e) {
       System.out.println("Erro: " + e.getMessage());
    }
    //finally {...}  
  }     
}

class ThreadCliente extends Thread {
    Socket socket;
    DataOutputStream out;

    public ThreadCliente(Socket socket) throws IOException {
        this.socket = socket;
        this.out = new DataOutputStream(socket.getOutputStream());
        this.start();
    }

    @Override
    public void run() {
        String str = "HTTP 200 TUDO OK \r\n\r\n mensagem deu certo";
        try {
            this.out.writeBytes(str);
            out.close();
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(ThreadCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
     


}