/*
 * Copyright (C) 2023 Tony Luken <tonyluken62+stringmathexpressionevaluator.gmail.com>
 * 
 * This file is part of StringMathExpressionEvaluator.
 * 
 * StringMathExpressionEvaluator is free software: you can redistribute it and/or modify it under the 
 * terms of the GNU General Public License as published by the Free Software Foundation, either 
 * version 3 of the License, or (at your option) any later version.
 * 
 * StringMathExpressionEvaluator is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
 * PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with 
 * StringMathExpressionEvaluator. If not, see <http://www.gnu.org/licenses/>.
 */

package stringMathExpressionEvaluator;

import java.util.ArrayList;
import java.util.List;

/**
 * A class for calculating the numerical value of math expressions given as strings
 */
public class StringMathExpressionEvaluator {
    private double angleConversion = 1.0;
    private String str;
    private int idx = -1;
    private int ch;
    
    /**
     * Evaluates the given math expression and returns its numerical value. See the link below for
     * more information on available operators and functions that can be used in the expression. 
     * @param expression - the math expression as a string
     * @return the numerical value of the expression
     * @throws InvalidMathExpressionException if the expression could not be evaluated
     * @see <a href="https://github.com/tonyluken/StringMathExpressionEvaluator/wiki">StringMathExpressionEvaluator Wiki</a> for a complete list of operators and functions
     */
    public double evaluate(String expression) throws InvalidMathExpressionException {
        this.str = expression;
        idx = -1;
        nextChar();
        double x = parseRelation();
        if (idx < str.length()) {
            throw new InvalidMathExpressionException("Unexpected character: '" + (char)ch + "' at index " + idx);
        }
        return x;
    }
    
    /**
     * Sets degree mode. In degree mode, all trig functions expect their input arguments to be 
     * in degrees and all inverse trig functions return their result in degrees.
     * @see #isDegreeMode()
     * @see #setRadianMode()
     * @see #isRadianMode()
     */
    public void setDegreeMode() {
        angleConversion = Math.PI / 180.0;
    }
    
    /**
     * Checks to see if degree mode is set.
     * @return true if in degree mode false otherwise
     * @see #setDegreeMode()
     * @see #setRadianMode()
     * @see #isRadianMode()
     */
    public boolean isDegreeMode() {
        return angleConversion < 0.5;
    }
    
    /**
     * Sets radian mode (the default). In radian mode, all trig functions expect their input 
     * arguments to be in radians and all inverse trig functions return their result in radians.
     * @see #isRadianMode()
     * @see #setDegreeMode()
     * @see #isDegreeMode()
     */
    public void setRadianMode() {
        angleConversion = 1.0;       
    }
    
    /**
     * Checks to see if radian mode is set (the default)
     * @return true if in radian mode false otherwise
     * @see #setRadianMode()
     * @see #setDegreeMode()
     * @see #isDegreeMode()
     */
    public boolean isRadianMode() {
        return angleConversion > 0.5;
    }
    
    /**
     * Moves the character pointer to the next character in the string
     */
    private void nextChar() {
        idx++;
        if (idx < str.length()) {
            ch = str.charAt(idx);
        }
        else {
            ch = -1;
        }
    }
    
    /**
     * Advances the character pointer to the next non-whitespace character in the string and tests 
     * its value to see if it matches the given character. If the character matches, the character 
     * pointer is advanced to the next non-whitespace character in the string and true is returned. 
     * Otherwise, the character pointer remains pointing at the non-matched character and false is 
     * returned.
     * @param charToConsume - the character to test for in the string
     * @return true if the character matches otherwise false
     */
    private boolean consume(int charToConsume) {
        while (Character.isWhitespace(ch)) {
            nextChar();
        }
        if (ch == charToConsume) {
            nextChar();
            return true;
        }
        return false;
    }
    
