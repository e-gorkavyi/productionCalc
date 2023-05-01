module com.prodcalc.produclioncalc {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.prodcalc.productioncalc to javafx.fxml;
    exports com.prodcalc.productioncalc;
}
