package application;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Side;
import javafx.scene.chart.NumberAxis;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

/**
 * This class represents a pane on which the graphs of functions may be drawn. It has fields for
 * maximum and minimum x and y values, x- and y-axes, lists of subdividing x and y lines, x- and
 * y-translation factors, and x- and y-scale factors. It has methods for adding and removing graphs,
 * updating, zooming, and panning.
 * @author Mark Kikta
 * @version 0.4
 */
public class GraphArea extends Pane {
	private double xMin, xMax;					// The minimum and maximum x values to display.
	private double yMin, yMax;					// The minimum and maximum y values to display.
	private double xIncrement, yIncrement;		// How much distance is between tick marks on the axes.
	
	private NumberAxis xAxis, yAxis;			// The axes of this GraphArea.
	
	private double xTranslation, yTranslation;	// The translation factors that need to be applied to a graph.
	private double xScale, yScale;				// The scale factors that need to be applied to a graph.
	
	private static final double ZOOM = 1.05;	// How quickly to zoom.
	private double xZoom = 0;					// How much offset in the x direction that zooming is responsible for.
	private double yZoom = 0;					// How much offset in the y direction that zooming is responsible for.
	private double xTempPan = 0;				// How much offset in the x direction that current panning is responsible for.
	private double yTempPan = 0;				// How much offset in the y direction that current panning is responsible for.
	private double xPermaPan = 0;				// How much offset in the x direction that previous panning is responsible for.
	private double yPermaPan = 0;				// How much offset in the y direction that previous panning is responsible for.
	
	private InputBox ib;						// Box for user input.
	private List<Graph> graphs;					// List of graphs.
	
	/**
	 * Set this GraphArea's fields, calculate its translation and scale factors, and create and format
	 * its x- and y-axes. Then draw subdividing lines across it.
	 * @param width The width of this GraphArea.
	 * @param height The height of this GraphArea.
	 * @param xMin The minimum x value.
	 * @param xMax The maximum x value.
	 * @param yMin The minimum y value.
	 * @param yMax The maximum y value.
	 */
	public GraphArea (int width, int height, double xMin, double xMax, double xIncrement, double yMin, double yMax, double yIncrement) {
		
		// Set all the fields that can be set easily.
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
		this.xIncrement = xIncrement;
		this.yIncrement = yIncrement;
		ib = new InputBox(height, width, this);
		graphs = new ArrayList<Graph>();
		
		// Set the width and height.
		setWidth(width);
		setHeight(height);
		
		/*
		 * Create the xAxis with the given quantities, display tick marks on its bottom, and do not
		 * display ticks between the set ones.
		 */
		xAxis = new NumberAxis(xMin, xMax, xIncrement);	
		xAxis.setSide(Side.BOTTOM);
		xAxis.setMinorTickVisible(false);
		
		// Repeat the above with the respective values for the y-axis.
		yAxis = new NumberAxis(yMin, yMax, yIncrement);
		yAxis.setSide(Side.RIGHT);
		yAxis.setMinorTickVisible(false);
		
		// Update the Graph Area. This method contains most of the code that used to be in the constructor.
		update();
		
		// Add a new class representing this one to the CSS file.
		getStyleClass().add("graph-area");
	}
	
