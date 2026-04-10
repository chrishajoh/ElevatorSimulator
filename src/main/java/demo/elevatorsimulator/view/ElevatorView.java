package demo.elevatorsimulator.view;

import demo.elevatorsimulator.controller.ElevatorController;
import demo.elevatorsimulator.model.FloorButton;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

/**
 * JavaFX view for the elevator simulator.
 * Builds the UI by creating a JavaFX button for each {@link FloorButton}
 * provided by the controller, and binds each button to its press action.
 *
 * @author Christoffer Johansen
 */
public class ElevatorView {

    private final VBox root = new VBox();

    /**
     * Creates the view and generates a JavaFX button for each floor button (1-9).
     * Each button is wired to call {@link FloorButton#press()} when clicked.
     *
     * @param controller the elevator controller providing the floor buttons
     */
    public ElevatorView(ElevatorController controller) {
        for (FloorButton floorButton : controller.getButtons()) {
            Button btn = new Button("Floor " + floorButton.getFloor());
            btn.setOnAction(e -> floorButton.press());
            root.getChildren().add(btn);
        }
    }

    /**
     * Returns the root layout node to be added to the scene.
     *
     * @return the {@link VBox} containing all floor buttons
     */
    public VBox getRoot() {
        return root;
    }
}
