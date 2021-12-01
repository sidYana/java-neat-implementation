package com.siddy.JNeat.generationData;

public class GenerationData {

	private long generation = 0;
	private int totalSpecies = 0;
	private double highestFitness = 0;
	private double averageFitness = 0;
	private double averageSpeciesFitness = 0;

	public long getGeneration() {
		return generation;
	}

	public void setGeneration(long generation2) {
		this.generation = generation2;
	}

	public int getTotalSpecies() {
		return totalSpecies;
	}

	public void setTotalSpecies(int totalSpecies) {
		this.totalSpecies = totalSpecies;
	}

	public double getHighestFitness() {
		return highestFitness;
	}

	public void setHighestFitness(double highestFitness) {
		this.highestFitness = highestFitness;
	}

	public double getAverageFitness() {
		return averageFitness;
	}

	public void setAverageFitness(double averageFitness) {
		this.averageFitness = averageFitness;
	}

	public double getAverageSpeciesFitness() {
		return averageSpeciesFitness;
	}

	public void setAverageSpeciesFitness(double averageSpeciesFitness) {
		this.averageSpeciesFitness = averageSpeciesFitness;
	}

}
