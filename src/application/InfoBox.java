package application;

import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

/**
 * This class will be a button that displays info on how to use the graphing
 * calculator when clicked on. I was too busy to implement this this week.
 * @author Mark Kikta
 * @version 0.1
 */
public class InfoBox extends Button {
	ImageView qm = new ImageView("application/qm.png");
	
	public InfoBox () {
		setGraphic(qm);
	}
	
	/*
	 * Text that will be displayed:
	 * Write your function as an expression of x. Type spaces between all 
	 * characters and symbols. Currently Supported functions include: 
	 * "abs", "acos", "asin", "atan", "cbrt", "ceil", "cos", "cosh", "exp", "floor", "log", "ln", "max", "min", "round", "sin", "sinh", "sqrt","tan", "tanh"
	 * Single-argument functions should be entered in the form "sin ( x )."
	 * Two-argument functions should be entered "max ( x , 2 )."
	 */
}
