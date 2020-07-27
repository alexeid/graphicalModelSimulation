package lphy.core.distributions;

import beast.core.BEASTInterface;
import beast.core.parameter.RealParameter;
import lphy.beast.BEASTContext;
import lphy.graphicalModel.*;
import org.apache.commons.math3.distribution.BetaDistribution;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by adru001 on 18/12/19.
 */
public class Beta implements GenerativeDistribution<Double> {

    private final String alphaParamName;
    private final String betaParamName;
    private Value<Double> alpha;
    private Value<Double> beta;

    public Beta(@ParameterInfo(name="alpha", description="the first shape parameter.") Value<Double> alpha,
                @ParameterInfo(name="beta", description="the second shape parameter.") Value<Double> beta) {
        this.alpha = alpha;
        this.beta = beta;
        alphaParamName = getParamName(0);
        betaParamName = getParamName(1);
    }

    @GeneratorInfo(name="Beta", description="The beta probability distribution.")
    public RandomVariable<Double> sample() {

        BetaDistribution betaDistribution = new BetaDistribution(alpha.value(), beta.value());

        double randomVariable = betaDistribution.sample();

        return new RandomVariable<>("x", randomVariable, this);
    }

    public double logDensity(Double d) {
        BetaDistribution betaDistribution = new BetaDistribution(alpha.value(), beta.value());
        return betaDistribution.logDensity(d);
    }

    @Override
    public Map<String,Value> getParams() {
        SortedMap<String, Value> map = new TreeMap<>();
        map.put(alphaParamName, alpha);
        map.put(betaParamName, beta);
        return map;
    }

    @Override
    public void setParam(String paramName, Value value) {
        if (paramName.equals(alphaParamName)) alpha = value;
        else if (paramName.equals(betaParamName)) beta = value;
        else throw new RuntimeException("Unrecognised parameter name: " + paramName);
    }

    public String toString() {
        return getName();
    }

    public BEASTInterface toBEAST(BEASTInterface value, BEASTContext context) {
        beast.math.distributions.Beta betaDistribution = new beast.math.distributions.Beta();
        betaDistribution.setInputValue("alpha", context.getBEASTObject(getParams().get("alpha")));
        betaDistribution.setInputValue("beta", context.getBEASTObject(getParams().get("beta")));
        betaDistribution.initAndValidate();
        return BEASTContext.createPrior(betaDistribution, (RealParameter)value);
    }


}