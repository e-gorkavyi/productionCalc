package com.prodcalc.productioncalc;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CalcController implements Initializable {

    @FXML
    private ListView<String> materialListView;
    @FXML
    private Button addMaterialBtn;
    @FXML
    private Button deleteMaterialBtn;
    @FXML
    private TextField productLenghtTextField;
    @FXML
    private TextField productWidthTextField;
    @FXML
    private TextField productHeightTextField;
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

    @FXML
    void onMaterialListClicked(MouseEvent mouseEvent) throws IOException {
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
            if (mouseEvent.getClickCount() == 2)
                editMaterial();
        }
    }

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
        materialListView.setItems(model.materialViewList);
//        materialListView.getItems().addAll(model.materialNamesList);
//        ObservableList<String> itemList = FXCollections.observableList(model.materialNamesList);
//        materialListView.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
//            @Override
//            public ListCell<String> call(ListView<String> stringListView) {
//                return new ListCell<>() {
//                    @Override
//                    protected void updateItem(String t, boolean bln) {
//                        super.updateItem(t, bln);
//                        if (t != null)
//                            setText(t);
//                    }
//                };
//            }
//        });

        materialListView.getSelectionModel().select(0);
        materialListView.getSelectionModel().selectedItemProperty().addListener((observableValue, s, t1) -> {
            model.selectedMaterial = materialListView.getSelectionModel().getSelectedItem();
            getResult(model);
        });
        productLenghtTextField.textProperty().addListener((observableValue, s, t1) -> {
            model.productLength = intValidator(t1);
            getResult(model);
        });
        productWidthTextField.textProperty().addListener((observableValue, s, t1) -> {
            model.productWidth = intValidator(t1);
            getResult(model);
        });
        productHeightTextField.textProperty().addListener((observableValue, s, t1) -> {
            model.productHeight = intValidator(t1);
            getResult(model);
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

    public void onAddMaterialBtn(ActionEvent actionEvent) throws IOException {
        MaterialDialogWindow materialDialogWindow = new MaterialDialogWindow();
        MaterialProperties materialProperties = materialDialogWindow.display(
                model.getSelectedMaterial(),
                addMaterialBtn.getScene().getWindow(),
                false);
        if (materialProperties == null)
            System.out.println("Empty");
        else {
            if (model.addMaterial(materialProperties)) {
                materialListView.getItems().add(materialProperties.name);
            }
        }
    }

    public void onDeleteMaterialBtn(ActionEvent actionEvent) {
        model.deleteMaterial(model.getSelectedMaterial());
        materialListView.getItems().remove(materialListView.getSelectionModel().getSelectedIndex());
    }

    private void editMaterial() throws IOException {
        MaterialProperties sourceMat = model.getSelectedMaterial();
        MaterialDialogWindow materialDialogWindow = new MaterialDialogWindow();
        MaterialProperties destMaterial = materialDialogWindow.display(
                model.getSelectedMaterial(),
                addMaterialBtn.getScene().getWindow(),
                true);
        if (destMaterial == null)
            System.out.println("Empty");
        else {
            if (model.editMaterial(sourceMat, destMaterial)) {
                materialListView.getItems().set(
                        materialListView.getSelectionModel().getSelectedIndex(), destMaterial.name
                );
                getResult(model);
            }
        }
    }
}

enum ProductTypes {
    F0201,
    F0427,
    F0471,
    BODY_LID
}

class MaterialDialogWindow {
    MaterialProperties display(MaterialProperties materialProperties, Window parent, boolean edit) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("MaterialDialog.fxml"));
        Parent root = loader.load();

        MaterialDialog matController = loader.getController();
        if (edit)
            matController.setData(materialProperties);

        Stage primaryStage = new Stage();
        primaryStage.setTitle("Параметры материала");
        primaryStage.setScene(new Scene(root));
        root.setStyle("-fx-font-size: 11pt;");
        primaryStage.initModality(Modality.WINDOW_MODAL);
        primaryStage.initOwner(parent);

        primaryStage.showAndWait();

        return matController.getData();
    }
}