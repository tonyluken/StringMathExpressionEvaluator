/*
 * Copyright (C) 2023 Tony Luken <tonyluken62+mathexpressionevaluator.gmail.com>
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

import java.util.Random;

import org.junit.jupiter.api.Test;

import stringMathExpressionEvaluator.InvalidMathExpressionException;
import stringMathExpressionEvaluator.StringMathExpressionEvaluator;

public class MathExpressionTest {
    private static final double EQUALITY_TOLERANCE = 1e-9;
    
    @Test
    public void testSimpleExamples() throws Exception {
        StringMathExpressionEvaluator smee = new StringMathExpressionEvaluator();
        String expression = "";
        double result;
        double expected;
        
        expression = "2*(3+7)";
        result = smee.evaluate(expression);
        System.out.println("\"" + expression + "\" evaluates to " + result);
        expected = 20;
        checkEquality(result, expected);

        expression = "3.2*sin(0.56)";
        result = smee.evaluate(expression);
        System.out.println("\"" + expression + "\" evaluates to " + result);
        expected = 3.2*Math.sin(0.56);
        checkEquality(result, expected);

        expression = "atan2(-1, -1)";
        result = smee.evaluate(expression);
        System.out.println("\"" + expression + "\" evaluates to " + result);
        expected = Math.atan2(-1, -1);
        checkEquality(result, expected);

        expression = "17.3*(2-0.145)/7";
        result = smee.evaluate(expression);
        System.out.println("\"" + expression + "\" evaluates to " + result);
        expected = 4.5845;
        checkEquality(result, expected);

        expression = "sqrt(3^2 + 4^2)";
        result = smee.evaluate(expression);
        System.out.println("\"" + expression + "\" evaluates to " + result);
        expected = 5;
        checkEquality(result, expected);

        expression = "atan(1/sqrt(3))";
        smee.setDegreeMode();
        result = smee.evaluate(expression);
        System.out.println("\"" + expression + "\" evaluates to " + result + " degrees");
        expected = 30;
        checkEquality(result, expected);
        smee.setRadianMode();
        result = smee.evaluate(expression);
        System.out.println("\"" + expression + "\" evaluates to " + result + " radians");
        expected = 30 * Math.PI / 180;
        checkEquality(result, expected);

        //One of the roots of x^2-6x+8=0 using quadratic formula
        expression = "(6 + sqrt((-6)^2-4*1*8))/(2*1)";
        result = smee.evaluate(expression);
        System.out.println("\"" + expression + "\" evaluates to " + result);
        expected = 4;
        checkEquality(result, expected);

        //The other root of x^2-6x+8=0 using quadratic formula
        expression = "(6 - sqrt((-6)^2-4*1*8))/(2*1)";
        result = smee.evaluate(expression);
        System.out.println("\"" + expression + "\" evaluates to " + result);
        expected = 2;
        checkEquality(result, expected);
    
        boolean caughtError = false;
        try {
            expression = "(6 - sqt((-6)^2-4*1*8))/(2*1)";
            result = smee.evaluate(expression);
            System.out.println("\"" + expression + "\" evaluates to " + result);
            expected = 2;
            checkEquality(result, expected);
        }
        catch (InvalidMathExpressionException ex) {
            System.out.println("\"" + expression + "\" failed to evaluate (this is expected) due to " + ex.getMessage());
            caughtError = true;
        }
        if (!caughtError) {
            throw new Exception("Invalid expression didn't throw an exception as expected");
        }
    }
    
    @Test
    public void testTrigIdentities() throws Exception {
        Random rng = new Random(62);
        StringMathExpressionEvaluator mee = new StringMathExpressionEvaluator();
        String expression;
        double result;
        double expected;
        
        for (int i=0; i<10000; i++) {
            double angle = (2*rng.nextDouble() - 1)*Math.PI;
            
            expression = String.format("sin(%.14f)^2 + cos(%.14f)^2", angle, angle);
            System.out.println(expression);
            
            result = mee.evaluate(expression);
            expected = 1.0;
            
            checkEquality(result, expected);
            
            if (Math.abs(Math.sin(angle)) < 0.99) { //avoid angles very near +/-90 degrees
                expression = String.format("1/cos(%.14f)^2 - tan(%.14f)^2", angle, angle);
                System.out.println(expression);
                
                result = mee.evaluate(expression);
                expected = 1.0;
                
                checkEquality(result, expected);

                expression = String.format("sin(%.14f) / cos(%.14f)", angle, angle);
                System.out.println(expression);
                
                result = mee.evaluate(expression);
                expected = Math.tan(angle);
                
                checkEquality(result, expected);
            }

            expression = String.format("cosh(%.14e)^2 - sinh(%.14f)^2", angle, angle);
            System.out.println(expression);
            
            result = mee.evaluate(expression);
            expected = 1.0;
            
            checkEquality(result, expected);

            expression = String.format("tanh(%.14e)^2 + 1/cosh(%.14f)^2", angle, angle);
            System.out.println(expression);
            
            result = mee.evaluate(expression);
            expected = 1.0;
            
            checkEquality(result, expected);

            expression = String.format("sinh(%.14e) / cosh(%.14f)", angle, angle);
            System.out.println(expression);
            
            result = mee.evaluate(expression);
            expected = Math.tanh(angle);
            
            checkEquality(result, expected);
        }

        expression = String.format("sin(pi()/2)");
        System.out.println(expression);
        
        result = mee.evaluate(expression);
        expected = 1;
        
        checkEquality(result, expected);

        expression = String.format("cos(pi())");
        System.out.println(expression);
        
        result = mee.evaluate(expression);
        expected = -1;
        
        checkEquality(result, expected);
    }
    
    @Test
    public void testInvTrigIdentities() throws Exception {
        Random rng = new Random(63);
        StringMathExpressionEvaluator mee = new StringMathExpressionEvaluator();
        String expression;
        double result;
        double expected;
        
        for (int i=0; i<10000; i++) {
            double angle = (2*rng.nextDouble() - 1)*Math.PI;
            
            expression = String.format("asin(sin(%.14e))", angle);
            System.out.println(expression);
            
            result = mee.evaluate(expression);
            expected = angle;
            if (expected > Math.PI/2) {
                expected = Math.PI - expected;
            }
            else if (expected < -Math.PI/2) {
                expected = -Math.PI - expected;
            }
            
            checkEquality(result, expected);

            expression = String.format("acos(cos(%.14e))", angle);
            System.out.println(expression);
            
            result = mee.evaluate(expression);
            expected = angle;
            if (expected < 0) {
                expected = -expected;
            }
            
            checkEquality(result, expected);

            expression = String.format("atan(tan(%.14e))", angle);
            System.out.println(expression);
            
            result = mee.evaluate(expression);
            expected = angle;
            if (expected > Math.PI/2) {
                expected = expected - Math.PI;
            }
            else if (expected < -Math.PI/2) {
                expected = expected + Math.PI;
            }
            
            checkEquality(result, expected);

            expression = String.format("atan2(sin(%.14e), cos(%.14e))", angle, angle);
            System.out.println(expression);
            
            result = mee.evaluate(expression);
            expected = angle;
            
            checkEquality(result, expected);

            expression = String.format("asinh(sinh(%.14e))", angle);
            System.out.println(expression);
            
            result = mee.evaluate(expression);
            expected = angle;
            
            checkEquality(result, expected);

            expression = String.format("acosh(cosh(%.14e))", angle);
            System.out.println(expression);
            
            result = mee.evaluate(expression);
            expected = Math.abs(angle);
            
            checkEquality(result, expected);

            expression = String.format("atanh(tanh(%.14e))", angle);
            System.out.println(expression);
            
            result = mee.evaluate(expression);
            expected = angle;
            
            checkEquality(result, expected);

        }
    }

    @Test
    public void testAngleModes() throws Exception {
        Random rng = new Random(63);
        StringMathExpressionEvaluator mee = new StringMathExpressionEvaluator();
        String expression;
        double result;
        double expected;
        
        for (int i=0; i<10000; i++) {
            double angle = (2*rng.nextDouble() - 1)*Math.PI;
            
            mee.setDegreeMode();
            
            expression = String.format("sin(%.14e)", Math.toDegrees(angle));
            System.out.println(expression);
            result = mee.evaluate(expression);
            expected = Math.sin(angle);
            checkEquality(result, expected);
            
            expression = String.format("cos(%.14e)", Math.toDegrees(angle));
            System.out.println(expression);
            result = mee.evaluate(expression);
            expected = Math.cos(angle);
            checkEquality(result, expected);
            
            if (Math.abs(Math.sin(angle)) < 0.99) {
                expression = String.format("tan(%.14e)", Math.toDegrees(angle));
                System.out.println(expression);
                result = mee.evaluate(expression);
                expected = Math.tan(angle);
                checkEquality(result, expected);
            }
            
            expression = String.format("asin(sin(%.14e))", Math.toDegrees(angle));
            System.out.println(expression);
            result = mee.evaluate(expression);
            expected = Math.toDegrees(angle);
            if (expected > 90) {
                expected = 180 - expected;
            }
            else if (expected < -90) {
                expected = -180 - expected;
            }
            checkEquality(result, expected);
            
            expression = String.format("acos(cos(%.14e))", Math.toDegrees(angle));
            System.out.println(expression);
            result = mee.evaluate(expression);
            expected = Math.toDegrees(angle);
            if (expected < 0) {
                expected = -expected;
            }
            checkEquality(result, expected);
            
            expression = String.format("atan(tan(%.14e))", Math.toDegrees(angle));
            System.out.println(expression);
            result = mee.evaluate(expression);
            expected = Math.toDegrees(angle);
            if (expected > 90) {
                expected = expected - 180;
            }
            else if (expected < -90) {
                expected = expected + 180;
            }
            checkEquality(result, expected);

            expression = String.format("atan2(sin(%.14e), cos(%.14e))", Math.toDegrees(angle), Math.toDegrees(angle));
            System.out.println(expression);
            result = mee.evaluate(expression);
            expected = Math.toDegrees(angle);
            checkEquality(result, expected);

            mee.setRadianMode();
            
            expression = String.format("sin(%.14e)", angle);
            System.out.println(expression);
            result = mee.evaluate(expression);
            expected = Math.sin(angle);
            checkEquality(result, expected);
        }        
    }
    
    @Test
    public void testExpAndLogs() throws Exception {
        Random rng = new Random(64);
        StringMathExpressionEvaluator mee = new StringMathExpressionEvaluator();
        String expression;
        double result;
        double expected;
        
        for (int i=0; i<10000; i++) {
            double n = (2*rng.nextDouble() - 1)*10;
            double m = rng.nextDouble()*10;

            expression = String.format("exp(%.14f)", n);
            System.out.println(expression);
            result = mee.evaluate(expression);
            expected = Math.exp(n);
            checkEquality(result, expected);

            expression = String.format("pow(%.14e, %.14e)", m, n);
            System.out.println(expression);
            result = mee.evaluate(expression);
            expected = Math.pow(m, n);
            checkEquality(result, expected);

            expression = String.format("log(%.14e)", Math.exp(n));
            System.out.println(expression);
            result = mee.evaluate(expression);
            expected = n;
            checkEquality(result, expected);

            expression = String.format("log10(%.14e)", Math.pow(10, n));
            System.out.println(expression);
            result = mee.evaluate(expression);
            expected = n;
            checkEquality(result, expected);

            expression = String.format("log2(%.14e)", Math.pow(2, n));
            System.out.println(expression);
            result = mee.evaluate(expression);
            expected = n;
            checkEquality(result, expected);

            expression = String.format("log(3, %.14e)", Math.pow(3, n));
            System.out.println(expression);
            result = mee.evaluate(expression);
            expected = n;
            checkEquality(result, expected);

        }
        expression = String.format("log(e())");
        System.out.println(expression);
        
        result = mee.evaluate(expression);
        expected = 1;
        
        checkEquality(result, expected);

    }
    
    @Test
    public void testOtherFunctions() throws Exception {
        Random rng = new Random(65);
        StringMathExpressionEvaluator mee = new StringMathExpressionEvaluator();
        String expression;
        double result;
        double expected;
        
        for (int i=0; i<10000; i++) {
            double n = (2*rng.nextDouble() - 1)*1000;
            double m = (2*rng.nextDouble() - 1)*1000;
            
            expression = String.format("abs(%.14f)", n);
            System.out.println(expression);
            result = mee.evaluate(expression);
            expected = Math.abs(n);
            checkEquality(result, expected);

            expression = String.format("ceil(%.14f)", n);
            System.out.println(expression);
            result = mee.evaluate(expression);
            expected = Math.ceil(n);
            checkEquality(result, expected);

            expression = String.format("floor(%.14f)", n);
            System.out.println(expression);
            result = mee.evaluate(expression);
            expected = Math.floor(n);
            checkEquality(result, expected);

            expression = String.format("round(%.14f)", n);
            System.out.println(expression);
            result = mee.evaluate(expression);
            expected = Math.round(n);
            checkEquality(result, expected);

            expression = String.format("signum(%.14f)", n);
            System.out.println(expression);
            result = mee.evaluate(expression);
            expected = Math.signum(n);
            checkEquality(result, expected);

            expression = String.format("sqrt(%.14f)", Math.abs(n));
            System.out.println(expression);
            result = mee.evaluate(expression);
            expected = Math.sqrt(Math.abs(n));
            checkEquality(result, expected);

            expression = String.format("cbrt(%.14f)", n);
            System.out.println(expression);
            result = mee.evaluate(expression);
            expected = Math.cbrt(n);
            checkEquality(result, expected);

            expression = String.format("toRadians(%.14f)", n);
            System.out.println(expression);
            result = mee.evaluate(expression);
            expected = Math.toRadians(n);
            checkEquality(result, expected);

            expression = String.format("toDegrees(%.14f)", n);
            System.out.println(expression);
            result = mee.evaluate(expression);
            expected = Math.toDegrees(n);
            checkEquality(result, expected);

            expression = String.format("max(%.14f, %.14f)", n, m);
            System.out.println(expression);
            result = mee.evaluate(expression);
            expected = Math.max(n, m);
            checkEquality(result, expected);

            expression = String.format("min(%.14f, %.14f)", n, m);
            System.out.println(expression);
            result = mee.evaluate(expression);
            expected = Math.min(n, m);
            checkEquality(result, expected);

            expression = String.format("hypot(%.14f, %.14f)", n, m);
            System.out.println(expression);
            result = mee.evaluate(expression);
            expected = Math.hypot(n, m);
            checkEquality(result, expected);

            expression = String.format("if(floor(%.14f/100)-ceil(%.14f/100), 0, 1)", n, m);
            System.out.println(expression);
            result = mee.evaluate(expression);
            expected = Math.floor(n/100) == Math.ceil(m/100) ? 1 : 0;
            checkEquality(result, expected);

            expression = String.format("if(floor(%.14f/100) == ceil(%.14f/100), 1, 0)", n, m);
            System.out.println(expression);
            result = mee.evaluate(expression);
            expected = Math.floor(n/100) == Math.ceil(m/100) ? 1 : 0;
            checkEquality(result, expected);

            expression = String.format("not(floor(%.14f/100))", n);
            System.out.println(expression);
            result = mee.evaluate(expression);
            expected = Math.floor(n/100) == 0 ? 1 : 0;
            checkEquality(result, expected);

            expression = String.format("and(floor(%.14f/100), ceil(%.14f/100))", n, m);
            System.out.println(expression);
            result = mee.evaluate(expression);
            expected = Math.floor(n/100) != 0 && Math.ceil(m/100) != 0 ? 1 : 0;
            checkEquality(result, expected);

            expression = String.format("or(floor(%.14f/100), ceil(%.14f/100))", n, m);
            System.out.println(expression);
            result = mee.evaluate(expression);
            expected = Math.floor(n/100) != 0 || Math.ceil(m/100) != 0 ? 1 : 0;
            checkEquality(result, expected);

            expression = String.format("xor(floor(%.14f/100), ceil(%.14f/100))", n, m);
            System.out.println(expression);
            result = mee.evaluate(expression);
            expected = (Math.floor(n/100) != 0 && Math.ceil(m/100) == 0) || 
                    (Math.floor(n/100) == 0 && Math.ceil(m/100) != 0) ? 1 : 0;
            checkEquality(result, expected);
        }
    }
    
    @Test
    public void testFactorials() throws Exception {
        Random rng = new Random(66);
        StringMathExpressionEvaluator mee = new StringMathExpressionEvaluator();
        String expression;
        double result;
        double expected;
        
        for (int i=0; i<10000; i++) {
            double m = Math.ceil(rng.nextDouble()*100);
            double n = Math.ceil(rng.nextDouble()*m);
            
            expression = String.format("fact(%.14f)", n);
            System.out.println(expression);
            result = mee.evaluate(expression);
            expected = 1;
            for (int j=2; j<=n; j++) {
                expected *= j; 
            }
            checkEquality(result, expected);
            
            expression = String.format("perm(%f,%f)", m, n);
            System.out.println(expression);
            result = mee.evaluate(expression);
            expected = 1;
            for (int k=(int) m; k>=m-n+1; k--) {
                expected *= k;
            }
            checkEquality(result, expected);
            
            expression = String.format("comb(%f,%f)", m, n);
            System.out.println(expression);
            result = mee.evaluate(expression);
            expected = 1;
            for (int k=(int) m, j=1; k>=m-n+1; k--, j++) {
                expected *= k /(double) j;
            }
            checkEquality(result, expected);
        }

        expression = String.format("fact(13)");
        System.out.println(expression);
        result = mee.evaluate(expression);
        expected = 6227020800d;
        checkEquality(result, expected);

        expression = String.format("perm(16,3)");
        System.out.println(expression);
        result = mee.evaluate(expression);
        expected = 3360;
        checkEquality(result, expected);

        expression = String.format("comb(16,3)");
        System.out.println(expression);
        result = mee.evaluate(expression);
        expected = 560;
        checkEquality(result, expected);
}
    
    @Test
    public void testOperatorPrecedence() throws Exception {
        StringMathExpressionEvaluator mee = new StringMathExpressionEvaluator();
        String expression;
        double result;
        double expected;

        expression = "2.2208+4.785*-2.8064*-1.1535*-2.7919*-0.6243*1.7646^3.2975*0.463/-1.0355*0.3547";
        System.out.println(expression);
        result = mee.evaluate(expression);
        expected = 2.2208+4.785*-2.8064*-1.1535*-2.7919*-0.6243*Math.pow(1.7646, 3.2975)*0.463/-1.0355*0.3547;
        checkEquality(result, expected);
        
        expression = "2.8539/-(4.5479^-2.4599)/4.3172-3.9807*4.8725-3.3499*-2.4608-4.657*3.6559*-4.4297";
        System.out.println(expression);
        result = mee.evaluate(expression);
        expected = 2.8539/-Math.pow(4.5479, -2.4599)/4.3172-3.9807*4.8725-3.3499*-2.4608-4.657*3.6559*-4.4297;
        checkEquality(result, expected);
        
        expression = "-1.9241--2.2071*1.2548*-3.2781%-3.9063/-4.7487*4.4248-2.6183*1.9207+3.4083-3.8262";
        System.out.println(expression);
        result = mee.evaluate(expression);
        expected = -1.9241- -2.2071*1.2548*-3.2781%-3.9063/-4.7487*4.4248-2.6183*1.9207+3.4083-3.8262;
        checkEquality(result, expected);
        
        expression = "4.6158*4.5758/-(0.4841^0.0535)+-0.9155%1.1605*1.3407*1.7868*-0.5533*-4.238/1.0738";
        System.out.println(expression);
        result = mee.evaluate(expression);
        expected = 4.6158*4.5758/-Math.pow(0.4841, 0.0535)+-0.9155%1.1605*1.3407*1.7868*-0.5533*-4.238/1.0738;
        checkEquality(result, expected);
        
        expression = "-3.0343--3.4878--4.4417-(0.4019^-1.2241)--4.1597*1.2363*0.8073*-4.1893*-3.172--1.8528";
        System.out.println(expression);
        result = mee.evaluate(expression);
        expected = -3.0343- -3.4878- -4.4417-Math.pow(0.4019, -1.2241)- -4.1597*1.2363*0.8073*-4.1893*-3.172- -1.8528;
        checkEquality(result, expected);
        
        expression = "3.4493/-1.7578+-4.3397*-2.748-3.6107%-(4.5103^0.6214)-4.0037*-2.946--2.3541/0.7738";
        System.out.println(expression);
        result = mee.evaluate(expression);
        expected = 3.4493/-1.7578+-4.3397*-2.748-3.6107%-Math.pow(4.5103, 0.6214)-4.0037*-2.946- -2.3541/0.7738;
        checkEquality(result, expected);
        
        expression = "4.6844*-2.867+-3.3371+2.6685-4.7441/1.1^-0.2371+-3.7584-1.875*-2.5294*-4.3819";
        System.out.println(expression);
        result = mee.evaluate(expression);
        expected = 4.6844*-2.867+-3.3371+2.6685-4.7441/Math.pow(1.1, -0.2371)+-3.7584-1.875*-2.5294*-4.3819;
        checkEquality(result, expected);
        
        expression = "-0.1864+-1.2236*2.7308*2.5989*-2.0192--0.2859-3.7345/4.1428-2.1943%-0.8877-2.2487";
        System.out.println(expression);
        result = mee.evaluate(expression);
        expected = -0.1864+-1.2236*2.7308*2.5989*-2.0192- -0.2859-3.7345/4.1428-2.1943%-0.8877-2.2487;
        checkEquality(result, expected);
        
        expression = "-2.3822+-3.6714-2.6229+-1.7579+-0.3666*1.2313*-3.3788*4.5436-1.3729-0.8498-0.918";
        System.out.println(expression);
        result = mee.evaluate(expression);
        expected = -2.3822+-3.6714-2.6229+-1.7579+-0.3666*1.2313*-3.3788*4.5436-1.3729-0.8498-0.918;
        checkEquality(result, expected);
        
        expression = "-2.5972/-(1.9989^2.9895)*0.6369*0.9013*-4.0044--2.2818/-3.1296-1.3388*0.9995-4.5406";
        System.out.println(expression);
        result = mee.evaluate(expression);
        expected = -2.5972/-Math.pow(1.9989, 2.9895)*0.6369*0.9013*-4.0044- -2.2818/-3.1296-1.3388*0.9995-4.5406;
        checkEquality(result, expected);
        
        expression = "1.609*2.4454+0.5606/-(0.5543^0.013)+-3.3755*-0.8675%0.102/4.4884+1.736*0.7262";
        System.out.println(expression);
        result = mee.evaluate(expression);
        expected = 1.609*2.4454+0.5606/-Math.pow(0.5543, 0.013)+-3.3755*-0.8675%0.102/4.4884+1.736*0.7262;
        checkEquality(result, expected);
        
        expression = "-4.2276*3.4031--0.0463-0.2323*3.2583*-1.939/4.2213^-4.9794--1.3005--1.5148*3.603";
        System.out.println(expression);
        result = mee.evaluate(expression);
        expected = -4.2276*3.4031- -0.0463-0.2323*3.2583*-1.939/Math.pow(4.2213, -4.9794)- -1.3005- -1.5148*3.603;
        checkEquality(result, expected);
        
        expression = "-4.7593+2.6149--0.2998*-3.4556*-0.8271*4.4705*2.8729--0.3527*-0.4255*2.097-3.9019";
        System.out.println(expression);
        result = mee.evaluate(expression);
        expected = -4.7593+2.6149- -0.2998*-3.4556*-0.8271*4.4705*2.8729- -0.3527*-0.4255*2.097-3.9019;
        checkEquality(result, expected);
        
        expression = "4.45*-1.3744+4.4908-2.4204*-(4.1478^-1.9615)-1.9073*-3.8482--2.9022-3.6177*-4.3448";
        System.out.println(expression);
        result = mee.evaluate(expression);
        expected = 4.45*-1.3744+4.4908-2.4204*-Math.pow(4.1478, -1.9615)-1.9073*-3.8482- -2.9022-3.6177*-4.3448;
        checkEquality(result, expected);
        
        expression = "-3.8399%2.2788-4.8677*-4.3996*-2.2837-0.743-4.6171*-3.4952*-2.9772+2.2944*0.9536";
        System.out.println(expression);
        result = mee.evaluate(expression);
        expected = -3.8399%2.2788-4.8677*-4.3996*-2.2837-0.743-4.6171*-3.4952*-2.9772+2.2944*0.9536;
        checkEquality(result, expected);
        
        expression = "3.3042+0.1673%-0.6474*-0.5481+0.0905*1.6631--3.7156*-3.6589*4.681-0.6171/2.6906";
        System.out.println(expression);
        result = mee.evaluate(expression);
        expected = 3.3042+0.1673%-0.6474*-0.5481+0.0905*1.6631- -3.7156*-3.6589*4.681-0.6171/2.6906;
        checkEquality(result, expected);
        
        expression = "4.7438^2.9815/-2.0488*-2.0306+2.2637/-3.7361+1.1179--3.4875*-1.9181+2.2823+-1.6";
        System.out.println(expression);
        result = mee.evaluate(expression);
        expected = Math.pow(4.7438, 2.9815)/-2.0488*-2.0306+2.2637/-3.7361+1.1179- -3.4875*-1.9181+2.2823+-1.6;
        checkEquality(result, expected);
        
        expression = "-3.1891--0.7873*4.4247*-4.164%-1.8007*-1.6798/0.0521/0.9201*2.4464*-3.6196*0.4059";
        System.out.println(expression);
        result = mee.evaluate(expression);
        expected = -3.1891- -0.7873*4.4247*-4.164%-1.8007*-1.6798/0.0521/0.9201*2.4464*-3.6196*0.4059;
        checkEquality(result, expected);
        
        expression = "3.7636--1.8965*-3.6897*3.5998-0.6459/-0.3304--2.7811*-4.6296--0.4595/1.3496*-3.7824";
        System.out.println(expression);
        result = mee.evaluate(expression);
        expected = 3.7636- -1.8965*-3.6897*3.5998-0.6459/-0.3304- -2.7811*-4.6296- -0.4595/1.3496*-3.7824;
        checkEquality(result, expected);
        
        expression = "0.0284^3.1291- -(0.7541^4.1036)/2.8711%2.0964-3.4974- -1.3439-0.3688- -0.0742%3.6318";
        System.out.println(expression);
        result = mee.evaluate(expression);
        expected = Math.pow(0.0284, 3.1291)- -Math.pow(0.7541, 4.1036)/2.8711%2.0964-3.4974- -1.3439-0.3688- -0.0742%3.6318;
        checkEquality(result, expected);
        
        expression = "3.4735-0.6032+-0.8867/0.1191*-0.1552-4.4251+4.342*-4.3911- -2.9697+-2.9063*-2.4964";
        System.out.println(expression);
        result = mee.evaluate(expression);
        expected = 3.4735-0.6032+-0.8867/0.1191*-0.1552-4.4251+4.342*-4.3911- -2.9697+-2.9063*-2.4964;
        checkEquality(result, expected);
        
        expression = "2.6232%4.1044-4.1845%-2.4917*-0.4125*-2.9051*-4.6679*4.9186- -3.3271/3.568*-4.6377";
        System.out.println(expression);
        result = mee.evaluate(expression);
        expected = 2.6232%4.1044-4.1845%-2.4917*-0.4125*-2.9051*-4.6679*4.9186- -3.3271/3.568*-4.6377;
        checkEquality(result, expected);
        
        expression = "2.5971%4.0389/4.387+3.6301*-3.896-+1.5801^0.5482*-3.8977+3.7778/-2.9168+-3.185";
        System.out.println(expression);
        result = mee.evaluate(expression);
        expected = 2.5971%4.0389/4.387+3.6301*-3.896-Math.pow(+1.5801, 0.5482)*-3.8977+3.7778/-2.9168+-3.185;
        checkEquality(result, expected);
        
        expression = "-4.8888--1.6612/-2.5362*-0.3903-0.0369+0.0111*-0.522/-4.874- -2.3376*3.8134^-0.2961";
        System.out.println(expression);
        result = mee.evaluate(expression);
        expected = -4.8888- -1.6612/-2.5362*-0.3903-0.0369+0.0111*-0.522/-4.874- -2.3376*Math.pow(3.8134, -0.2961);
        checkEquality(result, expected);
        
        expression = "1.6595+-3.1294*4.7332*-3.3163-4.1696*-2.2167/-4.1625*4.8659*0.9059^-4.6192+-4.8376";
        System.out.println(expression);
        result = mee.evaluate(expression);
        expected = 1.6595+-3.1294*4.7332*-3.3163-4.1696*-2.2167/-4.1625*4.8659*Math.pow(0.9059, -4.6192)+-4.8376;
        checkEquality(result, expected);

        expression = "(2.2208+4.785*-2.8064)*-1.1535*-2.7919*-0.6243*1.7646^3.2975*0.463/(-1.0355*0.3547)";
        System.out.println(expression);
        result = mee.evaluate(expression);
        expected = (2.2208+4.785*-2.8064)*-1.1535*-2.7919*-0.6243*Math.pow(1.7646, 3.2975)*0.463/(-1.0355*0.3547);
        checkEquality(result, expected);
        
        expression = "2.8539/-(4.5479^-2.4599)/(4.3172-3.9807*4.8725-3.3499)*-2.4608-4.657*3.6559*-4.4297";
        System.out.println(expression);
        result = mee.evaluate(expression);
        expected = 2.8539/-Math.pow(4.5479, -2.4599)/(4.3172-3.9807*4.8725-3.3499)*-2.4608-4.657*3.6559*-4.4297;
        checkEquality(result, expected);
        
        expression = "((-1.9241--2.2071)*1.2548*-3.2781)%-3.9063/-4.7487*(4.4248-2.6183*(1.9207+3.4083-3.8262))";
        System.out.println(expression);
        result = mee.evaluate(expression);
        expected = ((-1.9241- -2.2071)*1.2548*-3.2781)%-3.9063/-4.7487*(4.4248-2.6183*(1.9207+3.4083-3.8262));
        checkEquality(result, expected);
        
        expression = "4.6158*4.5758/-(0.4841^0.0535+-0.9155%1.1605)*1.3407*1.7868*-0.5533*+4.238/1.0738";
        System.out.println(expression);
        result = mee.evaluate(expression);
        expected = 4.6158*4.5758/-(Math.pow(0.4841, 0.0535)+-0.9155%1.1605)*1.3407*1.7868*-0.5533*+4.238/1.0738;
        checkEquality(result, expected);
        
        expression = "-3.0343--3.4878-((((4.4417-0.4019)^-1.2241)--4.1597)*1.2363*0.8073*-4.1893)*-3.172--1.8528";
        System.out.println(expression);
        result = mee.evaluate(expression);
        expected = -3.0343- -3.4878- ((Math.pow(4.4417-0.4019, -1.2241)- -4.1597)*1.2363*0.8073*-4.1893)*-3.172- -1.8528;
        checkEquality(result, expected);
        
        expression = "3.4493/(-1.7578+-40*(-2.748-3.6107%(-(4.5103^0.6214)-4.0037)*-2.946--2.3541)/0.7738)";
        System.out.println(expression);
        result = mee.evaluate(expression);
        expected = 3.4493/(-1.7578+-40*(-2.748-3.6107%(-Math.pow(4.5103, 0.6214)-4.0037)*-2.946- -2.3541)/0.7738);
        checkEquality(result, expected);
        
        expression = "3.4493/(-1.7578+-40*(-2.748-3.6107%(-(4.5103^(0.6214-4.0037)))*-2.946--2.3541)/0.7738)";
        System.out.println(expression);
        result = mee.evaluate(expression);
        expected = 3.4493/(-1.7578+-40*(-2.748-3.6107%(-Math.pow(4.5103, 0.6214-4.0037))*-2.946- -2.3541)/0.7738);
        checkEquality(result, expected);
        
        expression = "1.1^1.2^1.3^1.4";
        System.out.println(expression);
        result = mee.evaluate(expression);
        expected = Math.pow(1.1, Math.pow(1.2, Math.pow(1.3, 1.4)));
        checkEquality(result, expected);
        
        expression = "1.1^(1.2^1.3)^1.4";
        System.out.println(expression);
        result = mee.evaluate(expression);
        expected = Math.pow(1.1, Math.pow(Math.pow(1.2 , 1.3), 1.4));
        checkEquality(result, expected);
        
    }

    @Test
    public void testRelations() throws Exception {
        StringMathExpressionEvaluator mee = new StringMathExpressionEvaluator();
        String expression;
        double result;
        double expected;

        expression = "2.2208+4.785*-2.8064*-1.1535*-2.7919*-.6243*1.7646^3.2975*0.463/-1.0355*3.547E-1 == 2.2208+4.785*-2.8064*-1.1535*-2.7919*-.6243*1.7646^3.2975*0.463/-1.0355*0.3547  ";
        System.out.println(expression);
        result = mee.evaluate(expression);
        expected = 1;
        checkEquality(result, expected);

        expression = "2 < 4 < 1 == 5 < 6 >= 1";
        System.out.println(expression);
        result = mee.evaluate(expression);
        expected = 0;
        checkEquality(result, expected);

        expression = "2 < 4 < 1 != 5 < 6 >= 1";
        System.out.println(expression);
        result = mee.evaluate(expression);
        expected = 1;
        checkEquality(result, expected);

        expression = "2 > 4 < 1 == 5 < 6 >= 1";
        System.out.println(expression);
        result = mee.evaluate(expression);
        expected = 1;
        checkEquality(result, expected);

        expression = "2 > 4 < 1 != 5 < 6 >= 1";
        System.out.println(expression);
        result = mee.evaluate(expression);
        expected = 0;
        checkEquality(result, expected);

        expression = "2 < (4 < 1) == 5 < (6 >= 1)";
        System.out.println(expression);
        result = mee.evaluate(expression);
        expected = 1;
        checkEquality(result, expected);

        expression = "2 < (4 < 1) == -5 < (6 >= 1)";
        System.out.println(expression);
        result = mee.evaluate(expression);
        expected = 0;
        checkEquality(result, expected);

        expression = "2 < (4 < (1 == 5) < 6) >= 1";
        System.out.println(expression);
        result = mee.evaluate(expression);
        expected = 0;
        checkEquality(result, expected);

        expression = "0 < (-4 < (1 == 5) < 6) >= 1";
        System.out.println(expression);
        result = mee.evaluate(expression);
        expected = 1;
        checkEquality(result, expected);

        expression = "0 <= (-4 < (1 == 5) < 6) >= 1";
        System.out.println(expression);
        result = mee.evaluate(expression);
        expected = 1;
        checkEquality(result, expected);

        expression = "2 <= (-4 < (1 == 5) < 6) >= 1";
        System.out.println(expression);
        result = mee.evaluate(expression);
        expected = 0;
        checkEquality(result, expected);

        expression = "2 <= (-4 < (1 == 5) < 6) > 1";
        System.out.println(expression);
        result = mee.evaluate(expression);
        expected = 0;
        checkEquality(result, expected);

        expression = "2 <= (-4 < (1 == 5) < 6) > -1";
        System.out.println(expression);
        result = mee.evaluate(expression);
        expected = 1;
        checkEquality(result, expected);
    }
    
    private void checkEquality(double a, double b) throws Exception {
        if (Double.isFinite(a) && Double.isFinite(b)) {
            if (b != 0) {
                if (Math.abs(a/b - 1) > EQUALITY_TOLERANCE) {
                    throw new Exception("Miscompare: " + a + " != " + b);
                }
            }
            else if (Math.abs(a) > EQUALITY_TOLERANCE) {
                throw new Exception("Miscompare: " + a + " != " + b);
            }
        }
        else if (Double.isFinite(a)) {
            if ((b > 0 && a < 1/EQUALITY_TOLERANCE) || (b < 0 && a > -1/EQUALITY_TOLERANCE)) {
                throw new Exception("Miscompare: " + a + " != " + b);
            }
            
        }
        else if (a != b) {
            throw new Exception("Miscompare: " + a + " != " + b);
        }
    }
}
