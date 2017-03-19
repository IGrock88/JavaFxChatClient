package igor.korobeynikov;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static igor.korobeynikov.Controller.*;

public class Engine {

    private String serverAdress;
    private int serverPort;
    private static Socket sock;
    private static DataInputStream in;
    private static DataOutputStream out;
    private static boolean isAuthorized;
    private static boolean flag = false;
    private static ExecutorService executorService = Executors.newFixedThreadPool(100);;
    private static Controller controller;

    public Engine(String serverAdress, int serverPort, Controller controller) {
        this.serverAdress = serverAdress;
        this.serverPort = serverPort;
        this.controller = controller;
        connect();
    }

    public void connect(){
        try {
            sock = new Socket(serverAdress, serverPort);
            in = new DataInputStream(sock.getInputStream());
            out = new DataOutputStream(sock.getOutputStream());
            exchangeData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void disconnect(){
        try {
            if(sock != null) sock.close();
            if(in != null) in.close();
            if(out != null) out.close();
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    public void exchangeData(){
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        String str = in.readUTF();
                        if (str.equals("775588")) {
//                            setAuthorized(true);
                            controller.textAreaChat.appendText("Вы авторизованы\n");
                            break;
                        }
                        if (str != null) {
                            controller.textAreaChat.appendText(str);
                            controller.textAreaChat.appendText("\n");
                        }
                    }

                    while (true) {
                        String str = in.readUTF();
                        if (str.startsWith("/")) {
                            if(str.startsWith("/users ")) {
                                String[] u = str.split(" ");
                                controller.userNickArea.setText("");
                                for (int i = 1; i < u.length; i++) {
                                    controller.userNickArea.appendText(u[i] + "\n");
                                }
                            }
                            continue;
                        }
//                        if (str != null) {
//                            if(str.startsWith("/registrationPasitiveAnswer")){
//                                textAreaChat.appendText("Регистрация прошла успешно\n");
//                            }
//                            textAreaChat.appendText(str);
//                            textAreaChat.appendText("\n");
//                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void sendAuthMsg(String login, String pass) {
        try {
            String a = "/auth " + login + " " + pass;
            System.out.println(a);
            out.writeUTF(a);
            out.flush();
        } catch (IOException e) {
            controller.textAreaChat.appendText("Ошибка авторизации");
        }
    }


}


