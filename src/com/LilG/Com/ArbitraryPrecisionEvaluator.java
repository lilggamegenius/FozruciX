package com.LilG.Com;

import com.fathzer.soft.javaluator.*;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;

/** An evaluator that is able to evaluate arithmetic expressions on real numbers.
 * <br>Built-in operators:<ul>
 * <li>+: Addition</li>
 * <li>-: Subtraction</li>
 * <li>-: Unary minus</li>
 * <li>*: Multiplication</li>
 * <li>/: Division</li>
 * <li>^: Exponentiation.<br>Warning: Exponentiation is implemented using java.lang.Math.pow which has some limitations (please read oracle documentation about this method to known details).<br>For example (-1)^(1/3) returns NaN.</li>
 * <li>%: Modulo</li>
 * </ul>
 * Built-in functions:<ul>
 * <li>abs: absolute value</li>
 * <li>acos: arc cosine</li>
 * <li>asin: arc sine</li>
 * <li>atan: arc tangent</li>
 * <li>average: average of arguments</li>
 * <li>ceil: nearest upper integer</li>
 * <li>cos: cosine</li>
 * <li>cosh: hyperbolic cosine</li>
 * <li>floor: nearest lower integer</li>
 * <li>ln: natural logarithm (base e)</li>
 * <li>log: base 10 logarithm</li>
 * <li>max: maximum of arguments</li>
 * <li>min: minimum of arguments</li>
 * <li>round: nearest integer</li>
 * <li>sin: sine</li>
 * <li>sinh: hyperbolic sine</li>
 * <li>sum: sum of arguments</li>
 * <li>tan: tangent</li>
 * <li>tanh: hyperbolic tangent</li>
 * <li>random: pseudo-random number (between 0 and 1)</li>
 * </ul>
 * Built-in constants:<ul>
 * <li>e: Base of natural algorithms</li>
 * <li>pi: Ratio of the circumference of a circle to its diameter</li>
 * </ul>
 * @author Jean-Marc Astesana
 * @see <a href="../../../license.html">License information</a>
 */
public class ArbitraryPrecisionEvaluator extends AbstractEvaluator<BigDecimal> {
    /**
     * A constant that represents pi (3.14159...)
     */
    public static final Constant PI = new Constant("pi");
    /**
     * A constant that represents e (2.718281...)
     */
    public static final Constant E = new Constant("e");

    /**
     * Returns the smallest integer >= argument
     */
    public static final Function CEIL = new Function("ceil", 1);
    /**
     * Returns the largest integer <= argument
     */
    public static final Function FLOOR = new Function("floor", 1);
    /**
     * Returns the closest integer of a number
     */
    public static final Function ROUND = new Function("round", 1);
    /**
     * Returns the absolute value of a number
     */
    public static final Function ABS = new Function("abs", 1);

    /**
     * Returns the trigonometric sine of an angle. The angle is expressed in radian.
     */
    public static final Function SINE = new Function("sin", 1);
    /**
     * Returns the trigonometric cosine of an angle. The angle is expressed in radian.
     */
    public static final Function COSINE = new Function("cos", 1);
    /**
     * Returns the trigonometric tangent of an angle. The angle is expressed in radian.
     */
    public static final Function TANGENT = new Function("tan", 1);
    /**
     * Returns the trigonometric arc-cosine of an angle. The angle is expressed in radian.
     */
    public static final Function ACOSINE = new Function("acos", 1);
    /**
     * Returns the trigonometric arc-sine of an angle. The angle is expressed in radian.
     */
    public static final Function ASINE = new Function("asin", 1);
    /**
     * Returns the trigonometric arc-tangent of an angle. The angle is expressed in radian.
     */
    public static final Function ATAN = new Function("atan", 1);

    /**
     * Returns the hyperbolic sine of a number.
     */
    public static final Function SINEH = new Function("sinh", 1);
    /**
     * Returns the hyperbolic cosine of a number.
     */
    public static final Function COSINEH = new Function("cosh", 1);
    /**
     * Returns the hyperbolic tangent of a number.
     */
    public static final Function TANGENTH = new Function("tanh", 1);

    /**
     * Returns the minimum of n numbers (n>=1)
     */
    public static final Function MIN = new Function("min", 1, Integer.MAX_VALUE);
    /**
     * Returns the maximum of n numbers (n>=1)
     */
    public static final Function MAX = new Function("max", 1, Integer.MAX_VALUE);
    /**
     * Returns the sum of n numbers (n>=1)
     */
    public static final Function SUM = new Function("sum", 1, Integer.MAX_VALUE);
    /**
     * Returns the average of n numbers (n>=1)
     */
    public static final Function AVERAGE = new Function("avg", 1, Integer.MAX_VALUE);

    /**
     * Returns the natural logarithm of a number
     */
    public static final Function LN = new Function("ln", 1);
    /**
     * Returns the decimal logarithm of a number
     */
    public static final Function LOG = new Function("log", 1);

    /**
     * Returns a pseudo random number
     */
    public static final Function RANDOM = new Function("random", 0);

