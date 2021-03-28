package application;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.function.Function;

/**
 * This class has a method to parse a string representation of a mathematical expression
 * using Dijkstra's Shunting-Yard Algorithm. It then returns it as a function.
 * @author Mark Kikta
 * @version 0.1
 */
public class Parser {
	
	/**
	 * Tokenizes the input string and puts it into Reverse Polish Notation using 
	 * Dijkstra's Shunting-Yard Algorithm. After that, it turns it into a function
	 * using an evaluation of the postfix.
	 * Currently can only handle only one x in the whole expression.
	 * It is also limited by the amount of tokens that are currently recognized.
	 * @param input The String to be parsed.
	 * @return The resulting function.
	 */
	public static Function<Double, Double> parse (String input) {
		
		if (input.equals("")) {
			return null;
		}
		
		// Split the input into tokens using whitespace as a delimiter.
		StringTokenizer st = new StringTokenizer(input);
		
		// Data structures necessary for the algorithm.
		Queue<Token> queue = new LinkedList<Token>();
		Stack<Token> stack = new Stack<Token>();
		
		// Make a placeholder value for the head of the stack.
		Token placeholder = new Token("NULL");
		stack.add(placeholder);
		
		// Create a variable for the tokens.
		Token token;
		
		// Go through each token in the input.
		while (st.hasMoreTokens()) {
			token = new Token(st.nextToken());
			
			// If the token is a number (or an x) add push it to the output queue.
			if (token.isNumeric()) {
				queue.add(token);
			} 
			
			// If the token is a a function, push it to the operator stack.
			else if (token.isFunction()) {
				stack.push(token);
			} 
			
			/*
			 *  If the token is an operator, then while an operator is on top of the stack, that operator has greater precedence
			 *  or if it has equal precedence and is left-associative, and it is not a left parentheses, pop the top of the 
			 *  operator stack to the output queue. After this, push the token to the operator stack.
			 */
			else if (token.isOperator()) {
				while(stack.peek().isOperator() 
						&& (stack.peek().getPrecedence() > token.getPrecedence() 
							|| (stack.peek().getPrecedence() == token.getPrecedence()
								&& stack.peek().getAssociativity() == 0))
						&& !stack.peek().getSymbol().equals("(")) {
					queue.add(stack.pop());
				}
				stack.push(token);
			} 
			
			// If the token is a left parentheses, push it to the operator stack.
			else if (token.getSymbol().equals("(")) {
				stack.push(token);
			} 
			
			/*
			 * If the token is a right parentheses, pop everything between it and its matching left parentheses
			 * from the operator stack onto the output queue, pop it from the operator stack, and pop the next
			 * token from the operator stack to the output queue if it is a function.
			 */
			else if (token.getSymbol().equals(")")) {
				while (!stack.peek().getSymbol().equals("(")) {
					queue.add(stack.pop());
				}
				if (stack.peek().getSymbol().equals("(")) {
					stack.pop();
				}
				if (stack.peek().isFunction()) {
					queue.add(stack.pop());
				}
			}
		}
		
		// Remove the placeholder from the operator stack.
		stack.remove(placeholder);
		
		// Pop the rest of the operator stack to the output queue.
		while (!stack.empty()) {
				queue.add(stack.pop());
		}
		
		// Check if the algorithm worked correctly.
		/* for (Token t : queue) {
			System.out.print(t.getSymbol() + " ");
		}*/
		
		// Create necessary variables for turning the postfix notation into a function.
		// TODO: Get this working totally properly.
		Token a, b;
		Function<Double, Double> op;
		Function<Double, Double> result = (x) -> x;	// The identity function, I:R -> R s.t. I(x) = x.
		
		// Go through each element in the queue.
		while (!queue.isEmpty()) {
			
			// If the token is a number or an x, push it to the stack.
			if (queue.peek().isNumeric()) {
				stack.push(queue.remove());
			} 
			
			// If the token is a function, check whether it is unary or not, and act accordingly.
			else if (queue.peek().isFunction()) {
				
				/*
				 * If the function is unary, pop the top of the stack and apply it to that, and push the
				 * value to the stack.
				 * If it is an x, compose this function with the result function, and push an x to the
				 * stack.
				 */
				if (queue.peek().isUnary() == 0) {
					a = stack.pop();
					op = queue.peek().getUnaryFunction();
					queue.remove();
					if (a.isX()) {
						result = result.andThen(op);
						stack.push(new Token("x"));
					} else {
						stack.push(new Token(op.apply(Double.parseDouble(a.getSymbol())).toString()));
					}
				} 
				
				/*
				 * If the function is not unary, pop the top two elements of the stack. If neither is x, 
				 * apply it to both tokens, and push the resulting value to the stack. 
				 * If either is an x, then use the other value as an argument of the function, so it may be 
				 * used as a unary operator. Then, compose it with the result function and push an x to the
				 * stack.
				 */
				else if (queue.peek().isUnary() > 0) {
					b = stack.pop();
					a = stack.pop();
					if (a.isX()) {
						op = queue.peek().getBinaryFunction(b);
						queue.remove();
						result = result.andThen(op);
						stack.push(new Token("x"));
					} else if (b.isX()) {
						op = queue.peek().getBinaryFunction(a);
						queue.remove();
						result = result.andThen(op);
						stack.push(new Token("x"));
					} else {
						
						// Check associativity for which token to use as an argument.
						if (queue.peek().getAssociativity() == 0) {
							op = queue.peek().getBinaryFunction(b);
							stack.push(new Token(op.apply(Double.parseDouble(a.getSymbol())).toString()));
						} else {
							op = queue.peek().getBinaryFunction(a);
							stack.push(new Token(op.apply(Double.parseDouble(b.getSymbol())).toString()));
						}
					}
				}
			} 
			
			/*
			 * If the token is an operator, repeat the logic for a non-unary function
			 * using the methods and values corresponding to an operator instead.
			 */
			else if (queue.peek().isOperator()) {
				b = stack.pop();
				a = stack.pop();
				if (a.isX()) {
					op = queue.peek().getOperator(b);
					queue.remove();
					result = result.andThen(op);
					stack.push(new Token("x"));
				} else if (b.isX()) {
					op = queue.peek().getOperator(a);
					queue.remove();
					result = result.andThen(op);
					stack.push(new Token("x"));
				} else {
					if (queue.peek().getAssociativity() == 0) {
						op = queue.peek().getBinaryFunction(b);
						stack.push(new Token(op.apply(Double.parseDouble(a.getSymbol())).toString()));
					} else {
						op = queue.peek().getBinaryFunction(a);
						stack.push(new Token(op.apply(Double.parseDouble(b.getSymbol())).toString()));
					}
				}
			}
		}
		
		// Return the composite function.
		return result;
	}
}
