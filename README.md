# StringMathExpressionEvaluator

StringMathExpressionEvaluator is a Java class that provides methods for finding the numerical value of math expressions written as strings. It can evaluate, as a string, any mathematical expression that could be written in a standard Java program using the same standard Java operators. It also can evaluate most functions available in the [java.lang.Math](https://docs.oracle.com/javase/8/docs/api/java/lang/Math.html) library. Several extensions to standard Java are also supported such as the '^' operator for the exponentiation, relational operators return double values, and several additional functions. See the [wiki](https://github.com/tonyluken/StringMathExpressionEvaluator/wiki) for a compete set of operators and functions that are supported.

## How to get StringMathExpressionEvaluator
For Maven users, simply add the following dependency to your pom:

	<dependency>
		<groupId>io.github.tonyluken</groupId>
		<artifactId>StringMathExpressionEvaluator</artifactId>
		<version>1.0.0</version>
	</dependency>

Other users can download a jar file [here](https://repo.maven.apache.org/maven2/io/github/tonyluken/StringMathExpressionEvaluator/).

## License
StringMathExpressionEvaluator is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

StringMathExpressionEvaluator is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the [GNU General Public License](LICENSE.md) for more details.

## Requirements
StringMathExpressionEvaluator is written in pure Java with no external dependencies. Java 1.7 JDK, or later must be installed in order to compile the code.

## Unit Testing
StringMathExpressionEvaluator includes an extensive set of unit tests to verify its correctness. 

## Example Usage
The [wiki](https://github.com/tonyluken/StringMathExpressionEvaluator/wiki) shows an example of how to use StringMathExpressionEvaluator. The unit [tests]() also provide many additional examples. 

## Limitations
StringMathExpressionEvaluator only handles real floating point arithmetic. It does not perform integer arithmetic nor does it handle numbers from the complex domain. It also does not support the use of variables.

## Credits
Much of the work here was inspired by [StackOverflow how-to-evaluate-a-math-expression-given-in-string-form](https://stackoverflow.com/questions/3422673/how-to-evaluate-a-math-expression-given-in-string-form)
