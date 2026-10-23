package chromosome;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class PermutationChromosome extends Chromosome 
{
    private final int[] genes;
    
    public PermutationChromosome(int[] genes) {
        validate_Permuation(genes);
        this.genes = Arrays.copyOf(genes, genes.length);
    }

    private void validate_Permuation(int[] genes) {
        Set<Integer> uniqueGenes = new HashSet<>();

        for (int gene : genes) {
            if (!uniqueGenes.add(gene)) {
                throw new IllegalArgumentException( "Permutation contains duplicate gene: " + gene);
            }
        }
    }

    public int[] getGenes() {
        return Arrays.copyOf(genes, genes.length);
    }
    
    @Override 
    public PermutationChromosome copy() {
        return new PermutationChromosome(genes);
    }

    @Override
    public int length() {
        return genes.length;
    }

    @Override
    public String get_Representation_Type() {
        return "PERMUTATION";
    }

    @Override
    public String toString() {
        return Arrays.toString(genes);
    }
}
