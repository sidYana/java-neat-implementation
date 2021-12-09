package com.siddy.JNeat.buildingBlocks;

import static com.siddy.JNeat.activationFunction.ActivationFunction.*;
import static com.siddy.JNeat.buildingBlocks.NeuronType.*;
import static com.siddy.JNeat.properties.NeatProperties.bias;

import java.io.Serializable;

import com.siddy.JNeat.activationFunction.ActivationFunctionIF;

public class Neuron implements Serializable {
	private static final long serialVersionUID = -8718300621036222601L;
	private long id;
	private transient double value;
	private NeuronType type;
	private ActivationFunctionIF activationFunction;
	
	public Neuron(long id, NeuronType type) {
		this.id = id;
		this.type = type;
		if (isInput() || isBias()) {
			this.activationFunction = LINEAR;
		} else if (isHidden()) {
			this.activationFunction = TANH;
		} else {
			this.activationFunction = SIGMOID;
		}
	}

	private Neuron(Neuron neuronToClone) {
		this(neuronToClone.getId(), neuronToClone.getType());
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

//	public double getValue() {
//		return value;
//	}

	public NeuronType getType() {
		return type;
	}

	public void setType(NeuronType type) {
		this.type = type;
	}
	
	public boolean isInput() {
		return type == INPUT;
	}
	
	public boolean isBias() {
		return type == BIAS;
	}
	
	public boolean isHidden() {
		return type == HIDDEN;
	}
	
	public boolean isOutput() {
		return type == OUTPUT;
	}
	
	public void feedData(double value) {
		this.value = isBias() ? bias : activationFunction.applyActivation(value);
	}
	
	public double getData() {
		return isBias() ? bias : value;
	}

	public Neuron clone() {
		return new Neuron(this);
	}
	
	@Override
	public String toString() {
		return "Neuron [id=" + id + ", value=" + value + ", type=" + type + ", activationFunction=" + activationFunction
				+ "]";
	}
}
