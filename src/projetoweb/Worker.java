/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projetoweb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import org.json.simple.JSONObject;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 *
 * @author Wesley
 */
class Worker extends Thread {

    Socket socket;
    BufferedWriter out;
    BufferedReader in;
    String HTTPmethod;
    String ResourcePATH;
    String HTTPProtocol;
    String QParam;
    String cookie;
    Integer count = 0;
    String Resposta;
    private HashMap headerMap;
    String dynParam;
    String dynfName;
    String dynResponse;
    String dynfile;
    String dynfPath;
    long dynfTam;
    String feedback;
    Integer conexoes;
    long dataInicialLong;
    String dataInicial;

//    String exePathFile;
//    String exeResposta;
//    String exeHtml;
    public BufferedReader getIn() {
        return in;
    }

    public void setIn(BufferedReader in) {
        this.in = in;
    }

    public String getHTTPmethod() {
        return HTTPmethod;
    }

    public void setHTTPmethod(String HTTPmethod) {
        this.HTTPmethod = HTTPmethod;
    }

    public String getResourcePATH() {
        return ResourcePATH;
    }

    public void setResourcePATH(String ResourcePATH) {
        this.ResourcePATH = ResourcePATH;
    }

    public String getHTTPProtocol() {
        return HTTPProtocol;
    }

    public void setHTTPProtocol(String HTTPProtocol) {
        this.HTTPProtocol = HTTPProtocol;
    }

    public String getQParam() {
        return QParam;
    }

    public void setQParam(String QParam) {
        this.QParam = QParam;
    }

    public Worker(Socket socket, int connect, String d, long a) throws IOException {
        this.socket = socket;
        this.out = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        //this.myMap = new HashMap<String,String>();
        this.headerMap = new HashMap();
        this.conexoes = connect;
        this.dataInicial = d;
        this.dataInicialLong = a;
        this.start();
    }

    public void processarCabecalho() throws IOException, URISyntaxException {
        adicionaData();
        int flag = 0;
        String msg;
        String[] first;

        //List<String> listasplit = new ArrayList();
        /*this.setHTTPmethod(first[0]);
        if(first[1].contains("?")) {
            String[] aux = first[1].split("?");
            this.setResourcePATH(aux[0]);
            this.setQParam(aux[1]);
        }
        else{
            this.setResourcePATH(first[1]);}
        this.setHTTPProtocol(first[2]);*/
        while (!(msg = in.readLine()).equals("")) {
            if (flag == 0) {
                first = msg.split(" ");
                this.setHTTPmethod(first[0]);
                if (first[1].contains("?")) {
                    String[] aux = first[1].split("?");
                    this.setResourcePATH(new java.net.URI(aux[0]).getPath());
                    //this.setResourcePATH(aux[0]);
                    System.out.println("path: " + this.ResourcePATH);
                    this.setQParam(aux[1]);
                } else {
                    if (first[1].equals("\favicon.ico")) {
                        char c;
                        c = first[1].charAt(1);
                        first[1] = Character.toString(c);

                    }
                    this.setResourcePATH(new java.net.URI(first[1]).getPath());
                }

                System.out.println("first 2" + first[2]);
                this.HTTPProtocol = first[2];
                flag = 1;
            } else {
                first = msg.split(": ");
                this.headerMap.put(first[0], first[1]);
            }
        }
        /*    String[] spli = msg.split(": ");
            //System.out.println("split1: "+spli[0]+" split 2: "+spli[1]);
            this.myMap.put(spli[0], spli[1]);
            if (msg.equals("")) {
                break;
            }
        }
        
         */
        System.out.println("-------------------------------------");
    }

    public void setCookie() {
        this.cookie = ("HTTP 200 TUDO OK  Set-Cookie: Count=0\r\n\r\n");

    }

    public void Diretorios() throws IOException {

        String dirName = this.ResourcePATH;

        Files.list(new File(dirName).toPath())
                .limit(10)
                .forEach(path -> {
                    System.out.println(path);
                });
    }

    public void adicionaData() {
        Date data = new Date();
        this.Resposta = ("Date: " + data.toString() + "\r\n");
    }

    public void concatenaResposta(String str) {

        if (this.Resposta.isEmpty()) {
            this.Resposta = str;
        } else {
            this.Resposta = this.Resposta.concat(str);
        }

    }

