package lphy.core.functions;

import lphy.graphicalModel.FunctionInfo;
import lphy.graphicalModel.ParameterInfo;
import lphy.graphicalModel.Value;
import lphy.graphicalModel.types.DoubleArray2DValue;

/**
 * Created by adru001 on 2/02/20.
 */
public class F81 extends RateMatrix {

    String freqParamName;

    public F81(@ParameterInfo(name = "freq", description = "the base frequencies.") Value<Double[]> freq) {
        freqParamName = getParamName(0);
        setParam(freqParamName, freq);
    }


    @FunctionInfo(name = "hky", description = "The HKY instantaneous rate matrix. Takes a kappa and base frequencies and produces an HKY85 rate matrix.")
    public Value<Double[][]> apply() {
        Value<Double[]> freq = getParams().get(freqParamName);
        return new DoubleArray2DValue( f81(freq.value()), this);
    }

    private Double[][] f81(Double[] freqs) {

        int numStates = 4;

        Double[][] Q = new Double[numStates][numStates];

        double[] totalRates = new double[numStates];

        for (int i = 0; i < numStates; i++) {
            for (int j = 0; j < numStates; j++) {
                if (i != j) {
                    Q[i][j] = freqs[j];
                } else Q[i][i] = 0.0;
                totalRates[i] += Q[i][j];
            }
            Q[i][i] = -totalRates[i];
        }

        // normalise rate matrix to one expected substitution per unit time
        normalize(freqs, Q);

        return Q;
    }
}