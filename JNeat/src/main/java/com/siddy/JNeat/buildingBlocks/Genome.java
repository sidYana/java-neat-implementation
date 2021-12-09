package com.siddy.JNeat.buildingBlocks;

import static com.siddy.JNeat.buildingBlocks.NeuronType.BIAS;
import static com.siddy.JNeat.buildingBlocks.NeuronType.HIDDEN;
import static com.siddy.JNeat.buildingBlocks.NeuronType.INPUT;
import static com.siddy.JNeat.buildingBlocks.NeuronType.OUTPUT;
import static com.siddy.JNeat.graph.GraphUtility.computeTopologicalData;
import static com.siddy.JNeat.graph.GraphUtility.isCyclic;
import static com.siddy.JNeat.properties.NeatProperties.addNodeMutation;
import static com.siddy.JNeat.properties.NeatProperties.addlinkMutationRate;
import static com.siddy.JNeat.properties.NeatProperties.crossoverRate;
import static com.siddy.JNeat.properties.NeatProperties.inputNodes;
import static com.siddy.JNeat.properties.NeatProperties.isFullyConnected;
import static com.siddy.JNeat.properties.NeatProperties.linkExpressedMutationRate;
import static com.siddy.JNeat.properties.NeatProperties.linkWeightMutationRate;
import static com.siddy.JNeat.properties.NeatProperties.outputNodes;
import static com.siddy.JNeat.util.RandomUtil.getRandomFloat;
import static com.siddy.JNeat.util.RandomUtil.getRandomNumberBetween;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.siddy.JNeat.JNeat;
import com.siddy.JNeat.graph.Graph;

import processing.core.PApplet;
import processing.core.PVector;

public class Genome implements Serializable{

	private static final long serialVersionUID = 7483014397828399031L;
	
	private transient long id;
	private Map<Long, Neuron> neurons;
	private List<Connection> connections;
	
	private transient TopologicalData topologicalData;
	private transient double fitness;

	public Genome() {
		this.id = JNeat.INSTANCE.getNewGenomeId();
		neurons = new HashMap<Long, Neuron>();
		connections = new ArrayList<Connection>();

		initialize();
	}

	private Genome(Genome genome) {
		this.id = JNeat.INSTANCE.getNewGenomeId();
		
		neurons = genome.getNeurons()
				.values()
				.stream()
				.collect(Collectors.toMap(Neuron::getId, Neuron::clone));
		
		connections = genome.getConnections()
				.stream()
				.map(Connection::clone)
				.collect(Collectors.toList());
	}
	
	private void initialize() {
		long index = 0;

		for (; index < inputNodes; index++) {
			Neuron neuron = new Neuron(index, INPUT);
			neurons.put(index, neuron);
		}

		neurons.put(index, new Neuron(index, BIAS));

		index++;
		
		for (; index < inputNodes + 1 + outputNodes; index++) {
			Neuron neuron = new Neuron(index, OUTPUT);
			neurons.put(index, neuron);
		}
	}

	public void createInitialConnection() {
		List<Neuron> inputNeuronPool = neurons.values()
				.stream()
				.filter(neuron -> neuron.isInput() || neuron.isBias())
				.collect(Collectors.toList());
		
		List<Neuron> outputNeuronPool = neurons.values()
				.stream()
				.filter(neuron -> neuron.isOutput())
				.collect(Collectors.toList());
		
		if(isFullyConnected) {
			
			for(Neuron inputNeuron : inputNeuronPool) {
				for(Neuron outputNeuron : outputNeuronPool) {
					Connection newConnection = new Connection(inputNeuron.getId(), outputNeuron.getId());
					
					JNeat.INSTANCE.assignInnovation(newConnection);
					
					connections.add(newConnection);
				}
			}
			
		} else {
			Neuron inputNeuron = inputNeuronPool.get(getRandomNumberBetween(inputNeuronPool.size()));
			Neuron outputNeuron = outputNeuronPool.get(getRandomNumberBetween(outputNeuronPool.size()));
			
			Connection newConnection = new Connection(inputNeuron.getId(), outputNeuron.getId());
			
			JNeat.INSTANCE.assignInnovation(newConnection);
			
			connections.add(newConnection);
		}
			
		computeTopologicalOrder();
	}
	