    // Grammar:
    // relation = expression | expression `==` relation | expression `!=` relation
    //     | expression `>` expression | expression `>=` expression 
    //     | expression `<` expression | expression `<=` expression
    // expression = term | expression `+` term | expression `-` term
    // term = factor | term `*` factor | term `/` factor | term `%` factor
    // factor = `+` factor | `-` factor | `(` expression `)` | number
    //        | function | factor `^` factor
    // function = functionName `(` relationList `)`
    // relationList = relation | relation `,` relationList
    
    /**
     * Parses the string for a number starting at the character pointer. The character pointer 
     * is left pointing at the next non-whitespace character beyond the number.
     * @return the numerical value of the number
     * @throws InvalidMathExpressionException if a valid number is not found
     */
    private double parseNumber() throws InvalidMathExpressionException {
        int startIdx = this.idx;
        boolean pointFound = false;
        boolean exponent = false;
        boolean exponentStarting = false;
        while (Character.isDigit(ch) || (!pointFound && !exponent && ch == '.') || 
                (!exponent && (ch == 'e' || ch == 'E')) ||
                (exponentStarting && (ch == '+' || ch == '-'))) {
            pointFound = pointFound || ch == '.';
            if (exponent && exponentStarting) {
                exponentStarting = false;
            }
            if (!exponent && (ch == 'e' || ch == 'E')) {
                exponent = true;
                exponentStarting = true;
            }
            nextChar();
        }
        try {
            double x = Double.parseDouble(str.substring(startIdx, this.idx));
            return x;
        }
        catch (NumberFormatException ex) {
            throw new InvalidMathExpressionException("Invalid number: " + str.substring(startIdx, this.idx) +
                    " at index " + startIdx);
        }
        
    }
    
