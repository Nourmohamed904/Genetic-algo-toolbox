package operators.selection;

import chromosome.Chromosome;
import core.OptimizationDirection;

import java.util.List;
import java.util.Random;

public class TournamentSelection implements Selection {

    private final int tournamentSize;
    private final OptimizationDirection optimizationDirection;

    public TournamentSelection(int tournamentSize) {
        this(tournamentSize, OptimizationDirection.MINIMIZE);
    }

    public TournamentSelection(
            int tournamentSize,
            OptimizationDirection optimizationDirection) {

        if (tournamentSize < 1) {
            throw new IllegalArgumentException(
                    "Tournament size must be positive."
            );
        }

        if (optimizationDirection == null) {
            throw new IllegalArgumentException(
                    "Optimization direction cannot be null."
            );
        }

        this.tournamentSize = tournamentSize;
        this.optimizationDirection = optimizationDirection;
    }

    @Override
    public Chromosome select(
            List<Chromosome> population,
            List<Double> fitnessValues,
            Random random) {

        if (population == null || population.isEmpty()) {
            throw new IllegalArgumentException(
                    "Population cannot be empty."
            );
        }

        if (fitnessValues == null
                || population.size() != fitnessValues.size()) {
            throw new IllegalArgumentException(
                    "Population and fitness sizes must match."
            );
        }

        if (random == null) {
            throw new IllegalArgumentException(
                    "Random generator cannot be null."
            );
        }

        int bestIndex = -1;

        for (int i = 0; i < tournamentSize; i++) {
            int candidateIndex = random.nextInt(population.size());

            if (bestIndex == -1
                    || isBetter(
                            fitnessValues.get(candidateIndex),
                            fitnessValues.get(bestIndex))) {
                bestIndex = candidateIndex;
            }
        }

        return population.get(bestIndex);
    }

    private boolean isBetter(double candidate, double currentBest) {
        if (optimizationDirection == OptimizationDirection.MINIMIZE) {
            return candidate < currentBest;
        }

        return candidate > currentBest;
    }
}