	public double[] evaluate(double[] input) {
		List<Long> topologicalOrder = topologicalData.getTopologicalOrder();
		
		for(int inputIndex = 0; inputIndex < inputNodes; inputIndex++) {
			neurons.get((long)inputIndex).feedData(input[inputIndex]);
		}
		
		topologicalOrder.stream()
		.map(neuronIdToProcess -> (Neuron) neurons.get(neuronIdToProcess))
		.forEach(neuron -> {
			if(neuron.isOutput() || neuron.isHidden()) {
				// Process incoming connections (Hidden and Output as only these have incoming connections)
				double sum = connections.stream()
						.filter(connection -> connection.isEnabled() && (connection.getOutputNeuronId() == neuron.getId()))
						.mapToDouble(Connection::getData)
						.sum();
				
				neuron.feedData(sum);
			}

			// Feed outgoing connections (Input and Hidden)
			connections.stream()
			.filter(connection -> connection.isEnabled() && (connection.getInputNeuronId() == neuron.getId()))
			.forEach(connection -> connection.feedData(neuron.getData()));
		});
		
		double[] output = new double[(int) outputNodes];
		int outputIndex = 0;
		for(long outputNodeIndex = inputNodes + 1; outputNodeIndex < inputNodes + outputNodes + 1; outputNodeIndex++) {
			output[outputIndex++] = neurons.get(outputNodeIndex).getData();
		}
		
		return output;
	}

	public void mutate() {
		applyAddNodeMutation();
		applyAddLinkMutation();
		applyLinkWeightMutation();
		applyLinkExpressedMutation();
		computeTopologicalOrder();
	}

	private void computeTopologicalOrder() {
		Graph graph = new Graph();
		
		connections.stream().forEach(connection -> {
			graph.addEdge(connection.getInputNeuronId(), connection.getOutputNeuronId());
		});
		
		topologicalData = computeTopologicalData(graph);
	}

	private void applyLinkExpressedMutation() {
		connections.stream()
		.filter(connection -> linkExpressedMutationRate > getRandomFloat())
		.forEach(connection -> {
			connection.toggleEnabled();
		});
	}

	private void applyLinkWeightMutation() {
		connections.stream()
		.forEach(connection -> {
			if (linkWeightMutationRate > getRandomFloat()) {
				connection.setWeight(connection.getWeight() + (getRandomNumberBetween(-0.1f, 0.1f)));
			}
		});
	}

	private void applyAddLinkMutation() {
		if(addlinkMutationRate > getRandomFloat()) {
			List<Neuron> inputNeuronPool = neurons.values()
					.stream()
					.filter(neuron -> !neuron.isOutput())
					.collect(Collectors.toList());
			
			List<Neuron> outputNeuronPool = neurons.values()
					.stream()
					.filter(neuron -> neuron.isHidden() || neuron.isOutput())
					.collect(Collectors.toList());
			
			int retry = 0;
			while(retry < 10) {
				Neuron inputNeuron = inputNeuronPool.get(getRandomNumberBetween(inputNeuronPool.size()));
				Neuron outputNeuron = outputNeuronPool.get(getRandomNumberBetween(outputNeuronPool.size()));
				
				if(inputNeuron.getId() == outputNeuron.getId() 
						|| isPreExistingConnection(inputNeuron.getId(), outputNeuron.getId())
						|| createsCyclic(inputNeuron.getId(), outputNeuron.getId())) {
					retry++;
					continue;
				}
				
				Connection newConnection = new Connection(inputNeuron.getId(), outputNeuron.getId());
				
				JNeat.INSTANCE.assignInnovation(newConnection);
				
				connections.add(newConnection);
				
				break;
			}
		}
	}

	private void applyAddNodeMutation() {
		if(addNodeMutation > getRandomFloat()) {
			List<Connection> enabledConnections = connections.stream().filter(Connection::isEnabled).collect(Collectors.toList());
			
			if(enabledConnections.isEmpty()) {
				return;
			}
			
			Connection connection = enabledConnections.get(getRandomNumberBetween(enabledConnections.size()));
			
			Neuron newNeuron = new Neuron(getNewNeuronId(), HIDDEN);
			Connection newConnection1 = new Connection(connection.getInputNeuronId(), newNeuron.getId(), 1.0);
			Connection newConnection2 = new Connection(newNeuron.getId(), connection.getOutputNeuronId(), connection.getWeight());
			
			neurons.put(newNeuron.getId(), newNeuron);
			JNeat.INSTANCE.assignInnovation(newConnection1);
			JNeat.INSTANCE.assignInnovation(newConnection2);
			
			// As we are fetching enabled connections only, this will disable the connection
			connection.disableConnection();
			connections.add(newConnection1);
			connections.add(newConnection2);
		}
	}

