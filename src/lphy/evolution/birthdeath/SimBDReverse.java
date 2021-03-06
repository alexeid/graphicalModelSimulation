package lphy.evolution.birthdeath;

import lphy.core.distributions.Utils;
import lphy.evolution.Taxa;
import lphy.evolution.tree.TaxaConditionedTreeGenerator;
import lphy.evolution.tree.TimeTree;
import lphy.evolution.tree.TimeTreeNode;
import lphy.evolution.tree.TimeTreeUtils;
import lphy.graphicalModel.*;
import lphy.math.MathUtils;
import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.random.RandomGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static lphy.evolution.birthdeath.BirthDeathConstants.*;
import static lphy.graphicalModel.ValueUtils.doubleValue;

/**
 * A java implementation of https://rdrr.io/cran/TreeSim/src/R/sim2.bd.reverse.single.R
 */
public class SimBDReverse extends TaxaConditionedTreeGenerator {

    private Value<Number> birthRate;
    private Value<Number> deathRate;
    private Value<Number> rhoVal;
    
    public SimBDReverse(@ParameterInfo(name = lambdaParamName, description = "per-lineage birth rate.") Value<Number> birthRate,
                        @ParameterInfo(name = muParamName, description = "per-lineage death rate.") Value<Number> deathRate,
                        @ParameterInfo(name = taxaParamName, description = "The extant taxa that this process are conditioned on") Value<Taxa> taxa,
                        @ParameterInfo(name = rhoParamName, description = "The fraction of total extant species that the conditioned-on taxa represent. " +
                                "The resulting tree will have taxa.ntaxa()/rho total extant taxa.") Value<Number> rho) {

        super(null, taxa, null);

        this.birthRate = birthRate;
        this.deathRate = deathRate;
        this.rhoVal = rho;
        this.random = Utils.getRandom();

        // Taxa to condition on must all be extant!
        if (!taxa.value().isUltrametric() || taxa.value().getTaxon(0).getAge() != 0.0) {
            throw new IllegalArgumentException("Taxa to condition on must all be extant!");
        }
    }


    @GeneratorInfo(name = "SimBDReverse", description = "A complete birth-death tree with both extant and extinct species.<br>" +
            "Conditioned on (a fraction of) extant taxa.")
    public RandomVariable<TimeTree> sample() {

        TimeTree tree = new TimeTree();

        double lambda = doubleValue(birthRate);
        double mu = doubleValue(deathRate);
        double rho = doubleValue(rhoVal);

        List<TimeTreeNode> activeNodes = createLeafTaxa(tree);

        int maxleaf = (int) Math.round(activeNodes.size()/rho);

        while (activeNodes.size() < maxleaf) {
            activeNodes.add(new TimeTreeNode(0.0));
        }

        TimeTreeNode rootNode = null;
        TimeTreeNode originNode = null;

        double time = 0.0;

        while (activeNodes.size() > 0) {

            double timestep = MathUtils.nextExponential(activeNodes.size()*(lambda+mu), random);

            time += timestep;

            double specevent = random.nextDouble();

            if (lambda/(lambda+mu) >= specevent) { // speciation

                if (activeNodes.size() > 1) { // do speciation backwards (i.e. coalescent event)

                    TimeTreeNode a = drawRandomNode(activeNodes);
                    TimeTreeNode b = drawRandomNode(activeNodes);

                    TimeTreeNode parent = new TimeTreeNode(time, new TimeTreeNode[]{a, b});
                    activeNodes.add(parent);

                } else { // last speciation event back in time, so this is the origin
                    rootNode = activeNodes.remove(0);
                    originNode = new TimeTreeNode(time, new TimeTreeNode[] {rootNode});

                    if (activeNodes.size() != 0) throw new AssertionError();
                }
            } else { // extinction back in time, means adding a new anonymous tip at this time.
                activeNodes.add(new TimeTreeNode(time));
            }
        }

        tree.setRoot(originNode, true);
        
        return new RandomVariable<>(null, tree, this);
    }

    @Override
    public double logDensity(TimeTree timeTree) {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public Map<String, Value> getParams() {
        Map<String, Value> params = super.getParams();
        params.put(lambdaParamName, birthRate);
        params.put(muParamName, deathRate);
        params.put(rhoParamName, rhoVal);
        return params;
    }

    @Override
    public void setParam(String paramName, Value value) {
        if (paramName.equals(lambdaParamName)) birthRate = value;
        else if (paramName.equals(muParamName)) deathRate = value;
        else if (paramName.equals(rhoParamName)) rhoVal = value;
        else super.setParam(paramName, value);
    }

    public String toString() {
        return getName();
    }
}
