package core;

import chromosome.Chromosome;
import operators.selection.Selection;
import operators.crossover.Crossover;
import operators.mutation.Mutation;
import operators.replacement.Replacement;
import problem.FitnessFunction;
import problem.FeasibilityFunction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Genetic_algo {

    private final GAConfig config;
    private final FitnessFunction fitnessFunction;
    private final FeasibilityFunction feasibilityFunction;

    private final Selection selection;
    private final Crossover crossover;
    private final Mutation mutation;
    private final Replacement replacement;

    private final Random random;

    private List<Chromosome> population;
    private Chromosome bestChromosome;
    private double bestFitness;
    private int bestGeneration = -1;

    public Genetic_algo(
            GAConfig config,
            FitnessFunction fitnessFunction,
            FeasibilityFunction feasibilityFunction,
            Selection selection,
            Crossover crossover,
            Mutation mutation,
            Replacement replacement) {

        if (config == null
                || fitnessFunction == null
                || feasibilityFunction == null
                || selection == null
                || crossover == null
                || mutation == null
                || replacement == null) {
            throw new IllegalArgumentException(
                    "GA configuration, functions, and operators are required."
            );
        }

        this.config = config;
        this.fitnessFunction = fitnessFunction;
        this.feasibilityFunction = feasibilityFunction;
        this.selection = selection;
        this.crossover = crossover;
        this.mutation = mutation;
        this.replacement = replacement;
        this.random = new Random();

        this.bestFitness = getWorstInitialFitness();
    }

    public Chromosome run(List<Chromosome> initialPopulation) {

        if (initialPopulation == null
                || initialPopulation.size() != config.getPopulationSize()) {
            throw new IllegalArgumentException(
                    "Initial population size must match the configured population size."
            );
        }

        population = new ArrayList<>(initialPopulation);

        validateInitialPopulation();

        bestChromosome = null;
        bestFitness = getWorstInitialFitness();
        bestGeneration = -1;

        for (int generation = 0;
                generation < config.getMaxGenerations();
                generation++) {

            List<Double> fitnessValues = evaluatePopulation(population);

            updateBest(population, fitnessValues, generation);

            List<Chromosome> offspring = new ArrayList<>();

            while (offspring.size() < config.getPopulationSize()) {

                Chromosome parent1 = selection.select(
                        population, fitnessValues, random
                );

                Chromosome parent2 = selection.select(
                        population, fitnessValues, random
                );

                List<Chromosome> children;

                if (random.nextDouble() < config.getCrossoverRate()) {
                    children = crossover.cross(parent1, parent2, random);
                } else {
                    children = new ArrayList<>();
                    children.add(parent1.copy());
                    children.add(parent2.copy());
                }

                if (children == null || children.isEmpty()) {
                    throw new IllegalStateException(
                            "Crossover must return at least one child."
                    );
                }

                for (Chromosome child : children) {

                    if (child == null) {
                        throw new IllegalStateException(
                                "Crossover cannot return null children."
                        );
                    }

                    Chromosome candidate = child.copy();

                    if (random.nextDouble() < config.getMutationRate()) {
                        candidate = mutation.mutate(
                                candidate,
                                random,
                                generation,
                                config.getMaxGenerations()
                        );

                        if (candidate == null) {
                            throw new IllegalStateException(
                                    "Mutation cannot return null."
                            );
                        }
                    }

                    // Check whether the offspring is feasible.
                    if (!feasibilityFunction.isFeasible(candidate)) {

                        // Reject the infeasible offspring and copy one of its parents.
                        candidate = random.nextBoolean()
                                ? parent1.copy()
                                : parent2.copy();
                    }

                    offspring.add(candidate);

                    if (offspring.size() >= config.getPopulationSize()) {
                        break;
                    }
                }
            }

            List<Double> offspringFitness = evaluatePopulation(offspring);

            population = replacement.replace(
                    population,
                    fitnessValues,
                    offspring,
                    offspringFitness,
                    config.getPopulationSize()
            );

            if (population == null
                    || population.size() != config.getPopulationSize()) {
                throw new IllegalStateException(
                        "Replacement must return the configured population size."
                );
            }

            for (Chromosome chromosome : population) {

                if (chromosome == null) {
                    throw new IllegalStateException(
                            "Replacement returned a null chromosome."
                    );
                }

                if (!feasibilityFunction.isFeasible(chromosome)) {
                    throw new IllegalStateException(
                            "Replacement returned an infeasible chromosome."
                    );
                }
            }
        }

        // Evaluate the final population so its members are considered.
        List<Double> finalFitness = evaluatePopulation(population);
        updateBest(
                population,
                finalFitness,
                config.getMaxGenerations()
        );

        return bestChromosome.copy();
    }

    private void validateInitialPopulation() {

        for (Chromosome chromosome : population) {
            if (chromosome == null) {
                throw new IllegalArgumentException(
                        "Initial population cannot contain null chromosomes."
                );
            }

            if (!feasibilityFunction.isFeasible(chromosome)) {
                throw new IllegalArgumentException(
                        "Initial population contains an infeasible chromosome."
                );
            }
        }
    }

    private List<Double> evaluatePopulation(
            List<Chromosome> chromosomes) {

        List<Double> fitnessValues = new ArrayList<>();

        for (Chromosome chromosome : chromosomes) {

            double fitness = fitnessFunction.evaluate(chromosome);

            if (Double.isNaN(fitness)) {
                fitness = getWorstInitialFitness();
            } else if (config.getOptimizationDirection()
                    == OptimizationDirection.MINIMIZE
                    && fitness == Double.NEGATIVE_INFINITY) {
                fitness = Double.POSITIVE_INFINITY;
            } else if (config.getOptimizationDirection()
                    == OptimizationDirection.MAXIMIZE
                    && fitness == Double.POSITIVE_INFINITY) {
                fitness = Double.NEGATIVE_INFINITY;
            }

            fitnessValues.add(fitness);
        }

        return fitnessValues;
    }

    private void updateBest(
            List<Chromosome> chromosomes,
            List<Double> fitnessValues,
            int generation) {

        for (int i = 0; i < chromosomes.size(); i++) {

            double fitness = fitnessValues.get(i);

            if (bestChromosome == null || isBetter(fitness, bestFitness)) {
                bestFitness = fitness;
                bestChromosome = chromosomes.get(i).copy();
                bestGeneration = generation;
            }
        }
    }

    private boolean isBetter(double candidate, double reference) {
        if (config.getOptimizationDirection()
                == OptimizationDirection.MINIMIZE) {
            return candidate < reference;
        }

        return candidate > reference;
    }

    private double getWorstInitialFitness() {
        if (config.getOptimizationDirection()
                == OptimizationDirection.MINIMIZE) {
            return Double.POSITIVE_INFINITY;
        }

        return Double.NEGATIVE_INFINITY;
    }

    public List<Chromosome> getPopulation() {
        return population == null
                ? Collections.emptyList()
                : Collections.unmodifiableList(population);
    }

    public Chromosome getBestChromosome() {
        return bestChromosome == null
                ? null
                : bestChromosome.copy();
    }

    public double getBestFitness() {
        return bestFitness;
    }

    public int getBestGeneration() {
        return bestGeneration;
    }
}