	/**
	 * Called when the scene needs to be updated. This refactors all fields in accordance
	 * with whatever changes have been made to the scene.
	 */
	public void update () {
		
		// Remove all children from this GraphArea.
		getChildren().clear();
		
		// Create variables for these so they don't have to be repeatedly called.
		double width = getWidth();
		double height = getHeight();
		
		// Change the increments to a default of 10 ticks on screen if too few or too many tick marks are displayed.
		if (xIncrement > (xMax - xMin) / 4 || yIncrement < (yMax - yMin) / 20) {
			xIncrement = (xMax - xMin) / 10;
		}
		if (yIncrement > (yMax - yMin) / 4 || yIncrement < (yMax - yMin) / 20) {
			yIncrement = (yMax - yMin) / 10;
		}
		
		// Set the preferred width and height.
		setPrefWidth(width);
		setPrefHeight(height);
		
		// The scale is equal to the width of the scene divided by the displayed lengths of its axes.
		xScale = width / (xMax - xMin);
		yScale = height / (yMax - yMin);
		
		/*
		 * Graphs need to be translated so that the point (0, 0) lies at the origin of the GraphArea
		 * rather than at the top left corner of the scene. The x translation is equal to the width
		 * of the GraphArea times the absolute value of the displayed length of the x axis on the left 
		 * side of the origin divided by the total displayed length of the x-axis. The y translation is 
		 * equal to the height of the GraphArea times the absolute value of the displayed length of
		 * the y-axis on the top side of the origin divided by the total displayed length of the y-axis.
		 */
		// The offsets due to zooming and panning are also factored in now.
		xTranslation = width * (Math.abs(xMin)  + xZoom  + xTempPan + xPermaPan) / (xMax - xMin);
		yTranslation = height * (Math.abs(yMax)  + yZoom + yTempPan + yPermaPan) / (yMax - yMin) ;
		
		/*
		 * Set the bounds to new values, set the tick Unit, set the width equal to the width of this 
		 * GraphArea, and translate the xAxis. Make sure to adjust the bounds based off of zooming and panning.
		 */
		xAxis.setLowerBound(xMin - xZoom - xTempPan - xPermaPan);
		xAxis.setUpperBound(xMax - xZoom - xTempPan - xPermaPan);
		xAxis.setTickUnit(xIncrement);
		xAxis.setPrefWidth(width);
		xAxis.setLayoutY(yTranslation);
		
		// Repeat for the y-axis with its respective values.
		yAxis.setLowerBound(yMin + yZoom + yTempPan + yPermaPan);
		yAxis.setUpperBound(yMax + yZoom + yTempPan + yPermaPan);
		yAxis.setTickUnit(yIncrement);
		yAxis.setPrefHeight(height);
		yAxis.setLayoutX(xTranslation);
		
		// Instantiate a temporary Line.
		Line l;
				
		// Draw subdividing lines across the GraphArea, first in the x direction.
		for (double i = 0; i < 2 * xMax; i += xIncrement / 2) {
					
			// Account for the scale factor and set the stroke width.
			l = new Line(i * xScale, 0, i * xScale, height);
			l.setStrokeWidth(0.2);
					
			// I could not figure out how to color this with CSS.
			l.setStroke(Color.GREY);
					
			// Add this line to the list of lines and to the list of child nodes.
			getChildren().add(l);
		}
				
		// Repeat for the y direction.
		for (double j = 0; j < 2 * yMax; j += yIncrement / 2) {
			l = new Line(0, j * yScale, width, j * yScale);
			l.setStrokeWidth(0.2);
			l.setStroke(Color.GREY);
			getChildren().add(l);
		}
		
		// Add the axes and input box to the GraphArea.
		getChildren().addAll(xAxis, yAxis);
		
		for (Graph g : graphs) {
			getChildren().add(g);
			try {
				g.draw();
			} catch (Exception e) {
				// InputBox will display error message.
			}
		}
		getChildren().add(ib);
	}
	
