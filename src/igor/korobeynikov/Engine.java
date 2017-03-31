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

    public static void setIsAuthorized(boolean isAuthorized) {
        Engine.isAuthorized = isAuthorized;
    }

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
            inputAuthMsg();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void disconnect(){
        executorService.shutdown();
        try {

            if(in != null) in.close();
            if(out != null) out.close();
            if(sock != null) sock.close();
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    public void inputAuthMsg(){
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        String str = in.readUTF();
                        if (str.equals("775588")) {
                            setIsAuthorized(true);
                            controller.textAreaChat.appendText("Вы авторизованы\n");
                            inputMsg();
                            break;
                        }
                        if (str != null) {
                            controller.textAreaChat.appendText(str);
                            controller.textAreaChat.appendText("\n");
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void inputMsg() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        if(!isAuthorized){

                            break;
                        }
                        String str = in.readUTF();
                        if (str.startsWith("/")) {
                            if (str.startsWith("/users ")) {
                                String[] u = str.split(" ");
                                controller.userNickArea.setText("");
                                for (int i = 1; i < u.length; i++) {
                                    controller.userNickArea.appendText(u[i] + "\n");
                                }
                            }
                            continue;
                        }
                        if (str != null) {
                            controller.textAreaChat.appendText(str);
                            controller.textAreaChat.appendText("\n");
                        }
                    }catch (IOException e){
                        e.printStackTrace();
                    }
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

    public void sendMsg(String message){
        try {
            out.writeUTF(message);
            out.flush();
        }catch (IOException e){
            controller.textAreaChat.appendText("Ошибка отправки сообщения");
        }

    }


}


