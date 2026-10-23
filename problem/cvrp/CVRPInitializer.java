package problem.cvrp;

import chromosome.Chromosome;
import chromosome.PermutationChromosome;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class CVRPInitializer {

    public static List<Chromosome> createPopulation(
            int populationSize,
            CVRP problem,
            Random random) {

        if (populationSize <= 0) {
            throw new IllegalArgumentException(
                "Population size must be positive."
            );
        }

        if (problem == null || random == null) {
            throw new IllegalArgumentException(
                "Problem and random generator are required."
            );
        }

        List<Chromosome> population = new ArrayList<>();

        for (int i = 0; i < populationSize; i++) {
            List<List<Customer>> routes = createFeasibleRoutes(problem, random);

            int[] genes = encodeRoutes(routes);

            PermutationChromosome chromosome =
                new PermutationChromosome(genes);

            if (!problem.isFeasible(chromosome)) {
                throw new IllegalStateException(
                    "Generated chromosome is infeasible."
                );
            }

            population.add(chromosome);
        }

        return population;
    }

    private static List<List<Customer>> createFeasibleRoutes(
            CVRP problem,
            Random random) {

        List<Customer> shuffledCustomers =
            new ArrayList<>(problem.getCustomers());

        // Randomize first so customers with equal demands can appear
        // in different orders across initial solutions.
        Collections.shuffle(shuffledCustomers, random);

        // Assign larger-demand customers first to reduce backtracking.
        shuffledCustomers.sort(
            Comparator.comparingDouble(Customer::getDemand).reversed()
        );

        List<List<Customer>> routes = new ArrayList<>();
        double[] loads = new double[problem.getVehicleCount()];

        for (int i = 0; i < problem.getVehicleCount(); i++) {
            routes.add(new ArrayList<>());
        }

        boolean success = assignCustomers(
            shuffledCustomers,
            0,
            routes,
            loads,
            problem.getVehicleCapacity()
        );

        if (!success) {
            throw new IllegalStateException(
                "Could not construct a feasible initial CVRP solution."
            );
        }

        // The assignment is now feasible. Randomize visiting order
        // within each vehicle route.
        for (List<Customer> route : routes) {
            Collections.shuffle(route, random);
        }

        return routes;
    }

    private static boolean assignCustomers(
            List<Customer> customers,
            int index,
            List<List<Customer>> routes,
            double[] loads,
            double capacity) {

        if (index == customers.size()) {
            return true;
        }

        Customer customer = customers.get(index);
        double demand = customer.getDemand();

        // Avoid trying equivalent vehicles with identical current loads.
        List<Double> triedLoads = new ArrayList<>();

        for (int vehicle = 0; vehicle < routes.size(); vehicle++) {
            if (loads[vehicle] + demand > capacity) {
                continue;
            }

            boolean equivalentLoad = false;

            for (double triedLoad : triedLoads) {
                if (Double.compare(triedLoad, loads[vehicle]) == 0) {
                    equivalentLoad = true;
                    break;
                }
            }

            if (equivalentLoad) {
                continue;
            }

            triedLoads.add(loads[vehicle]);

            routes.get(vehicle).add(customer);
            loads[vehicle] += demand;

            if (assignCustomers(
                    customers,
                    index + 1,
                    routes,
                    loads,
                    capacity)) {
                return true;
            }

            routes.get(vehicle).remove(routes.get(vehicle).size() - 1);
            loads[vehicle] -= demand;
        }

        return false;
    }

    private static int[] encodeRoutes(List<List<Customer>> routes) {
        int customerCount = 0;

        for (List<Customer> route : routes) {
            customerCount += route.size();
        }

        int[] genes = new int[customerCount + routes.size() - 1];
        int index = 0;

        for (int vehicle = 0; vehicle < routes.size(); vehicle++) {
            for (Customer customer : routes.get(vehicle)) {
                genes[index++] = customer.getId();
            }

            // Unique separator markers are required because
            // PermutationChromosome enforces unique genes.
            if (vehicle < routes.size() - 1) {
                genes[index++] = -(vehicle + 1);
            }
        }

        return genes;
    }
}