	private boolean isPreExistingConnection(long inputNeuronId, long outputNeuronId) {
		return connections.stream()
				.filter(connection -> connection.getInputNeuronId() == inputNeuronId && connection.getOutputNeuronId() == outputNeuronId)
				.findFirst()
				.isPresent();
	}
	
	private boolean createsCyclic(long inputNeuronId, long outputNeuronId) {
		Graph graph = new Graph();
		
		connections.stream().forEach(connection -> {
			graph.addEdge(connection.getInputNeuronId(), connection.getOutputNeuronId());
		});
		
		graph.addEdge(inputNeuronId, outputNeuronId);
		
		return isCyclic(graph);
	}
	
	private long getNewNeuronId() {
		List<Long> nodeIds = neurons.keySet().stream().sorted().collect(Collectors.toList());		
		return nodeIds.get(nodeIds.size() - 1) + 1;
	}

	public List<Connection> getConnections() {
		return connections;
	}
	
	public Map<Long, Neuron> getNeurons() {
		return neurons;
	}
	
	// Assuming the current genome is most fit and other is second
	public Genome crossover(Genome other) {
		Genome child = clone();
		
		for(Connection parent1Connection : this.getConnections()) {
			for(Connection parent2Connection : other.getConnections()) {
				if(parent1Connection.getInnovationNumber() == parent2Connection.getInnovationNumber()) {
					if(crossoverRate > getRandomFloat()) {
						child.updateConnection(parent2Connection);
					}
					break;
				}
			}
		}
		
		return child;
	}

	public void updateConnection(Connection updatedConnection) {
		connections.stream()
		.filter(connection -> connection.getInnovationNumber() == updatedConnection.getInnovationNumber())
		.findFirst()
		.ifPresent(connection -> {
			connection.setEnabled(updatedConnection.isEnabled());
			connection.setWeight(updatedConnection.getWeight());
		});
	}

	public Long getId() {
		return id;
	}

	public double getFitness() {
		return fitness;
	}
	
	public void setFitness(double fitness) {
		this.fitness = fitness;
	}

	@Override
	public String toString() {
		return "Genome [id=" + id + "\n, neurons=" + neurons + "\n, connections=" + connections + "\n, topologicalData="
				+ topologicalData + "\n, fitness=" + fitness + "]";
	}

	public List<List<Long>> getLayout() {
		return topologicalData.getLayout();
	}

	public void show(PApplet ctx) {
		List<List<Long>> layers = topologicalData.getLayout();
	
		Map<Long, PVector> nodePos = new HashMap<Long, PVector>();
		
		float xRes = ctx.width / (layers.size() + 1);
		float x = xRes;
		for(List<Long> layer : layers) {
			float yRes = ctx.height / (layer.size() + 1);
			float y = yRes;
			for(long nodeId : layer) {
				nodePos.put(nodeId, new PVector(x, y));
				
				Neuron neuron = neurons.get(nodeId);
				
				if(neuron.isInput()) {
					ctx.fill(255, 0 , 0);
				}
				
				if(neuron.isBias()) {
					ctx.fill(255, 255, 0);
				}
				
				if(neuron.isHidden()) {
					ctx.fill(0, 255, 0);
				}
				
				if(neuron.isOutput()) {
					ctx.fill(0,0,255);
				}
				
				ctx.ellipse(x, y, 20, 20);
				ctx.fill(0);
				ctx.text(nodeId, x - 20, y - 20);
				ctx.noFill();
				y+=yRes;
			}
			x+=xRes;
		}
		
		connections.forEach(connection -> {
			if(connection.isEnabled()) {
				if(connection.getWeight() >= 0) {
					ctx.stroke(255, 0, 0);
				} else {
					ctx.stroke(0,255,0);
				}
			} else {
				ctx.stroke(128, 128, 128);
			}
			PVector inputNeuronPos = nodePos.get(connection.getInputNeuronId());
			PVector outputNeuronPos = nodePos.get(connection.getOutputNeuronId());
			ctx.line(inputNeuronPos.x, inputNeuronPos.y, outputNeuronPos.x, outputNeuronPos.y);
			ctx.pushMatrix();
			ctx.translate(outputNeuronPos.x, outputNeuronPos.y);
			float a = PApplet.atan2(inputNeuronPos.x - outputNeuronPos.x, outputNeuronPos.y - inputNeuronPos.y);
			ctx.rotate(a);
			ctx.line(0, 0, -10, -10);
			ctx.line(0, 0, 10, -10);
			ctx.popMatrix();
		});
		ctx.stroke(0);
	}
	
	public Genome clone() {
		return new Genome(this);
	}
}
