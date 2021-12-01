package com.siddy.JNeat.activationFunction;

public interface ActivationFunctionIF {
	public double applyActivation(double input);
	public double applyActivationDerivative(double input);
	public String getName();
}
