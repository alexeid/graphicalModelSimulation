package james.graphicalModel;

import james.swing.HasComponentView;

import javax.swing.*;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by adru001 on 18/12/19.
 */
public class Value<T> implements Viewable {

    private T value;
    String id;
    List<ValueListener> listeners = new ArrayList<>();

    // the function that produced this value, or null if this value was initialized another way;
    Function function = null;

    public Value(String id, T value) {
        this.id = id;
        this.value = value;
    }

    public Value(String id, T value, Function function) {
        this(id, value);
        this.function = function;
    }

    public T value() {
        return value;
    }

    void print(PrintWriter p) {
        if (function !=  null) {
            for (Object val : function.getParams().values()) {
                ((Value)val).print(p);
                p.print("\n");
            }

            p.print(id + " <- ");
            function.print(p);
            //p.print(";");
        } else {
            p.print(toString() + ";");
        }


    }

    public String toString() {
        return id + " = " + value;
    }

    public void setValue(T value) {
        this.value = value;
        for (ValueListener listener : listeners) {
            listener.valueSet();
        }
    }

    JComponent viewer = null;
    static int BORDER_SIZE = 20;

    public JComponent getViewer() {
        if (value instanceof HasComponentView) {
            JComponent component = ((HasComponentView) value).getComponent();
            component.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createEmptyBorder(BORDER_SIZE, 0, BORDER_SIZE, BORDER_SIZE),
                    id)
            );
            return component;
        }

        if (viewer == null) {
            if (this instanceof DoubleValue) {
                viewer = new DoubleValueEditor((DoubleValue) this);
            } if (this instanceof IntegerValue) {
                viewer = new IntegerValueEditor((IntegerValue) this);
            } else {
                viewer = new JPanel();
                new BoxLayout(viewer, BoxLayout.LINE_AXIS);
                viewer.add(new JLabel(toString()));
            }
            viewer.setBorder(BorderFactory.createEmptyBorder(BORDER_SIZE, BORDER_SIZE, BORDER_SIZE, BORDER_SIZE));
        }
        return viewer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Function getFunction() {
        return function;
    }

    public void addValueListener(ValueListener listener) {
        listeners.add(listener);
    }
}
