import java.util.*;

import static java.lang.Double.NaN;
import static java.lang.Math.pow;


/*
 *   A calculator for rather simple arithmetic expressions
 *
 *   This is not the program, it's a class declaration (with methods) in it's
 *   own file (which must be named Calculator.java)
 *
 *   NOTE:
 *   - No negative numbers implemented
 */
public class Calculator {

    // Here are the only allowed instance variables!
    // Error messages (more on static later)
    final static String MISSING_OPERAND = "Missing or bad operand";
    final static String DIV_BY_ZERO = "Division with 0";
    final static String MISSING_OPERATOR = "Missing operator or parenthesis";
    final static String OP_NOT_FOUND = "Operator not found";

    // Definition of operators
    final static String OPERATORS = "+-*/^";

    // Method used in REPL
    double eval(String expr) {
        if (expr.length() == 0) {
            return NaN;
        }
        List<String> tokens = tokenize(expr);
        List<String> postfix = infix2Postfix(tokens);
        return evalPostfix(postfix);
    }

    // ------  Evaluate RPN expression -------------------

    double evalPostfix(List<String> postfix) {
        // TODO
        Deque<Double> stack = new ArrayDeque<>();
        for(String s: postfix) {
            if (OPERATORS.contains(s)) {
                if (stack.size() < 2) {
                    throw new IllegalArgumentException(MISSING_OPERAND);
                } else {
                    double d1 = stack.pop();
                    double d2 = stack.pop();
                    stack.push(applyOperator(s, d1, d2));
                }
            } else {
                stack.push(Double.valueOf(s));
            }
        }
        if (stack.size() > 1) {
            throw new IllegalArgumentException(MISSING_OPERATOR);
        } else {
            return stack.peek();
        }
    }

    double applyOperator(String op, double d1, double d2) {
        switch (op) {
            case "+":
                return d1 + d2;
            case "-":
                return d2 - d1;
            case "*":
                return d1 * d2;
            case "/":
                if (d1 == 0) {
                    throw new IllegalArgumentException(DIV_BY_ZERO);
                }
                return d2 / d1;
            case "^":
                return pow(d2, d1);
        }
        throw new RuntimeException(OP_NOT_FOUND);
    }

    // ------- Infix 2 Postfix ------------------------

    List<String> infix2Postfix(List<String> infix) {
        // TODO
        Deque<String> stack = new ArrayDeque<>();
        List<String> postfix = new ArrayList<>();
        for (String s : infix) {
            if (OPERATORS.contains(s)) {
                if (stack.isEmpty() || "()".contains(stack.peek())) {
                } else {
                    manage(postfix, stack, s);
                }
                stack.push(s);
            } else if ("()".contains(s)) {
                stack.push(s);
                checkParenthes(postfix, stack);

            } else {
                postfix.add(s);
            }
        }
        while (!stack.isEmpty()) {
            if ("()".contains(stack.peek())) {
                throw new IllegalArgumentException(MISSING_OPERATOR);
            }
            postfix.add(stack.peek());
            stack.pop();
        }
        return postfix;
    }

    void manage(List<String> postfix, Deque<String> stack, String s) {
        if (getPrecedence(s) < getPrecedence(stack.peek())) {
            while (getPrecedence(s) < getPrecedence(stack.peek())) {
                postfix.add(stack.pop());
                if (stack.isEmpty() || !OPERATORS.contains(stack.peek())) {
                    break;
                }
            }
        }else if (getAssociativity(s).equals(Assoc.LEFT)) {
            while (getPrecedence(s) == getPrecedence(stack.peek())) {
                postfix.add(stack.pop());
                if (stack.isEmpty() || !OPERATORS.contains(stack.peek())) {
                    break;
                }
            }
        }

    }

    void checkParenthes(List<String> postfix, Deque<String> stack) {
        if (")".contains(stack.peek())) {
            stack.pop();
            while (OPERATORS.contains(stack.peek())) {
                postfix.add(stack.pop());
                if (stack.isEmpty()){
                    break;
                }
            }
            if (stack.isEmpty() || !"(".contains(stack.peek())) {
                throw new IllegalArgumentException(MISSING_OPERATOR);
            } else {
                stack.pop();
            }

        }
    }

    int getPrecedence(String op) {
        if ("+-".contains(op)) {
            return 2;
        } else if ("*/".contains(op)) {
            return 3;
        } else if ("^".contains(op)) {
            return 4;
        } else {
            throw new RuntimeException(OP_NOT_FOUND);
        }
    }

    Assoc getAssociativity(String op) {
        if ("+-*/".contains(op)) {
            return Assoc.LEFT;
        } else if ("^".contains(op)) {
            return Assoc.RIGHT;
        } else {
            throw new RuntimeException(OP_NOT_FOUND);
        }
    }

    enum Assoc {
        LEFT,
        RIGHT
    }

    // ---------- Tokenize -----------------------

    // List String (not char) because numbers (with many chars)
    List<String> tokenize(String expr) {
        // TODO
        List<String> token = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (char c : expr.toCharArray()) {
            if (Character.isDigit(c)) {
                sb.append(c);
            } else {
                if (!sb.toString().isEmpty()) {
                    token.add(sb.toString());
                    sb.delete(0, sb.length());
                }
                if (!Character.isWhitespace(c)) {
                    token.add(Character.toString(c));
                }
            }
        }
        if (!sb.toString().isEmpty()) {
            token.add(sb.toString());
        }
        return token;
    }
}
