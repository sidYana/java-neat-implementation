package com.siddy.examples.flappyBird;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.siddy.JNeat.JNeat;
import com.siddy.JNeat.buildingBlocks.Genome;
import com.siddy.JNeat.properties.NeatProperties;

import processing.core.PApplet;
import processing.core.PImage;

public class FlappyBird extends PApplet{

	private PImage pipeImg;
	private PImage pipeRevImg;
	private PImage birdImg;
	private PImage backgroundImg;
	
	private List<Bird> birds;
	private List<Pipe> pipes;
	
	boolean reset = false;
	private int count = 0;
	private final int testDuration = 1500;
	private int testDurationCounter = 0;
	private int speed = 1;
	private boolean pipeCrossed = false;
	
	private NetworkView networkView;
	private SecondWindow secondWindow;
	
	private Bird birdToShow;
	
	public static void main(String[] args) {
		PApplet.main(FlappyBird.class);
	}

	public void settings() {
		size(600, 500);
	}
	
	public void setup() {
		pipeImg = loadImage("/src/main/resources/flappyBird/pipe-green.png");
		pipeRevImg = loadImage("/src/main/resources/flappyBird/pipe-green-reverse.png");
		birdImg = loadImage("/src/main/resources/flappyBird/yellowbird-midflap.png");
		backgroundImg = loadImage("/src/main/resources/flappyBird/background-day.png");
		
		JNeat.INSTANCE.setTestName("FLAPPY_BIRD");
			
		NeatProperties.inputNodes = 7;
		NeatProperties.outputNodes = 1;
		NeatProperties.populationSize = 3000;
		NeatProperties.isFullyConnected = false;
		NeatProperties.addNodeMutation = 0.05;
		
		this.birds = new ArrayList<>();
		this.pipes = new ArrayList<>();
		
		initializeBrains();
		
		Pipe pipe = new Pipe(this, pipeImg, pipeRevImg);
		pipes.add(pipe);
		
		networkView = new NetworkView();
		secondWindow = new SecondWindow();
	}
	
	public void draw() {
		background(0);
		image(backgroundImg, 0, 0);
		image(backgroundImg, 280, 0);
		image(backgroundImg, 560, 0);
		
		addNewPipe();
		removeOffScreenPipes();
		
		for(Pipe pipe : pipes) {
			pipe.update();
			pipe.show();
		}
		
		Pipe closestPipe = getClosestPipe();
//		closestPipe.highlight();
		
		imageMode(CENTER);
		birds
		.parallelStream()
		.filter(bird -> !bird.isDead())
		.forEach(bird -> {
			if(closestPipe.collides(bird)) {
				bird.died();
			} else {
				bird.react(closestPipe);
				bird.update();
				bird.show();
				if(pipeCrossed) {
					bird.pipeCrossed();
				}
			}
		});
		imageMode(CORNER);
		
		if(birds.stream().filter(bird -> !bird.isDead()).count() == 0 || testDurationCounter > testDuration) {
			reset();
		}
		testDurationCounter++;
		
		fill(0,255,0);
		text(testDurationCounter, 10, 20);
		text(birds.parallelStream().filter(bird -> !bird.isDead()).count(), 10, 40);
		pipeCrossed = false;
		
		setBrainToShow();
	}
	
	private void setBrainToShow() {
		if(birdToShow != null && birdToShow.isDead()) {
			birdToShow = birds.stream().filter(b -> !b.isDead()).findFirst().orElse(null);
		} else if (birdToShow == null) {
			birdToShow = birds.get(0);
		}
		
		if(birdToShow != null) {
			networkView.setBrainToShow(birdToShow.getBrain());
		}
		
	}

	private void reset() {
		testDurationCounter = 0;
		reset = true;
		pipes.clear();
		JNeat.INSTANCE.breedNewSpecies();
		initializeBrains();
	}
	
	private void initializeBrains() {
		List<Genome> brains = JNeat.INSTANCE.getPopulation();
		
		for(Genome brain : brains) {
			birds.add(new Bird(this, birdImg, brain));
		}
	}
	
	private Pipe getClosestPipe() {
		float posX = birds.get(0).getPos().x;
		return pipes.stream().filter(pipe -> pipe.getPos().x + pipe.getWidth() + birds.get(0).getWidth() > posX).findAny().get();
	}

	private void removeOffScreenPipes() {
		ListIterator<Pipe> itr = pipes.listIterator();
		
		while(itr.hasNext()) {
			if(itr.next().isOutOfBounds()) {
				itr.remove();
				pipeCrossed = true;
			}
		}
		
	}

	private void addNewPipe() {
		count++;
		if(count % 90 == 0 || reset) {
			pipes.add(new Pipe(this, pipeImg, pipeRevImg));
			reset = false;
			count = 0;
		}
	}

	public void keyPressed() {
		if(key == 'x') {
			speed+=1;
		} else if(key =='z') {
			speed-=1;
		}
		
		if(speed > 10) {
			speed = 10;
		}
		
		if(speed < 1) {
			speed = 1;
		}
		
	}
	
}