    /**
     * Parses the string for a function starting at the character pointer. The character pointer is
     * left pointing to the next non-whitespace after the function.
     * @return the numerical value of the function
     * @throws InvalidMathExpressionException if the function is unknown or there is a problem with
     * one or more of the function's arguments
     */
    private double parseFunction() throws InvalidMathExpressionException {
        int startIdx = this.idx;
        while (Character.isLetterOrDigit(ch)) {
            nextChar();
        }
        String func = str.substring(startIdx, this.idx).toLowerCase();
        Double[] args;
        if (consume('(')) {
            args = parseRelationList();
            if (!consume(')')) {
                throw new InvalidMathExpressionException(
                        "Missing ')' after argument to " + func + " at index " + idx);
            }
        } else {
            throw new InvalidMathExpressionException(
                    "Missing '(' after " + func + " at index " + idx);
        }
        switch (args.length) {
            case 0:
                switch (func) {
                    case "pi":
                        return Math.PI;
                    case "e":
                        return Math.E;
                    default:
                        throw new InvalidMathExpressionException("Unknown function: " + func + "() at index " + startIdx);    
                }
            case 1:
                switch (func) {
                    case "abs":
                        return Math.abs(args[0]);
                    case "ceil":
                        return Math.ceil(args[0]);
                    case "floor":
                        return Math.floor(args[0]);
                    case "round":
                        return Math.round(args[0]);
                    case "signum":
                        return Math.signum(args[0]);
                    case "sqrt":
                        return Math.sqrt(args[0]);
                    case "cbrt":
                        return Math.cbrt(args[0]);
                    case "sin":
                        return Math.sin(args[0]*angleConversion);
                    case "cos":
                        return Math.cos(args[0]*angleConversion);
                    case "tan":
                        return Math.tan(args[0]*angleConversion);
                    case "asin":
                        return Math.asin(args[0])/angleConversion;
                    case "acos":
                        return Math.acos(args[0])/angleConversion;
                    case "atan":
                        return Math.atan(args[0])/angleConversion;
                    case "sinh":
                        return Math.sinh(args[0]);
                    case "cosh":
                        return Math.cosh(args[0]);
                    case "tanh":
                        return Math.tanh(args[0]);
                    case "asinh":
                        return Math.log(args[0] + Math.sqrt(args[0]*args[0] + 1));
                    case "acosh":
                        return Math.log(args[0] + Math.sqrt(args[0]*args[0] - 1));
                    case "atanh":
                        return 0.5*Math.log((1 + args[0]) / (1 - args[0]));
                    case "exp":
                        return Math.exp(args[0]);
                    case "log":
                        return Math.log(args[0]);
                    case "log2":
                        return Math.log(args[0]) / Math.log(2);
                    case "log10":
                        return Math.log10(args[0]);
                    case "toradians":
                        return Math.toRadians(args[0]);
                    case "todegrees":
                        return Math.toDegrees(args[0]);
                    case "not":
                        return args[0] == 0 ? 1 : 0;
                    case "fact":
                        double x = args[0];
                        if (Math.round(x) != x || x < 0) {
                            throw new InvalidMathExpressionException("Factorial of non-integer or non-positive integer at index " + startIdx);
                        }
                        double ret = 1;
                        for (int j=2; j<=x; j++) {
                            ret *= j; 
                        }
                        return ret;
                    default:
                        throw new InvalidMathExpressionException("Unknown function: " + func + " with one argument at index " + startIdx);    
                }
            case 2:
                switch (func) {
                    case "atan":
                    case "atan2":
                        return Math.atan2(args[0], args[1])/angleConversion;
                    case "hypot":
                        return Math.hypot(args[0], args[1]);
                    case "log":
                        return Math.log(args[1]) / Math.log(args[0]);
                    case "max":
                        return Math.max(args[0], args[1]);
                    case "min":
                        return Math.min(args[0], args[1]);
                    case "pow":
                        return Math.pow(args[0], args[1]);
                    case "and":
                        return args[0] != 0 && args[1] != 0 ? 1 : 0;
                    case "or":
                        return args[0] != 0 || args[1] != 0 ? 1 : 0;
                    case "xor":
                        return (args[0] != 0 && args[1] == 0) || (args[0] == 0 && args[1] != 0) ? 1 : 0;
                    case "comb": //m taken n at a time
                        if (Math.round(args[0]) != args[0] || args[0] < 0 || 
                                Math.round(args[1]) != args[1] || args[1] < 0 || args[0] < args[1]) {
                            throw new InvalidMathExpressionException("In comb(m,n), n and m must be non-negative integers with m>=n at index " + startIdx);
                        }
                        double ret = 1;
                        for (int i=args[0].intValue(), j=1; i>=args[0]-args[1]+1; i--, j++) {
                            ret *= i /(double) j;
                        }
                    return ret;
                    case "perm": //m taken n at a time
                        if (Math.round(args[0]) != args[0] || args[0] < 0 || 
                                Math.round(args[1]) != args[1] || args[1] < 0 || args[0] < args[1]) {
                            throw new InvalidMathExpressionException("In perm(m,n), n and m must be non-negative integers with m>=n at index " + startIdx);
                        }
                        ret = 1;
                        for (int i=args[0].intValue(); i>=args[0]-args[1]+1; i--) {
                            ret *= i;
                        }
                        return ret;
                    default:
                        throw new InvalidMathExpressionException("Unknown function: " + func + " with two arguments at index " + startIdx);    
                }
            case 3:
                switch (func) {
                    case "if":
                        return args[0] != 0 ? args[1] : args[2];
                    default:
                        throw new InvalidMathExpressionException("Unknown function: " + func + " with three arguments at index " + startIdx);    
                }
            default:
                throw new InvalidMathExpressionException("Unknown function: " + func + " with " + args.length + " arguments at index " + startIdx);    
        }
    }
    
    /**
     * Parses the string for a comma separated list of relations starting at the character pointer.
     * The character pointer is left at the next non-whitespace character past the last relation.
     * @return an array containing the numerical values of each of the relations
     * @throws InvalidMathExpressionException if any of the relations is invalid
     */
    private Double[] parseRelationList() throws InvalidMathExpressionException {
        List<Double> ret = new ArrayList<>();
        if (ch != ')') {
            ret.add(parseRelation());
        }
        while (consume(',')) {
            ret.add(parseRelation());
        }
        Double[] retArray = {};
        return ret.toArray(retArray);
    }
    
