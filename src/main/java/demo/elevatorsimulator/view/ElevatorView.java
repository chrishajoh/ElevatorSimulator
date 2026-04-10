package demo.elevatorsimulator.view;

import demo.elevatorsimulator.controller.ElevatorController;
import demo.elevatorsimulator.model.FloorButton;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class ElevatorView {

    private final VBox root = new VBox();

    public ElevatorView(ElevatorController controller) {
        for (FloorButton floorButton : controller.getButtons()) {
            Button btn = new Button("Floor " + floorButton.getFloor());
            btn.setOnAction(e -> floorButton.press());
            root.getChildren().add(btn);
        }
    }

    public VBox getRoot() {
        return root;
    }
}