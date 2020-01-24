package james.core.distributions;

import james.graphicalModel.GenerativeDistribution;
import james.graphicalModel.ParameterInfo;
import james.graphicalModel.RandomVariable;
import james.graphicalModel.Value;
import org.apache.commons.math3.distribution.LogNormalDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;

import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Normal distribution
 */
public class Normal implements GenerativeDistribution<Double> {

    private final String meanParamName;
    private final String sdParamName;
    private Value<Double> mean;
    private Value<Double> sd;

    private Random random;

    NormalDistribution normalDistribution;

    public Normal(@ParameterInfo(name = "\u03bc", description = "the mean of the distribution.") Value<Double> mean,
                  @ParameterInfo(name = "\u03c3", description = "the standard deviation of the distribution.") Value<Double> sd,
                  Random random) {

        this.mean = mean;
        this.sd = sd;
        this.random = random;

        meanParamName = getParamName(0);
        sdParamName = getParamName(1);
    }

    public RandomVariable<Double> sample() {

        normalDistribution = new NormalDistribution(mean.value(), sd.value());
        double x = normalDistribution.sample();
        return new RandomVariable<>("x", x, this);
    }

    @Override
    public double density(Double x) {
        return normalDistribution.density(x);
    }

    public Map<String, Value> getParams() {
        SortedMap<String, Value> map = new TreeMap<>();
        map.put(meanParamName, mean);
        map.put(sdParamName, sd);
        return map;
    }

    @Override
    public void setParam(String paramName, Value value) {
        if (paramName.equals(meanParamName)) mean = value;
        else if (paramName.equals(sdParamName)) sd = value;
        else throw new RuntimeException("Unrecognised parameter name: " + paramName);
    }

    public String toString() {
        return getName();
    }

}