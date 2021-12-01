package com.siddy.JNeat.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.siddy.JNeat.buildingBlocks.TopologicalData;

public class GraphUtility {

	public static boolean isCyclic(Graph graph) {
		Map<Long, List<Long>> edges = graph.getEdges();
		
		Map<Long, Boolean> visited = new HashMap<>();
		Map<Long, Boolean> recStack = new HashMap<>();
		
		for(long id : edges.keySet()) {
			visited.put(id, false);
			recStack.put(id, false);
		}

		for (long node : edges.keySet())
			if (isCyclic(node, visited, recStack, edges))
				return true;

		return false;
	}

	private static boolean isCyclic(long nodeId, Map<Long, Boolean> visited, Map<Long, Boolean> recStack, Map<Long, List<Long>> edges) {
		if (recStack.containsKey(nodeId) && recStack.get(nodeId))
			return true;

		if (visited.containsKey(nodeId) && visited.get(nodeId))
			return false;

		visited.put(nodeId, true);
		recStack.put(nodeId, true);
		
		List<Long> children = (edges.get(nodeId) == null) ? new LinkedList<>() : edges.get(nodeId);

		for (Long child : children)
			if (isCyclic(child, visited, recStack, edges))
				return true;

		recStack.put(nodeId,  false);

		return false;
	}
	
	public static TopologicalData computeTopologicalData(Graph graph) {
		Map<Long, List<Long>> edges = graph.getEdges();
		Map<Long, Integer> indegrees = new HashMap<>();
		
		for(long inputNodeId : edges.keySet()) {
			indegrees.put(inputNodeId, indegrees.getOrDefault(inputNodeId, 0));
			for(long outputNodeId : edges.get(inputNodeId)) {
				indegrees.put(outputNodeId, indegrees.getOrDefault(outputNodeId, 0) + 1);
			}
		}
		
		Queue<Long> q = new LinkedList<>();
		int layoutIndex = 0;
		List<List<Long>> layout = new ArrayList<>();
		layout.add(new ArrayList<>());
		for(long inputNodeId : edges.keySet()) {
			if(indegrees.get(inputNodeId) == 0) {
				q.add(inputNodeId);
				layout.get(layoutIndex).add(inputNodeId);
			}
		}
		layoutIndex++;
		long inputNodeId = -1;
		List<Long> topologicalOrder = new ArrayList<Long>();
		while(q.peek()!=null) {
			inputNodeId = q.poll();
			topologicalOrder.add(inputNodeId);
			if(edges.get(inputNodeId)!=null) {
				layout.add(new ArrayList<>());
				for(long outNodeId : edges.get(inputNodeId)) {
					indegrees.put(outNodeId, indegrees.getOrDefault(outNodeId, 0) - 1);
					if(indegrees.get(outNodeId) == 0) {
						q.add(outNodeId);
						layout.get(layoutIndex).add(outNodeId);
					}
				}
				if(layout.get(layoutIndex).isEmpty()) {
					layout.remove(layoutIndex);
				}else {
					layoutIndex++;
				}
			}
		}
		
		return new TopologicalData(topologicalOrder, layout);
	}
	
}
