using com.fathzer.soft.javaluator;
using java.lang;
using java.math;
using java.text;
using java.util;
using org.apfloat;

namespace FozruciCS.Math{
	public class ArbitraryPrecisionEvaluator : AbstractEvaluator{
        /**
         * A constant that represents pi (3.14159...)
         */
        public static readonly Constant Pi = new Constant("pi");
        /**
         * A constant that represents e (2.718281...)
         */
        public static readonly Constant E = new Constant("e");
    
        /**
         * Returns the smallest integer >= argument
         */
        public static readonly Function Ceil = new Function("ceil", 1);
        /**
         * Returns the largest integer <= argument
         */
        public static readonly Function Floor = new Function("floor", 1);
        /**
         * Returns the closest integer of a number
         */
        public static readonly Function Round = new Function("round", 1);
        /**
         * Returns the absolute value of a number
         */
        public static readonly Function Abs = new Function("abs", 1);
    
        /**
         * Returns the trigonometric sine of an angle. The angle is expressed in radian.
         */
        public static readonly Function Sine = new Function("sin", 1);
        /**
         * Returns the trigonometric cosine of an angle. The angle is expressed in radian.
         */
        public static readonly Function Cosine = new Function("cos", 1);
        /**
         * Returns the trigonometric tangent of an angle. The angle is expressed in radian.
         */
        public static readonly Function Tangent = new Function("tan", 1);
        /**
         * Returns the trigonometric arc-cosine of an angle. The angle is expressed in radian.
         */
        public static readonly Function Acosine = new Function("acos", 1);
        /**
         * Returns the trigonometric arc-sine of an angle. The angle is expressed in radian.
         */
        public static readonly Function Asine = new Function("asin", 1);
        /**
         * Returns the trigonometric arc-tangent of an angle. The angle is expressed in radian.
         */
        public static readonly Function Atan = new Function("atan", 1);
    
        /**
         * Returns the hyperbolic sine of a number.
         */
        public static readonly Function Sineh = new Function("sinh", 1);
        /**
         * Returns the hyperbolic cosine of a number.
         */
        public static readonly Function Cosineh = new Function("cosh", 1);
        /**
         * Returns the hyperbolic tangent of a number.
         */
        public static readonly Function Tangenth = new Function("tanh", 1);
    
        /**
         * Returns the minimum of n numbers (n>=1)
         */
        public static readonly Function Min = new Function("min", 1, int.MaxValue);
        /**
         * Returns the maximum of n numbers (n>=1)
         */
        public static readonly Function Max = new Function("max", 1, int.MaxValue);
        /**
         * Returns the sum of n numbers (n>=1)
         */
        public static readonly Function Sum = new Function("sum", 1, int.MaxValue);
        /**
         * Returns the average of n numbers (n>=1)
         */
        public static readonly Function Average = new Function("avg", 1, int.MaxValue);
    
        /**
         * Returns the natural logarithm of a number
         */
        public static readonly Function Ln = new Function("ln", 1);
        /**
         * Returns the decimal logarithm of a number
         */
        public static readonly Function Log = new Function("log", 1);
    
        /**
         * Returns a pseudo random number
         */
        public static readonly Function Random = new Function("random", 0);
    
        /**
         * The negate unary operator_ in the standard operator_ precedence.
         */
        public static readonly Operator Negate = new Operator("-", 1, Operator.Associativity.RIGHT, 3);
        /**
         * The negate unary operator_ in the Excel like operator_ precedence.
         */
        public static readonly Operator NegateHigh = new Operator("-", 1, Operator.Associativity.RIGHT, 5);
        /**
         * The substraction operator_.
         */
        public static readonly Operator Minus = new Operator("-", 2, Operator.Associativity.LEFT, 1);
        /**
         * The addition operator_.
         */
        public static readonly Operator Plus = new Operator("+", 2, Operator.Associativity.LEFT, 1);
        /**
         * The multiplication operator_.
         */
        public static readonly Operator Multiply = new Operator("*", 2, Operator.Associativity.LEFT, 2);
        /**
         * The division operator_.
         */
        public static readonly Operator Divide = new Operator("/", 2, Operator.Associativity.LEFT, 2);
        /**
         * The exponentiation operator_.
         */
        public static readonly Operator Exponent = new Operator("^", 2, Operator.Associativity.LEFT, 4);
        /**
         * The <a href="http://en.wikipedia.org/wiki/Modulo_operation">modulo operator_</a>.
         */
        public static readonly Operator Modulo = new Operator("%", 2, Operator.Associativity.LEFT, 2);
    
