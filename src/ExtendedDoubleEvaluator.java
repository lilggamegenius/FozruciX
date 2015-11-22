import com.fathzer.soft.javaluator.DoubleEvaluator;
import com.fathzer.soft.javaluator.Function;
import com.fathzer.soft.javaluator.Operator;
import com.fathzer.soft.javaluator.Parameters;

import java.util.Iterator;

/**
 * A subclass of DoubleEvaluator that supports SQRT function.
 */
public class ExtendedDoubleEvaluator extends DoubleEvaluator {
    /**
     * The logical OR operator.
     */
    public final static Operator OR = new Operator("|", 2, Operator.Associativity.LEFT, 1);
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

    protected Double eveluate(Operator operator, Iterator<Double> operands, Object evaluationContext) {
        if (operator == AND) {
            return (double) (operands.next().intValue() & operands.next().intValue());
        } else if (operator == OR) {
            return (double) (operands.next().intValue() | operands.next().intValue());
        } else if (operator == XOR) {
            return (double) (operands.next().intValue() ^ operands.next().intValue());
        } else {
            return super.evaluate(operator, operands, evaluationContext);
        }
    }

    @Override
    protected Double evaluate(Function function, Iterator<Double> arguments, Object evaluationContext) {
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