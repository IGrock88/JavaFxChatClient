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

    private static String serverAdress;
    private static int serverPort;
    private static Socket sock;
    private static DataInputStream in;
    private static DataOutputStream out;
    private static boolean isAuthorized;
    private static ExecutorService executorService = Executors.newFixedThreadPool(100);;
    private static Controller controller;

    public static void setIsAuthorized(boolean isAuthorized) {
        Engine.isAuthorized = isAuthorized;
    }

    public static boolean getIsIsAuthorized() {
        return isAuthorized;
    }

    public Engine(String serverAdress, int serverPort, Controller controller) {
        this.serverAdress = serverAdress;
        this.serverPort = serverPort;

        this.controller = controller;

        connect();
    }

    public static void connect(){
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
            if(sock != null) {
                sock.close();
                sock = null;
            }

        } catch (IOException e){
            e.printStackTrace();
        }

    }

    public static void inputAuthMsg(){
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        if(controller != null) {
                            String str = in.readUTF();
                            if (str.equals("775588")) {
                                setIsAuthorized(true);
                                controller.textAreaChat.appendText("Вы авторизованы\n");
                                controller.showOrHidenOkAndDisButtons(true);
                                inputMsg();
                                break;
                            }

                            if (str != null) {
                                controller.textAreaChat.appendText(str);
                                controller.textAreaChat.appendText("\n");
                            }

                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void inputMsg() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {

                        String str = in.readUTF();
                        if (str.startsWith("/")) {
                            if (str.equals("/end885577")) {
                                controller.textAreaChat.setText("Отключение от сервера");
                                controller.showOrHidenOkAndDisButtons(false);
                                disconnect();
                                break;
                            }

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

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public static void sendAuthMsg(String login, String pass) {
        try {
            String a = "/auth " + login + " " + pass;
            System.out.println(a);
            out.writeUTF(a);
            out.flush();
        } catch (IOException e) {
            controller.textAreaChat.appendText("Ошибка авторизации");

        }
    }

    public static void sendMsg(String message){
        try {
            out.writeUTF(message);
            out.flush();
        }catch (IOException e){
            controller.textAreaChat.appendText("Ошибка отправки сообщения");
        }

    }


}


