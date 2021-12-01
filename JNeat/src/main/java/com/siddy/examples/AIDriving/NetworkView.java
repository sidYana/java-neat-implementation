package com.siddy.examples.AIDriving;

import com.siddy.JNeat.buildingBlocks.Genome;

import processing.core.PApplet;

public class NetworkView extends PApplet{

	private Genome genome;
	
	public NetworkView() {
		super();
		PApplet.runSketch(new String[]{this.getClass().getName()}, this);
	}
	
	public void settings() {
		size(700, 500);
	}
	
	public void setup() {
	}
	
	public void draw() {
		background(225);
		if(genome != null) {
			genome.show(this);
		}
	}
	
	public void setBrainToShow(Genome genome) {
		this.genome = genome;
	}
	
}
