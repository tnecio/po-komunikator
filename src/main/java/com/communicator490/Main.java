package com.communicator490;

import com.communicator490.communicator.Communicator;
import com.communicator490.controllers.mainWindow.mainWindowController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private static Communicator communicator;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/communicator490/fxml/mainWindow/mainWindow.fxml"));
        Parent root = loader.load();
        mainWindowController controller = loader.getController();
        controller.setStage(primaryStage);
        controller.setCommunicator(communicator);
        primaryStage.setTitle("Komunikator490");
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/com/communicator490/css/mainWindow.css").toExternalForm());
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        communicator = new Communicator();
        launch(args);
    }
}