    public void escreveNoArquivo(File file) throws IOException {
        byte[] buffer = new byte[1024];
        System.out.println("PATH DO ARQUIVO" + file.getPath());
        int bytes;
        File fileaux = new File(file.getPath());
        FileInputStream In = new FileInputStream(fileaux);
        OutputStream Out = new DataOutputStream(this.socket.getOutputStream());

        while ((bytes = In.read(buffer)) != -1) {
            Out.write(buffer, 0, bytes);
            Out.flush();
        }
        In.close();
    }


    public boolean autorizado() throws UnsupportedEncodingException {
        boolean flag;
        if (this.headerMap.containsKey("Authorization")) {
            String aux = (String) this.headerMap.get("Authorization");
            String[] aux2 = aux.split("Basic ");
            byte[] decodificado = Base64.getDecoder().decode(aux2[1]);
            String login = new String(decodificado, "UTF-8");
            String[] split = login.split(":");
            if (split[0].equals("admin") && split[1].equals("admin")) {
                flag = true;
            } else {
                flag = false;
            }
        } else {
            flag = false;
        }

        return flag;

    }

    /*public void executaexe() throws IOException {
        Process processo = Runtime.getRuntime().exec(this.ResourcePATH);
        BufferedReader in = new BufferedReader(new InputStreamReader(processo.getInputStream()));
        String linha;
        while ((linha = in.readLine()) != null) {
            if (this.exeResposta.isEmpty()){
            this.exeResposta = linha;
        }
        this.exeResposta =this.exeResposta.concat(linha);
        }
   }
     */
    public void subResposta() {
        String regex = Pattern.quote("<%") + "(.*?)" + Pattern.quote("%>");
        this.dynfile = this.dynfile.replaceAll(regex, this.dynResponse);
    }

    public void executarFuncao() {
        Date data = new Date();
        SimpleDateFormat date;
        try {
            date = new SimpleDateFormat(this.dynParam);
            this.dynResponse = date.format(data).toString();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            this.dynResponse = "Erro";
        }
    }

    public void dynNomeParam() throws FileNotFoundException {
        String file = new String();
        File arquivo = new File(this.ResourcePATH);
        this.dynfTam = arquivo.length();
        Scanner scanner = new Scanner(arquivo);
        while (scanner.hasNextLine()) {
            file = file.concat(scanner.nextLine());
        }
        this.dynfile = file;
        String[] split = file.split("<%");
        split = split[1].split("%>");
        String funcao = split[0];
        funcao = funcao.replace("\"", "");
        funcao = funcao.trim();
        this.dynParam = funcao.substring(funcao.indexOf("(") + 1, funcao.indexOf(")"));
        this.dynfName = funcao.substring(0, funcao.indexOf("("));
    }

    public void requisiçao401() throws IOException {
        this.out.write("HTTP/1.1 401 Authorization Required\r\n");
        this.out.write("WWW-Authenticate: Basic realm=\"User Visible Realm\"");
        this.out.write("\r\n");
        this.out.flush();
    }

    public String tudoOk() {
        return ("HTTP/1.1 200 OK\r\n");
    }

    public void readFile(File file) throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead;
        FileInputStream fileIn = new FileInputStream(file);
        OutputStream dataOut = new DataOutputStream(this.socket.getOutputStream());

