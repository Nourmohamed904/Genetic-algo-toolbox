package chromosome;

import java.util.Arrays;

public class IntegerChromosome extends Chromosome
{
    private final int[] genes;

    public IntegerChromosome(int[] genes) {
        this.genes = Arrays.copyOf(genes, genes.length);
    }

    public int[] getGenes() {
        return Arrays.copyOf(genes, genes.length);
    }

    @Override
    public IntegerChromosome copy() {
        return new IntegerChromosome(genes);
    }

    @Override
    public int length() {
        return genes.length;
    }

    @Override
    public String get_Representation_Type() {
        return "INTEGER";
    }

    @Override
    public String toString() {
        return Arrays.toString(genes);
    }
    
}
