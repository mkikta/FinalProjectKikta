package application;

import java.util.function.Function;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;

/**
 * This class represents the graph of a function. It has fields for parallel lists of x- and y-coordinates, a
 * measure of how far between x values the function should be calculated, and the GraphArea that this graph belongs
 * to. It has a method for drawing itself onto its GraphArea.
 * @author Mark Kikta
 * @version 0.5
 */
public class Graph extends Path {
	
	private GraphArea ga;					// The GraphArea that this graph belongs to.
	private Function<Double, Double> func;	// The function that this graph represents.
	
	/**
	 * Set this graph's fields and determine the points on the function.
	 * @param func The function to be graphed.
	 * @param increment How far between x values to calculate the function.
	 * @param ga The GraphArea that this graph belongs to.
	 */
	public Graph (Function<Double, Double> func, GraphArea ga) {
		
		// Set all the fields that can be set easily.
		this.ga = ga;
		this.func = func;
		
		// Set this path's stroke width.
		setStrokeWidth(2);
		
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
		double diff = ga.getXMax() - ga.getXMin();
		
		// Make increments dynamic.
		double increment = diff / 20000;
		
		// Move the path to the first point on the left edge of the scene.
		double x = ga.getXMin() - ga.getXZoom() - ga.getXTempPan() - ga.getXPermaPan();
		double y = func.apply(x);
		double prevY;
		getElements().add(new MoveTo(x * xScale + xTrans, -y * yScale + yTrans));
		
		// For each x value after the first, calculate its y value and plot it.
		// The modifiers on xMin and xMax account for zooming and panning on ga.
		for (x = ga.getXMin() - ga.getXZoom() - ga.getXTempPan() - ga.getXPermaPan() + increment; x <= ga.getXMax() - ga.getXZoom() - ga.getXTempPan() - ga.getXPermaPan(); x += increment) {
			prevY = y;
			
			// Calculate the y-coordinate of the point by applying the given function.
			y = func.apply(x);

			//TODO: Better method for determining discontinuity
			if (Math.abs((y - prevY) / increment) > 999) {
				
				// If the function is discontinuous, merely move to the next point.
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