package com.prodcalc.productioncalc;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class CalcController implements Initializable {

    @FXML
    private ListView<String> materialListView;
    @FXML
    private Button addMaterialBtn;
    @FXML
    private Button deleteMaterialBtn;
    @FXML
    private ToggleGroup productType;
    @FXML
    private RadioButton f0201;
    @FXML
    private RadioButton f0427;
    @FXML
    private RadioButton f0471;
    @FXML
    private RadioButton body_lid;
    @FXML
    private ToggleGroup selectPrint;
    @FXML
    private ToggleGroup selectHand;
    @FXML
    private TextField productLenghtTextField;
    @FXML
    private TextField productWidthTextField;
    @FXML
    private TextField productHeightTextField;
    @FXML
    private RadioButton printYesRadio;
    @FXML
    private RadioButton printNoRadio;
    @FXML
    private RadioButton handlerYesRadio;
    @FXML
    private RadioButton handlerNoRadio;
    @FXML
    private Label cutTimeLabel;
    @FXML
    private Label sheetSizeLabel;
    @FXML
    private Label partSizeLabel;
    @FXML
    private Label partOnSheetLabel;
    @FXML
    private Label priceLabel;


    private CalculatorModel model = new CalculatorModel();

    public void getResult(CalculatorModel model) {
        model.go();
        cutTimeLabel.setText(model.cutTimeTxt);
        sheetSizeLabel.setText(model.sheetSizeTxt);
        partSizeLabel.setText(model.partSizeTxt);
        partOnSheetLabel.setText(model.partOnSheetTxt);
        priceLabel.setText(model.priceTxt);
    }

    public void onF0201Change(ActionEvent actionEvent) {
        model.boxType = ProductTypes.F0201;
        getResult(model);
    }

    public void onF0427Change(ActionEvent actionEvent) {
        model.boxType = ProductTypes.F0427;
        getResult(model);
    }

    public void onF0471Change(ActionEvent actionEvent) {
        model.boxType = ProductTypes.F0471;
        getResult(model);
    }

    public void onBodyLidChange(ActionEvent actionEvent) {
        model.boxType = ProductTypes.BODY_LID;
        getResult(model);
    }

    public int intValidator(String newValue) {
        try {
            return Integer.parseInt(newValue);
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        materialListView.getItems().addAll(model.materialNamesList);
        materialListView.getSelectionModel().select(0);
        materialListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                model.selectedMaterial = materialListView.getSelectionModel().getSelectedItem();
                getResult(model);
            }
        });
        productLenghtTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                model.productLength = intValidator(t1);
                getResult(model);
            }
        });
        productWidthTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                model.productWidth = intValidator(t1);
                getResult(model);
            }
        });
        productHeightTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                model.productHeight = intValidator(t1);
                getResult(model);
            }
        });

        getResult(model);

    }

    public void onPrintYes(ActionEvent actionEvent) {
        model.printOn = true;
        getResult(model);
    }

    public void onPrintNo(ActionEvent actionEvent) {
        model.printOn = false;
        getResult(model);
    }

    public void onHandlerYes(ActionEvent actionEvent) {
        model.handlerOn = true;
        getResult(model);
    }

    public void onHandlerNo(ActionEvent actionEvent) {
        model.handlerOn = false;
        getResult(model);
    }

    public void onAddMateriaBtn(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MaterialDialog.fxml"));
        Parent root = loader.load();

        MaterialDialog matController = loader.getController();
        matController.setData(model.getSelectedMaterial());

        Stage primaryStage = new Stage();
        primaryStage.setTitle("Параметры материала");
        primaryStage.setScene(new Scene(root));
        root.setStyle("-fx-font-size: 11pt;");
        primaryStage.initModality(Modality.WINDOW_MODAL);
        primaryStage.initOwner(addMaterialBtn.getScene().getWindow());

        primaryStage.show();
    }

    public void onDeleteMaterialBtn(ActionEvent actionEvent) {
    }
}

enum ProductTypes {
    F0201,
    F0427,
    F0471,
    BODY_LID
}