        while ((bytesRead = fileIn.read(buffer)) != -1) {
            dataOut.write(buffer, 0, bytesRead);
            dataOut.flush();
        }
        fileIn.close();
    }

    public void notfound404() throws IOException {
        System.out.println("resource path" + this.ResourcePATH);
        File file = new File("src/projetoweb/notfound.html");
        Path f1 = Paths.get(file.getPath());
        this.out.write("HTTP/1.1 404 NOT FOUND\r\n");
        this.out.write("content-type: " + Files.probeContentType(f1) + "\r\n");
        this.out.write("content-lenght: " + file.length() + "\r\n");
        this.out.write("\r\n");
        this.out.flush();
        //this.tudoOk();
        this.readFile(file);
//        this.out.write("<!DOCTYPE html>"+
//                        "<html>"+
//                        "<title>Not Found</title>"+
//                        "<body>"+
//                            "<div style=\"width: 100%\">"+
//                                "<h1>Not Found</h1>"+
//                                "<form action=\"/virtual/feedback\" method=\"POST\">"+
//                                "Feedback:<br>"+
//                                "<input type=\"text\" name=\"feedback\" value=\"\">"+
//                                " <br>"+
//                                "<input type=\"submit\" value=\"Submit\">"+
//                                "</form>"+
//                             "</div>"+
//                            "</body>"+
//                            "</html> ");

    }

    public void GET() throws IOException {
        Path arquivos = Paths.get(this.getResourcePATH());
        if (Files.isDirectory(arquivos)) {
            //this.ResourcePATH.charAt(this.ResourcePATH.length()-1 )
            if (autorizado() == true) { //autorizado() ==
                if ((this.ResourcePATH.charAt(this.ResourcePATH.length() - 1)) != '/') {
                    this.out.write("HTTP/1.1 301 Moved Permanently\r\n");
                    this.out.flush();
                    this.out.write("Location: " + this.ResourcePATH + "/\r\n");
                    this.out.flush();
                    this.out.write("\r\n");
                    this.out.flush();
                    this.out.write("\r\n");
                    this.out.flush();
                } else {
                    File diretorio = new File(this.ResourcePATH);
                    File[] arquivosAux = diretorio.listFiles();
                    this.out.write(tudoOk());
                    System.out.println("RESPOSTAAAAAA" + this.Resposta);
                    this.out.write(this.Resposta);
                    this.out.write("\r\n");
                    this.out.flush();
                    Path fpath = Paths.get(this.ResourcePATH);
                    String html = new String();
                    for (File f : arquivosAux) {

                        // html = "<tr><td valign=\"top\">"+"<img src=\"/src/unknown.gif\" alt=\"[   ]\"></td>"+"<td><a href=\">kurumin-7.0.iso</a>"
//                        html = html.concat("<tr><td valign=\"top\"></td><td><a href=\"" + f.getName() + "\">" + f.getName()
//                    + "</a></td><td></td><td>" + f.length() + "</td></tr>\n");
                        FileTime auxiliar = Files.getLastModifiedTime(fpath);
                        String last = auxiliar.toString();
                        String[] aux2 = last.split("T");

                        html = html.concat("<tr><td><a href=\"" + f.getName() + "\">" + f.getName() + "</a></td><td>" + aux2[0] + "</td><td></td><td >" + f.length() + "</td></tr>");
                    }
                    String file = "<!DOCTYPE html>"
                            + "<html>"
                            + "<head>"
                            + "<title>Wesley Server</title>"
                            + "<meta charset=" + "utf-8" + ">"
                            + "<meta name=" + "viewport" + "content" + "width=device-width, initial-scale=1" + ">"
                            + "<link rel=" + "stylesheet" + "href=" + "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" + ">"
                            + "<script src=" + "https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js" + "></script>"
                            + "<script src=" + "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js" + "></script>"
                            + "</head>"
                            + "<body>"
                            + "<div class=" + "container" + ">"
                            + "<div class=\"table-responsive\">"
                            + "<h2>Wesley</h2>"
                            + "<table class=" + "table table-condensed" + ">"
                            + "<thead>"
                            + "<tr>"
                            + "<th>Arquivo</th>"
                            + "<th>Last Modified</th>"
                            + "<th>     Tamanho</th>"
                            + "</tr>"
                            + "</thead>"
                            + "<tbody>"
                            + html
                            + "</tbody>"
                            + "</table>"
                            + "</div>"
                            + "</body></html>";

                    file = file.trim();
                    String regex = Pattern.quote("<%") + "(.*?)" + Pattern.quote("%>");
                    file = file.replaceAll(regex, html);
                    this.out.write(file);

                }

            } else {
                this.requisiçao401();
            }
        } else if (Files.exists(arquivos)) {
            if (this.ResourcePATH.endsWith(".dyn")) {
                dynNomeParam();
                executarFuncao();
                subResposta();

                this.out.write(this.tudoOk());
                this.concatenaResposta("content-type: " + Files.probeContentType(arquivos) + "\r\n");
                this.concatenaResposta("content-lenght: " + this.dynfTam + "\r\n");
                this.out.write(this.Resposta);
                this.out.write("\r\n");
                this.out.flush();
                this.out.write(this.dynfile);
            } else {
                File path = new File(this.ResourcePATH);
                this.out.write(this.tudoOk());
                this.concatenaResposta("content-type: " + Files.probeContentType(arquivos) + "\r\n");
                this.concatenaResposta("content-lenght: " + path.length() + "\r\n");
                this.out.write(this.Resposta);
                this.out.write("\r\n");
                this.out.flush();
                this.escreveNoArquivo(path);

            }
        } else if (this.ResourcePATH.startsWith("/virtual/status/")) {
            System.out.println("entrei no virtual/status");

            if (this.ResourcePATH.startsWith("/virtual/status/telemetria.html")) {
                System.out.println("entrei no virtual/status/telemetria");
                File html = new File("src/projetoweb/telemetria.html");
                Path aux = Paths.get(html.getPath());
                this.out.write(this.tudoOk());
                this.concatenaResposta("Content-type: " + Files.probeContentType(aux) + "\r\n");
                this.concatenaResposta("content-lenght: " + html.length() + "\r\n");
                this.out.write(this.Resposta);
                this.out.write("\r\n");
                this.out.flush();
                String telemetria = new String();
                Scanner scan = new Scanner(new File("src/projetoweb/telemetria.html"));
                while (scan.hasNextLine()) {
                    telemetria = telemetria.concat(scan.nextLine());
                }
                String conexoesRegex = Pattern.quote("<%C") + "(.*?)" + Pattern.quote("C%>");
                String tempoRegex = Pattern.quote("<%t") + "(.*?)" + Pattern.quote("t%>");
                String diaRegex = Pattern.quote("<%i") + "(.*?)" + Pattern.quote("i%>");

                telemetria = telemetria.replaceAll(diaRegex, this.dataInicial);
                telemetria = telemetria.replaceAll(conexoesRegex, this.conexoes.toString());
                telemetria = telemetria.replaceAll(tempoRegex, calculaDias());
                this.out.write(telemetria);
            }
            else if(this.ResourcePATH.startsWith("/virtual/status/status.json")) {
                   JSONObject aux = new JSONObject();
                   String tempoOnline = calculaDias();
                   aux.put("conexoes", this.conexoes);
                   aux.put("tempoOnline", tempoOnline);
                   aux.put("dataInicial", this.dataInicial);
                   this.out.write(tudoOk());
                   this.concatenaResposta("content-type: application/json\r\n");
                   byte[] buff = aux.toString().getBytes();
                   this.concatenaResposta("content-lenght: " + buff.length + "\r\n");
                   this.concatenaResposta("Access-Control-Allow-Origin: *\r\n");
                   this.out.write(this.Resposta);
                   this.out.write("\r\n");
                   this.out.flush();
                   OutputStream Dados = new DataOutputStream(this.socket.getOutputStream());
                   Dados.write(buff, 0, buff.length);
                   Dados.flush();
            }
                else if (this.ResourcePATH.startsWith("/virtual/status/telemetria.js")) {
                File x = new File("src/projetoweb/telemetria2.html");
                Path Path = Paths.get(x.getPath());
                this.out.write(tudoOk());
                this.concatenaResposta("content-type: " + Files.probeContentType(Path) + "\r\n");
                this.concatenaResposta("content-lenght: " + x.length() + "\r\n");
                this.out.write(this.Resposta);
                this.out.write("\r\n");
                this.out.flush();
                this.escreveNoArquivo(x);
                
        }else{
                    notfound404();
                }
        }
            else if (!Files.exists(arquivos)) {
            notfound404();
            System.out.println("404");
        } else {
            File pathArq = new File(this.ResourcePATH);
            this.out.write(tudoOk());
            this.concatenaResposta("content-type: " + Files.probeContentType(arquivos) + "\r\n");
            this.concatenaResposta("content-lenght: " + pathArq.length() + "\r\n");
            this.out.write(this.Resposta);
            this.out.flush();
            this.out.write("\r\n");
            this.out.flush();
            this.readFile(pathArq);
        }
    
}

    public String calculaDias() {
        long dif = System.currentTimeMillis() - this.dataInicialLong;
        long sec = (dif / 1000) % 60;
        long min = (dif / (60 * 1000)) % 60;
        long hora = (dif / (60 * 60 * 1000)) % 24;
        String strFinal;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        //strFinal = String.format("%02d", hours)+":"+String.format("%02d", min)+ ":" + String.format("%02d", sec);
        //System.out.println(strFinal);
        return (String.format("%02d:%02d:%02d", hora, min, sec));

    }

    public static int strToInt(String valor, int padrao) {
        try {
            return Integer.valueOf(valor); // Para retornar um Integer, use Integer.parseInt
        } catch (NumberFormatException e) {  // Se houver erro na conversão, retorna o valor padrão
            return padrao;
        }
    }

    public static void escritor(String path, String str) throws IOException {
        BufferedWriter buffWrite = new BufferedWriter(new FileWriter(path));

        buffWrite.append(str);
        buffWrite.close();
    }

    public void POST() throws IOException {
        int i = strToInt((String) this.headerMap.get("Content-Length"), 0);
        char[] buffer = new char[i];
        in.read(buffer);
        String comentario = new String(buffer);
        String[] str = comentario.split("=");

        str[1] = str[1].replace("+", " ");
        System.out.println("connexoes " + this.conexoes);
//System.out.println("POST POST POST POST  str1 "+str[1] );
        //this.headerMapFeedback.put(str[0], str[1]);
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
        File f = new File("src/feedback/" + sdf.format(d) + ".txt");
        f.createNewFile();
        String path = "src/feedback/" + sdf.format(d) + ".txt";
        escritor(path, str[1]);
        //this.writeFeedback();
    }

    public void chamaMetodo() throws IOException {
        if (this.HTTPmethod.equals("GET")) {
            GET();
        } else if (this.HTTPmethod.equals("POST")) {
            POST();
        } else {
            System.out.println("nao foi metodo get.");
        }

    }

    private String AtualizaCookie() {
        String Cookie1 = (String) this.headerMap.get("Cookie");
        String[] split;
        int newcookie;
        if (Cookie1.contains("; ")) {
            split = Cookie1.split("; ");
            String[] Cookie2 = split[0].split("=");
            newcookie = Integer.parseInt(Cookie2[1]);
        } else {
            split = Cookie1.split("=");
            newcookie = Integer.parseInt(split[1]);
        }
        return ("set-cookie: count=" + (newcookie + 1) + "\r\n");
    }

    private String criaCookie() {
        return ("set-cookie: count=0\r\n");
    }

    public void cookie() {

        if (this.headerMap.containsKey("Cookie")) {
            this.concatenaResposta(this.AtualizaCookie());
        } else {
            this.concatenaResposta(this.criaCookie());
        }
//        if (flag == true){
//           String Cookieatual = (String)this.headerMap.get("Cookie");
//        String[] corta;
//        int Value;
//        if (Cookieatual.contains("; ")) {
//            corta = Cookieatual.split("; ");
//            String[] lCookie = corta[0].split("=");
//            Value = Integer.parseInt(lCookie[1]);
//        } else {
//            corta = Cookieatual.split("=");
//            Value = Integer.parseInt(corta[1]);
//        }
//        this.Resposta.concat("set-cookie: count=" + (Value + 1) + "\r\n");
//        }
//        else {
//            this.Resposta.concat("set-cookie: count=0\r\n");
//        }

    }

    @Override
    public void run() {
        String str = "HTTP 200 OK \r\n\r\n\r\n mensagem deu certo";
        try {

            //  this.out.writeBytes(str);
            processarCabecalho();
            cookie();
            chamaMetodo();
            //Diretorios();
            out.close();
            socket.close();

        } catch (IOException ex) {
            Logger.getLogger(Worker.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}

/*index of mostra primeira vez q ocorreu : 
if key == cookie 
processa cookie
else
this.header.put(key,value)

 */
 /*
    this.requestCookie(key, value)

switch (metodo)
case get: process get  //verifica o path, ve se o resource existe   se for um DIR (diretorio)
case post:
case xxx:

 */

/* 
    count = 1 tem q vira 2
    this.response cookie  == variavel global da classe

linha / header / conteudo 

this.responseHeader 
*/
