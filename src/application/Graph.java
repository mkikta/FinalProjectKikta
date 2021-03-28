package application;

import java.util.ArrayList;
import java.util.List;
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
 * @version 0.3
 */
public class Graph extends Path {
	
	private List<Double> xPoints, yPoints;	// Parallel lists of the x- and y-coordinates of the function.
	private double increment;				// How close between x values the function should be calculated.
	private GraphArea ga;					// The GraphArea that this graph belongs to.
	private int startIndex, stopIndex;		// Where to start drawing x values and where to stop drawing x values.
	
	/**
	 * Set this graph's fields and determine the points on the function.
	 * @param func The function to be graphed.
	 * @param increment How far between x values to calculate the function.
	 * @param ga The GraphArea that this graph belongs to.
	 */
	public Graph (Function<Double, Double> func, double increment, GraphArea ga) {
		
		// Set all the fields that can be set easily.
		this.increment = increment;
		this.ga = ga;
		
		// Create two empty ArrayLists to hold the coordinates of points.
		xPoints = new ArrayList<Double>();
		yPoints = new ArrayList<Double>();
		
		// Set this path's stroke width.
		setStrokeWidth(2);
		
		// For each a wide range of points, both on and off the scene, add the point along the function to the lists.
		double x, y;
		for (x = ga.getXMin() - Math.abs(ga.getXMin()) * 100; x <= ga.getXMax() + Math.abs(ga.getXMax()) * 100; x += increment) {
			
			// Set the initial start and stop indices to the last value before xMin and the first value after xMax.
			if (x <= ga.getXMin() - increment) {
				startIndex = xPoints.size();
			} else if (x <= ga.getXMax() + increment) {
				stopIndex = xPoints.size();
			}
			
			// Calculate the y-coordinate of the point by applying the given function.
			y = func.apply(x);
			xPoints.add(x);
			yPoints.add(y);
		}
		
		// Add a new class representing this one to the CSS file.
		getStyleClass().add("graph");
	}
	
	/**
	 * Draw this graph onto its GraphArea.
	 */
	public void draw () {
		
		// Clear the path.
		getElements().clear();
		
		//TODO: determine how to increment start and stop indices.
		// The current value of 1000 is just a proof of concept.
		startIndex -= 1000;
		stopIndex += 1000;
		
		// Create variables for these so the counterpart functions don't have to be repeatedly called.
		double xScale = ga.getXScale();
		double xTrans = ga.getXTranslation();
		double yScale = ga.getYScale();
		double yTrans = ga.getYTranslation();
		
		// Set the first point and add it to this path's list of elements.
		double x = xPoints.get(startIndex);
		double y = yPoints.get(startIndex);
		getElements().add(new MoveTo(x * xScale + xTrans, -y * yScale + yTrans));
		
		// This variable represents the previous y-coordinate, and will be used for determining continuity.
		double prevY;
		
		// For each point after the one already used in the lists of coordinates, add it to the path elements.
		for (int i = startIndex + 1; i < stopIndex; ++i) {
			prevY = y;
			x = xPoints.get(i);
			y = yPoints.get(i);
			
			/*
			 *  If the change between y-coordinates is too steep given a single increment, assume the function
			 *  has a discontinuity there. There is definitely a better way to determine this.
			 */
			if (Math.abs(y - prevY) / increment > 999) {
				
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