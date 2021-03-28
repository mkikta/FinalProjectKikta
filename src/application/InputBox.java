package application;

import java.util.function.Function;

import javafx.event.EventHandler;
import javafx.scene.CacheHint;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * This class represents a place for the user to input functions that they want 
 * graphed. It has fields for the graph area it belongs to and for the number of
 * rows that it contains. It has a private class ButtonBox that is used for each
 * input row. It has methods for removing rows and adding rows.
 * @author Mark Kikta
 * @version 0.1
 */
public class InputBox extends VBox {
	
	private GraphArea ga;	// The graph area that this belongs to.
	private int rows;		// The number of rows in this.
	
	/**
	 * Set the fields and create the initial row.
	 * @param width Width of this InputBox.
	 * @param height Height of this InputBox.
	 * @param ga The graph area that this belongs to.
	 */
	public InputBox (int width, int height, GraphArea ga) {
		
		// Set these values.
		this.ga = ga;
		setHeight(width);
		setWidth(height);
		setSpacing(10);
		
		// Create a new button box and add it to this.
		ButtonBox bb = new ButtonBox();
		getChildren().add(bb);
		rows = 1;
	}
	
	/**
	 * Remove a given row and decrement the number of rows.
	 * @param bb The given row.
	 */
	public void removeRow(ButtonBox bb) {
		getChildren().remove(bb);
		rows--;
	}
	
	/**
	 * Add a row after the given row and increment the number of rows.
	 * @param bb The given row.
	 */
	public void addRow (ButtonBox bb) {
		int index = getChildren().indexOf(bb);
		getChildren().add(index + 1, new ButtonBox());
		rows++;
	}
	
	/**
	 * This private class represents a textfield for entering functions and a button for
	 * deleting/clearing its corresponding textfield. It has fields for its textfield, button,
	 * and the graph currently associated with it. It has methods to add a new row after itself,
	 * remove itself from an input box, and to graph its contents as a function.
	 * @author Mark Kikta
	 * @version 0.1
	 */
	private class ButtonBox extends HBox {
		private TextField tf;	// The textfield for entering functions.
		private Button b;		// The button that deletes/clears this row.
		private Graph g;		// The graph currently associated with this.
		
		/**
		 * Instantiate the textfield and button, set their listeners, and add them
		 * to this.
		 */
		private ButtonBox () {
			setSpacing(0);
			
			// Create a new button and replace it with an x.
			b = new Button();
			ImageView iv = new ImageView("application/x.png");
			
			// These color adjusts will be used when interacting with the button.
			ColorAdjust grey = new ColorAdjust();
			grey.setBrightness(-0.5);
			ColorAdjust white = new ColorAdjust();
			white.setBrightness(1.0);
			
			// When the user presses on the button, it turns grey.
			b.setOnMousePressed(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent e) {
					iv.setEffect(grey);
					iv.setCache(true);
					iv.setCacheHint(CacheHint.SPEED);
				}
			});
			
			/*
			 * When the user releases the button, it turns white clears the textfield,
			 * removes the current graph, and removes this row.
			 */
			b.setOnMouseReleased(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent e) {
					iv.setEffect(white);
					iv.setCache(true);
					iv.setCacheHint(CacheHint.SPEED);
					tf.clear();
					ga.removeGraph(g);
					removeThisRow();
				}
			});
			
			// Finish formatting button.
			iv.setFitHeight(15);
			iv.setPreserveRatio(true);
			b.setGraphic(iv);
			
			// Create a new textfield that graphs its content when the user presses enter.
			tf = new TextField();
			tf.setPromptText("f(x)=");
			tf.setOnKeyPressed( event -> {
				if (event.getCode() == KeyCode.ENTER) {
					addNewRow();
					graph();
				}
			});
			
			// Add these nodes to this button box, and create a style class.
			getChildren().add(tf);
			getChildren().add(b);
			getStyleClass().add("button-box");
		}
		
		/**
		 * Graph the contents of the textfield.
		 * TODO: Make better error handling.
		 */
		private void graph() {
			
			// Remove the current graph.
			ga.removeGraph(g);
			
			// Parse the contents of the textfield, then graph it.
			try {
				Function<Double, Double> f = Parser.parse(tf.getCharacters().toString());
				g = new Graph(f, 0.001, ga);
				ga.addGraph(g);
				g.draw();
			} catch (Exception e) {
				System.out.println("Parser error or invalid input.");
			}
		}

		/**
		 * Call InputBox.removeRow on this if this is not the only button box.
		 */
		private void removeThisRow () {
			if (rows > 1) {
				removeRow(this);
			}
		}
		
		/**
		 * Add a new row below this one.
		 */
		private void addNewRow () {
			addRow(this);
		}
	}
}