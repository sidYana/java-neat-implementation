package com.siddy.JNeat;

import static com.siddy.JNeat.properties.NeatProperties.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.siddy.JNeat.buildingBlocks.Connection;
import com.siddy.JNeat.buildingBlocks.Genome;
import com.siddy.JNeat.buildingBlocks.Innovation;
import com.siddy.JNeat.buildingBlocks.Species;
import com.siddy.JNeat.generationData.GenerationData;
import com.siddy.JNeat.networkSaver.GenomeNetworkSaver;
import com.siddy.JNeat.util.RandomUtil;

public class JNeat {

	public static final JNeat INSTANCE = new JNeat();

	private String testName;
	private long speciesId = 0;
	private long innovationNumber = 1;
	private List<Innovation> innovations;
	private List<Species> speciesList;
	private long generation = 0;

	private long genomeCounter = 1;

	private final Object generationDataLock = new Object();
	private List<GenerationData> generationDataList = new ArrayList<GenerationData>();

	private double allTimeBestFitness;
	private Genome allTimeBestGenome;
	
	private JNeat() {
		innovations = new ArrayList<Innovation>();
		speciesList = new ArrayList<Species>();
	}

	public void assignInnovation(Connection connection) {
		innovations.stream()
		.filter(innovation -> innovation.matches(connection))
		.findFirst()
				.ifPresentOrElse(innovation -> {
					connection.setInnovationNumber(innovation.getId());
				}, () -> {
					innovations.add(new Innovation(innovationNumber, connection));
					connection.setInnovationNumber(innovationNumber);
					innovationNumber++;
				});
	}

	private List<Genome> generateInitialPoulation() {
		List<Genome> population = new ArrayList<>();
		for (int index = 0; index < populationSize; index++) {
			Genome genome = new Genome();
			genome.createInitialConnection();
			assignSpecies(genome);
			population.add(genome);
		}
		return population;
	}

	private void assignSpecies(Genome genome) {
		speciesList.stream()
		.filter(s -> s.belongs(genome))
		.findFirst()
		.ifPresentOrElse(species -> {
			species.addToSpecies(genome);
		}, () -> {
			speciesList.add(new Species(speciesId++, genome));
		});
	}

	public void breedNewSpecies() {
		saveBestGenome();
		cullSpecies();
		killBadSpecies();
		generateNewPopulation();
		removeEmptySpecies();
		generateCurrentGenerationData();
		generation++;
	}

	private void removeEmptySpecies() {
		for(ListIterator<Species> itr = speciesList.listIterator(); itr.hasNext();) {
			Species s = itr.next();
			
			if(s.getPopulation().isEmpty()) {
				itr.remove();
			}
		}
	}

	private void generateNewPopulation() {
		if(speciesList.isEmpty()) {
			innovations.clear();
			generateInitialPoulation();
		} else {
			speciesList.sort((s1, s2) -> Double.compare(s2.getAverageFitness(), s1.getAverageFitness()));
			
			double averageFitnessSum = getAverageFitnessSum();
			
			List<Genome> newPopulation = new ArrayList<Genome>();
			
			long amountLeft = populationSize - speciesList.stream().mapToLong(Species::getPopulationSize).sum();
			
			for(Species s : speciesList) {
				long amountToCreate = (long) Math.floor(s.getAverageFitness() / averageFitnessSum * populationSize);
				
				amountLeft -= amountToCreate;
				if (amountLeft < 0) {
					amountToCreate += amountLeft;
					amountLeft = 0;
				}
				newPopulation.addAll(s.createNewPopulation(amountToCreate));
				
				s.resetPopulationFitness();
			}
			
			if(amountLeft > 0) {
				newPopulation.addAll(speciesList.get(RandomUtil.getRandomNumberBetween(speciesList.size())).createNewPopulation(amountLeft));
			}
			
			for(Genome g : newPopulation) {
				assignSpecies(g);
			}
		}
	}

	private void killBadSpecies() {
		double averageFitnessSum = getAverageFitnessSum();
		
		for(ListIterator<Species> itr = speciesList.listIterator(); itr.hasNext();) {
			Species s = itr.next();
			
			if(s.getAverageFitness() / averageFitnessSum * populationSize < 1) {
				itr.remove();
			}
		}
	}
	
	private void saveBestGenome() {
		if(testName != null) {
			boolean newBestDetected = false;
			for(Species s : speciesList) {
				for(Genome g : s.getPopulation()) {
					if(g.getFitness() > allTimeBestFitness) {
						allTimeBestFitness = g.getFitness();
						allTimeBestGenome = g;
						newBestDetected = true;
					}
				}
			}
			if(newBestDetected) {
				GenomeNetworkSaver.saveGenome(testName , allTimeBestGenome);
			}
		}
	}

	private void generateCurrentGenerationData() {
		synchronized (generationDataLock) {
			generationDataList.add(new GenerationData() {
				{
					setGeneration(generation);
					setTotalSpecies(speciesList.size());
					setHighestFitness(speciesList.parallelStream().mapToDouble(species -> species.getHighestFitness())
							.max().getAsDouble());
					setAverageFitness(speciesList.parallelStream().mapToDouble(species -> species.getHighestFitness())
							.average().getAsDouble());
				}
			});
		}
	}

	public List<GenerationData> getGenerationDataList() {
		synchronized (generationDataLock) {
			return generationDataList;
		}
	}

	private void cullSpecies() {
		for(ListIterator<Species> itr = speciesList.listIterator(); itr.hasNext();) {
			Species s = itr.next();
			
			s.sortPopulation();
			s.removeWeakGenomes();
			s.setBest();
			s.performFitnessSharing();
			s.calculateAverageFitness();
			s.computeStaleness();
			
			if(s.isStale()) {
				itr.remove();
				continue;
			}
		}
	}

	private double getAverageFitnessSum() {
		double sum = 0;
		
		for(Species s : speciesList) {
			sum += s.getAverageFitness();
		}
		
		return sum;
	}

	public List<Species> getSpeciesList() {
		return speciesList;
	}

	public List<Genome> getPopulation() {
		if (generation == 0) {
			return generateInitialPoulation();
		}
		List<Genome> population = new ArrayList<>();
		for (Species species : speciesList) {
			population.addAll(species.getPopulation());
		}
		return population;
	}

	public long getNewGenomeId() {
		return genomeCounter++;
	}

	public double getHighestFitness() {
		return speciesList.stream().mapToDouble(Species::getHighestFitness).max().getAsDouble();
	}

	public Genome getBestPerformerBrain() {
		if (speciesList.isEmpty()) {
			return null;
		}
		List<Genome> bestPerformers = new ArrayList<Genome>();
		speciesList.parallelStream().map(Species::getPopulation).forEach(pop -> {
			bestPerformers.addAll(pop);
		});
		bestPerformers.sort((p1, p2) -> Double.compare(p2.getFitness(), p1.getFitness()));
		return bestPerformers.get(0);
	}
	
	public void setTestName(String testName) {
		DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss");  
		String formatDateTime = LocalDateTime.now().format(format);
        System.out.println("Starting test with timestamp" + formatDateTime);  
		this.testName = testName + "_" + formatDateTime;
	}
}
