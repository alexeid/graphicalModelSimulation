package lphy.core.distributions;

import lphy.graphicalModel.*;
import lphy.parser.ParserUtils;

import java.lang.reflect.Array;
import java.util.*;

public class VectorizedDistribution<T> implements GenerativeDistribution<T[]> {

    // the generative distribution to be vectorized
    GenerativeDistribution<T> baseDistribution;

    // the parameters (vectors)
    Map<String, Value> params;

    // the base types of the parameters in the base distribution
    Map<String, Class> baseTypes = new TreeMap<>();
    
    public VectorizedDistribution(GenerativeDistribution<T> baseDistribution, Map<String, Value> params) {
        this.baseDistribution = baseDistribution;

        this.params = params;

        baseDistribution.getParams().forEach((key, value) -> {
            if (value != null) baseTypes.put(key, value.getType());
        });
    }

    @Override
    public RandomVariable<T[]> sample() {

        int size = 1;
        for (Map.Entry<String, Value> entry : params.entrySet()) {
            String name = entry.getKey();
            Value v = entry.getValue();
            if (isArrayOfType(v, baseTypes.get(name))) {
                int vectorSize = Array.getLength(v.value());
                if (size == 1) {
                    size = vectorSize;
                } else if (size != vectorSize) {
                    throw new RuntimeException("Vector sizes do not match!");
                }
                Object input = Array.get(v.value(), 0);
                baseDistribution.setParam(name, new Value(null, input));
            } else {
                baseDistribution.setParam(name, v);
            }
        }
        Value<T> first = baseDistribution.sample();

        T[] result = (T[]) Array.newInstance(first.value().getClass(), size);
        result[0] = first.value();
        for (int i = 1; i < result.length; i++) {
            for (Map.Entry<String, Value> entry : params.entrySet()) {
                String name = entry.getKey();
                Value v = entry.getValue();
                if (isArrayOfType(v, baseTypes.get(name))) {
                    Object input = Array.get(v.value(), i);
                    baseDistribution.setParam(name, new Value(null, input));
                }
            }
            result[i] = baseDistribution.sample().value();
        }
        return new RandomVariable<>(null, result, this);
    }

    /**
     * @param maybeArray
     * @param ofType
     * @return
     */
    static boolean isArrayOfType(Value maybeArray, Class ofType) {

        if (maybeArray.value().getClass().isArray()) {
            Class componentClass = maybeArray.value().getClass().getComponentType();
            return componentClass.isAssignableFrom(ofType);
        }
        return false;
    }

    @Override
    public Map<String, Value> getParams() {
        return params;
    }

    @Override
    public void setParam(String paramName, Value value) {
        params.put(paramName, value);
    }

    @Override
    public String getName() {
        return baseDistribution.getName();
    }

    public static void main(String[] args) {

        Value a = new Value<>("alpha", 2.0);
        Value b = new Value<>("beta", 2.0);

        Beta beta = new Beta(a, b);

        Map<String, Value> params = new HashMap<>();
        params.put("alpha", new Value<>("alpha", new Double[] {200.0, 200.0, 200.0, 3.0, 3.0, 3.0}));
        params.put("beta", new Value<>("beta", 2.0));
        Object[] initArgs = {params.get("alpha"),params.get("beta")};


        System.out.println(" vector match = " + ParserUtils.vectorMatch(Generator.getParameterInfo(beta.getClass(), 0),initArgs));

        VectorizedDistribution<Double> v = new VectorizedDistribution<>(beta, params);

        RandomVariable<Double[]> rbeta = v.sample();

        System.out.println(Arrays.toString(rbeta.value()));
    }
}
