package com.siddy.examples.xor;

import java.util.List;

import com.siddy.JNeat.JNeat;
import com.siddy.JNeat.buildingBlocks.Genome;
import com.siddy.JNeat.properties.NeatProperties;

import processing.core.PApplet;

public class XORTest extends PApplet{

	private final double[][] inputs = {{0,0}, {0,1}, {1,0}, {1,1}};
	private final double[][] outputs = {{0}, {1}, {1}, {0}};
	
	int count = 0;
	
	public static void main(String[] args) {
		PApplet.main(XORTest.class);
	}
	
	public void settings() {
		size(750, 750);
	}
	
	public void setup() {
		NeatProperties.populationSize = 2000;
		NeatProperties.inputNodes = 2;
		NeatProperties.outputNodes = 1;
	}
	
	public void draw() {
		background(255);
		List<Genome> population = JNeat.INSTANCE.getPopulation();
		
		population.parallelStream()
		.forEach(genome -> {
			double diff;
			double fitness = 0;
			for(int i = 0; i < inputs.length; i++) {
				double[] actual = genome.evaluate(inputs[i]);
				diff = Math.abs(outputs[i][0] - actual[0]);
				fitness += ((1 - diff) * 25);
			}
			genome.setFitness(fitness);
		});
		
		Genome genome = population.parallelStream().sorted((p1, p2) -> Double.compare(p2.getFitness(), p1.getFitness())).findFirst().get();
			
		drawXORMap(genome);
		
		JNeat.INSTANCE.breedNewSpecies();
	}
	
	private void drawXORMap(Genome genome) {
		double[] value = null;
		for(int i = 0; i < width; i+=25) {
			for(int j = 0; j < height; j+=25) {
				value = genome.evaluate(new double[] {PApplet.map(i, 0, 750, 0, 1), PApplet.map(j, 0, 750, 0, 1)});
				fill((int) (value[0] * 255));
				rect(i, j, 50, 50);
			}
		}
		
	}
}
