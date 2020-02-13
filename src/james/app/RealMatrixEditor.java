package james.app;

import org.apache.commons.math3.linear.RealMatrix;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

public class RealMatrixEditor extends JPanel {

    RealMatrix matrix;

    JTextField[][] textFields;
    JLabel[][] labels;

    int maxFracDigits = 5;

    int GAP = 9;
    int corner = 5;

    public RealMatrixEditor(RealMatrix matrix, boolean editable) {
        this.matrix = matrix;

        DecimalFormat format = new DecimalFormat();
        format.setMaximumFractionDigits(maxFracDigits);
        setOpaque(false);

        int rowCount = matrix.getRowDimension();
        int colCount = matrix.getColumnDimension();

        if (editable) {
            textFields = new JTextField[rowCount][colCount];
        } else {
            labels = new JLabel[rowCount][colCount];
        }

        GridLayout gridLayout = new GridLayout(rowCount, colCount);
        gridLayout.setHgap(GAP);
        gridLayout.setVgap(GAP);
        setBorder(BorderFactory.createEmptyBorder(GAP,GAP-1,GAP,GAP));
        setLayout(gridLayout);

        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < colCount; j++) {
                if (editable) {
                    textFields[i][j] = new JTextField(Double.toString(matrix.getEntry(i, j)), 8);
                    add(textFields[i][j]);

                } else {
                    labels[i][j] = new JLabel(format.format(matrix.getEntry(i, j)));
                    labels[i][j].setHorizontalAlignment(SwingConstants.RIGHT);
                    add(labels[i][j]);
                }
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.black);

        Graphics2D g2d = (Graphics2D)g;
        g2d.setStroke(new BasicStroke(1.5f));

        // draw left bracket
        g.drawLine(1,1,1,getHeight());
        g.drawLine(1,1,corner,1);
        g.drawLine(1,getHeight()-1,corner,getHeight()-1);

        // draw right bracket
        g.drawLine(getWidth()-1,1,getWidth()-corner-1,1);
        g.drawLine(getWidth()-1,getHeight()-1,getWidth()-corner-1,getHeight()-1);
        g.drawLine(getWidth()-1,1,getWidth()-1,getHeight());
    }
}