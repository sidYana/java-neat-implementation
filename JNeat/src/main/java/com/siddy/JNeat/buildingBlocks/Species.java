package com.siddy.JNeat.buildingBlocks;

import static com.siddy.JNeat.properties.NeatProperties.*;
import static com.siddy.JNeat.util.RandomUtil.getRandomNumberBetween;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.siddy.JNeat.properties.NeatProperties;
import com.siddy.JNeat.util.RandomUtil;

public class Species {

	private long id;
	private List<Genome> population;
	private Genome representative = null;
	private Genome currentBest = null;
	private float excessCoeff = 1f;
	private float weightDiffCoeff = 0.5f;
	private float compatibilityThreshold = 1.0f;
	private double averageFitness;
	private double highestFitness;
	private double allTimeBestFitness;
	private int staleness = 0;
	private boolean stale = false;
	
	private Species() {
		population = new ArrayList<>();
	}

	public Species(long id, Genome newGenome) {
		this();
		this.id = id;
		this.population.add(newGenome);
		this.representative = newGenome;
	}
	
	public boolean addToSpecies(Genome newGenome) {
		if (belongs(newGenome)) {
			population.add(newGenome);
			return true;
		}
		return false;
	}
	
	private boolean belongs(Genome genome) {
		int excessAndDisjointNodes = 0;
		float avgWeightDiff = 0.0f;
		float totalWeightDiff = 0.0f;
		
		List<Connection> newGenomeInnovations = genome.getConnections();
		List<Connection> representativeInnovatios = representative.getConnections();
		
		int genome1Size = newGenomeInnovations.size();
		int genome2Size = representativeInnovatios.size();
			
		int matching = 0;
		for (Connection repInnovation : representativeInnovatios) {
			for(Connection newGenomeInnovation : newGenomeInnovations) {
				if(repInnovation.getInnovationNumber() == newGenomeInnovation.getInnovationNumber()) {
					matching++;
					totalWeightDiff += Math.abs(newGenomeInnovation.getWeight() - repInnovation.getWeight());
				}
			}
		}

		excessAndDisjointNodes = genome1Size + genome2Size - (2 * matching);
		avgWeightDiff = (matching == 0) ? 100 : totalWeightDiff / matching;

		float compatibility = ((excessCoeff * excessAndDisjointNodes) / Math.max(genome1Size, 1)) + (weightDiffCoeff * avgWeightDiff);

		return compatibilityThreshold > compatibility;
	}

	public boolean isEmpty() {
		return population.isEmpty();
	}
	
	public long getPopulationSize() {
		return population.size();
	}

	public double getAverageFitness() {
		return population.stream().mapToDouble(Genome::getFitness).average().getAsDouble();
	}

	public double getTotalFitness() {
		return population.stream().mapToDouble(Genome::getFitness).sum();
	}
	
	// TODO : could be improved
	private Genome pickOne() {
		double sum = population.stream().mapToDouble(Genome::getFitness).sum();
		double[] prob = population.stream().mapToDouble(g -> g.getFitness() / sum).toArray();
		
		float u = RandomUtil.getRandomFloat();
		
		for(int i = 0; i < population.size(); i++) {
			u -= prob[i];
			if(u < 0) {
				return population.get(i);
			}
		}
		return population.get(getRandomNumberBetween(population.size()));
	}

	public void clearPopulation() {
		population.clear();
		averageFitness = 0;
	}

	public List<Genome> getPopulation() {
		return population;
	}

	public List<Genome> createNewPopulation(long newIndividualsCount) {
		Collections.shuffle(population);
		List<Genome> newPopulation = new ArrayList<>();
		for(int i = 0; i < newIndividualsCount && population.size() >= 1; i++) {
			Genome parentA = pickOne();
			Genome parentB = pickOne();
			Genome child = null;
			
			if(parentA.getId() == parentB.getId()) {
				child = parentA.clone();
			} else if(parentA.getFitness() > parentB.getFitness()) {
				child = parentA.crossover(parentB);
			} else {
				child = parentB.crossover(parentA);
			}
			
			child.mutate();
			newPopulation.add(child);
		}
		return newPopulation;
	}

	public void calculateGenomeAdjustedFitness() {
		population.stream().forEach(genome -> genome.setFitness(genome.getFitness() / population.size()));
	}
	
	public void computeStaleness() {
		if(allTimeBestFitness < currentBest.getFitness()) {
			allTimeBestFitness = currentBest.getFitness();
			staleness = 0;
		} else {
			staleness++;
			if(staleness > staleSpeciesLimit) {
				stale  = true;
			}
		}
	}
	
	public void removeWeakGenomes() {
		if(population.size() > 5) {
			int middle = (int) Math.ceil(population.size() * NeatProperties.populationRetentionRate);
			
			population = population.subList(0, middle);
		}
	}
	
	public void sortPopulation() {
		population.sort((p1, p2) -> Double.compare(p2.getFitness(), p1.getFitness()));
	}
	
	public double calculateHighestFitness() {
		return population.stream().mapToDouble(Genome::getFitness).max().getAsDouble();
	}
	
	public void calculateAverageFitness() {
		this.averageFitness = population.stream().mapToDouble(Genome::getFitness).average().getAsDouble();
	}

	public double getHighestFitness() {
		return highestFitness;
	}

	public int getStaleness() {
		return staleness;
	}
	
	public void setBest() {
		this.currentBest = population.get(0);
		this.highestFitness = currentBest.getFitness();
	}
	
	public Genome getBest() {
		return currentBest;
	}

	public boolean isStale() {
		return stale;
	}

	public void performFitnessSharing() {
		for(Genome g : population) {
			g.setFitness(g.getFitness() / population.size());
		}
	}

	public void resetPopulationFitness() {
		population.forEach(p -> p.setFitness(0));
	}
}
