package com.siddy.JNeat.buildingBlocks;

import static com.siddy.JNeat.properties.NeatProperties.*;

import java.io.Serializable;

import com.siddy.JNeat.util.RandomUtil;

public class Connection implements Serializable{
	private static final long serialVersionUID = -3725887820143860868L;
	private long innovationNumber;
	private long inputNeuronId;
	private long outputNeuronId;
	private double weight;
	private double data;
	private boolean enabled;
	
	public Connection(long inputNeuronId, long outputNeuronId) {
		this.inputNeuronId = inputNeuronId;
		this.outputNeuronId = outputNeuronId;
		this.enabled = true;
		this.data = 0;
		this.weight = RandomUtil.getRandomNumberBetween(weightInitializationFromRange, weightInitializationToRange);
	}
	
	public Connection(long inputNeuronId, long outputNeuronId, double weight) {
		this.inputNeuronId = inputNeuronId;
		this.outputNeuronId = outputNeuronId;
		this.enabled = true;
		this.data = 0;
		this.weight = weight;
	}
	
	public Connection(Connection connectionToClone) {
		this.innovationNumber = connectionToClone.getInnovationNumber();
		this.inputNeuronId = connectionToClone.getInputNeuronId();
		this.outputNeuronId = connectionToClone.getOutputNeuronId();
		this.enabled = connectionToClone.isEnabled();
		this.data = 0;
		this.weight = connectionToClone.getWeight();
	}
	
	public long getInnovationNumber() {
		return innovationNumber;
	}
	public void setInnovationNumber(long innovationNumber) {
		this.innovationNumber = innovationNumber;
	}
	public long getInputNeuronId() {
		return inputNeuronId;
	}
	public void setInputNeuronId(long inputNeuronId) {
		this.inputNeuronId = inputNeuronId;
	}
	public long getOutputNeuronId() {
		return outputNeuronId;
	}
	public void setOutputNeuronId(long outputNeuronId) {
		this.outputNeuronId = outputNeuronId;
	}
	public double getWeight() {
		return weight;
	}
	public void setWeight(double weight) {
		this.weight = weight;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}	
	public void toggleEnabled() {
		this.enabled = !enabled;
	}
	public void disableConnection() {
		this.enabled = false;
	}
	public double getData() {
		return data;
	}

	public void feedData(double value) {
		this.data = weight * value;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Connection) {
			Connection other = (Connection) obj;
			return other.getInputNeuronId() == inputNeuronId 
					&& other.getOutputNeuronId() == outputNeuronId;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "Connection [innovationNumber=" + innovationNumber + ", inputNeuronId=" + inputNeuronId
				+ ", outputNeuronId=" + outputNeuronId + ", weight=" + weight + ", data=" + data + ", enabled="
				+ enabled + "]";
	}
}
