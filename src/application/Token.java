package application;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * This class represents a token to be used in the parser. It has fields for the string the token represents,
 * the type of token it is, its associativity, and its precedence. It has methods for applying functions and 
 * operators.
 * @author Mark Kikta
 * @version 0.3
 */
public class Token {
	private String symbol;										// The string that this token represents.
	private TokenType type;										// The type of this token.
	private int precedence;										// The precedence of this token, -1 if not operator.
	private boolean associativity;								// The associativity of this token, -1 if not operator.
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
		
		// Set this token's type.
		if (isNumeric()) {
			type = TokenType.CONSTANT;
		} else if (symbol.equals("x")) {
			type = TokenType.VARIABLE;
		} else if (symbol.equals(",")) {
			type = TokenType.COMMA;
		} else if (symbol.equals("(") || symbol.equals(")")) {
			type = TokenType.PARENTHESES;
		}
		
		/*
		 * If it is an operator, also set its precedence and associativity.
		 * Precedence is based on PEMDAS.
		 * True means left-associative, false means right-associative.
		 */
		else if (OPERATORS.contains(symbol)) {
			type = TokenType.OPERATOR;
			if (symbol.equals("+")) {
				precedence = 1;
				associativity = true;
			} else if (symbol.equals("-")) {
				precedence = 1;
				associativity = true;
			} else if (symbol.equals("*")) {
				precedence = 2;
				associativity = true;
			} else if (symbol.equals("/")) {
				precedence = 2;
				associativity = true;
			} else if (symbol.equals("^")) {
				precedence = 3;
				associativity = false;
			}
		} else if (FUNCTIONS.contains(symbol)) {
			type = TokenType.FUNCTION;
		} 
		
