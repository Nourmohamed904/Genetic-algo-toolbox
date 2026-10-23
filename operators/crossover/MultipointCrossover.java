package operators.crossover;

import chromosome.BinaryChromosome;
import chromosome.Chromosome;
import chromosome.IntegerChromosome;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MultipointCrossover implements Crossover
{
    private final int numberOfPoints;

    public MultipointCrossover(int numberOfPoints) {
        if (numberOfPoints < 1) {
            throw new IllegalArgumentException("Number of crossover points must be positive.");
        }

        this.numberOfPoints = numberOfPoints;
    }
    
    @Override
    public List<Chromosome> cross(
            Chromosome parent1,
            Chromosome parent2,
            Random random
    ) {
        if (parent1.getClass() != parent2.getClass()) {
            throw new IllegalArgumentException("Parents must have the same chromosome type.");
        }

        if (parent1.length() != parent2.length()) {
            throw new IllegalArgumentException("Parents must have equal chromosome lengths.");
        }

        int length = parent1.length();

        if (parent1.length() != parent2.length()) {
            throw new IllegalArgumentException("Number of crossover points must be less than chromosome length.");
        }

        List<Integer> possiblePoints = new ArrayList<>();

        for (int i = 1; i < length; i++) {
            possiblePoints.add(i);
        }

        Collections.shuffle(possiblePoints, random);

        List<Integer> points = new ArrayList<>(
                possiblePoints.subList(0, numberOfPoints)
        );

        Collections.sort(points);

        if (parent1 instanceof BinaryChromosome p1
                && parent2 instanceof BinaryChromosome p2) {

            boolean[] genes1 = p1.getGenes();
            boolean[] genes2 = p2.getGenes();

            boolean[] child1 = new boolean[length];
            boolean[] child2 = new boolean[length];
                    
            buildChildren(genes1, genes2, child1, child2, points);

             return Arrays.asList(
                    new BinaryChromosome(child1),
                    new BinaryChromosome(child2)
            );
        }

        if (parent1 instanceof IntegerChromosome p1
                && parent2 instanceof IntegerChromosome p2) {

            int[] genes1 = p1.getGenes();
            int[] genes2 = p2.getGenes();

            int[] child1 = new int[length];
            int[] child2 = new int[length];
                    
            buildChildren(genes1, genes2, child1, child2, points);

             return Arrays.asList(
                    new IntegerChromosome(child1),
                    new IntegerChromosome(child2)
            );
        }

        throw new IllegalArgumentException(
                "Multi-point crossover supports binary and integer chromosomes only."
        );
    }

    private void buildChildren( boolean[] parent1, boolean[] parent2,
                                boolean[] child1, boolean[] child2,
                                List<Integer> points
    ) {
        int segment = 0;

        for (int i = 0; i < parent1.length; i++) {
            while (segment < points.size() && i >= points.get(segment)) {
                segment++;
            }         
            
            boolean takeParent1 = segment % 2 == 0;

            child1[i] = takeParent1 ? parent1[i] : parent2[i];
            child2[i] = takeParent1 ? parent2[i] : parent1[i];
        }
    }
    
    private void buildChildren( int[] parent1, int[] parent2,
                                int[] child1, int[] child2,
                                List<Integer> points
    ) {
        int segment = 0;

        for (int i = 0; i < parent1.length; i++) {
            while (segment < points.size() && i >= points.get(segment)) {
                segment++;
            }         
            
            boolean takeParent1 = segment % 2 == 0;

            child1[i] = takeParent1 ? parent1[i] : parent2[i];
            child2[i] = takeParent1 ? parent2[i] : parent1[i];
        }
    }
}
