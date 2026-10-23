package problem;

import chromosome.Chromosome;

public interface FeasibilityFunction {

    boolean isFeasible(Chromosome chromosome);
}