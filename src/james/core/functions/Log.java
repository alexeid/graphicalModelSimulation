package james.core.functions;

import james.graphicalModel.*;

public class Log extends Function<Double, Double> {

    @Override
    public Value<Double> apply(Value<Double> v) {
        setParam("x", v);
        return new DoubleValue("log " + v.getId(), Math.log(v.value()), this);
    }

    @Override
    public String getName() {
        return "log";
    }
}
