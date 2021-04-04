package application;

import java.util.function.Function;

/**
 * This class extends the Token class and is used to represent a function during the conversion
 * of a post-fix expression to a function. It has a field for the function that it represents.
 * @author Mark Kikta
 * @version 0.1
 */
public class FunctionToken extends Token {
	Function<Double, Double> function;	// The function that this represents.
	
	/**
	 * Create a new Function representing a given function.
	 * @param func The given function.
	 */
	public FunctionToken (Function<Double, Double> function) {
		
		// This token's symbol will be empty.
		super("");
		this.function = function;
	}
	
	/**
	 * @return function
	 */
	public Function<Double, Double> getFunction () {
		return function;
	}
}
