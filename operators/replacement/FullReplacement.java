package operators.replacement;

import chromosome.Chromosome;

import java.util.ArrayList;
import java.util.List;

public class FullReplacement implements Replacement
{
    @Override 
    public List<Chromosome> replace( 
                    List<Chromosome> currentPopulation,
                    List<Double> currentFitness,
                    List<Chromosome> offspring,
                    List<Double> offspringFitness,
                    int targetPopulationSize
    ) {
        if (offspring.size() < targetPopulationSize) {
            throw new IllegalArgumentException(
                    "Not enough offspring to fill the next population."
            );
        }

        List<Chromosome> nextGeneration = new ArrayList<>();

        for (int i = 0; i < targetPopulationSize; i++) {
            nextGeneration.add(offspring.get(i).copy());
        }

        return nextGeneration;
    }
}
