/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projetoweb;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Wesley
 */
class ThreadCliente extends Thread {

    Socket socket;
    DataOutputStream out;
    BufferedReader in;

    public ThreadCliente(Socket socket) throws IOException {
        this.socket = socket;
        this.out = new DataOutputStream(socket.getOutputStream());
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.start();
    }

    public void processarCabecalho() throws IOException {
        String msg;
        List<String> lista = new ArrayList();
        List<String> listasplit = new ArrayList();
        while (true) {
            msg = this.in.readLine();
            lista.add(msg);
            if (msg.equals("")) {
                break;
            }
        }
        //while(!(this.in.readLine().equals(""))){}

        System.out.println("-------------------------------------");

        for (String x : lista) {
            String[] spli = x.split(" ");
            for (String nova : spli) {
                listasplit.add(nova);
            }
        }
        System.out.println("TESTE SPLIT ");
        for(String z : listasplit){
            System.out.println(z);
        }

}

@Override
        public void run() {
        String str = "HTTP 200 TUDO OK \r\n\r\n mensagem deu certo";
        try {
            this.out.writeBytes(str);
            processarCabecalho();
            out.close();
            socket.close();
        

} catch (IOException ex) {
            Logger.getLogger(ThreadCliente.class
.getName()).log(Level.SEVERE, null, ex);
        }
        
}
}
