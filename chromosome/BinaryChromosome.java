package chromosome;

import java.util.Arrays;

public class BinaryChromosome extends Chromosome
{
    private final boolean[] genes;

    public BinaryChromosome(boolean[] genes) {
        this.genes = Arrays.copyOf(genes, genes.length);
    }

    public boolean[] getGenes() {
        return Arrays.copyOf(genes, genes.length);
    }

    @Override 
    public BinaryChromosome copy() {
        return new BinaryChromosome(genes);
    }

    @Override 
    public int length() {
        return genes.length;
    }

    @Override 
    public String get_Representation_Type() {
        return "BINARY";
    }

    @Override 
    public String toString() {
        return Arrays.toString(genes);
    }
}
