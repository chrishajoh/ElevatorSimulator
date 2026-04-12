module demo.elevatorsimulator {
    requires javafx.controls;
    requires transitive javafx.graphics;
    opens demo.elevatorsimulator to javafx.fxml;
    exports demo.elevatorsimulator;
    exports demo.elevatorsimulator.model;
    exports demo.elevatorsimulator.view;
    exports demo.elevatorsimulator.controller;
}