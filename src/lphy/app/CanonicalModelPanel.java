package lphy.app;

import lphy.graphicalModel.code.CanonicalCodeBuilder;
import lphy.graphicalModel.code.CodeBuilder;
import lphy.parser.codecolorizer.ColorizerStyles;
import lphy.parser.codecolorizer.DataModelCodeColorizer;
import lphy.parser.codecolorizer.DataModelToLaTeX;
import lphy.utils.LoggerUtils;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.StyledDocument;
import javax.swing.text.rtf.RTFEditorKit;
import java.awt.*;

public class CanonicalModelPanel extends JComponent {
    GraphicalLPhyParser parser;
    JTextPane pane = new JTextPane();
    JScrollPane scrollPane;

    public CanonicalModelPanel(GraphicalLPhyParser parser) {
        this.parser = parser;

        pane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        pane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        pane.setEditable(false);
        pane.setEditorKit(new RTFEditorKit());

        JPanel noWrapPanel = new JPanel(new BorderLayout());
        noWrapPanel.add(pane);
        scrollPane = new JScrollPane(noWrapPanel);

        setText();

        BoxLayout boxLayout = new BoxLayout(this, BoxLayout.PAGE_AXIS);
        setLayout(boxLayout);
        add(scrollPane);

        parser.addGraphicalModelChangeListener(this::setText);
    }

    private void setText() {

        try {
            pane.getDocument().remove(0, pane.getDocument().getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        CodeBuilder codeBuilder = new CanonicalCodeBuilder();
        String text = codeBuilder.getCode(parser);

        System.out.println(text);

        if (text.length() > 0) {
            try {
                pane.setEditorKit(new RTFEditorKit());
                DataModelCodeColorizer codeColorizer = new DataModelCodeColorizer(parser, pane);
                codeColorizer.parse(text);

            }  catch (Exception e) {
                pane.setText(text);
                LoggerUtils.log.severe("CanonicalModelPanel failed with exception: " + e.getMessage());
            }
        }
    }
}
