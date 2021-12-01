package com.siddy.examples.AIDriving;

import processing.core.PApplet;
import processing.core.PVector;

public class Boundary {
	private final PVector from;
	private final PVector to;
	
	public Boundary(PVector from, PVector to) {
		this.from = from;
		this.to = to;
	}
	
	public void show(PApplet ctx) {
		ctx.line(from.x, from.y, to.x, to.y);
	}

	public PVector getMidPoint() {
		return new PVector((from.x + to.x) / 2.0f, (from.y + to.y) / 2.0f);
	}
	
	public PVector getFrom() {
		return from;
	}

	public PVector getTo() {
		return to;
	}
}
