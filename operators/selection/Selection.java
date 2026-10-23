package operators.selection;

import chromosome.Chromosome;
import java.util.List;
import java.util.Random;

public interface Selection 
{
    Chromosome select( List<Chromosome> population,
                       List<Double> fitnessValues,
                       Random randome);
}