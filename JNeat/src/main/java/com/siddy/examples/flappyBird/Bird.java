package com.siddy.examples.flappyBird;

import com.siddy.JNeat.buildingBlocks.Genome;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class Bird {
	private PImage birdImg;
	private PApplet ctx;

	private Genome brain;

	private PVector pos;
	private float lift = -20;
	private float vel;
	private float gravity = 0.8f;
	private float airResistance = 1f;
	private float width = 25;
	private float height = 25;
	private double memory = 0.1;
	private boolean dead = false;

	public Bird(PApplet ctx, PImage img, Genome brain) {
		this.birdImg = img;
		this.ctx = ctx;
		this.brain = brain;
		pos = new PVector(100, ctx.height / 2);
	}

	public void show() {
//		ctx.fill(255);
//		ctx.ellipse(pos.x, pos.y, width, height);
		ctx.image(birdImg, pos.x, pos.y);
	}

	public void jump() {
		vel += lift;
	}

	public void update() {
		vel += gravity;
		vel *= airResistance;
		pos.add(0, vel);

		if (pos.y > ctx.height - 12.5 || pos.y < 12.5) {
			dead = true;
		}
	}

	public PVector getPos() {
		return pos;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	public void react(Pipe closestPipe) {
		PVector pipePos = closestPipe.getPos();

		double[] inputs = new double[] { 
				PApplet.map(pos.x, ctx.width, 0, 0, 1), 
				PApplet.map(pos.y, ctx.height, 0, 0, 1),
				PApplet.map(vel, -20, 20, 0, 1),
				PApplet.map(pipePos.x, 0, ctx.width, 0, 1),
				PApplet.map(pipePos.y, 0, ctx.height, 0, 1),
				PApplet.map(pipePos.y + closestPipe.getGap(), 0, ctx.height, 0, 1),
				memory
				};
		
		double[] outputs = brain.evaluate(inputs);
		
		if (outputs[0] > 0.6) {
			jump();
		}
		
		memory = outputs[0];
		
		if(pos.y > pipePos.y && pos.y < pipePos.y + closestPipe.getGap()) {
			brain.setFitness(brain.getFitness() + 0.1);
		}
	}
	
	public boolean isDead() {
		return dead;
	}

	public boolean hasCollided(Pipe closestPipe) {
		 if(((pos.y - height/2) < pos.y || (pos.y + height/2) > closestPipe.getPos().y + closestPipe.getGap()) 
				&& ((pos.x + width/2) > closestPipe.getPos().x && (pos.x - width/2) < closestPipe.getPos().x + closestPipe.getWidth())) {
			 dead = true;
		 }
		 return dead;
	}

	public void died() {
		dead = true;
	}

	public void pipeCrossed() {
		brain.setFitness(brain.getFitness() + 10);
	}
	
	public Genome getBrain() {
		return brain;
	}
}