	/**
	 * Multiplies the mins and maxes of this Graph Area by a set factor, then translates it
	 * so that the point where the mouse started is still in the same spot.
	 * @param deltaY Which direction the wheel scrolled.
	 * @param x Where on the scene the scroll occurred in the x direction.
	 * @param y Where on the scene the scroll occurred in the y direction.
	 */
	public void zoom (double deltaY, double x, double y) {
		
		// Determine the coordinates of the mouse with respect to the coordinate system.
		double graphX1 = x / getWidth() * (xMax - xMin) - Math.abs(xMax);
		double graphY1 = -y / getHeight() * (yMax - yMin) + Math.abs(yMin);
		
		// If the user scrolled forward zoom in, otherwise zoom out.
		if (deltaY > 0) {
			
			/*
			 * Divide each value by ZOOM or its reciprocal, depending on whether or not
			 * the value is positive or negative.
			 */
			xMax /= xMax > 0 ? ZOOM : 1 / ZOOM;
			xMin /= xMin < 0 ? ZOOM : 1 / ZOOM;
			yMax /= yMax > 0 ? ZOOM : 1 / ZOOM;
			yMin /= yMin < 0 ? ZOOM : 1 / ZOOM;
		} else {
			
			// Do the same as above with multiplication.
			xMax *= xMax > 0 ? ZOOM : 1 / ZOOM;
			xMin *= xMin < 0 ? ZOOM : 1 / ZOOM;
			yMax *= yMax > 0 ? ZOOM : 1 / ZOOM;
			yMin *= yMin < 0 ? ZOOM : 1 / ZOOM;
		}

		// Determine the coordinates of the mouse with respect to the modified coordinate system.
		double graphX2 = x / getWidth() * (xMax - xMin) - Math.abs(xMax);
		double graphY2 = -y / getHeight() * (yMax - yMin) + Math.abs(yMin);
		
		// Adjust these values to account for the change between coordinate position.
		xZoom += graphX2 - graphX1;
		yZoom += graphY1 - graphY2;
		
	}
	
	/**
	 * Translate the Graph Area to account for the distance between where the pan started and
	 * where the mouse currently is located.
	 * @param xStart Where the pan started in the x direction.
	 * @param yStart Where the pan started in the y direction.
	 * @param x Where the pan is currently at in the x direction.
	 * @param y Where the pan is currently at in the y direction.
	 */
	public void pan (double xStart, double yStart, double x, double y) {
		
		// Determine the coordinates of the mouse with respect to the coordinate system.
		x = x / getWidth() * (xMax - xMin) - Math.abs(xMax);
		y = -y / getHeight() * (yMax - yMin) + Math.abs(yMin);
		
		// Set the temporary pan equal to how far the mouse has moved in each direction.
		xTempPan = x - xStart;
		yTempPan = yStart - y;
	}

	/**
	 * When a pan ends, add the temporary pan to the permanent pan values.
	 */
	public void endPan() {
		xPermaPan += xTempPan;
		yPermaPan += yTempPan;
		xTempPan = 0;
		yTempPan = 0;
	}
	
	/**
	 * Add a graph to this GraphArea.
	 * @param g The graph to be added.
	 */
	public void addGraph (Graph g) {
		graphs.add(g);
		update();
	}
	
	/**
	 * Remove a given graph from this GraphArea.
	 * @param g The graph to be removed.
	 */
	public void removeGraph (Graph g) {
		graphs.remove(g);
		update();
	}

	/**
	 * @return xMin;
	 */
	public double getXMin() {
		return xMin;
	}

	/**
	 * @return xMax;
	 */
	public double getXMax() {
		return xMax;
	}

	/**
	 * @return yMin;
	 */
	public double getYMin() {
		return yMin;
	}

	/**
	 * @return yMax
	 */
	public double getYMax() {
		return yMax;
	}

	/**
	 * @return xTranslation
	 */
	public double getXTranslation() {
		return xTranslation;
	}

	/**
	 * @return yTranslation
	 */
	public double getYTranslation() {
		return yTranslation;
	}

	/**
	 * @return xScale
	 */
	public double getXScale() {
		return xScale;
	}

	/**
	 * @return yScale
	 */
	public double getYScale() {
		return yScale;
	}

	/**
	 * @return xZoom
	 */
	public double getXZoom() {
		return xZoom;
	}

	/**
	 * @return xTempPan
	 */
	public double getXTempPan() {
		return xTempPan;
	}

	/**
	 * @return xPermaPan
	 */
	public double getXPermaPan() {
		return xPermaPan;
	}
}