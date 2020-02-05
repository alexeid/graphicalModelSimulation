package james.swing;

import james.graphicalModel.GraphicalModelParser;
import james.graphicalModel.RandomVariable;
import james.graphicalModel.Value;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class StatePanel extends JPanel {

    GraphicalModelParser parser;

    List<JLabel> labels = new ArrayList<>();
    List<JComponent> editors = new ArrayList<>();
    GroupLayout layout = new GroupLayout(this);

    boolean includeRandomVariables = true;
    boolean includeFixedValues = true;
    boolean includeFunctionValues = false;

    public StatePanel(GraphicalModelParser parser, boolean includeFixedValues, boolean includeRandomVariables, boolean includeFunctionValues) {
        this.parser = parser;

        this.includeFixedValues = includeFixedValues;
        this.includeFunctionValues = includeFunctionValues;
        this.includeRandomVariables = includeRandomVariables;

        setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        generateComponents();

        parser.addGraphicalModelChangeListener(() -> generateComponents());
    }

    void generateComponents() {

        labels.clear();
        editors.clear();
        removeAll();

        for (Value value : parser.getDictionary().values()) {
            if (((value instanceof RandomVariable) && includeRandomVariables) || (value.getFunction() != null && includeFunctionValues) || (isFixedValue(value) && includeFixedValues)) {
                JLabel label = new JLabel(value.getId()+":");
                label.setForeground(Color.gray);
                labels.add(label);
                editors.add(value.getViewer());
            }
        }
        GroupLayout.ParallelGroup horizParallelGroup = layout.createParallelGroup(GroupLayout.Alignment.TRAILING);
        GroupLayout.ParallelGroup horizParallelGroup2 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        GroupLayout.SequentialGroup vertSequentialGroup = layout.createSequentialGroup();
        for (int i = 0; i < labels.size(); i++) {
            horizParallelGroup.addComponent(labels.get(i));
            horizParallelGroup2.addComponent(editors.get(i));
            GroupLayout.ParallelGroup vertParallelGroup = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);
            vertParallelGroup.addComponent(labels.get(i));
            vertParallelGroup.addComponent(editors.get(i));
            vertSequentialGroup.addGroup(vertParallelGroup);
        }

        GroupLayout.SequentialGroup horizSequentialGroup = layout.createSequentialGroup();
        horizSequentialGroup.addGroup(horizParallelGroup).addGroup(horizParallelGroup2);

        layout.setHorizontalGroup(horizSequentialGroup);

        layout.setVerticalGroup(vertSequentialGroup);
    }

    private boolean isFixedValue(Value value) {
        return !(value instanceof RandomVariable) && value.getFunction() == null;
    }
}
