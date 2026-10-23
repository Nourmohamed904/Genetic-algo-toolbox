package operators.crossover;

import chromosome.Chromosome;
import java.util.List;
import java.util.Random;

public interface Crossover {

    List<Chromosome> cross(Chromosome parent1,
                           Chromosome parent2,
                           Random random);
}