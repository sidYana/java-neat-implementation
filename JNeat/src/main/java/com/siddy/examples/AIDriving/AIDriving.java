package com.siddy.examples.AIDriving;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.siddy.JNeat.JNeat;
import com.siddy.JNeat.properties.NeatProperties;
import com.siddy.JNeat.util.RandomUtil;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

public class AIDriving extends PApplet{

	private List<Vehicle> vehicles = new ArrayList<>();
	private List<Boundary> outerTrack = new ArrayList<>();
	private List<Boundary> innerTrack = new ArrayList<>();
	private List<Boundary> checkpoints = new ArrayList<>(); 
	private List<Boundary> allTracks = new ArrayList<Boundary>();
	private Vehicle vehicleToShow = null;
	
	private NetworkView networkView;
	
	private int testCounter = 0;
	private boolean debug = false;
	
	public static void main(String[] args) {
		PApplet.main(AIDriving.class);
	}

	public void settings() {
		size(1000, 800);
	}
	
	public void setup() {
		NeatProperties.inputNodes = 10;
		NeatProperties.outputNodes = 1;
		NeatProperties.isFullyConnected = false;
		
		networkView = new NetworkView();
		
		reset();
	}
	
	public void draw() {
		testCounter++;
		background(0);
		stroke(255);
		strokeWeight(2);
		noFill();
		drawTrack();
			
		vehicles.parallelStream()
		.filter(v -> !v.isDead())
		.forEach(v -> {
			v.react(allTracks, this);
			v.update();
		});
		
		if(debug) {
			vehicles.stream()
			.filter(v -> !v.isDead())
			.sorted((v1, v2) -> Double.compare(v2.getFitness(), v1.getFitness()))
			.findFirst()
			.ifPresent(v -> {
				v.show(this, debug);
				networkView.setBrainToShow(v.getBrain());
			});
		} else {
			vehicles.forEach(v -> v.show(this, debug));
		}
	
		if(vehicles.stream().filter(v -> !v.isDead()).count() < 1 || testCounter == 1000) {
			JNeat.INSTANCE.breedNewSpecies();
			reset();
		}
		
		setBrainToShow();
	}
	
	public void reset() {
		testCounter = 0;
		outerTrack.clear();
		innerTrack.clear();
		allTracks.clear();
		checkpoints.clear();
		vehicles.clear();
		calculateTrack();
		
		JNeat.INSTANCE.getPopulation().forEach(g -> {
			vehicles.add(new Vehicle(g, checkpoints));
		});
	}
	
	private void drawTrack() {
		for(int i = 0; i < outerTrack.size(); i++) {
			innerTrack.get(i).show(this);
			outerTrack.get(i).show(this);
			if(i == 0) {
				checkpoints.get(i).show(this);
			}
		}
	}

	public void calculateTrack() {
		Map<String, List<PVector>> points = new HashMap<>();
		points.put("INNER", new ArrayList<>());
		points.put("OUTER", new ArrayList<>());
		int noiseMax = 5;
		noiseSeed(RandomUtil.getRandomNumberBetween(1, 100));
		for(float a = 0.0f; a < PConstants.TWO_PI; a+=radians(5)) {
			float xOff = map(cos(a), -1, 1, 0, noiseMax);
			float yOff = map(sin(a), -1, 1, 0, noiseMax);
			float r = map(noise(xOff, yOff), 0, 1, 100, height / 2);
			float x1 = (width / 2) + (r - 50) * cos(a);
			float y1 = (height / 2) + (r - 50) * sin(a);
			float x2 = (width / 2) + (r + 50) * cos(a);
			float y2 = (height / 2) + (r + 50) * sin(a);
			points.get("INNER").add(new PVector(x1, y1));
			points.get("OUTER").add(new PVector(x2, y2));
			checkpoints.add(new Boundary(new PVector(x1, y1), new PVector(x2, y2)));
		}
		
		for(int i = 0; i < points.get("INNER").size(); i++) {
			PVector a1 = points.get("INNER").get(i);
			PVector b1 = points.get("INNER").get((i + 1) % points.get("INNER").size());
			Boundary innerboundary = new Boundary(a1, b1);
			innerTrack.add(innerboundary);
			
			PVector a2 = points.get("OUTER").get(i);
			PVector b2 = points.get("OUTER").get((i + 1) % points.get("OUTER").size());
			Boundary outerboundary = new Boundary(a2, b2);
			outerTrack.add(outerboundary);
		}
		
		allTracks.addAll(outerTrack);
		allTracks.addAll(innerTrack);
	}
	
	private void setBrainToShow() {
		if(!debug) {
			if(vehicleToShow != null && vehicleToShow.isDead()) {
				vehicleToShow = vehicles.stream().filter(b -> !b.isDead()).findFirst().orElse(null);
			} else if (vehicleToShow == null) {
				vehicleToShow = vehicles.get(0);
			}
			
			if(vehicleToShow != null) {
				networkView.setBrainToShow(vehicleToShow.getBrain());
			}
		}
	}
	
	public void keyPressed() {
		if(key == 'd') {
			debug = !debug;
		}
	}
}
