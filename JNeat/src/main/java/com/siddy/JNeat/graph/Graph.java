package com.siddy.JNeat.graph;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Graph {
	private Map<Long, List<Long>> edges;
	
	public Graph() {
		this.edges = new HashMap<Long, List<Long>>();
	}

	public void addEdge(long in, long out) {
		if(!edges.containsKey(in)) {
			edges.put(in, new LinkedList<>());
		}
		edges.get(in).add(out);
	}
	
	public Map<Long, List<Long>> getEdges() {
		return edges;
	}
}
