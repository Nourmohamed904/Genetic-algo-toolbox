package chromosome;

import java.util.Arrays;

public class RealChromosome extends Chromosome {

    private final double[] genes;

    public RealChromosome(double[] genes) {
        this.genes = Arrays.copyOf(genes, genes.length);
    }

    public double[] getGenes() {
        return Arrays.copyOf(genes, genes.length);
    }

    @Override
    public RealChromosome copy() {
        return new RealChromosome(genes);
    }

    @Override
    public int length() {
        return genes.length;
    }

    @Override
    public String get_Representation_Type() {
        return "REAL";
    }

    @Override
    public String toString() {
        return Arrays.toString(genes);
    }
}