package com.siddy.examples.flappyBird;

import java.util.List;

import com.siddy.JNeat.JNeat;
import com.siddy.JNeat.generationData.GenerationData;

import grafica.GPlot;
import grafica.GPointsArray;
import processing.core.PApplet;

public class SecondWindow extends PApplet{

	private GPlot fitnessPlot;
	private GPlot speciesSizePlot;
	
	public SecondWindow() {
		super();
		PApplet.runSketch(new String[]{this.getClass().getName()}, this);
	}
	
	public void settings() {
		size(700, 700);
	}
	
	public void setup() {
		fitnessPlot = new GPlot(this, 0, 0, 700, 350);
		fitnessPlot.setTitleText("Fitness / Generation graph");
		fitnessPlot.getXAxis().setAxisLabelText("Generation");
		fitnessPlot.getYAxis().setAxisLabelText("Fitness");
		
		speciesSizePlot = new GPlot(this, 0, 350, 700, 350);
		speciesSizePlot.setTitleText("Species Size / Generation graph");
		speciesSizePlot.getXAxis().setAxisLabelText("Generation");
		speciesSizePlot.getYAxis().setAxisLabelText("Species Size");
	}
	
	public void draw() {
		List<GenerationData> generationDataList = JNeat.INSTANCE.getGenerationDataList();
		
		if(!generationDataList.isEmpty()) {
			GPointsArray fitnessPoints = new GPointsArray(generationDataList.size());
			GPointsArray speciesSizePoints = new GPointsArray(generationDataList.size());
			
			for(int i = 0; i < generationDataList.size(); i++) {
				fitnessPoints.add(i, (float) generationDataList.get(i).getHighestFitness());
				speciesSizePoints.add(i, generationDataList.get(i).getTotalSpecies());
			}
	
			fitnessPlot.setPoints(fitnessPoints);
			speciesSizePlot.setPoints(speciesSizePoints);
		}
		
		fitnessPlot.defaultDraw();
		speciesSizePlot.defaultDraw();
	}
	
}
