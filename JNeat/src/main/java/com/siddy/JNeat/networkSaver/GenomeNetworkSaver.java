package com.siddy.JNeat.networkSaver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.siddy.JNeat.buildingBlocks.Genome;

public class GenomeNetworkSaver {

	private static final String TEST_REPORTS_DIR = "testReports";

	public static void saveGenome(String testName, Genome genome) {
		if (new File(TEST_REPORTS_DIR).isDirectory()) {
			File currentTestDirectory = new File(TEST_REPORTS_DIR + "/" + testName);
			if (!currentTestDirectory.exists()) {
				currentTestDirectory.mkdir();
			}

			DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss");
			String formatDateTime = LocalDateTime.now().format(format);

			String filePath = TEST_REPORTS_DIR + "/" + testName + "/" + "Genome_" + formatDateTime + ".bin";
			
			File genomeFile = new File(filePath);

			try (FileOutputStream fos = new FileOutputStream(genomeFile);
					ObjectOutputStream oos = new ObjectOutputStream(fos)) {
				oos.writeObject(genome);
				oos.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
