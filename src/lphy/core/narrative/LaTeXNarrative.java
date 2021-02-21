package lphy.core.narrative;

import lphy.app.Symbols;
import lphy.app.graphicalmodelcomponent.GraphicalModelComponent;
import lphy.core.LPhyParser;
import lphy.graphicalModel.Citation;
import lphy.graphicalModel.Value;
import lphy.graphicalModel.code.CanonicalCodeBuilder;
import lphy.graphicalModel.code.CodeBuilder;
import lphy.parser.codecolorizer.DataModelToLaTeX;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

import static lphy.graphicalModel.NarrativeUtils.sanitizeDOI;

public class LaTeXNarrative implements Narrative {

    List<Citation> references = new ArrayList<>();
    List<String> keys = new ArrayList<>();
    boolean mathMode = false;
    boolean mathModeInline = false;

    boolean boxStyle = false;
    boolean twoColumn = false;
    boolean smallCodeText = true;
    static int sectionsPerMiniPage = 3;

    int sectionCount = 0;

    public String beginDocument(String title) {
        keys.clear();
        references.clear();
        sectionCount = 0;
        StringBuilder builder = new StringBuilder();
        builder.append("\\documentclass{article}\n\n");
        builder.append("\\usepackage{color}\n");
        builder.append("\\usepackage{xcolor}\n");
        builder.append("\\usepackage{alltt}\n");
        builder.append("\\usepackage{tikz}\n");
        builder.append("\\usepackage{bm}\n\n");
        if (boxStyle) {
            builder.append("\\usepackage[breakable]{tcolorbox} % for text box\n");
            builder.append("\\usepackage{graphicx} % for minipage\n");
            builder.append("\\usepackage[margin=2cm]{geometry} % margins\n");
        }

        builder.append("\\usetikzlibrary{bayesnet}\n\n");

        builder.append("\\begin{document}\n");

        if (boxStyle) {
            builder.append("\\begin{tcolorbox}[breakable, width=\\textwidth, colback=gray!10, boxrule=0pt,\n" +
                    "  title=" + title + ", fonttitle=\\bfseries]\n\n");
        }

        if (twoColumn) {
            builder.append("\\begin{minipage}[t]{0.50\\textwidth}\n");
        }


        return  builder.toString();
    }

    public String endDocument() {
        keys.clear();
        references.clear();

        StringBuilder builder = new StringBuilder();

        if (twoColumn) {
            builder.append("\\end{minipage}\n");
        }

        if (boxStyle){
            builder.append("\\end{tcolorbox}\n\n");
        }

        builder.append("\\end{document}\n");

        return builder.toString();
    }

    /**
     * @param header the heading of the section
     * @return a string representing the start of a new section
     */
    public String section(String header) {

        if (twoColumn) {
            StringBuilder builder = new StringBuilder();
            if (sectionCount >= sectionsPerMiniPage) {
                sectionCount = 0;
                builder.append("\\end{minipage}\n");
                builder.append("\\begin{minipage}[t]{0.50\\textwidth}\n");
            }
            builder.append("\\section*{" + header + "}\n\n");
            sectionCount += 1;
            return builder.toString();
        } else {
            return "\\section*{" + header + "}\n\n";
        }
    }


    static String specials = "&%$#_{}";

    /**
     * @param text raw text with not intended latex code
     * @return sanitized text
     */
    public String text(String text) {
        //if (text.startsWith("\"") && text.endsWith("\"")) {

        StringBuilder builder = new StringBuilder();

        for (char ch : text.toCharArray()) {
            if (specials.indexOf(ch)>=0) {
                builder.append('\\');
                builder.append(ch);
            } else if (ch == '\\') {
                builder.append("\\textbackslash{}");
            } else if (ch == '~') {
                builder.append("\\textasciitilde{}");
            } else if (ch == '^') {
                builder.append("\\textasciicircum{}");
            } else builder.append(ch);

        }
        return builder.toString();
    }