    /**
     * The negate unary operator in the standard operator precedence.
     */
    public static final Operator NEGATE = new Operator("-", 1, Operator.Associativity.RIGHT, 3);
    /**
     * The negate unary operator in the Excel like operator precedence.
     */
    public static final Operator NEGATE_HIGH = new Operator("-", 1, Operator.Associativity.RIGHT, 5);
    /**
     * The substraction operator.
     */
    public static final Operator MINUS = new Operator("-", 2, Operator.Associativity.LEFT, 1);
    /**
     * The addition operator.
     */
    public static final Operator PLUS = new Operator("+", 2, Operator.Associativity.LEFT, 1);
    /**
     * The multiplication operator.
     */
    public static final Operator MULTIPLY = new Operator("*", 2, Operator.Associativity.LEFT, 2);
    /**
     * The division operator.
     */
    public static final Operator DIVIDE = new Operator("/", 2, Operator.Associativity.LEFT, 2);
    /**
     * The exponentiation operator.
     */
    public static final Operator EXPONENT = new Operator("^", 2, Operator.Associativity.LEFT, 4);
    /**
     * The <a href="http://en.wikipedia.org/wiki/Modulo_operation">modulo operator</a>.
     */
    public static final Operator MODULO = new Operator("%", 2, Operator.Associativity.LEFT, 2);

    /**
     * The standard whole set of predefined operators
     */
    private static final Operator[] OPERATORS = new Operator[]{NEGATE, MINUS, PLUS, MULTIPLY, DIVIDE, EXPONENT, MODULO};
    /**
     * The excel like whole set of predefined operators
     */
    private static final Operator[] OPERATORS_EXCEL = new Operator[]{NEGATE_HIGH, MINUS, PLUS, MULTIPLY, DIVIDE, EXPONENT, MODULO};
    /**
     * The whole set of predefined functions
     */
    private static final Function[] FUNCTIONS = new Function[]{SINE, COSINE, TANGENT, ASINE, ACOSINE, ATAN, SINEH, COSINEH, TANGENTH, MIN, MAX, SUM, AVERAGE, LN, LOG, ROUND, CEIL, FLOOR, ABS, RANDOM};
    /**
     * The whole set of predefined constants
     */
    private static final Constant[] CONSTANTS = new Constant[]{PI, E};
    private static final ThreadLocal<NumberFormat> FORMATTER = new ThreadLocal<NumberFormat>() {
        @Override
        protected NumberFormat initialValue() {
            return NumberFormat.getNumberInstance(Locale.US);
        }
    };
    private static Parameters DEFAULT_PARAMETERS;

    /** Constructor.
     * <br>This default constructor builds an instance with all predefined operators, functions and constants.
     */
    public ArbitraryPrecisionEvaluator() {
        this(getParameters());
    }

    /**
     * Constructor.
     * <br>This constructor can be used to reduce the set of supported operators, functions or constants,
     * or to localize some function or constant's names.
     *
     * @param parameters The parameters of the evaluator.
     */
    public ArbitraryPrecisionEvaluator(Parameters parameters) {
        super(parameters);
    }

    /** Gets a copy of DoubleEvaluator standard default parameters.
     * <br>The returned parameters contains all the predefined operators, functions and constants.
     * <br>Each call to this method create a new instance of Parameters.
     * @return a Paramaters instance
     * @see com.fathzer.soft.javaluator.DoubleEvaluator.Style
     */
    public static Parameters getDefaultParameters() {
        return getDefaultParameters(com.fathzer.soft.javaluator.DoubleEvaluator.Style.STANDARD);
    }

    /**
     * Gets a copy of DoubleEvaluator default parameters.
     * <br>The returned parameters contains all the predefined operators, functions and constants.
     * <br>Each call to this method create a new instance of Parameters.
     *
     * @return a Paramaters instance
     */
    public static Parameters getDefaultParameters(com.fathzer.soft.javaluator.DoubleEvaluator.Style style) {
        Parameters result = new Parameters();
        result.addOperators(style == com.fathzer.soft.javaluator.DoubleEvaluator.Style.STANDARD ? Arrays.asList(OPERATORS) : Arrays.asList(OPERATORS_EXCEL));
        result.addFunctions(Arrays.asList(FUNCTIONS));
        result.addConstants(Arrays.asList(CONSTANTS));
        result.addFunctionBracket(BracketPair.PARENTHESES);
        result.addExpressionBracket(BracketPair.PARENTHESES);
        return result;
    }

    private static Parameters getParameters() {
        if (DEFAULT_PARAMETERS == null) {
            DEFAULT_PARAMETERS = getDefaultParameters();
        }
        return DEFAULT_PARAMETERS;
    }

    @Override
    protected BigDecimal toValue(String literal, Object evaluationContext) {
        ParsePosition p = new ParsePosition(0);
        Number result = FORMATTER.get().parse(literal, p);
        if (p.getIndex() == 0 || p.getIndex() != literal.length()) {
            throw new IllegalArgumentException(literal + " is not a number");
        }
        return BigDecimal.valueOf(result.doubleValue());
    }

