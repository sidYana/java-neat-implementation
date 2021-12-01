package com.siddy.JNeat.properties;

public class NeatProperties {

	public static long populationSize = 1000;
	public static long inputNodes = 10;
	public static long outputNodes = 10;
	public static double bias = 1;
	
	public static float weightInitializationFromRange = -2.5f;
	public static float weightInitializationToRange = 2.5f;
	
	// Mutation
	public static double linkWeightMutationRate = 0.2;
	public static double linkExpressedMutationRate = 0.1;
	public static double addlinkMutationRate = 0.05;
	public static double addNodeMutation = 0.01;
	
	public static double crossoverRate = 0.5;
	
	public static double populationRetentionRate = 0.2;
	
	public static int stalePoolLimit = 20;
	public static int staleSpeciesLimit = 20;
	
	public static boolean useRandomActivationFunctions = false;
	public static boolean isFullyConnected=true;
	
}
