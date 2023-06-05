package com.prodcalc.productioncalc;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MaterialDialog implements Initializable {
    @FXML
    private TextField nameField;
    @FXML
    private TextField lendthField;
    @FXML
    private TextField widthField;
    @FXML
    private TextField caliberField;
    @FXML
    private TextField priceField;
    @FXML
    private RadioButton lengthSideFluteRB;
    @FXML
    private RadioButton shortSideFluteRB;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;

    private boolean answer = false;
    private ArrayList<String> namesList;
    private final String errorColor = "tomato";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        nameField.textProperty().addListener((observableValue, s, t1) -> {
           if (namesList.contains(t1) || t1.equals("")) {
               nameField.styleProperty().set("-fx-background-color: " + errorColor);
               okButton.setDisable(true);
           } else {
               nameField.styleProperty().set("");
               okButton.setDisable(false);
           }
        });
        lendthField.textProperty().addListener((observableValue, s, t1) -> {
            floatFieldCheck(lendthField);
        });
    }

    private void floatFieldCheck(TextField textField) {
        if (!textField.getText().matches("-?\\d+(\\.\\d+)?")) {
            textField.styleProperty().set("-fx-background-color: " + errorColor);
            okButton.setDisable(true);
        } else {
            textField.styleProperty().set("");
            okButton.setDisable(false);
        }
    }

    public void onOkAction(ActionEvent actionEvent) {
        answer = true;
        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }

    public void onCancelAction(ActionEvent actionEvent) {
        answer = false;
        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }

    public void setData(ArrayList<String> namesList) {
        this.namesList = namesList;
    }

    public void setData(MaterialProperties receivedMaterial, ArrayList<String> namesList) {
        this.namesList = namesList;
        nameField.setText(receivedMaterial.name);
        lendthField.setText(String.valueOf(receivedMaterial.length));
        widthField.setText(String.valueOf(receivedMaterial.width));
        caliberField.setText(String.valueOf(receivedMaterial.caliber));
        priceField.setText(String.valueOf(receivedMaterial.price));
        if (receivedMaterial.fluteDirection.equals("L"))
            lengthSideFluteRB.setSelected(true);
        else if (receivedMaterial.fluteDirection.equals("W"))
            shortSideFluteRB.setSelected(true);
    }

    public MaterialProperties getData() {
        if (answer) {
            return new MaterialProperties(
                    nameField.getText(),
                    Float.parseFloat(caliberField.getText()),
                    Float.parseFloat(lendthField.getText()),
                    Float.parseFloat(widthField.getText()),
                    Float.parseFloat(priceField.getText()),
                    Float.parseFloat(caliberField.getText()) * 2 / 3,
                    Float.parseFloat(caliberField.getText()) / 3,
                    lengthSideFluteRB.isSelected() ? "L" : "W"
            );
        } else {
            return null;
        }
    }
}