		// If it is not a valid token, set it to this default token type.
		else {
			type = TokenType.NULL;
		}
	}
	
	/**
	 * @return Whether or not the this token is numeric.
	 */
	private boolean isNumeric () {
		
		// If the symbol cannot be parsed as a double, than it is not numeric.
		try {
			Double.parseDouble(symbol);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	/**
	 * @return How many arguments this function takes. -1 if not a function.
	 */
	public int getNumArgs () {
		if (symbol.equals("sin")) {
			return 1;
		} else if (symbol.equals("cos")) {
			return 1;
		} else if (symbol.equals("tan")) {
			return 1;
		} else if (symbol.equals("max")) {
			return 2;
		} else if (symbol.equals("min")) {
			return 2;
		}
		return -1;
	}
	
	/**
	 * Apply the operator corresponding to this token's symbol to the two given tokens.
	 * How it is applied depends on the type of these tokens.
	 * a and b are reversed to reflect the first-in last-out nature of stacks.
	 * @param b The second argument.
	 * @param a The first argument
	 * @return The new function. null if not valid.
	 */
	public Function<Double, Double> operate (Token b, Token a) {
		
		// Variable to hold the value of a if it is a constant.
		double y;
		
		// If a is a variable, check the type of b and use 'x' as the first operand.
		if (a.getType() == TokenType.VARIABLE) {
			
			// If b is a variable, use 'x' for both operands.
			if (b.getType() == TokenType.VARIABLE) {
				if (symbol.equals("+")) {
					return (x) ->  x + x;
				} else if (symbol.equals("-")) {
					return (x) ->  x - x;
				} else if (symbol.equals("*")) {
					return (x) ->  x * x;
				} else if (symbol.equals("/")) {
					return (x) ->  x / x;
				} else if (symbol.equals("^")) {
					return (x) ->  Math.pow(x, x);
				}
			} 
			
			// If b is a constant, use the value of b as the second operand.
			else if (b.getType() == TokenType.CONSTANT) {
				y = Double.parseDouble(b.getSymbol());
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
			} 
			
			// If b is a function token, apply it to 'x,' then use that as the second operand.
			else if (b instanceof FunctionToken) { 
				if (symbol.equals("+")) {
					return (x) ->  x + ((FunctionToken) b).getFunction().apply(x);
				} else if (symbol.equals("-")) {
					return (x) ->  x - ((FunctionToken) b).getFunction().apply(x);
				} else if (symbol.equals("*")) {
					return (x) ->  x * ((FunctionToken) b).getFunction().apply(x);
				} else if (symbol.equals("/")) {
					return (x) ->  x / ((FunctionToken) b).getFunction().apply(x);
				} else if (symbol.equals("^")) {
					return (x) ->  Math.pow(x, ((FunctionToken) b).getFunction().apply(x));
				}
			}
		} 
		
		// If a is a constant, check the type of b and use the value of a as the first operand.
		else if (a.getType() == TokenType.CONSTANT) {
			y = Double.parseDouble(a.getSymbol());
			
			// If b is a variable, then use 'x' as the second operand.
			if (b.getType() == TokenType.VARIABLE) {
				if (symbol.equals("+")) {
					return (x) ->  y + x;
				} else if (symbol.equals("-")) {
					return (x) ->  y - x;
				} else if (symbol.equals("*")) {
					return (x) ->  y * x;
				} else if (symbol.equals("/")) {
					return (x) ->  y / x;
				} else if (symbol.equals("^")) {
					return (x) ->  Math.pow(y, x);
				}
			} 
			
			// If b is a constant, use the value of b as the second operand.
			else if (b.getType() == TokenType.CONSTANT) {
				double z = Double.parseDouble(b.getSymbol());
				if (symbol.equals("+")) {
					return (x) ->  y + z;
				} else if (symbol.equals("-")) {
					return (x) ->  y - z;
				} else if (symbol.equals("*")) {
					return (x) ->  y * z;
				} else if (symbol.equals("/")) {
					return (x) ->  y / z;
				} else if (symbol.equals("^")) {
					return (x) ->  Math.pow(y, z);
				}
			} 
			
			// If b is a function token, apply it to 'x' and use that as the second operand.
			else if (b instanceof FunctionToken) {
				if (symbol.equals("+")) {
					return (x) ->  y + ((FunctionToken) b).getFunction().apply(x);
				} else if (symbol.equals("-")) {
					return (x) ->  y - ((FunctionToken) b).getFunction().apply(x);
				} else if (symbol.equals("*")) {
					return (x) ->  y * ((FunctionToken) b).getFunction().apply(x);
				} else if (symbol.equals("/")) {
					return (x) ->  y / ((FunctionToken) b).getFunction().apply(x);
				} else if (symbol.equals("^")) {
					return (x) ->  Math.pow(y, ((FunctionToken) b).getFunction().apply(x));
				}
			}
		} 
		
		// If a is a function token, check the type of b then apply it to 'x' and use that as the first operand.
		else if (a instanceof FunctionToken) {
			
			// If b is a variable, use 'x' as the second operand.
			if (b.getType() == TokenType.VARIABLE) {
				if (symbol.equals("+")) {
					return (x) -> ((FunctionToken)a).getFunction().apply(x) + x;
				} else if (symbol.equals("-")) {
					return (x) ->  ((FunctionToken)a).getFunction().apply(x) - x;
				} else if (symbol.equals("*")) {
					return (x) ->  ((FunctionToken)a).getFunction().apply(x) * x;
				} else if (symbol.equals("/")) {
					return (x) ->  ((FunctionToken)a).getFunction().apply(x) / x;
				} else if (symbol.equals("^")) {
					return (x) ->  Math.pow(((FunctionToken)a).getFunction().apply(x), x);
				}
			} 
			
			// If b is a constant, use its value as the second operand.
			else if (b.getType() == TokenType.CONSTANT) {
				y = Double.parseDouble(b.getSymbol());
				if (symbol.equals("+")) {
					return (x) -> ((FunctionToken)a).getFunction().apply(x) + y;
				} else if (symbol.equals("-")) {
					return (x) ->  ((FunctionToken)a).getFunction().apply(x) - y;
				} else if (symbol.equals("*")) {
					return (x) ->  ((FunctionToken)a).getFunction().apply(x) * y;
				} else if (symbol.equals("/")) {
					return (x) ->  ((FunctionToken)a).getFunction().apply(x) / y;
				} else if (symbol.equals("^")) {
					return (x) ->  Math.pow(((FunctionToken)a).getFunction().apply(x), y);
				}
			} 
			
			// If b is a function token, apply it to 'x' and use that as the second operand.
			else if (b instanceof FunctionToken) {
				if (symbol.equals("+")) {
					return (x) -> ((FunctionToken)a).getFunction().apply(x) + ((FunctionToken)b).getFunction().apply(x);
				} else if (symbol.equals("-")) {
					return (x) -> ((FunctionToken)a).getFunction().apply(x) - ((FunctionToken)b).getFunction().apply(x);
				} else if (symbol.equals("*")) {
					return (x) -> ((FunctionToken)a).getFunction().apply(x) * ((FunctionToken)b).getFunction().apply(x);
				} else if (symbol.equals("/")) {
					return (x) -> ((FunctionToken)a).getFunction().apply(x) / ((FunctionToken)b).getFunction().apply(x);
				} else if (symbol.equals("^")) {
					return (x) ->  Math.pow(((FunctionToken)a).getFunction().apply(x), ((FunctionToken)b).getFunction().apply(x));
				}
			}
		}
		
		// If the tokens are invalid, return null.
		return null;
	}
	
	/**
	 * Apply the function corresponding to this token's symbol to the given token.
	 * This method is for functions that take one argument.
	 * @param a The argument token.
	 * @return The new function. null if not valid.
	 */
	public Function<Double, Double> applySingleArg (Token a) {
		
		// If a is a variable, use 'x' as the argument.
		if (a.getType() == TokenType.VARIABLE) {
			if (symbol.equals("sin")) {
				return (x) -> Math.sin(x);
			} else if (symbol.equals("cos")) {
				return (x) -> Math.cos(x);
			} else if (symbol.equals("tan")) {
				return (x) -> Math.tan(x);
			}
		} 
		
		// If a is a constant, use its value as the argument.
		else if (a.getType() == TokenType.CONSTANT) {
			double y = Double.parseDouble(a.getSymbol());
			if (symbol.equals("sin")) {
				return (x) -> Math.sin(y);
			} else if (symbol.equals("cos")) {
				return (x) -> Math.cos(y);
			} else if (symbol.equals("tan")) {
				return (x) -> Math.tan(y);
			}
		} 
		
		// If a is a function token, apply it to 'x' and use that as the argument.
		else if (a instanceof FunctionToken) {
			if (symbol.equals("sin")) {
				return (x) -> Math.sin(((FunctionToken) a).getFunction().apply(x));
			} else if (symbol.equals("cos")) {
				return (x) -> Math.cos(((FunctionToken) a).getFunction().apply(x));
			} else if (symbol.equals("tan")) {
				return (x) -> Math.tan(((FunctionToken) a).getFunction().apply(x));
			}
		}
		
		// If the tokens are invalid, return null.
		return null;
	}
	
	/**
	 * Apply the function corresponding to this token's symbol to the given tokens.
	 * This method is for functions that take two arguments.
	 * a and b are reversed to reflect the first-in last-out nature of stacks.
	 * @param b The second argument.
	 * @param a The first argument.
	 * @return
	 */
	public Function<Double, Double> applyTwoArgs (Token b, Token a) {
		
		// Variable to hold the value of a if it is a constant.
		double y;
		
		// Essentially repeat the logic from the operate function, just using different functions.
		if (a.getType() == TokenType.VARIABLE) {
			if (b.getType() == TokenType.VARIABLE) {
				if (symbol.equals("max")) {
					return (x) -> Math.max(x, x);
				} else if (symbol.equals("min")) {
					return (x) -> Math.min(x, x);
				}
			} else if (b.getType() == TokenType.CONSTANT) {
				y = Double.parseDouble(b.getSymbol());
				if (symbol.equals("max")) {
					return (x) -> Math.max(x, y);
				} else if (symbol.equals("min")) {
					return (x) -> Math.min(x, y);
				}
			} else if (b instanceof FunctionToken) {
				if (symbol.equals("max")) {
					return (x) -> Math.max(x, ((FunctionToken) b).getFunction().apply(x));
				} else if (symbol.equals("min")) {
					return (x) -> Math.min(x, ((FunctionToken) b).getFunction().apply(x));
				}
			}
		} else if (a.getType() == TokenType.CONSTANT) {
			y = Double.parseDouble(a.getSymbol());
			if (b.getType() == TokenType.VARIABLE) {
				if (symbol.equals("max")) {
					return (x) -> Math.max(y, x);
				} else if (symbol.equals("min")) {
					return (x) -> Math.min(y, x);
				}
			} else if (b.getType() == TokenType.CONSTANT) {
				double z = Double.parseDouble(b.getSymbol());
				if (symbol.equals("max")) {
					return (x) -> Math.max(y, z);
				} else if (symbol.equals("min")) {
					return (x) -> Math.min(y, z);
				}
			} else if (b instanceof FunctionToken) {
				if (symbol.equals("max")) {
					return (x) -> Math.max(y, ((FunctionToken) b).getFunction().apply(x));
				} else if (symbol.equals("min")) {
					return (x) -> Math.min(y, ((FunctionToken) b).getFunction().apply(x));
				}
			}
		} else if (a instanceof FunctionToken) {
			if (b.getType() == TokenType.VARIABLE) {
				if (symbol.equals("max")) {
					return (x) -> Math.max(((FunctionToken) a).getFunction().apply(x), x);
				} else if (symbol.equals("min")) {
					return (x) -> Math.min(((FunctionToken) a).getFunction().apply(x), x);
				}
			} else if (b.getType() == TokenType.CONSTANT) {
				y = Double.parseDouble(b.getSymbol());
				if (symbol.equals("max")) {
					return (x) -> Math.max(((FunctionToken) a).getFunction().apply(x), y);
				} else if (symbol.equals("min")) {
					return (x) -> Math.min(((FunctionToken) a).getFunction().apply(x), y);
				}
			} else if (b instanceof FunctionToken) {
				if (symbol.equals("max")) {
					return (x) -> Math.max(((FunctionToken) a).getFunction().apply(x), ((FunctionToken) b).getFunction().apply(x));
				} else if (symbol.equals("min")) {
					return (x) -> Math.min(((FunctionToken) a).getFunction().apply(x), ((FunctionToken) b).getFunction().apply(x));
				}
			}
		}
		return null;
	}
	
	/**
	 * @return precedence
	 */
	public int getPrecedence() {
		return precedence;
	}
	
	/**
	 * @return associativity
	 */
	public boolean getAssociativity () {
		return associativity;
	}
	
	/**
	 * @return symbol
	 */
	public String getSymbol () {
		return symbol;
	}
	
	/**
	 * @return type
	 */
	public TokenType getType () {
		return type;
	}
}