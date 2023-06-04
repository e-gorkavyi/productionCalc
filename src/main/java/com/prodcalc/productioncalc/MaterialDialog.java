package com.prodcalc.productioncalc;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

public class MaterialDialog {
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

    public void OnOkAction(ActionEvent actionEvent) {
    }

    public void onCancelAction(ActionEvent actionEvent) {
    }


    public void setData(materialProperties receivedMaterial) {
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
}