    /**
     * Parses the string for a relation starting at the character pointer. The character pointer is
     * left at the next non-whitespace character past the relation.
     * @return the numerical value of the relation, either 1 if the relation is true or 0 if the 
     * relation is false
     * @throws InvalidMathExpressionException if the relation is invalid
     */
    private double parseRelation() throws InvalidMathExpressionException {
        double x = parseExpression();
        for (;;) {
            if (consume('=')) {
                if (consume('=')) {
                    double y = parseRelation();
                    x = x == y ? 1 : 0;
                }
                else {
                    throw new InvalidMathExpressionException("Invalid operator, probably missing '=' at index " + idx);
                }
            }
            else if (consume('!')) {
                if (consume('=')) {
                    double y = parseRelation();
                    x = x != y ? 1 : 0;
                }
                else {
                    throw new InvalidMathExpressionException("Invalid operator, probably missing '=' at index " + idx);
                }
            }
            else if (consume('>')) {
                if (consume('=')) {
                    double y = parseExpression();
                    x = x >= y ? 1 : 0;
                }
                else {
                    double y = parseExpression();
                    x = x > y ? 1 : 0;
                }
            }
            else if (consume('<')) {
                if (consume('=')) {
                    double y = parseExpression();
                    x = x <= y ? 1 : 0;
                }
                else {
                    double y = parseExpression();
                    x = x < y ? 1 : 0;
                }
            }
            else {
                return x;
            }
        }
    }
    
    /**
     * Parses the string for an expression starting at the character pointer. The character pointer
     * is left at the next non-whitespace character past the expression. An expression is one or 
     * more terms combined together with the + and/or - operators.
     * @return the numerical value of the expression
     * @throws InvalidMathExpressionException if the expression is not valid
     */
    private double parseExpression() throws InvalidMathExpressionException {
        double x = parseTerm();
        for (;;) {
            if (consume('+')) { // addition
                x += parseTerm();
            }
            else if (consume('-')) { // subtraction
                x -= parseTerm();
            }
            else {
                return x;
            }
        }
    }
    
    /**
     * Parses the string for a term starting at the character pointer. The character pointer is left
     * at the next non-whitespace character past the term. A term is one or more factors combined
     * together with the *, /, and/or % operators.
     * @return the numerical value of the term
     * @throws InvalidMathExpressionException if the term is invalid
     */
    private  double parseTerm() throws InvalidMathExpressionException {
        double x = parseFactor();
        for (;;) {
            if (consume('*')) { // multiplication
                x *= parseFactor();
            }
            else if (consume('/')) { // division
                x /= parseFactor();
            }
            else if (consume('%')) { //modulus
                x %= parseFactor();
            }
            else {
                return x;
            }
        }
    }
    
    /**
     * Parses the string for a factor starting at the character pointer. The character pointer is
     * left at the next non-whitespace character past the end of the factor. A factor is an optional
     * unary '+' or '-' sign followed by either 1) a relation enclosed in parentheses, 2) a number, 
     * or 3) a function call. This can optionally be followed by an exponentiation operator '^'
     * followed by another factor.
     * @return the numerical value of the factor
     * @throws InvalidMathExpressionException if the factor is invalid
     */
    private double parseFactor() throws InvalidMathExpressionException {
        if (consume('+')) { // unary plus
            return +parseFactor();
        }
        if (consume('-')) { // unary minus
            return -parseFactor();
        }
        
        double x;
        if (consume('(')) { // parentheses
            x = parseRelation();
            if (!consume(')')) {
                throw new InvalidMathExpressionException("Missing ')' at index " + idx);
            }
        }
        else if (Character.isDigit(ch) || ch == '.') { // numbers
            x = parseNumber();
        } else if (Character.isLetter(ch)) { // functions
            x = parseFunction();
        } else {
            throw new InvalidMathExpressionException("Unexpected character: '" + (char)ch + "' at index " + idx );
        }
        
        if (consume('^')) {
            x = Math.pow(x, parseFactor());
        }
        return x;
    }
}
