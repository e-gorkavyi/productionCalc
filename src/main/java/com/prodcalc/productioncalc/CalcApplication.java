package com.prodcalc.productioncalc;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.util.Properties;

public class CalcApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(CalcApplication.class.getResource("CalcDialog.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Расчёт стоимости типовых конструкций");
        stage.setScene(scene);
//        Parent root = scene.getRoot();
//        root.setStyle("-fx-font-size: 10pt;");

        String appPrefsPath = System.getProperty(
                "user.home") + File.separator +
                ".config" + File.separator +
                "productionCalc" + File.separator +
                "config.conf";

        appPrefsLoad(stage, appPrefsPath);

        stage.show();
        stage.setOnCloseRequest(we -> appPrefsSave(stage, appPrefsPath));
    }

    public void appPrefsLoad(Stage stage, String appPrefsPath) {
        Properties properties = new Properties();

        if (new File(appPrefsPath).exists()) {
            try {
                FileInputStream inputStream = new FileInputStream(appPrefsPath);
                properties.loadFromXML(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            properties.setProperty("WindowHeight", "662");
            properties.setProperty("WindowWidth", "552");
            properties.setProperty("WindowPositionX", "200");
            properties.setProperty("WindowPositionY", "200");
        }

        stage.setHeight(Double.parseDouble(properties.getProperty("WindowHeight")));
        stage.setWidth(Double.parseDouble(properties.getProperty("WindowWidth")));
        stage.setX(Double.parseDouble(properties.getProperty("WindowPositionX")));
        stage.setY(Double.parseDouble(properties.getProperty("WindowPositionY")));
    }

    public void appPrefsSave(Stage stage, String appPrefsPath) {
        Properties properties = new Properties();

        try {
            properties.setProperty("WindowHeight", String.valueOf(stage.getHeight()));
            properties.setProperty("WindowWidth", String.valueOf(stage.getWidth()));
            properties.setProperty("WindowPositionX", String.valueOf(stage.getX()));
            properties.setProperty("WindowPositionY", String.valueOf(stage.getY()));

            File outFile = new File(appPrefsPath);
            outFile.getParentFile().mkdirs();
            outFile.createNewFile();

            OutputStream outputstream = new FileOutputStream(outFile, false);
            properties.storeToXML(outputstream,"App Prefs");
            outputstream.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(appPrefsPath);
        }
    }


    public static void main(String[] args) {
        launch();
    }
}

