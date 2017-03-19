package igor.korobeynikov;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("mainWindow.fxml"));
        primaryStage.setResizable(false);
        primaryStage.setTitle("Chat client");
        primaryStage.setScene(new Scene(root, 489, 529));
        primaryStage.show();

    }

    @Override
    public void stop(){
        System.out.println("Отключение от сервера");
        Engine.disconnect();
    }




}
