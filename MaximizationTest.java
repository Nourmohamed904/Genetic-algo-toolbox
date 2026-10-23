import chromosome.BinaryChromosome;
import chromosome.Chromosome;
import core.GAConfig;
import core.Genetic_algo;
import core.OptimizationDirection;
import operators.crossover.MultipointCrossover;
import operators.mutation.BitFlipMutation;
import operators.replacement.MixingReplacement;
import operators.selection.RouletteWheelSelection;
import problem.FitnessFunction;
import problem.FeasibilityFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MaximizationTest {

    public static void main(String[] args) {

        // 1. Configure the GA for maximization.
        GAConfig config = new GAConfig();
        config.setPopulationSize(50);
        config.setMaxGenerations(100);
        config.setCrossoverRate(0.8);
        config.setMutationRate(0.05);
        config.setOptimizationDirection(
                OptimizationDirection.MAXIMIZE
        );

        // 2. Generate the initial population.
        Random random = new Random();
        List<Chromosome> population = new ArrayList<>();

        int chromosomeLength = 8;

        for (int i = 0; i < config.getPopulationSize(); i++) {
            boolean[] genes = new boolean[chromosomeLength];

            for (int j = 0; j < chromosomeLength; j++) {
                genes[j] = random.nextBoolean();
            }

            population.add(new BinaryChromosome(genes));
        }

        // 3. Fitness: maximize the number of true bits.
        FitnessFunction fitnessFunction = chromosome -> {
            BinaryChromosome binary = (BinaryChromosome) chromosome;

            int count = 0;

            for (boolean gene : binary.getGenes()) {
                if (gene) {
                    count++;
                }
            }

            return count;
        };

        // 4. Every binary chromosome is feasible.
        FeasibilityFunction feasibilityFunction =
                chromosome -> true;

        // 5. Configure the operators.
        RouletteWheelSelection selection =
                new RouletteWheelSelection(
                        config.getOptimizationDirection()
                );

        MultipointCrossover crossover =
                new MultipointCrossover(2);

        BitFlipMutation mutation = new BitFlipMutation();

        MixingReplacement replacement =
                new MixingReplacement(
                        config.getOptimizationDirection()
                );

        // 6. Create and run the GA.
        Genetic_algo ga = new Genetic_algo(
                config,
                fitnessFunction,
                feasibilityFunction,
                selection,
                crossover,
                mutation,
                replacement
        );

        ga.run(population);

        // 7. Display the result.
        BinaryChromosome best =
                (BinaryChromosome) ga.getBestChromosome();

        double bestFitness = ga.getBestFitness();

        System.out.println("===== MAXIMIZATION TEST =====");
        System.out.println("Direction: "
                + config.getOptimizationDirection());
        System.out.println("Best chromosome: "
                + formatChromosome(best.getGenes()));
        System.out.println("Best fitness: " + bestFitness);
        System.out.println("Optimal fitness: " + chromosomeLength);

        if (bestFitness == chromosomeLength) {
            System.out.println("RESULT: SUCCESS - Optimal solution found!");
        } else {
            System.out.println(
                    "RESULT: The optimum was not found in this run."
            );
        }
    }

    private static String formatChromosome(boolean[] genes) {
        StringBuilder result = new StringBuilder();

        for (boolean gene : genes) {
            result.append(gene ? '1' : '0');
        }

        return result.toString();
    }
}