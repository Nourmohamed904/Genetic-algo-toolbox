package operators.replacement;

import chromosome.Chromosome;
import core.OptimizationDirection;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MixingReplacement implements Replacement {

    private static class Candidate {
        private final Chromosome chromosome;
        private final double fitness;

        private Candidate(Chromosome chromosome, double fitness) {
            this.chromosome = chromosome;
            this.fitness = fitness;
        }
    }

    private final OptimizationDirection optimizationDirection;

    public MixingReplacement() {
        this(OptimizationDirection.MINIMIZE);
    }

    public MixingReplacement(
            OptimizationDirection optimizationDirection) {

        if (optimizationDirection == null) {
            throw new IllegalArgumentException(
                    "Optimization direction cannot be null."
            );
        }

        this.optimizationDirection = optimizationDirection;
    }

    @Override
    public List<Chromosome> replace(
            List<Chromosome> currentPopulation,
            List<Double> currentFitness,
            List<Chromosome> offspring,
            List<Double> offspringFitness,
            int targetPopulationSize) {

        if (currentPopulation == null
                || currentFitness == null
                || offspring == null
                || offspringFitness == null) {
            throw new IllegalArgumentException(
                    "Populations and fitness lists cannot be null."
            );
        }

        if (currentPopulation.size() != currentFitness.size()
                || offspring.size() != offspringFitness.size()) {
            throw new IllegalArgumentException(
                    "Each chromosome must have a corresponding fitness value."
            );
        }

        List<Candidate> candidates = new ArrayList<>();

        for (int i = 0; i < currentPopulation.size(); i++) {
            candidates.add(new Candidate(
                    currentPopulation.get(i),
                    currentFitness.get(i)
            ));
        }

        for (int i = 0; i < offspring.size(); i++) {
            candidates.add(new Candidate(
                    offspring.get(i),
                    offspringFitness.get(i)
            ));
        }

        if (targetPopulationSize < 1
                || targetPopulationSize > candidates.size()) {
            throw new IllegalArgumentException(
                    "Invalid target population size."
            );
        }

        Comparator<Candidate> comparator =
                Comparator.comparingDouble(candidate -> candidate.fitness);

        if (optimizationDirection == OptimizationDirection.MAXIMIZE) {
            comparator = comparator.reversed();
        }

        candidates.sort(comparator);

        List<Chromosome> nextGeneration = new ArrayList<>();

        for (int i = 0; i < targetPopulationSize; i++) {
            nextGeneration.add(
                    candidates.get(i).chromosome.copy()
            );
        }

        return nextGeneration;
    }
}