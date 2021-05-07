package application;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

/**
 * This class is a button that displays info on how to use the graphing
 * calculator when clicked on.
 * @author Mark Kikta
 * @version 0.1
 */
public class InfoBox extends Button {
	private ImageView qm;	// The image of the button
	
	/**
	 * Create an info box belonging to the given input box.
	 * @param ib The given input box.
	 */
	public InfoBox (InputBox ib) {
		
		// Create and format the graphic for the button.
		qm = new ImageView("application/qm.png");;
		ColorAdjust white = new ColorAdjust();
		white.setBrightness(1.0);
		qm.setFitHeight(30);
		qm.setPreserveRatio(true);
		qm.setEffect(white);
		qm.setOpacity(0.7);
		setGraphic(qm);
		
		// When the mouse is pressed, display the info.
		setOnMousePressed (new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				ib.displayInfo(true);
			}
		});
		
		// When the mouse leaves the button, remove the info.
		setOnMouseExited (new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				ib.displayInfo(false);
			}
		});
		
		// When the mouse leaves the button, remove the info.
		setOnMouseReleased (new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				ib.displayInfo(false);
			}
		});
	}
}
