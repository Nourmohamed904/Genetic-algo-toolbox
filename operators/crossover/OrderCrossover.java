package operators.crossover;

import chromosome.Chromosome;
import chromosome.PermutationChromosome;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class OrderCrossover implements Crossover 
{
    @Override
    public List<Chromosome> cross(
            Chromosome parent1,
            Chromosome parent2,
            Random random) {

        if (!(parent1 instanceof PermutationChromosome)
                || !(parent2 instanceof PermutationChromosome)) {
            throw new IllegalArgumentException(
                "Order crossover requires permutation chromosomes."
            );
        }

        int[] genes1 =
            ((PermutationChromosome) parent1).getGenes();

        int[] genes2 =
            ((PermutationChromosome) parent2).getGenes();

        if (genes1.length != genes2.length) {
            throw new IllegalArgumentException(
                "Parent chromosomes must have the same length."
            );
        }

        Set<Integer> parent1Genes = new HashSet<>();
        Set<Integer> parent2Genes = new HashSet<>();

        for (int gene : genes1) {
            parent1Genes.add(gene);
        }

        for (int gene : genes2) {
            parent2Genes.add(gene);
        }

        if (!parent1Genes.equals(parent2Genes)) {
            throw new IllegalArgumentException(
                "Parents must contain the same set of gene IDs."
            );
        }

        int n = genes1.length;

        // With fewer than two genes, crossover cannot change the order.
        if (n < 2) {
            List<Chromosome> children = new ArrayList<>();
            children.add(parent1.copy());
            children.add(parent2.copy());
            return children;
        }

        int start = random.nextInt(n);
        int end = random.nextInt(n);

        // Ensure start <= end.
        if (start > end) {
            int temp = start;
            start = end;
            end = temp;
        }

        int[] child1 = createChild(genes1, genes2, start, end);
        int[] child2 = createChild(genes2, genes1, start, end);

        List<Chromosome> children = new ArrayList<>();
        children.add(new PermutationChromosome(child1));
        children.add(new PermutationChromosome(child2));

        return children;
    }

    private int[] createChild(
            int[] first,
            int[] second,
            int start,
            int end) {

        int n = first.length;
        int[] child = new int[n];

        // Track actual gene IDs, not array indices.
        Set<Integer> used = new HashSet<>();

        // Copy the selected segment from the first parent.
        for (int i = start; i <= end; i++) {
            child[i] = first[i];
            used.add(first[i]);
        }

        // Continue filling the child after the copied segment,
        // wrapping around when necessary.
        int writeIndex = (end + 1) % n;

        for (int offset = 0; offset < n; offset++) {

            int readIndex = (end + 1 + offset) % n;
            int gene = second[readIndex];

            if (!used.contains(gene)) {
                child[writeIndex] = gene;
                used.add(gene);

                writeIndex = (writeIndex + 1) % n;
            }
        }

        return child;
    }
}