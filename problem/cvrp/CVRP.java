package problem.cvrp;

import chromosome.Chromosome;
import chromosome.PermutationChromosome;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CVRP implements problem.FitnessFunction,
        problem.FeasibilityFunction {

    private final Customer depot;
    private final List<Customer> customers;
    private final Map<Integer, Customer> customerById;
    private final double vehicleCapacity;
    private final int vehicleCount;
    private final int[][] distanceMatrix;

    public CVRP(
            Customer depot,
            List<Customer> customers,
            double vehicleCapacity,
            int vehicleCount,
            int[][] distanceMatrix) {

        if (depot == null || customers == null || customers.isEmpty()) {
            throw new IllegalArgumentException(
                "Depot and customers are required."
            );
        }

        if (!Double.isFinite(vehicleCapacity) || vehicleCapacity <= 0) {
            throw new IllegalArgumentException(
                "Vehicle capacity must be positive and finite."
            );
        }

        if (vehicleCount <= 0) {
            throw new IllegalArgumentException(
                "Vehicle count must be positive."
            );
        }

        if (distanceMatrix == null
                || distanceMatrix.length != customers.size() + 1) {
            throw new IllegalArgumentException(
                "Distance matrix must contain the depot and all customers."
            );
        }

        for (int[] row : distanceMatrix) {
            if (row == null || row.length != distanceMatrix.length) {
                throw new IllegalArgumentException(
                    "Distance matrix must be square."
                );
            }

            for (int distance : row) {
                if (distance < 0) {
                    throw new IllegalArgumentException(
                        "Distances cannot be negative."
                    );
                }
            }
        }

        this.depot = depot;
        this.vehicleCapacity = vehicleCapacity;
        this.vehicleCount = vehicleCount;
        this.distanceMatrix = distanceMatrix;
        this.customers = new ArrayList<>(customers);
        this.customerById = new HashMap<>();

        if (depot.getId() < 0
                || depot.getId() >= distanceMatrix.length) {
            throw new IllegalArgumentException(
                "Depot ID is outside the distance matrix."
            );
        }

        for (Customer customer : this.customers) {
            int id = customer.getId();

            if (id < 0 || id >= distanceMatrix.length) {
                throw new IllegalArgumentException(
                    "Customer ID is outside the distance matrix: " + id
                );
            }

            if (id == depot.getId()) {
                throw new IllegalArgumentException(
                    "A customer cannot have the depot's ID."
                );
            }

            if (!Double.isFinite(customer.getDemand())
                    || customer.getDemand() < 0) {
                throw new IllegalArgumentException(
                    "Customer demand must be finite and nonnegative."
                );
            }

            if (customer.getDemand() > vehicleCapacity) {
                throw new IllegalArgumentException(
                    "Customer " + id + " exceeds vehicle capacity."
                );
            }

            if (customerById.put(id, customer) != null) {
                throw new IllegalArgumentException(
                    "Duplicate customer ID: " + id
                );
            }
        }

        if (customerById.size() != distanceMatrix.length - 1) {
            throw new IllegalArgumentException(
                "Customer IDs must cover every matrix location except the depot."
            );
        }

        double totalDemand = 0;

        for (Customer customer : this.customers) {
            totalDemand += customer.getDemand();
        }

        if (totalDemand > vehicleCount * vehicleCapacity) {
            throw new IllegalArgumentException(
                "Total demand exceeds the fleet's total capacity."
            );
        }
    }

    @Override
    public boolean isFeasible(Chromosome chromosome) {
        if (!(chromosome instanceof PermutationChromosome)) {
            return false;
        }

        int[] genes =
            ((PermutationChromosome) chromosome).getGenes();

        // Customers plus one separator between consecutive routes.
        int expectedLength = customers.size() + vehicleCount - 1;

        if (genes.length != expectedLength) {
            return false;
        }

        Set<Integer> visited = new HashSet<>();
        Set<Integer> separators = new HashSet<>();

        List<List<Integer>> routes = decodeRoutes(genes);

        if (routes == null || routes.size() != vehicleCount) {
            return false;
        }

        // Every customer must appear exactly once.
        for (int[] unused = new int[0]; unused.length > 0; ) {
            // Intentionally empty; customer validation is performed below.
        }

        for (int gene : genes) {
            if (gene < 0) {
                if (!separators.add(gene)) {
                    return false;
                }
            } else {
                if (!customerById.containsKey(gene)
                        || !visited.add(gene)) {
                    return false;
                }
            }
        }

        if (visited.size() != customers.size()
                || separators.size() != vehicleCount - 1) {
            return false;
        }

        // Every route must respect vehicle capacity.
        for (List<Integer> route : routes) {
            double load = 0;

            for (int customerId : route) {
                Customer customer = customerById.get(customerId);

                if (customer == null) {
                    return false;
                }

                load += customer.getDemand();
            }

            if (load > vehicleCapacity) {
                return false;
            }
        }

        return true;
    }

    @Override
    public double evaluate(Chromosome chromosome) {
        if (!isFeasible(chromosome)) {
            return infeasiblePenalty();
        }

        List<List<Integer>> routes =
            decodeRoutes(((PermutationChromosome) chromosome).getGenes());

        double totalDistance = 0;

        for (List<Integer> route : routes) {
            int previousId = depot.getId();

            for (int customerId : route) {
                totalDistance += distance(previousId, customerId);
                previousId = customerId;
            }

            // Return to the depot after completing this route.
            totalDistance += distance(previousId, depot.getId());
        }

        return totalDistance;
    }

    public List<List<Integer>> decode(
            PermutationChromosome chromosome) {

        if (!isFeasible(chromosome)) {
            throw new IllegalArgumentException(
                "Cannot decode an infeasible chromosome."
            );
        }

        return decodeRoutes(chromosome.getGenes());
    }

    private List<List<Integer>> decodeRoutes(int[] genes) {
        List<List<Integer>> routes = new ArrayList<>();
        List<Integer> currentRoute = new ArrayList<>();

        Set<Integer> separators = new HashSet<>();

        for (int gene : genes) {
            if (gene < 0) {
                // Each separator must be unique.
                if (!separators.add(gene)) {
                    return null;
                }

                routes.add(currentRoute);
                currentRoute = new ArrayList<>();
            } else {
                currentRoute.add(gene);
            }
        }

        routes.add(currentRoute);

        if (routes.size() != vehicleCount) {
            return null;
        }

        return routes;
    }

    private double distance(int fromId, int toId) {
        return distanceMatrix[fromId][toId];
    }

    private double infeasiblePenalty() {
        double maxDistance = 0;

        for (int[] row : distanceMatrix) {
            for (int distance : row) {
                maxDistance = Math.max(maxDistance, distance);
            }
        }

        // Larger than the maximum possible distance of a feasible solution
        // for the supported nonnegative-distance CVRP representation.
        return (customers.size() + vehicleCount)
                * maxDistance + 1.0;
    }

    public double getVehicleCapacity() {
        return vehicleCapacity;
    }

    public int getVehicleCount() {
        return vehicleCount;
    }

    public Customer getDepot() {
        return depot;
    }

    public List<Customer> getCustomers() {
        return Collections.unmodifiableList(customers);
    }
}