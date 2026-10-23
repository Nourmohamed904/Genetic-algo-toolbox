package operators.selection;

import chromosome.Chromosome;
import core.OptimizationDirection;

import java.util.List;
import java.util.Random;

public class RouletteWheelSelection implements Selection {

    private final OptimizationDirection direction;

    public RouletteWheelSelection() {
        this(OptimizationDirection.MINIMIZE);
    }

    public RouletteWheelSelection(OptimizationDirection direction) {
        if (direction == null) {
            throw new IllegalArgumentException(
                    "Optimization direction cannot be null."
            );
        }

        this.direction = direction;
    }

    @Override
    public Chromosome select(
            List<Chromosome> population,
            List<Double> fitnessValues,
            Random random
    ) {
        if (population == null || population.isEmpty()) {
            throw new IllegalArgumentException(
                    "Population cannot be empty."
            );
        }

        if (fitnessValues == null ||
                population.size() != fitnessValues.size()) {
            throw new IllegalArgumentException(
                    "Population and fitness sizes must match."
            );
        }

        if (random == null) {
            throw new IllegalArgumentException(
                    "Random cannot be null."
            );
        }

        double bestFitness = direction == OptimizationDirection.MINIMIZE
                ? Double.POSITIVE_INFINITY
                : Double.NEGATIVE_INFINITY;

        for (Double fitness : fitnessValues) {
            if (fitness == null || !Double.isFinite(fitness)) {
                throw new IllegalArgumentException(
                        "Fitness values must be finite."
                );
            }

            if (direction == OptimizationDirection.MINIMIZE) {
                bestFitness = Math.min(bestFitness, fitness);
            } else {
                bestFitness = Math.max(bestFitness, fitness);
            }
        }

        double[] weights = new double[population.size()];
        double totalWeight = 0.0;

        for (int i = 0; i < population.size(); i++) {
            double fitness = fitnessValues.get(i);

            if (direction == OptimizationDirection.MINIMIZE) {
                weights[i] = 1.0 / (1.0 + fitness - bestFitness);
            } else {
                weights[i] = 1.0 / (1.0 + bestFitness - fitness);
            }

            totalWeight += weights[i];
        }

        double threshold = random.nextDouble() * totalWeight;
        double cumulativeWeight = 0.0;

        for (int i = 0; i < population.size(); i++) {
            cumulativeWeight += weights[i];

            if (threshold < cumulativeWeight) {
                return population.get(i);
            }
        }

        // Protect against floating-point rounding.
        return population.get(population.size() - 1);
    }
}