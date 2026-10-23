import chromosome.Chromosome;
import chromosome.PermutationChromosome;
import core.GAConfig;
import core.Genetic_algo;
import core.OptimizationDirection;
import operators.crossover.OrderCrossover;
import operators.mutation.SwapMutation;
import operators.selection.TournamentSelection;
import operators.replacement.MixingReplacement;
import problem.cvrp.CVRP;
import problem.cvrp.CVRPData;
import problem.cvrp.CVRPInitializer;
import problem.cvrp.CVRPResultPrinter;
import problem.cvrp.Customer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

    public static void main(String[] args) {

        if (CVRPData.DISTANCE_MATRIX.length != 17) {
            throw new AssertionError("Expected 17 locations.");
        }

        if (CVRPData.DEMANDS.length != 17) {
            throw new AssertionError("Expected 17 demand entries.");
        }

        int totalDemand = 0;

        for (int i = 1; i < CVRPData.DEMANDS.length; i++) {
            totalDemand += CVRPData.DEMANDS[i];
        }

        System.out.println("Total demand: " + totalDemand);
        System.out.println(
            "Total fleet capacity: "
            + CVRPData.VEHICLE_COUNT * CVRPData.VEHICLE_CAPACITY
        );

        if (totalDemand != 60) {
            throw new AssertionError("Unexpected total demand.");
        }

        // 1. Create the depot.
        Customer depot = new Customer(0, 0, 0, 0);

        // 2. Create customers from the reference demand array.
        List<Customer> customers = new ArrayList<>();

        for (int id = 1; id < CVRPData.DEMANDS.length; id++) {
            customers.add(
                new Customer(
                    id,
                    0,
                    0,
                    CVRPData.DEMANDS[id]
                )
            );
        }

        // 3. Create the CVRP problem.
        CVRP problem = new CVRP(
            depot,
            customers,
            CVRPData.VEHICLE_CAPACITY,
            CVRPData.VEHICLE_COUNT,
            CVRPData.DISTANCE_MATRIX
        );

        // 4. Configure the GA.
        GAConfig config = new GAConfig();

        config.setOptimizationDirection(
            OptimizationDirection.MINIMIZE
        );

        // 5. Create the initial population.
        Random random = new Random(42);

        List<Chromosome> initialPopulation =
            CVRPInitializer.createPopulation(
                config.getPopulationSize(),
                problem,
                random
        );

        // 6. Configure the operators.
        Genetic_algo ga = new Genetic_algo(
            config,
            problem,
            problem,
            new TournamentSelection(
                config.getTournamentSize(),
                config.getOptimizationDirection()
            ),
            new OrderCrossover(),
            new SwapMutation(),
            new MixingReplacement(
                config.getOptimizationDirection()
            )
        );

        // 7. Run the GA.
        Chromosome best = ga.run(initialPopulation);

        // 8. Print the result.
        System.out.println("Best fitness: " + ga.getBestFitness());
        System.out.println("Best found at generation: "
            + ga.getBestGeneration());
        System.out.println();

        CVRPResultPrinter.print(
            problem,
            (PermutationChromosome) best,
            CVRPData.DISTANCE_MATRIX
        );
    }
}