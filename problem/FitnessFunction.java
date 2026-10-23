package problem;

import chromosome.Chromosome;

public interface FitnessFunction {

    double evaluate(Chromosome chromosome);
}