    /**
     * @param text lphy code fragment
     * @return sanitized for this particular narrative type
     */
    public String code(String text) {
        if (text.startsWith("\"") && text.endsWith("\"")) {

            // sanitize backslash
            text = text.replace("\\", "\\textbackslash{}");
        }
        return text;
    }

    public String cite(Citation citation) {

        if (citation != null) {
            if (!references.contains(citation)) {
                references.add(citation);
                keys.add(generateKey(citation, keys));
            }

            return "\\cite{" + getKey(citation) + "}";
        }

        return null;
    }

    public String getId(Value value, boolean inlineMath) {

        String id = value.getId();
        String canonicalId = value.getCanonicalId();

        if (value.isAnonymous()) return null;

        boolean useCanonical = !id.equals(canonicalId);

        boolean containsUnderscore = id.indexOf('_') >= 0;

        if (useCanonical) {
            if (inlineMath) {
                return "$\\" + canonicalId + "$";
            } else if (mathMode) {
                return "\\" + canonicalId;
            } else return canonicalId;
        } else if (containsUnderscore) {
            if (mathMode) {
                return id;
            } else return "$" + id + "$";
        } else {
            if (inlineMath) {
                return "{\\it " + id + "}";
            } else if (mathMode) {
                return "\\textrm{" + id + "}";
            } else return id;
        }
    }

    public String symbol(String symbol) {
        String canonical = Symbols.getCanonical(symbol);
        if (!canonical.equals(symbol)) return "\\" + canonical;
        return symbol;
    }

    @Override
    public String startMathMode(boolean inline) {
        mathMode = true;
        mathModeInline = inline;
        if (inline) return "$";
        return "$$";
    }

    @Override
    public String endMathMode() {
        mathMode = false;
        if (mathModeInline) return "$";
        return "$$";
    }

    public String codeBlock(LPhyParser parser, int fontSize) {

        JTextPane dummyPane = new JTextPane();

        DataModelToLaTeX dataModelToLaTeX = new DataModelToLaTeX(parser, dummyPane);
        CodeBuilder codeBuilder = new CanonicalCodeBuilder();
        String text = codeBuilder.getCode(parser);
        dataModelToLaTeX.parse(text);

        StringBuilder builder = new StringBuilder();
        if (fontSize != 12) {
            builder.append(LaTeXUtils.getFontSize(fontSize));
            builder.append("\n");
        }
        builder.append(dataModelToLaTeX.getLatex());

        return builder.toString();
    }

    @Override
    public String graphicalModelBlock(GraphicalModelComponent component) {

        StringBuilder builder = new StringBuilder();
        builder.append("\\begin{center}\n");
        builder.append(component.toTikz(0.6, 0.6, true));
        builder.append("\\end{center}\n");

        return builder.toString();
    }


    private String getKey(Citation citation) {
        return keys.get(references.indexOf(citation));
    }

    private String generateKey(Citation citation, List<String> existingKeys) {

        String title = citation.title();
        String firstWord = title.split(" ")[0];

        String key = citation.authors()[0] + citation.year() + firstWord;

        if (existingKeys.contains(key)) {
            int index = 1;
            while (existingKeys.contains(key)) {
                key = citation.authors()[0] + citation.year() + firstWord + index;
                index += 1;
            }
        }
        return key;
    }

    @Override
    public void clearReferences() {
        references.clear();
    }

    public String referenceSection() {
        StringBuilder builder = new StringBuilder();
        if (references.size() > 0) {
            // \begin{thebibliography}{9}
            //
            //\bibitem{lamport94}
            //  Leslie Lamport,
            //  \textit{\LaTeX: a document preparation system},
            //  Addison Wesley, Massachusetts,
            //  2nd edition,
            //  1994.
            //
            //\end{thebibliography}

            builder.append("\\begin{thebibliography}{9}\n\n");
            for (int i = 0; i < references.size(); i++) {
                builder.append("\\bibitem{");
                builder.append(keys.get(i));
                builder.append("}\n");

                String ref = LaTeXUtils.sanitizeText(references.get(i).value());

                builder.append(ref);
                builder.append("\n\n");
            }
            builder.append("\\end{thebibliography}\n");
        }
        return builder.toString();
    }
}