package core;

public class GAConfig {

    private int populationSize = 70;
    private int maxGenerations = 150;

    private double crossoverRate = 0.7;
    private double mutationRate = 0.05;

    private int tournamentSize = 3;

    // Preserve the existing behavior by default.
    private OptimizationDirection optimizationDirection =
            OptimizationDirection.MINIMIZE;

    public int getPopulationSize() {
        return populationSize;
    }

    public void setPopulationSize(int populationSize) {
        if (populationSize < 2) {
            throw new IllegalArgumentException(
                    "Population size must be at least 2."
            );
        }

        this.populationSize = populationSize;
    }

    public int getMaxGenerations() {
        return maxGenerations;
    }

    public void setMaxGenerations(int maxGenerations) {
        if (maxGenerations < 1) {
            throw new IllegalArgumentException(
                    "Maximum generations must be positive."
            );
        }

        this.maxGenerations = maxGenerations;
    }

    public double getCrossoverRate() {
        return crossoverRate;
    }

    public void setCrossoverRate(double crossoverRate) {
        validateProbability(crossoverRate);
        this.crossoverRate = crossoverRate;
    }

    public double getMutationRate() {
        return mutationRate;
    }

    public void setMutationRate(double mutationRate) {
        validateProbability(mutationRate);
        this.mutationRate = mutationRate;
    }

    public int getTournamentSize() {
        return tournamentSize;
    }

    public void setTournamentSize(int tournamentSize) {
        if (tournamentSize < 1) {
            throw new IllegalArgumentException(
                    "Tournament size must be positive."
            );
        }

        this.tournamentSize = tournamentSize;
    }

    public OptimizationDirection getOptimizationDirection() {
        return optimizationDirection;
    }

    public void setOptimizationDirection(
            OptimizationDirection optimizationDirection) {

        if (optimizationDirection == null) {
            throw new IllegalArgumentException(
                    "Optimization direction cannot be null."
            );
        }

        this.optimizationDirection = optimizationDirection;
    }

    private void validateProbability(double value) {
        if (!Double.isFinite(value) || value < 0.0 || value > 1.0) {
            throw new IllegalArgumentException(
                    "Probability must be finite and between 0 and 1."
            );
        }
    }
}