    /* (non-Javadoc)
     * @see net.astesana.javaluator.AbstractEvaluator#evaluate(net.astesana.javaluator.Constant)
     */
    @Override
    protected BigDecimal evaluate(Constant constant, Object evaluationContext) {
        if (PI.equals(constant)) {
            return BigDecimal.valueOf(Math.PI);
        } else if (E.equals(constant)) {
            return BigDecimal.valueOf(Math.E);
        } else {
            return super.evaluate(constant, evaluationContext);
        }
    }

    /* (non-Javadoc)
     * @see net.astesana.javaluator.AbstractEvaluator#evaluate(net.astesana.javaluator.Operator, java.util.Iterator)
     */
    @Override
    protected BigDecimal evaluate(Operator operator, Iterator<BigDecimal> operands, Object evaluationContext) {
        if (NEGATE.equals(operator) || NEGATE_HIGH.equals(operator)) {
            return operands.next().negate();
        } else if (MINUS.equals(operator)) {
            return operands.next().subtract(operands.next());
        } else if (PLUS.equals(operator)) {
            return operands.next().add(operands.next());
        } else if (MULTIPLY.equals(operator)) {
            return operands.next().multiply(operands.next());
        } else if (DIVIDE.equals(operator)) {
            return operands.next().divide(operands.next());
        } else if (EXPONENT.equals(operator)) {
            return operands.next().pow(operands.next().intValueExact());
        } else if (MODULO.equals(operator)) {
            return operands.next().remainder(operands.next());
        } else {
            return super.evaluate(operator, operands, evaluationContext);
        }
    }

    /* (non-Javadoc)
     * @see net.astesana.javaluator.AbstractEvaluator#evaluate(net.astesana.javaluator.Function, java.util.Iterator)
     */
    @Override
    protected BigDecimal evaluate(Function function, Iterator<BigDecimal> arguments, Object evaluationContext) {
        BigDecimal result;
        if (ABS.equals(function)) {
            result = arguments.next().abs();
        } else if (CEIL.equals(function)) {
            result = BigDecimal.valueOf(Math.ceil(arguments.next().doubleValue()));
        } else if (FLOOR.equals(function)) {
            result = BigDecimal.valueOf(Math.floor(arguments.next().doubleValue()));
        } else if (ROUND.equals(function)) {
            result = arguments.next().round(MathContext.DECIMAL32);
        } else if (SINEH.equals(function)) {
            result = BigDecimal.valueOf(Math.sinh(arguments.next().doubleValue()));
        } else if (COSINEH.equals(function)) {
            result = BigDecimal.valueOf(Math.cosh(arguments.next().doubleValue()));
        } else if (TANGENTH.equals(function)) {
            result = BigDecimal.valueOf(Math.tanh(arguments.next().doubleValue()));
        } else if (SINE.equals(function)) {
            result = BigDecimal.valueOf(Math.sin(arguments.next().doubleValue()));
        } else if (COSINE.equals(function)) {
            result = BigDecimal.valueOf(Math.cos(arguments.next().doubleValue()));
        } else if (TANGENT.equals(function)) {
            result = BigDecimal.valueOf(Math.tan(arguments.next().doubleValue()));
        } else if (ACOSINE.equals(function)) {
            result = BigDecimal.valueOf(Math.acos(arguments.next().doubleValue()));
        } else if (ASINE.equals(function)) {
            result = BigDecimal.valueOf(Math.asin(arguments.next().doubleValue()));
        } else if (ATAN.equals(function)) {
            result = BigDecimal.valueOf(Math.atan(arguments.next().doubleValue()));
        } else if (MIN.equals(function)) {
            result = arguments.next();
            while (arguments.hasNext()) {
                result = result.min(arguments.next());
            }
        } else if (MAX.equals(function)) {
            result = arguments.next();
            while (arguments.hasNext()) {
                result = result.min(arguments.next());
            }
        } else if (SUM.equals(function)) {
            result = BigDecimal.ZERO;
            while (arguments.hasNext()) {
                result = result.add(arguments.next());
            }
        } else if (AVERAGE.equals(function)) {
            result = BigDecimal.ZERO;
            int nb = 0;
            while (arguments.hasNext()) {
                result = result.add(arguments.next());
                nb++;
            }
            result = result.divide(BigDecimal.valueOf(nb));
        } else if (LN.equals(function)) {
            result = BigDecimal.valueOf(Math.log(arguments.next().doubleValue()));
        } else if (LOG.equals(function)) {
            result = BigDecimal.valueOf(Math.log10(arguments.next().doubleValue()));
        } else if (RANDOM.equals(function)) {
            result =BigDecimal.valueOf( Math.random());
        } else {
            result = super.evaluate(function, arguments, evaluationContext);
        }
        errIfNaN(result, function);
        return result;
    }

    private void errIfNaN(BigDecimal result, Function function) {
        if (result == null) {
            throw new IllegalArgumentException("Invalid argument passed to "+function.getName());
        }
    }
}
