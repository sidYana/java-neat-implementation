package com.siddy.examples.NetworkTest;

import java.util.ArrayList;
import java.util.List;

import com.siddy.JNeat.JNeat;
import com.siddy.JNeat.buildingBlocks.Connection;
import com.siddy.JNeat.buildingBlocks.Genome;
import com.siddy.JNeat.properties.NeatProperties;

import processing.core.PApplet;

public class NetworkTest extends PApplet{

	List<Genome> pop = new ArrayList<Genome>();
	int count = 0;
	
	public static void main(String[] args) {
		PApplet.main(NetworkTest.class);
	}

	public void settings() {
		size(600, 600);
	}
	
	public void setup() {
		NeatProperties.inputNodes = 6;
		NeatProperties.outputNodes = 2;
		NeatProperties.populationSize = 10;
		pop = JNeat.INSTANCE.getPopulation();
		
		for(int i = 0; i < 100; i++) {
			for(Genome g : pop) {
				g.mutate();
			}
		}
		
		for(Genome g : pop) {
			for(Connection conn1 : g.getConnections()) {
				int dup = 0;
				for(Connection conn2 : g.getConnections()) {
					if(conn1.equals(conn2)) {
						dup++;
					}
				}
				if(dup > 1) {
					System.out.println("invalid connections");
				}
			}
		}
	}
	
	public void draw() {
		background(210);
		pop.get(count).show(this);
	}
	
	public void keyReleased() {
		if(key == CODED) {
			if(keyCode == UP) {
				count++;
			} else if(keyCode == DOWN) {
				count--;
			}
			
			if(count > pop.size() - 1) {
				count = 0;
			} else if(count < 0) {
				count = pop.size() - 1;
			}
			System.out.println("index : " + count);
		}
	}
	
}
