package com.LilG.math;

import com.fathzer.soft.javaluator.*;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;

/**
 * An evaluator that is able to evaluate arithmetic expressions on real numbers.
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
 *
 * @author Jean-Marc Astesana
 * @see <a href="../../../license.html">License information</a>
 */
public class ArbitraryPrecisionEvaluator extends AbstractEvaluator<Apfloat> {
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
	private static final ThreadLocal<NumberFormat> FORMATTER = ThreadLocal.withInitial(() -> NumberFormat.getNumberInstance(Locale.US));
	private static Parameters DEFAULT_PARAMETERS;

	private static long precision = 64;

    /**
     * Constructor.
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

    /**
     * Gets a copy of DoubleEvaluator standard default parameters.
     * <br>The returned parameters contains all the predefined operators, functions and constants.
     * <br>Each call to this method create a new instance of Parameters.
     *
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
    protected Apfloat toValue(String literal, Object evaluationContext) {
	    return new Apfloat(literal, precision);
    }

    /* (non-Javadoc)
     * @see net.astesana.javaluator.AbstractEvaluator#evaluate(net.astesana.javaluator.Constant)
     */
    @Override
    protected Apfloat evaluate(Constant constant, Object evaluationContext) {
        if (PI.equals(constant)) {
	        return ApfloatMath.pi(precision);
        } else if (E.equals(constant)) {
	        return new Apfloat(Math.E, precision);
        } else {
            return super.evaluate(constant, evaluationContext);
        }
    }

    /* (non-Javadoc)
     * @see net.astesana.javaluator.AbstractEvaluator#evaluate(net.astesana.javaluator.Operator, java.util.Iterator)
     */
    @Override
    protected Apfloat evaluate(Operator operator, Iterator<Apfloat> operands, Object evaluationContext) {
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
            return ApfloatMath.pow(operands.next(),(operands.next()));
        } else if (MODULO.equals(operator)) {
            return ApfloatMath.fmod(operands.next(),(operands.next()));
        } else {
            return super.evaluate(operator, operands, evaluationContext);
        }
    }

    /* (non-Javadoc)
     * @see net.astesana.javaluator.AbstractEvaluator#evaluate(net.astesana.javaluator.Function, java.util.Iterator)
     */
    @Override
    protected Apfloat evaluate(Function function, Iterator<Apfloat> arguments, Object evaluationContext) {
        Apfloat result;
        if (ABS.equals(function)) {
            result = ApfloatMath.abs(arguments.next());
        } else if (CEIL.equals(function)) {
            result = arguments.next().ceil();
        } else if (FLOOR.equals(function)) {
            result = arguments.next().floor();
        } else if (ROUND.equals(function)) {
            result = ApfloatMath.round(arguments.next(),0, RoundingMode.UP);
        } else if (SINEH.equals(function)) {
            result = ApfloatMath.sinh(arguments.next());
        } else if (COSINEH.equals(function)) {
            result = ApfloatMath.cosh(arguments.next());
        } else if (TANGENTH.equals(function)) {
            result = ApfloatMath.tanh(arguments.next());
        } else if (SINE.equals(function)) {
            result = ApfloatMath.sin(arguments.next());
        } else if (COSINE.equals(function)) {
            result = ApfloatMath.cos(arguments.next());
        } else if (TANGENT.equals(function)) {
            result = ApfloatMath.tan(arguments.next());
        } else if (ACOSINE.equals(function)) {
            result = ApfloatMath.acos(arguments.next());
        } else if (ASINE.equals(function)) {
            result = ApfloatMath.asin(arguments.next());
        } else if (ATAN.equals(function)) {
            result = ApfloatMath.atan(arguments.next());
        } else if (MIN.equals(function)) {
            result = arguments.next();
            while (arguments.hasNext()) {
                Apfloat next = arguments.next();
                if(result.compareTo(next) > 0) {
                    result = next;
                }
            }
        } else if (MAX.equals(function)) {
            result = arguments.next();
            while (arguments.hasNext()) {
                Apfloat next = arguments.next();
                if(result.compareTo(next) < 0) {
                    result = next;
                }
            }
        } else if (SUM.equals(function)) {
            result = Apfloat.ZERO;
            while (arguments.hasNext()) {
                result = result.add(arguments.next());
            }
        } else if (AVERAGE.equals(function)) {
            result = Apfloat.ZERO;
            int nb = 0;
            while (arguments.hasNext()) {
                result = result.add(arguments.next());
                nb++;
            }
	        result = result.divide(new Apfloat(nb, precision));
        } else if (LN.equals(function)) {
            result = ApfloatMath.log(arguments.next());
        } else if (LOG.equals(function)) {
	        result = ApfloatMath.log(arguments.next(), new Apfloat(10, precision));
        } else if (RANDOM.equals(function)) {
	        result = new Apfloat(Math.random(), precision);
        } else {
            result = super.evaluate(function, arguments, evaluationContext);
        }
        errIfNaN(result, function);
        return result;
    }

    private void errIfNaN(Apfloat result, Function function) {
        if (result == null) {
            throw new IllegalArgumentException("Invalid argument passed to " + function.getName());
        }
    }

	public void setPrecision(long precision) {
		if (precision > 0) {
			ArbitraryPrecisionEvaluator.precision = precision;
		}
	}
}
