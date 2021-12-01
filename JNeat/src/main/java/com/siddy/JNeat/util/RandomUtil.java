package com.siddy.JNeat.util;

import java.util.Random;

public class RandomUtil {

	private static Random random = new Random();

	public static int getRandomNumberBetween(int min, int max) {
		return random.nextInt((max - min) + 1) + min;
	}

	public static int getRandomNumberBetween(int max) {
		return random.nextInt(max);
	}

	public static float getRandomNumberBetween(float max) {
		return max * random.nextFloat();
	}

	public static float getRandomNumberBetween(float min, float max) {
		return min + random.nextFloat() * (max - min);
	}

	public static float getRandomFloat() {
		return random.nextFloat();
	}
	
	public static int getRandomInt() {
		return random.nextInt();
	}
	
}
