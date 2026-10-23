package operators.replacement;

import chromosome.Chromosome;
import java.util.List;

public interface Replacement {

    List<Chromosome> replace(
            List<Chromosome> currentPopulation,
            List<Double> currentFitness,
            List<Chromosome> offspring,
            List<Double> offspringFitness,
            int targetPopulationSize
    );
}