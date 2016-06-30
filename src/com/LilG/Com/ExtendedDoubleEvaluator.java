package com.LilG.Com;

import com.fathzer.soft.javaluator.DoubleEvaluator;
import com.fathzer.soft.javaluator.Function;
import com.fathzer.soft.javaluator.Operator;
import com.fathzer.soft.javaluator.Parameters;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

/**
 * A subclass of DoubleEvaluator that supports SQRT function.
 */
class ExtendedDoubleEvaluator extends DoubleEvaluator{
    /**
     * The logical OR operator.
     */
    private final static Operator OR = new Operator("|", 2, Operator.Associativity.LEFT, 1);
    /**
     * The logical AND operator.
     */
    private static final Operator AND = new Operator("&", 2, Operator.Associativity.LEFT, 2);
    /**
     * The logical XOR operator.
     */
    private static final Operator XOR = new Operator("^", 2, Operator.Associativity.LEFT, 2);
    /**
     * Defines the new function (square root).
     */
    private static final Function SQRT = new Function("sqrt", 1);
    /**
     * Defines the new function (Convert to base).
     */
    private static final Function BASE = new Function("base", 2);
    @NotNull
    private static final Parameters PARAMS;

    static {
        // Gets the default DoubleEvaluator's parameters
        PARAMS = DoubleEvaluator.getDefaultParameters();
        // add the new sqrt function to these parameters
        PARAMS.add(SQRT);
        PARAMS.add(BASE);
        PARAMS.add(AND);
        PARAMS.add(OR);
        PARAMS.add(XOR);
    }

    public ExtendedDoubleEvaluator() {
        super(PARAMS);
    }

    @Override
    protected Double evaluate(Function function, @NotNull Iterator<Double> arguments, Object evaluationContext) {
        if (function == SQRT) {
            // Implements the new function
            return Math.sqrt(arguments.next());
        } else if (function == BASE) {
            // Implements the new function
            return Double.parseDouble(Long.toString(arguments.next().longValue(), arguments.next().intValue()));
        } else if (function == SQRT) {
            // Implements the new function
            return Math.sqrt(arguments.next());
        } else {
            // If it's another function, pass it to DoubleEvaluator
            return super.evaluate(function, arguments, evaluationContext);
        }
    }
}