        /**
         * The standard whole set of predefined operator_s
         */
        private static readonly Operator[] Operators = {Negate, Minus, Plus, Multiply, Divide, Exponent, Modulo};
        /**
         * The excel like whole set of predefined operator_s
         */
        private static readonly Operator[] OperatorsExcel = {NegateHigh, Minus, Plus, Multiply, Divide, Exponent, Modulo};
        /**
         * The whole set of predefined functions
         */
	    // ReSharper disable once UnusedMember.Local
        private static readonly Function[] Functions = {Sine, Cosine, Tangent, Asine, Acosine, Atan, Sineh, Cosineh, Tangenth, Min, Max, Sum, Average, Ln, Log, Round, Ceil, Floor, Abs, Random};
        /**
         * The whole set of predefined constants
         */
        private static readonly Constant[] Constants = {Pi, E};
	    // ReSharper disable once UnusedMember.Local
        private static readonly NumberFormat Formatter = NumberFormat.getNumberInstance(Locale.US);
        private static Parameters _defaultParameters;
    
        private static long _precision = 64;
    
        /**
         * Constructor.
         * <br>This default constructor builds an instance with all predefined operator_s, functions and constants.
         */
        public ArbitraryPrecisionEvaluator() : this(getParameters()){}
    
        /**
         * Constructor.
         * <br>This constructor can be used to reduce the set of supported operator_s, functions or constants,
         * or to localize some function or constant's names.
         *
         * @param parameters The parameters of the evaluator.
         */
        public ArbitraryPrecisionEvaluator(Parameters parameters) : base(parameters){}
    
        /**
         * Gets a copy of DoubleEvaluator standard default parameters.
         * <br>The returned parameters contains all the predefined operator_s, functions and constants.
         * <br>Each call to this method create a new instance of Parameters.
         *
         * @return a Paramaters instance
         * @see DoubleEvaluator.Style
         */
        public static Parameters getDefaultParameters() {
            return getDefaultParameters(DoubleEvaluator.Style.STANDARD);
        }
    
        /**
         * Gets a copy of DoubleEvaluator default parameters.
         * <br>The returned parameters contains all the predefined operator_s, functions and constants.
         * <br>Each call to this method create a new instance of Parameters.
         *
         * @return a Paramaters instance
         */

	    // ReSharper disable CoVariantArrayConversion
        public static Parameters getDefaultParameters(DoubleEvaluator.Style style) {
            var result = new Parameters();
            result.addOperators(style == DoubleEvaluator.Style.STANDARD ? Arrays.asList(Operators) : Arrays.asList(OperatorsExcel));
            result.addFunctions(Arrays.asList());
            result.addConstants(Arrays.asList(Constants));
            result.addFunctionBracket(BracketPair.PARENTHESES);
            result.addExpressionBracket(BracketPair.PARENTHESES);
            return result;
        }
	    // ReSharper restore CoVariantArrayConversion
    
        private static Parameters getParameters(){
            return _defaultParameters ?? (_defaultParameters = getDefaultParameters());
        }
    
        
        protected override object toValue(string literal, object evaluationContext) {
            return new Apfloat(literal, _precision);
        }
    
        /* (non-Javadoc)
         * @see net.astesana.javaluator.AbstractEvaluator#evaluate(net.astesana.javaluator.Constant)
         */
        
        protected new Apfloat evaluate(Constant constant, object evaluationContext){
            if (Pi.equals(constant)) {
                return ApfloatMath.pi(_precision);
            }
            if (E.equals(constant)) {
                return new Apfloat(java.lang.Math.E, _precision);
            }
            return base.evaluate(constant, evaluationContext) as Apfloat;
        }
    
        /* (non-Javadoc)
         * @see net.astesana.javaluator.AbstractEvaluator#evaluate(net.astesana.javaluator.Operator, java.util.Iterator)
         */
        
