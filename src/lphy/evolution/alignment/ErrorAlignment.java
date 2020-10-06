package lphy.evolution.alignment;

import java.util.Map;

/**
 * Created by adru001 on 2/02/20.
 */
public class ErrorAlignment extends SimpleAlignment {

    SimpleAlignment parent;

    public ErrorAlignment(int length, SimpleAlignment parent) {
        super(length, parent); // copy source

        this.parent = parent;
    }

    public boolean isError(int i, int j) {
        return alignment[i][j] != parent.alignment[i][j];
    }
}
