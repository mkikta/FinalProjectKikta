package application;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.function.Function;

/**
 * This class has a method to parse a string representation of a mathematical expression
 * using Dijkstra's Shunting-Yard Algorithm. It has a helper method that converts this post-fix
 * expression into a function.
 * @author Mark Kikta
 * @version 0.2
 */
public class Parser {
	
	/**
	 * Tokenizes the input string and puts it into Reverse Polish Notation using 
	 * Dijkstra's Shunting-Yard Algorithm. After that, it turns it into a function
	 * using an evaluation of the post-fix.
	 * @param input The String to be parsed.
	 * @return The resulting function.
	 */
	public static Function<Double, Double> parse (String input) {
		
		// If the string is empty, return null.
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
			if (token.getType() == TokenType.CONSTANT || token.getType() == TokenType.VARIABLE) {
				queue.add(token);
			} 
			
			// If the token is a a function, push it to the operator stack.
			else if (token.getType() == TokenType.FUNCTION) {
				stack.push(token);
			} 
			
			/*
			 *  If the token is an operator, then while an operator is on top of the stack, that operator has greater precedence
			 *  or if it has equal precedence and is left-associative, and it is not a left parentheses, pop the top of the 
			 *  operator stack to the output queue. After this, push the token to the operator stack.
			 */
			else if (token.getType() == TokenType.OPERATOR) {
				while(stack.peek().getType() == TokenType.OPERATOR 
						&& (stack.peek().getPrecedence() > token.getPrecedence() 
							|| (stack.peek().getPrecedence() == token.getPrecedence()
								&& stack.peek().getAssociativity()))
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
				if (stack.peek().getType() == TokenType.FUNCTION) {
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
		
		// Return the evaluation of this post-fix.
		return evaluatePostfix(queue);
	}

	/**
	 * Convert a post-fix expression into a function.
	 * @param queue The post-fix expression to be converted.
	 * @return Function representation of the post-fix.
	 */
	private static Function<Double, Double> evaluatePostfix (Queue<Token> queue) {
		
		// Stack to hold the tokens as conversion takes place.
		Stack<Token> stack = new Stack<Token>();
		
		// For each token in the queue, check its type and perform the requisite actions.
		// Much of the logic lies in the methods called from the Token class.
		for (Token t : queue) {
			
			// If the token is a constant or a variable, push it to the stack.
			if (t.getType() == TokenType.CONSTANT || t.getType() == TokenType.VARIABLE) {
				stack.push(t);
			} 
			
			// If the token is an operator, operate on the top two tokens of the stack.
			else if (t.getType() == TokenType.OPERATOR) {
				stack.push(new FunctionToken(t.operate(stack.pop(), stack.pop())));
			} 
			
			// If the token is a function, apply it to the proper number of tokens from the top of the stack.
			else if (t.getType() == TokenType.FUNCTION) {
				if (t.getNumArgs() == 1) {
					stack.push(new FunctionToken(t.applySingleArg(stack.pop())));
				} else if (t.getNumArgs() == 2) {
					stack.push(new FunctionToken(t.applyTwoArgs(stack.pop(), stack.pop())));
				}
			}
		}
		 
		/*
		 * Return the function from the top of the stack.
		 * We know that the top of the stack will be a FunctionToken if the expression is valid,
		 * so the cast will always work.
		 */
		return ((FunctionToken)stack.pop()).getFunction();
	}
}