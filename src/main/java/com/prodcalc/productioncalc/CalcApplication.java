package com.prodcalc.productioncalc;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class CalcApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(CalcApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Расчёт стоимости типовых конструкций");
        stage.setScene(scene);
        Parent root = scene.getRoot();
        root.setStyle("-fx-font-size: 11pt;");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}