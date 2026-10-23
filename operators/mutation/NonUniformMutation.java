package operators.mutation;

import chromosome.Chromosome;
import chromosome.RealChromosome;

import java.util.Random;

public class NonUniformMutation implements Mutation {

    private final double lowerBound;
    private final double upperBound;
    private final double beta;

    public NonUniformMutation(
            double lowerBound,
            double upperBound,
            double beta
    ) {
        if (!Double.isFinite(lowerBound)
                || !Double.isFinite(upperBound)
                || lowerBound >= upperBound) {
            throw new IllegalArgumentException(
                    "Invalid mutation bounds."
            );
        }

        if (!Double.isFinite(beta) || beta <= 0) {
            throw new IllegalArgumentException(
                    "Beta must be positive."
            );
        }

        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.beta = beta;
    }

    @Override
    public Chromosome mutate(
            Chromosome chromosome,
            Random random,
            int generation,
            int maxGenerations
    ) {
        if (!(chromosome instanceof RealChromosome real)) {
            throw new IllegalArgumentException(
                    "Non-uniform mutation requires a real chromosome."
            );
        }

        if (maxGenerations <= 0
                || generation < 0
                || generation > maxGenerations) {
            throw new IllegalArgumentException(
                    "Invalid generation values."
            );
        }

        double[] genes = real.getGenes();

        if (genes.length == 0) {
            return real.copy();
        }

        int index = random.nextInt(genes.length);
        double current = genes[index];

        if (!Double.isFinite(current)
                || current < lowerBound
                || current > upperBound) {
            throw new IllegalArgumentException(
                    "Gene value is outside the permitted bounds."
            );
        }

        double r = random.nextDouble();

        double exponent = Math.pow(
                1.0 - (double) generation / maxGenerations,
                beta
        );

        boolean moveUp = random.nextBoolean();

        double distance = moveUp
                ? upperBound - current
                : current - lowerBound;

        double delta = distance * (1.0 - Math.pow(r, exponent));

        genes[index] = moveUp
                ? current + delta
                : current - delta;

        return new RealChromosome(genes);
    }
}