package operators.mutation;

import chromosome.BinaryChromosome;
import chromosome.Chromosome;

import java.util.Random;

public class BitFlipMutation implements Mutation {

    @Override
    public Chromosome mutate(
            Chromosome chromosome,
            Random random,
            int generation,
            int maxGenerations
    ) {
        if (!(chromosome instanceof BinaryChromosome binary)) {
            throw new IllegalArgumentException("Bit flip mutation requires a binary chromosome.");
        }

        boolean[] genes = binary.getGenes();

        if (genes.length == 0) {
            return binary.copy();
        }

        int index = random.nextInt(genes.length);
        genes[index] = !genes[index];

        return new BinaryChromosome(genes);
    }
}