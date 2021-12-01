package com.siddy.JNeat.buildingBlocks;

public class Innovation {

	private long id;
	private long inputNeuronId;
	private long outputNeuronId;
	
	public Innovation(long innovationNumber, Connection connection) {
		this.id = innovationNumber;
		this.inputNeuronId = connection.getInputNeuronId();
		this.outputNeuronId = connection.getOutputNeuronId();
	}

	public boolean matches(Connection connection) {
		return connection.getInputNeuronId() == inputNeuronId 
				&& connection.getOutputNeuronId() == outputNeuronId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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
	
}
