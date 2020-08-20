package lphy.evolution;

public class DataFrame implements NTaxa, NChar {

    int ntaxa;
    Integer[] partNChar = null;
    String[] partNames = null;
    DataFrame[] parts = null;
    int nchar;

    public DataFrame(int ntaxa, int nchar) {
        this.ntaxa = ntaxa;
        this.nchar = nchar;
    }

    public DataFrame(int ntaxa, String[] partNames, Integer[] partNChar) {
        this.ntaxa = ntaxa;
        this.partNChar = partNChar;
        this.partNames = partNames;
        this.parts = new DataFrame[partNames.length];
        for (int i = 0; i < partNChar.length; i++) {
            nchar += partNChar[i];
            parts[i] = new DataFrame(ntaxa, partNChar[i]);
        }
    }

    @Override
    public int ntaxa() {
        return ntaxa;
    }

    @Override
    public int nchar() {
        return nchar;
    }

    public boolean hasParts() {
        return partNChar != null;
    }

    public String[] partNames() {
        return partNames;
    }

    public DataFrame part(String name) {
        if (partNames == null) return null;
        for (int i = 0; i < partNames.length; i++) {
            if (partNames[i].equals(name)) {
                return parts[i];
            }
        }
        throw new IllegalArgumentException("Part named " + name + " not found!");
    }

    public String toString() {
        return ntaxa + " by " + nchar;
    }
}
