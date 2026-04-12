module demo.elevatorsimulator {
    requires javafx.controls;
    requires transitive javafx.graphics;  // For animasjon support
    opens demo.elevatorsimulator to javafx.fxml;
    exports demo.elevatorsimulator;
    exports demo.elevatorsimulator.model;
}