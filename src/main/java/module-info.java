module demo.elevatorsimulator {
    requires javafx.controls;
    requires javafx.fxml;


    opens demo.elevatorsimulator to javafx.fxml;
    exports demo.elevatorsimulator;
}