/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projetoweb;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Wesley
 */

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
     

