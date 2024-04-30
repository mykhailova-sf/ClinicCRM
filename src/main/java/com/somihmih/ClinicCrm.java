package com.somihmih;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class ClinicCrm extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClinicCrm.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1138, 990);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
    }

    @Override
    public void stop(){
        System.out.println("Stage is closing");
    }

    public static void main(String[] args) {
        launch();
    }
}