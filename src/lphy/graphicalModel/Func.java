package lphy.graphicalModel;

import javax.swing.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public abstract class Func implements Generator, Viewable {

    public String getName() {
        FunctionInfo fInfo = getFunctionInfo();
        if (fInfo != null) return fInfo.name();
        return getClass().getSimpleName();
    }

    public String getDescription() {
        FunctionInfo fInfo = getFunctionInfo();
        if (fInfo != null) return fInfo.description();
        return "";
    }

    protected TreeMap<String, Value> paramMap = new TreeMap<>();

    public Map<String, Value> getParams() {
        return paramMap;
    }

    public void setParam(String paramName, Value value) {
        paramMap.put(paramName, value);
    }

    public FunctionInfo getFunctionInfo() {

       return getFunctionInfo(getClass());
    }

    public static FunctionInfo getFunctionInfo(Class c) {

        Method[] methods = c.getMethods();

        for (Method method : methods) {
            for (Annotation annotation : method.getAnnotations()) {
                if (annotation instanceof FunctionInfo) {
                    return (FunctionInfo) annotation;
                }
            }
        }

        return null;
    }

    public static String getFunctionName(Class c) {
        return getFunctionInfo(c).name();
    }

    public String getRichDescription() {

        String html = "<html><h3>" + getName() + " function</h3> <ul>";
        html += "<li>" + getDescription();
        html += "</ul></html>";
        return html;
    }

    public JComponent getViewer() {
        return new JLabel(getRichDescription());
    }

    public String codeString() {
        Map<String, Value> map = getParams();

        Iterator<Map.Entry<String, Value>> iterator = map.entrySet().iterator();
        StringBuilder builder = new StringBuilder();
        builder.append(getName());
        builder.append("(");

        if (iterator.hasNext()) {
            Map.Entry<String, Value> entry = iterator.next();

            if (getParams().size() == 1) {
                builder.append(Generator.getArgumentValue(entry));
            } else {

                builder.append(Generator.getArgumentCodeString(entry));
                while (iterator.hasNext()) {
                    entry = iterator.next();
                    builder.append(", ");
                    builder.append(Generator.getArgumentCodeString(entry));
                }
            }
        }
        builder.append(")");
        return builder.toString();
    }
}