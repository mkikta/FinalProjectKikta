package application;

import java.util.function.Function;

import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;

/**
 * This class extends Path from JavaFX. Itrepresents the graph of a function. It has fields for
 * the GraphArea that this graph belongs to and for the function it represents. It has a method 
 * for drawing itself onto its GraphArea.
 * @author Mark Kikta
 * @version 1.0
 */
public class Graph extends Path {
	
	private GraphArea ga;					// The GraphArea that this graph belongs to.
	private Function<Double, Double> func;	// The function that this graph represents.
	
	/**
	 * Set this graph's fields and style.
	 * @param func The function to be graphed.
	 * @param ga The GraphArea that this graph belongs to.
	 * @param color The color of this graph.
	 */
	public Graph (Function<Double, Double> func, GraphArea ga, Color color) {
		
		// Set fields.
		this.ga = ga;
		this.func = func;
		
		// Set this path's stroke width and color.
		setStrokeWidth(2);
		this.setStroke(color);
		
		// Add a new class representing this one to the CSS file.
		getStyleClass().add("graph");
	}
	
	/**
	 * Draw this graph onto its GraphArea.
	 */
	public void draw () {
		
		// Clear the path.
		getElements().clear();
		
		// Create variables for these so the counterpart functions don't have to be repeatedly called.
		double xScale = ga.getXScale();
		double xTrans = ga.getXTranslation();
		double yScale = ga.getYScale();
		double yTrans = ga.getYTranslation();
		
		// Difference between max and min, will always be positive.
		double diff = ga.getXMax() - ga.getXMin();
		
		// Make increments dynamic.
		double increment = diff / 20000;
		
		// Move the path to the first point on the left edge of the scene, applying modifiers accordingly.
		double x = ga.getXMin() - ga.getXZoom() - ga.getXTempPan() - ga.getXPermaPan();
		double y = func.apply(x);
		getElements().add(new MoveTo(x * xScale + xTrans, -y * yScale + yTrans));
		
		// This variable will be used for checking when a function gets too steep to display.
		double prevY;
		
		// For each x value after the first, calculate its y value and plot it.
		// The modifiers on xMin and xMax account for zooming and panning on ga.
		for (x = ga.getXMin() - ga.getXZoom() - ga.getXTempPan() - ga.getXPermaPan() + increment; x <= ga.getXMax() - ga.getXZoom() - ga.getXTempPan() - ga.getXPermaPan(); x += increment) {
			// Save the current value of y as the previous.
			prevY = y;
			
			// Calculate the y-coordinate of the point by applying the given function.
			y = func.apply(x);

			// Check if the function gets too steep.
			if (Math.abs((y - prevY) / increment) > 999) {
				
				// If the function is (probably) discontinuous, merely move to the next point.
				getElements().add(new MoveTo(x * xScale + xTrans, -y * yScale + yTrans));
			} else {
				
				// If the function is continuous, draw a line to the next point instead.
				getElements().add(new LineTo(x * xScale + xTrans, -y * yScale + yTrans));
			}
		}
		
		// Ensure that the graph does not run off its GraphArea.
		setClip(new Rectangle(0, 0, ga.getPrefWidth(), ga.getPrefHeight()));
	}
}