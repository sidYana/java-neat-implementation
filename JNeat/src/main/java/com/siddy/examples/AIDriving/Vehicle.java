package com.siddy.examples.AIDriving;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.siddy.JNeat.buildingBlocks.Genome;
import com.siddy.JNeat.properties.NeatProperties;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

public class Vehicle {
	private Genome brain;
	private PVector pos;
	private PVector vel;
	private PVector acc;
	private PVector target;
	private float sight = 200;
	private boolean dead = false;

	private List<Ray> rays;

	private final float maxSpeed = 2.0f;
	private final float maxForce = 0.f;

	private final int fov = 180;

	private Map<Boundary, Boolean> checkpointsCrossed;

	public Vehicle(Genome brain, List<Boundary> checkpoints) {
		this.brain = brain;
		this.pos = checkpoints.get(0).getMidPoint();
		this.vel = new PVector(0, 5);
		this.acc = new PVector();
		this.target = new PVector();
		this.rays = new ArrayList<Ray>();
		checkpointsCrossed = new HashMap<>();

		for (Boundary b : checkpoints.subList(0, checkpoints.size() - 1)) {
			checkpointsCrossed.put(b, false);
		}
		
		checkpointsCrossed.put(checkpoints.get(checkpoints.size() - 1), true);

		float increment = 10f;
		for (float angle = 45; angle <= 135; angle += increment) {
			rays.add(new Ray(pos, PVector.fromAngle(PApplet.radians(angle))));
		}
	}

	public void update() {
		vel.add(acc);
		vel.limit(maxSpeed);
		pos.add(vel);
		acc.mult(0);
		checkCrossedCheckpoint();
//	    calculateTarget();
	}

	public void react(List<Boundary> walls, PApplet ctx) {

		double[] inputs = new double[(int) NeatProperties.inputNodes];

		int inputIndex = 0;

		for (Ray r : rays) {
			float record = sight;
			PVector closest = null;
			for (Boundary wall : walls) {
				PVector intersection = r.cast(wall);
				if (intersection != null) {
					float distance = PVector.dist(pos, intersection);
					if (distance < record) {
						record = distance;
						closest = intersection;
					}
				}
			}

			if (record < 5) {
				dead = true;
				return;
			}

			inputs[inputIndex++] = PApplet.map(record, 0, sight, 1, 0.001f);

//			if(closest != null) {
//				ctx.line(pos.x, pos.y, closest.x, closest.y);
//			}
		}

		double[] outputs = brain.evaluate(inputs);

		float angle = PApplet.map((float) outputs[0], 0, 1, -PConstants.PI / 10, PConstants.PI / 10);

		vel.rotate(angle);
		
		for(Ray r : rays) {
			r.getDirection().rotate(angle);
		}
//		PVector steering = PVector.fromAngle(angle);
//		steering.setMag(maxSpeed);
//		steering.sub(vel);
//		applyForce(steering);
	}

	private void checkCrossedCheckpoint() {
		for (Map.Entry<Boundary, Boolean> entry : checkpointsCrossed.entrySet()) {
			float d1 = PVector.dist(pos, entry.getKey().getFrom());
			float d2 = PVector.dist(pos, entry.getKey().getTo());

			// get the length of the line
			float lineLen = PVector.dist(entry.getKey().getFrom(), entry.getKey().getTo());

			// since floats are so minutely accurate, add
			// a little buffer zone that will give collision
			float buffer = 0.01f; // higher # = less accurate

			// if the two distances are equal to the line's
			// length, the point is on the line!
			// note we use the buffer here to give a range,
			// rather than one #
			if (d1 + d2 >= lineLen - buffer && d1 + d2 <= lineLen + buffer) {
				boolean alreadyCrossed = entry.getValue();
				if(alreadyCrossed) {
					dead = true;
				} else {
					entry.setValue(true);
					brain.setFitness(brain.getFitness() + 10);
				}
				break;
			}
		}
	}

	public void steer() {
		PVector desired = PVector.sub(target, pos);
		desired.normalize();
		desired.mult(maxSpeed);
		PVector steer = PVector.sub(desired, vel);
		steer.limit(maxForce);
		applyForce(steer);
	}

	private void calculateTarget() {
		PVector circlePos = vel.get();
		circlePos.normalize();
		circlePos.mult(30);
		circlePos.add(pos);

		float heading = vel.heading();

		target = circlePos;
	}

	private void applyForce(PVector force) {
		acc.add(force);
	}

	public void show(PApplet ctx, boolean debug) {
		ctx.fill(255, 0, 0);
		ctx.pushMatrix();
		ctx.translate(pos.x, pos.y);
		ctx.rotate(vel.heading());
		ctx.rectMode(PConstants.CENTER);
		ctx.rect(0, 0, 15, 5);
		ctx.rectMode(PConstants.CORNER);
		ctx.popMatrix();
		ctx.noFill();

		if(debug) {
			for(Ray ray : rays) {
				ray.show(ctx);
			}
		}

//		ctx.ellipse(target.x, target.y, 20, 20);
	}

	public boolean isDead() {
		return dead;
	}

	public double getFitness() {
		return brain.getFitness();
	}

	public Genome getBrain() {
		return brain;
	}
}
