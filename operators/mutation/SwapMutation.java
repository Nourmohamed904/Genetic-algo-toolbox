package operators.mutation;

import chromosome.Chromosome;
import chromosome.PermutationChromosome;

import java.util.Random;

public class SwapMutation implements Mutation
{
    @Override
    public Chromosome mutate(
            Chromosome chromosome,Random random,
            int generation,int maxGenerations
    ) {
        if (!(chromosome instanceof PermutationChromosome permutation)) {
            throw new IllegalArgumentException(
                    "Swap mutation requires a permutation chromosome."
            );
        }

        int[] genes = permutation.getGenes();

        if (genes.length < 2) {
            return permutation.copy();
        }

        int index1 = random.nextInt(genes.length);
        int index2 = random.nextInt(genes.length - 1);

        if (index2 >= index1) {index2++;}

        int temp = genes[index1];
        genes[index1] = genes[index2];
        genes[index2] = temp;

        return new PermutationChromosome(genes);
    }
}
