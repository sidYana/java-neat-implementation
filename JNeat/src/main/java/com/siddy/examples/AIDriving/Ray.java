package com.siddy.examples.AIDriving;

import processing.core.PApplet;
import processing.core.PVector;

public class Ray {
	private PVector position;
	private PVector direction;
	
	public Ray(PVector pos, PVector direction) {
		this.position = pos;
		this.direction = direction;
		direction.mult(100);
	}
	
	public void show(PApplet ctx) {
		ctx.stroke(255);
		ctx.pushMatrix();
		ctx.translate(position.x, position.y);
		ctx.line(0, 0, direction.x, direction.y);
		ctx.popMatrix();
	}
	
	public PVector cast(Boundary wall) {

		final float x1 = wall.getFrom().x;
		final float y1 = wall.getFrom().y;
		final float x2 = wall.getTo().x;
		final float y2 = wall.getTo().y;

		final float x3 = position.x;
		final float y3 = position.y;
		final float x4 = position.x + direction.x;
		final float y4 = position.y + direction.y;

		final float den = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);

		if (den == 0) {
			return null;
		}

		final float t = ((x1 - x3) * (y3 - y4) - (y1 - y3) * (x3 - x4)) / den;
		final float u = -((x1 - x2) * (y1 - y3) - (y1 - y2) * (x1 - x3)) / den;

		if (t > 0 && t < 1 && u > 0) {
			PVector intersection = new PVector();
			intersection.x = x1 + t * (x2 - x1);
			intersection.y = y1 + t * (y2 - y1);
			return intersection;
		}
		return null;
	}

	public PVector getDirection() {
		return direction;
	}

	public void setDirection(PVector direction) {
		this.direction = direction;
	}
}
