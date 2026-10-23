package operators.mutation;

import chromosome.Chromosome;
import java.util.Random;

public interface Mutation {

    Chromosome mutate(Chromosome chromosome, Random random,
                        int generation, int maxGenerations);
}