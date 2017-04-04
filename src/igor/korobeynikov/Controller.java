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
    public Button disconBut;

    public void messegeFieldAction(ActionEvent actionEvent) {
        String message = messegeField.getText();
        engine.sendMsg(message);
        messegeField.setFocusTraversable(true);
        messegeField.setText("");
    }



    public void initConnect() {
        try {
            if(engine == null) {
                String serverAdress = serverAdressField.getText();
                int port = Integer.parseInt(serverPortField.getText());

                engine = new Engine(serverAdress, port, this);
            }
        }catch (NumberFormatException e){
            textAreaChat.appendText("Введен не корректный порт");
        }
    }


    public void authorization(ActionEvent actionEvent) {
        if(!engine.getIsIsAuthorized()) {
            initConnect();

            String login = loginField.getText();
            String password = passwordField.getText();
            if (login.length() != 0 && password.length() != 0) {
                engine.sendAuthMsg(login, password);
            }
        }
    }


    public void disconnectServer(ActionEvent actionEvent) {

    }

    public void showOrHidenOkAndDisButtons(boolean isAuth){
        if(isAuth){
            loginField.setVisible(false);
            passwordField.setVisible(false);
            okButton.setVisible(false);
            disconBut.setVisible(true);
        } else {
            loginField.setVisible(true);
            passwordField.setVisible(true);
            okButton.setVisible(true);
            disconBut.setVisible(false);
        }
    }
}
