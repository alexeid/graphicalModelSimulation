package lphy.evolution.birthdeath;

import lphy.evolution.tree.TimeTree;
import lphy.graphicalModel.*;

import java.util.SortedMap;
import java.util.TreeMap;

import static lphy.graphicalModel.ValueUtils.doubleValue;

/**
 * A Birth-death-sampling tree generative distribution
 */
public class BirthDeathSamplingTreeDT implements GenerativeDistribution<TimeTree> {

    private static final String diversificationParamName = "diversification";
    private static final String turnoverParamName = "turnover";

    private Value<Number> diversificationRate;
    private Value<Number> turnover;
    private Value<Number> rho;
    private Value<Number> rootAge;

    BirthDeathSamplingTree wrapped;

    public BirthDeathSamplingTreeDT(@ParameterInfo(name = diversificationParamName, description = "diversification rate.") Value<Number> diversification,
                                    @ParameterInfo(name = turnoverParamName, description = "turnover.") Value<Number> turnover,
                                    @ParameterInfo(name = BirthDeathSamplingTree.rhoParamName, description = "the sampling proportion.") Value<Number> rho,
                                    @ParameterInfo(name = BirthDeathSamplingTree.rootAgeParamName, description = "the number of taxa.") Value<Number> rootAge
    ) {

        this.turnover = turnover;
        this.diversificationRate = diversification;
        this.rho = rho;
        this.rootAge = rootAge;
        setup();
    }

    @GeneratorInfo(name = "BirthDeathSampling", description = "The Birth-death-sampling tree distribution over tip-labelled time trees.<br>" +
            "Conditioned on root age.")
    public RandomVariable<TimeTree> sample() {

        setup();
        RandomVariable<TimeTree> tree = wrapped.sample();
        return new RandomVariable<>("\u03C8", tree.value(), this);
    }

    private void setup() {
        double turno = doubleValue(turnover);
        double divers = doubleValue(diversificationRate);

        double denom = Math.abs(1.0 - turno);
        double birth_rate = divers / denom;
        double death_rate = (turno * divers) / denom;

        wrapped = new BirthDeathSamplingTree(
                new Value<>("birthRate", birth_rate),
                new Value<>("deathRate", death_rate),
                rho, rootAge);
    }

    @Override
    public double logDensity(TimeTree timeTree) {

        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public SortedMap<String, Value> getParams() {
        SortedMap<String, Value> map = new TreeMap<>();
        map.put(diversificationParamName, diversificationRate);
        map.put(turnoverParamName, turnover);
        map.put(BirthDeathSamplingTree.rhoParamName, rho);
        map.put(BirthDeathSamplingTree.rootAgeParamName, rootAge);
        return map;
    }

    @Override
    public void setParam(String paramName, Value value) {
        if (paramName.equals(diversificationParamName)) diversificationRate = value;
        else if (paramName.equals(turnoverParamName)) turnover = value;
        else if (paramName.equals(BirthDeathSamplingTree.rhoParamName)) rho = value;
        else if (paramName.equals(BirthDeathSamplingTree.rootAgeParamName)) rootAge = value;
        else throw new RuntimeException("Unrecognised parameter name: " + paramName);
    }

    public Value<Number> getDiversificationRate() {
        return diversificationRate;
    }

    public Value<Number> getTurnover() {
        return turnover;
    }

    public Value<Number> getRho() {
        return rho;
    }

    public Value<Number> getRootAge() {
        return rootAge;
    }
}