package application;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;


/**
 * This class represents a token to be used in the parser. It has a field for the string the token represents.
 * It has methods for getting its associativity; getting its corresponding BinaryFunction, UnaryFunction, or
 * Operator; getting its precedence; getting its symbol; and checking whether it is a function, operator, number
 * or an x.
 * @author Mark Kikta
 * @version 0.1
 */
public class Token {
	private String symbol;										// The string that this token represents.
	private static final List<String> FUNCTIONS = 
			Arrays.asList("sin", "cos", "tan", "max", "min");	// Currently supported functions.
	private static final List<String> OPERATORS = 
			Arrays.asList("+", "-", "*", "/", "^");				// Currently supported operators.
	
	/**
	 * Create a new token with the given symbol.
	 * @param symbol The given symbol.
	 */
	public Token (String symbol) {
		this.symbol = symbol;
	}
	
	/**
	 * @return Whether or not the this token is a number or x.
	 */
	public boolean isNumeric () {
		try {
			Double.parseDouble(symbol);
		} catch (Exception e) {
			return (symbol.equals("x"));
		}
		return true;
	}
	
	/**
	 * @return Whether or not this token is an x.
	 */
	public boolean isX () {
		return (symbol.equals("x"));
	}
	
	/**
	 * @return Whether or not this token is a function.
	 */
	public boolean isFunction () {
		return FUNCTIONS.contains(symbol);
	}
	
	/**
	 * @return Whether or no this token is an operator.
	 */
	public boolean isOperator () {
		return OPERATORS.contains(symbol);
	}

	/**
	 * @return 0 if unary, 1 if binary, -1 if not a function.
	 */
	public int isUnary () {
		if (symbol.equals("sin")) {
			return 0;
		} else if (symbol.equals("cos")) {
			return 0;
		} else if (symbol.equals("tan")) {
			return 0;
		} else if (symbol.equals("max")) {
			return 1;
		} else if (symbol.equals("min")) {
			return 1;
		}
		return -1;
	}
	
	/**
	 * Create a unary operator out of a binary operator.
	 * @param t The argument to use as a built-in.
	 * @return The new function. Null if not a function.
	 */
	public Function<Double, Double> getOperator (Token t) {
		
		// Use the value from t in the functions.
		double y = Double.parseDouble(t.getSymbol());
		if (symbol.equals("+")) {
			return (x) ->  x + y;
		} else if (symbol.equals("-")) {
			return (x) ->  x - y;
		} else if (symbol.equals("*")) {
			return (x) ->  x * y;
		} else if (symbol.equals("/")) {
			return (x) ->  x / y;
		} else if (symbol.equals("^")) {
			return (x) ->  Math.pow(x, y);
		}
		return null;
	}
	
	/**
	 * @return The corresponding unary function. Null if not a unary function.
	 */
	public Function<Double, Double> getUnaryFunction () {
		if (symbol.equals("sin")) {
			return (x) -> Math.sin(x);
		} else if (symbol.equals("cos")) {
			return (x) -> Math.cos(x);
		} else if (symbol.equals("tan")) {
			return (x) -> Math.tan(x);
		}
		return null;
	}
	
	/**
	 * Create a unary function out of a binary function.
	 * @param t The argument to use as a built-in.
	 * @return The new function. Null if not a function.
	 */
	public Function<Double, Double> getBinaryFunction (Token t) {
		
		// Use the value from t in the function.
		double y = Double.parseDouble(t.getSymbol());
		if (symbol.equals("max")) {
			return (x) -> Math.max(x, y);
		} else if (symbol.equals("min")) {
			return (x) -> Math.min(x, y);
		}
		return null;
	}
	
	/**
	 * @return The precedence of the given token using PEMDAS.
	 */
	public int getPrecedence() {
		if (symbol.equals("+")) {
			return 1;
		} else if (symbol.equals("-")) {
			return 1;
		} else if (symbol.equals("*")) {
			return 2;
		} else if (symbol.equals("/")) {
			return 2;
		} else if (symbol.equals("^")) {
			return 3;
		}
		return -1;
	}
	
	/**
	 * @return 0 if operator is left-associative, 1 if right-associative, -1 if not operator.
	 */
	public int getAssociativity () {
		if (symbol.equals("+")) {
			return 0;
		} else if (symbol.equals("-")) {
			return 0;
		} else if (symbol.equals("*")) {
			return 0;
		} else if (symbol.equals("/")) {
			return 0;
		} else if (symbol.equals("^")) {
			return 1;
		}
		return -1;
	}
	
	/**
	 * @return symbol
	 */
	public String getSymbol () {
		return symbol;
	}
}
