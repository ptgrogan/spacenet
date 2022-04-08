package edu.mit.spacenet.io.gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NodeDemand {
	public double time;
	public Location location;
	public List<Demand> demands = new ArrayList<Demand>();
	
	public static List<NodeDemand> createFrom(Map<edu.mit.spacenet.scenario.SupplyEdge.SupplyPoint, edu.mit.spacenet.domain.resource.DemandSet> demands) {
		List<NodeDemand> ds = new ArrayList<NodeDemand>();
		for(edu.mit.spacenet.scenario.SupplyEdge.SupplyPoint point : demands.keySet()) {
			if(demands.get(point).getTotalMass() > 0) { // TODO does not consider zero mass resources
				NodeDemand d = new NodeDemand();
				d.time = point.getTime();
				d.location = Location.createFrom(point.getNode());
				d.demands = Demand.createFrom(demands.get(point));
				ds.add(d);
			}
		}
		return ds;
	}
}