package com.siddy.examples.flappyBird;

import java.util.UUID;

import com.siddy.JNeat.util.RandomUtil;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class Pipe {
	private PImage pipeImg;
	private PImage pipeRevImg;
	private PApplet ctx;
	private final String uuid;
	private PVector pos;
	private float width = 50;
	private float gap = 120;
	private float speed = 3f;

	public Pipe(PApplet ctx, PImage pipeImg2, PImage pipeRevImg2) {
		this.pipeImg = pipeImg2;
		this.pipeRevImg = pipeRevImg2;
		this.uuid = UUID.randomUUID().toString();
		this.ctx = ctx;
//		float y = RandomUtil.getRandomNumberBetween(gap, (float) ctx.height - 2*gap);
		float y = RandomUtil.getRandomNumberBetween((ctx.height / 100 ) * 10, (float) ctx.height - ((ctx.height / 100 ) * 30));
		this.pos = new PVector(ctx.width, y);
	}
	
	public void update() {
		pos.x-=speed;
	}
	
	public void highlight() {
		ctx.fill(255, 0 , 0);
		ctx.rect(pos.x, 0, width, pos.y);
		ctx.rect(pos.x, ctx.height, width, -(ctx.height - pos.y) + gap);
		ctx.fill(0, 255, 0);
		ctx.ellipse(pos.x, pos.y, 10, 10);
		ctx.ellipse(pos.x + getWidth(), pos.y, 10, 10);
		ctx.ellipse(pos.x, pos.y + getGap(), 10, 10);
		ctx.ellipse(pos.x + getWidth(), pos.y + getGap(), 10, 10);
		ctx.noFill();
	}
	
	public void show() {
		ctx.fill(255);
//      ctx.rect(pos.x, 0, width, pos.y);
		ctx.image(pipeRevImg, pos.x, 0, width, pos.y);
		ctx.image(pipeImg, pos.x, ctx.height, width, -(ctx.height -pos.y) + gap);
//		ctx.rect(pos.x, ctx.height, width, -(ctx.height -pos.y) + gap);
		ctx.noFill();
	}

	public boolean isOutOfBounds() {
		return pos.x < -width;
	}

	public boolean collides(Bird player) {
		return ((player.getPos().y - player.getHeight()/2) < pos.y || (player.getPos().y + player.getHeight()/2) > pos.y + gap) && 
			   ((player.getPos().x + player.getWidth()/2) > pos.x && (player.getPos().x - player.getWidth()/2) < pos.x + width);
	}

	public PVector getPos() {
		return pos;
	}

	public float getWidth() {
		return width;
	}
	
	public float getGap() {
		return gap;
	}
	
	public String getUuid() {
		return uuid;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Pipe) {
			Pipe other = (Pipe) obj;
			return this.uuid.equals(other.getUuid());
		}
		return false;
	}
	
}
