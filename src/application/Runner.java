package application;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

/**
 * This program displays a graph area and an input box for users to enter a function
 * to be graphed. The graph area is able to be smoothly zoomed and scrolled on.
 * @author Mark Kikta
 * @version 0.7
 */
public class Runner extends Application {
	
	private double xPanStart, yPanStart;	// These are used to keep track of where a pan starts.
	
	@Override
	/**
	 * This displays a GraphArea, adds some sample graphs to it, attaches
	 * the CSS file, and displays it on a fixed-size stage.
	 * @param stage The main stage of the application.
	 */
	public void start(Stage stage) {
		
		// Run it all in a try-catch block to catch weird JavaFX errors.
		try {
			
			// Create a new GraphArea.
			GraphArea ga = new GraphArea(800, 800,-10, 10, 2, -10, 10, 2);
			
			// Create a new Scene.
			Scene scene = new Scene(ga);
			
			
			// Update the graphArea, re-add the graph, and redraw it when the scene is resized.
			scene.heightProperty().addListener(event -> {ga.update();});
		    scene.widthProperty().addListener(event -> {ga.update();});
		    
		    // Create an event handler for zooming
		    EventHandler<ScrollEvent> zoomer = new EventHandler<ScrollEvent>() {
		    	@Override
		    	// When the user scrolls, call the Graph Area's zoom method, then update it.
		    	public void handle(ScrollEvent e) {
		    		ga.zoom(e.getDeltaY(), e.getX(), e.getY());
		    		ga.update();
		    	}
		    };
		    scene.addEventFilter(ScrollEvent.ANY, zoomer);
		    
		    // The next three event handlers are all necessary for panning.
		    // This one is called whenever a pan starts.
		    EventHandler<MouseEvent> panStart = new EventHandler<MouseEvent>() {
		    	@Override
		    	// Get the coordinates of where the mouse is when the pan starts.
		    	public void handle (MouseEvent e) {
		    		
		    		/*
		    		 *  Convert the location given by the mouse event (which is relative to the width and
		    		 *  height of the scene) to be relative to the coordinates of the graph area.
		    		 */
		    		xPanStart = e.getX() / ga.getWidth() * (ga.getXMax() - ga.getXMin()) - Math.abs(ga.getXMax());		    		
		    		yPanStart = -e.getY() / ga.getHeight() * (ga.getYMax() - ga.getYMin()) + Math.abs(ga.getYMin());
		    	}
		    };
		    scene.addEventFilter(MouseEvent.MOUSE_PRESSED, panStart);
		   
		    // This event caller is called at the end of a pan.
		    EventHandler<MouseEvent> panEnd = new EventHandler<MouseEvent>() {
		    	@Override
		    	// Call the Graph Area's pan ending method.
		    	public void handle (MouseEvent e) {
		    		ga.endPan();
		    	}
		    };
		    scene.addEventFilter(MouseEvent.MOUSE_RELEASED, panEnd);
		    
		    // This event handler is called while the user is panning.
		    EventHandler<MouseEvent> panner = new EventHandler<MouseEvent>() {
		    	@Override
		    	// Pan the graph based on where the pan started and where the mouse currently is.
		    	public void handle(MouseEvent e) {
		    		ga.pan(xPanStart, yPanStart, e.getX(), e.getY());
		    		ga.update();
		    	}
		    };
		    scene.addEventFilter(MouseEvent.MOUSE_DRAGGED, panner);
			
		    
		    // Attach the CSS file, add the scene to the stage, format the stage, and finally show the stage.
	    	scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
	 	    stage.setTitle("Graphing Calculator--Mark Kikta");
	 	    stage.setScene(scene);
	 	    stage.show();
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	};
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
