package org.manuel.test;

import java.util.Stack;

public class Calculadora {

    /**
     * Defines the precedence of mathematical operators.
     *
     * @param op The operator character.
     * @return An integer representing precedence (higher value means higher precedence).
     */
    private int precedence(char op) {
        if (op == '+' || op == '-') {
            return 1;
        }
        if (op == '*' || op == '/') {
            return 2;
        }
        return 0; // For parentheses or other characters
    }

    /**
     * Applies an operator to two operands.
     *
     * @param op The operator character.
     * @param b  The second operand (right-hand side).
     * @param a  The first operand (left-hand side).
     * @return The result of the operation.
     * @throws UnsupportedOperationException if division by zero is attempted.
     */
    private int applyOp(char op, int b, int a) {
        switch (op) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0) {
                    throw new UnsupportedOperationException("Cannot divide by zero");
                }
                return a / b;
        }
        return 0;
    }

    /**
     * Checks if a character is one of the supported operators.
     * @param c The character to check.
     * @return True if the character is an operator, false otherwise.
     */
    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    /**
     * Pre-processes the expression string:
     * 1. Removes all whitespace.
     * 2. Inserts explicit multiplication operators '*' where implicit multiplication is intended.
     * - Between a digit and an opening parenthesis (e.g., "2(" becomes "2*(").
     * - Between a closing parenthesis and an opening parenthesis (e.g., ")(" becomes ")*(").
     * - Between a closing parenthesis and a digit (e.g., ")2" becomes ")*2").
     *
     * @param expression The raw input expression string.
     * @return The processed expression string.
     */
    private String preprocess(String expression) {
        if (expression == null || expression.trim().isEmpty()) {
            throw new IllegalArgumentException("Expression cannot be null or empty.");
        }
        System.out.println("\nThe expression to pre-process is: " + expression);
        String noSpaces = expression.replaceAll("\\s+", "");
        if (noSpaces.isEmpty()) {
            throw new IllegalArgumentException("Expression results in empty string after removing spaces.");
        }

        StringBuilder newExpr = new StringBuilder();
        newExpr.append(noSpaces.charAt(0)); // Append the first character

        for (int i = 1; i < noSpaces.length(); i++) {
            char prev = noSpaces.charAt(i - 1);
            char current = noSpaces.charAt(i);

            // Insert '*' for implicit multiplications:
            if ((Character.isDigit(prev) && current == '(') || // e.g., "2(" -> "2*("
                (prev == ')' && current == '(') ||             // e.g., ")(" -> ")*("
                (prev == ')' && Character.isDigit(current))) { // e.g., ")2" -> ")*2"
                newExpr.append('*');
            }
            newExpr.append(current);
        }
        return newExpr.toString();
    }

    /**
     * Evaluates a mathematical expression string.
     * Supports operators: +, -, *, /, (, ).
     * Handles operator precedence and parentheses.
     * Handles unary minus for numbers (e.g., -3, 5*-2) and expressions (e.g., -(2+1)).
     * Implicit multiplication (e.g., (2)(3) or 2(3)) is handled by pre-processing.
     *
     * @param str The mathematical expression string.
     * @return The result of the evaluation as a double.
     */
    public int Calculator(String str) {
        String processedExpression = preprocess(str);

        Stack<Integer> values = new Stack<>();
        Stack<Character> ops = new Stack<>();

        for (int i = 0; i < processedExpression.length(); i++) {
            char token = processedExpression.charAt(i);

            // Check if token is the start of a number (positive or negative)
            if (Character.isDigit(token) ||
                (token == '-' &&
                 (i == 0 || processedExpression.charAt(i-1) == '(' || isOperator(processedExpression.charAt(i-1))) && // Unary context
                 (i + 1 < processedExpression.length() && Character.isDigit(processedExpression.charAt(i+1)))      // Followed by a digit
                )
               ) {
                StringBuilder sb = new StringBuilder();
                if (token == '-') {
                    sb.append('-');
                    i++; // Consume the '-'
                }
                while (i < processedExpression.length() && Character.isDigit(processedExpression.charAt(i))) {
                    sb.append(processedExpression.charAt(i));
                    i++;
                }
                values.push(Integer.parseInt(sb.toString()));
                i--; // Correct for the outer loop's i++ increment
            } else if (token == '(') {
                ops.push(token);
            } else if (token == ')') {
                while (!ops.isEmpty() && ops.peek() != '(') {
                    if (values.size() < 2) throw new IllegalArgumentException("Invalid expression: Not enough operands for operator " + ops.peek());
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                }
                if (!ops.isEmpty() && ops.peek() == '(') {
                    ops.pop(); // Pop the opening parenthesis
                } else {
                    throw new IllegalArgumentException("Mismatched parentheses in expression");
                }
            } else if (isOperator(token)) {
                // Handle unary minus for expressions like -(2+3), which becomes 0-(2+3)
                if (token == '-' &&
                    (i == 0 || processedExpression.charAt(i-1) == '(' || isOperator(processedExpression.charAt(i-1))) && // Unary context
                    (i + 1 < processedExpression.length() && processedExpression.charAt(i+1) == '(')                 // Followed by '('
                   ) {
                    values.push(0); // Insert 0 before the unary minus that applies to an expression
                }

                // While stack is not empty, top is not '(', and top has >= precedence than current token
                while (!ops.isEmpty() && ops.peek() != '(' && precedence(ops.peek()) >= precedence(token)) {
                     if (values.size() < 2) throw new IllegalArgumentException("Invalid expression: Not enough operands for operator " + ops.peek());
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                }
                ops.push(token);
            } else {
                 throw new IllegalArgumentException("Invalid character in expression: " + token);
            }
        }

        // Entire expression has been parsed, apply remaining operators to remaining values
        while (!ops.isEmpty()) {
            if (ops.peek() == '(') { // Mismatched parenthesis
                throw new IllegalArgumentException("Mismatched parentheses in expression");
            }
            if (values.size() < 2) throw new IllegalArgumentException("Invalid expression: Not enough operands for operator " + ops.peek());
            values.push(applyOp(ops.pop(), values.pop(), values.pop()));
        }

        // Top of 'values' contains result. If stack is not size 1, something went wrong.
        if (values.size() != 1) {
             throw new IllegalArgumentException("Invalid expression format leading to incorrect final value stack size.");
        }
        return values.pop();
    }

    public static void main(String[] args) {
        Calculadora evaluator = new Calculadora();
        // Test cases from the problem description
        String expr1 = "2+(3-1)*3"; // Expected: 8
        String expr2 = "(2-0)(6/2)"; // Expected: 6

        System.out.println("Expression: " + expr1 + " -> Result: " + evaluator.Calculator(expr1));
        System.out.println("Expression: " + expr2 + " -> Result: " + evaluator.Calculator(expr2));

        // Additional test cases
        String expr3 = "10-2*3"; // Expected: 4
        System.out.println("Expression: " + expr3 + " -> Result: " + evaluator.Calculator(expr3));

        String expr4 = "10/2*3"; // Expected: 15 (10/2=5, 5*3=15)
        System.out.println("Expression: " + expr4 + " -> Result: " + evaluator.Calculator(expr4));

        String expr5 = "2*3+4/2-1"; // Expected: 6+2-1 = 7
        System.out.println("Expression: " + expr5 + " -> Result: " + evaluator.Calculator(expr5));

        // Test cases with unary minus and implicit multiplication
        String expr6 = "-3+4"; // Expected: 1
        System.out.println("Expression: " + expr6 + " -> Result: " + evaluator.Calculator(expr6));

        String expr7 = "5*(-2)"; // Expected: -10
        System.out.println("Expression: " + expr7 + " -> Result: " + evaluator.Calculator(expr7));
        
        String expr8 = "5*-2"; // Expected: -10
        System.out.println("Expression: " + expr8 + " -> Result: " + evaluator.Calculator(expr8));

        String expr9 = "2(-3+1)"; // Expected: 2*(-2) = -4
        System.out.println("Expression: " + expr9 + " -> Result: " + evaluator.Calculator(expr9));

        String expr10 = "(2+3)-(1+1)"; // Expected: 5 - 2 = 3
        System.out.println("Expression: " + expr10 + " -> Result: " + evaluator.Calculator(expr10));
        
        String expr11 = "-(2+3)*2"; // Expected: -5 * 2 = -10
        System.out.println("Expression: " + expr11 + " -> Result: " + evaluator.Calculator(expr11));

        String expr12 = "2*(3+1)*2"; // Expected: 2*4*2 = 16
        System.out.println("Expression: " + expr12 + " -> Result: " + evaluator.Calculator(expr12));

        String expr13 = "(5+5)*(-1)"; // Expected: 10 * -1 = -10
        System.out.println("Expression: " + expr13 + " -> Result: " + evaluator.Calculator(expr13));
        
        String expresion14 = "(5+5)*(-1)"; // Expected: 10 * -1 = -10
        System.out.println("Expression: " + expresion14 + " -> Result: " + evaluator.Calculator(expresion14));
        
        String expresion15 = "6*(4/2)+3*1"; // Expected: 15 = 15
        System.out.println("Expression: " + expresion15 + " -> Result: " + evaluator.Calculator(expresion15));
        
        String expresion16 = "6/3-1"; // Expected: 2 - 1 = 1
        System.out.println("Expression: " + expresion16 + " -> Result: " + evaluator.Calculator(expresion16));

        try {
            String exprInvalid = "2+3*"; // Invalid
            System.out.println("Expression: " + exprInvalid + " -> Result: " + evaluator.Calculator(exprInvalid));
        } catch (Exception e) {
            System.out.println("Expression: Invalid expression (e.g., 2+3*) -> Error: " + e.getMessage());
        }
         try {
            String exprInvalidParen = "2+(3-1"; // Invalid
            System.out.println("Expression: " + exprInvalidParen + " -> Result: " + evaluator.Calculator(exprInvalidParen));
        } catch (Exception e) {
            System.out.println("Expression: Invalid parentheses (e.g., 2+(3-1) -> Error: " + e.getMessage());
        }
    }
}