        protected new Apfloat evaluate(Operator operator_, Iterator operands, object evaluationContext){
            if (Negate.equals(operator_) || NegateHigh.equals(operator_)) {
                return ((Apfloat)operands.next()).negate();
            }
            if (Minus.equals(operator_)) {
                return ((Apfloat)operands.next()).subtract((Apfloat)operands.next());
            }
            if (Plus.equals(operator_)) {
                return ((Apfloat)operands.next()).add(((Apfloat)operands.next()));
            }
            if (Multiply.equals(operator_)) {
                return ((Apfloat)operands.next()).multiply((Apfloat)operands.next());
            }
            if (Divide.equals(operator_)) {
                return ((Apfloat)operands.next()).divide((Apfloat)operands.next());
            }
            if (Exponent.equals(operator_)) {
                return ApfloatMath.pow((Apfloat)operands.next(),(Apfloat)operands.next());
            }
            if (Modulo.equals(operator_)) {
                return ApfloatMath.fmod((Apfloat)operands.next(),(Apfloat)operands.next());
            }
            return base.evaluate(operator_, operands, evaluationContext) as Apfloat;
        }
    
        /* (non-Javadoc)
         * @see net.astesana.javaluator.AbstractEvaluator#evaluate(net.astesana.javaluator.Function, java.util.Iterator)
         */
        
        protected new Apfloat evaluate(Function function, Iterator arguments, object evaluationContext) {
            Apfloat result;
            if (Abs.equals(function)) {
                result = ApfloatMath.abs((Apfloat)arguments.next());
            } else if (Ceil.equals(function)) {
                result = ((Apfloat)arguments.next()).ceil();
            } else if (Floor.equals(function)) {
                result = ((Apfloat)arguments.next()).floor();
            } else if (Round.equals(function)) {
                result = ApfloatMath.round((Apfloat)arguments.next(),0, RoundingMode.UP);
            } else if (Sineh.equals(function)) {
                result = ApfloatMath.sinh(((Apfloat)arguments.next()));
            } else if (Cosineh.equals(function)) {
                result = ApfloatMath.cosh(((Apfloat)arguments.next()));
            } else if (Tangenth.equals(function)) {
                result = ApfloatMath.tanh(((Apfloat)arguments.next()));
            } else if (Sine.equals(function)) {
                result = ApfloatMath.sin(((Apfloat)arguments.next()));
            } else if (Cosine.equals(function)) {
                result = ApfloatMath.cos(((Apfloat)arguments.next()));
            } else if (Tangent.equals(function)) {
                result = ApfloatMath.tan(((Apfloat)arguments.next()));
            } else if (Acosine.equals(function)) {
                result = ApfloatMath.acos(((Apfloat)arguments.next()));
            } else if (Asine.equals(function)) {
                result = ApfloatMath.asin(((Apfloat)arguments.next()));
            } else if (Atan.equals(function)) {
                result = ApfloatMath.atan(((Apfloat)arguments.next()));
            } else if (Min.equals(function)) {
                result = (Apfloat)arguments.next();
                while (arguments.hasNext()) {
                    var next = (Apfloat)arguments.next();
                    if(result.compareTo(next) > 0) {
                        result = next;
                    }
                }
            } else if (Max.equals(function)) {
                result = (Apfloat)arguments.next();
                while (arguments.hasNext()) {
                    var next = (Apfloat)arguments.next();
                    if(result.compareTo(next) < 0) {
                        result = next;
                    }
                }
            } else if (Sum.equals(function)) {
                result = Apcomplex.ZERO;
                while (arguments.hasNext()) {
                    result = result.add(((Apfloat)arguments.next()));
                }
            } else if (Average.equals(function)) {
                result = Apcomplex.ZERO;
                var nb = 0;
                while (arguments.hasNext()) {
                    result = result.add(((Apfloat)arguments.next()));
                    nb++;
                }
                result = result.divide(new Apfloat(nb, _precision));
            } else if (Ln.equals(function)) {
                result = ApfloatMath.log(((Apfloat)arguments.next()));
            } else if (Log.equals(function)) {
                result = ApfloatMath.log(((Apfloat)arguments.next()), new Apfloat(10, _precision));
            } else if (Random.equals(function)) {
                result = new Apfloat(java.lang.Math.random(), _precision);
            } else {
                result = base.evaluate(function, arguments, evaluationContext) as Apfloat;
            }
            errIfNaN(result, function);
            return result;
        }
    
	    // ReSharper disable once UnusedParameter.Local
        private static void errIfNaN(Apfloat result, Function function) {
            if(result == null){
                throw new IllegalArgumentException("Invalid argument passed to " + function.getName());
            }
        }
    
        public void setPrecision(long precision) {
            if (precision > 0) {
                _precision = precision;
            }
        }
	}
}