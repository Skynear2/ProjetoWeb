/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projetoweb;

import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Date;
import javax.swing.JOptionPane;


public class ClienteTCPBasico {
  public static void main(String[] args) {
      
    try {
      Socket cliente = new Socket("localhost",5555);
      ObjectInputStream entrada = new ObjectInputStream(cliente.getInputStream());
      Byte aux = entrada.readByte();
      
      JOptionPane.showMessageDialog(null,"MSG do servidor:" + aux);
      entrada.close();
      System.out.println("Conexão encerrada");
    }
    catch(Exception e) {
      System.out.println("Erro: " + e.getMessage());
    }
  }
}