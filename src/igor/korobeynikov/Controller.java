package igor.korobeynikov;

import javafx.event.ActionEvent;
import javafx.scene.control.*;

public class Controller {
    public TextArea textAreaChat;
    public TextField messegeField;
    public Button sendButton;
    public TextField loginField;
    public PasswordField passwordField;
    public Button okButton;
    public TextArea userNickArea;
    public Engine engine;

    public TextField serverAdressField;
    public TextField serverPortField;

    public void messegeFieldAction(ActionEvent actionEvent) {
        textAreaChat.appendText("");
    }




    public void initConnect() {
        try {
            String serverAdress = serverAdressField.getText();
            int port = Integer.parseInt(serverPortField.getText());

            engine = new Engine(serverAdress, port, this);
        }catch (NumberFormatException e){
            textAreaChat.appendText("Введен не корректный порт");
        }
    }


    public void authorization(ActionEvent actionEvent) {
        initConnect();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String login = loginField.getText();
        String password = passwordField.getText();
        if(login.length() != 0)
            engine.sendAuthMsg(login, password);
    }
}
