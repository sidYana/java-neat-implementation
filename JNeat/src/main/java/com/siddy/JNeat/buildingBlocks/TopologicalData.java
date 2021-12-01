package com.siddy.JNeat.buildingBlocks;

import java.util.ArrayList;
import java.util.List;

public class TopologicalData {

	private List<Long> topologicalOrder;
	private List<List<Long>> layout = new ArrayList<>();
	
	public TopologicalData(List<Long> topologicalOrder, List<List<Long>> layout) {
		this.topologicalOrder = topologicalOrder;
		this.layout = layout;
	}

	public List<Long> getTopologicalOrder() {
		return topologicalOrder;
	}

	public void setTopologicalOrder(List<Long> topologicalOrder) {
		this.topologicalOrder = topologicalOrder;
	}

	public List<List<Long>> getLayout() {
		return layout;
	}

	public void setLayout(List<List<Long>> layout) {
		this.layout = layout;
	}

} 
