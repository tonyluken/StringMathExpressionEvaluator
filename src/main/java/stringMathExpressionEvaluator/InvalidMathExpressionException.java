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

/**
 * An exception to indicate the math expression is invalid. Details of the cause of the exception
 * may be retrieved with the {@link Exception#getMessage() getMessage()} method.
 */
public class InvalidMathExpressionException extends Exception {
    private static final long serialVersionUID = 8867090037197025291L;

    InvalidMathExpressionException(String msg) {
        super(msg);
    }
}
