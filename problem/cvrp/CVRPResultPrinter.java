package problem.cvrp;

import chromosome.PermutationChromosome;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CVRPResultPrinter {

    public static void print(
            CVRP problem,
            PermutationChromosome best,
            int[][] distanceMatrix) {

        List<List<Integer>> routes = problem.decode(best);

        Map<Integer, Customer> customerMap = new HashMap<>();

        for (Customer customer : problem.getCustomers()) {
            customerMap.put(customer.getId(), customer);
        }

        int depotId = problem.getDepot().getId();
        double totalDistance = 0;

        System.out.println("CVRP Solution");
        System.out.println("=============");
        System.out.println("Vehicles: " + problem.getVehicleCount());
        System.out.println("Vehicle capacity: "
                + problem.getVehicleCapacity());
        System.out.println();

        for (int i = 0; i < routes.size(); i++) {
            List<Integer> route = routes.get(i);

            int load = 0;
            double routeDistance = 0;
            int previous = depotId;

            System.out.print("Route for vehicle " + i + ": ");
            System.out.print(depotId);

            for (int customerId : route) {
                Customer customer = customerMap.get(customerId);

                load += customer.getDemand();
                routeDistance += distanceMatrix[previous][customerId];

                System.out.print(" -> " + customerId);
                previous = customerId;
            }

            routeDistance += distanceMatrix[previous][depotId];

            System.out.println(" -> " + depotId);
            System.out.println("  Load: " + load + "/"
                    + problem.getVehicleCapacity());
            System.out.println("  Distance: " + routeDistance);
            System.out.println();

            totalDistance += routeDistance;
        }

        System.out.println("-----------------------------");
        System.out.println("Total distance: " + totalDistance);
    }
}