package edu.mit.spacenet.io.gson.scenario;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;

import edu.mit.spacenet.scenario.ScenarioType;

public class Scenario {
	public static final BiMap<String, ScenarioType> TYPE_MAP = new ImmutableBiMap.Builder<String, ScenarioType>()
			.put("ISS", ScenarioType.ISS)
			.put("Lunar", ScenarioType.LUNAR)
			.put("MoonOnly", ScenarioType.MOON_ONLY)
			.put("Martian", ScenarioType.MARTIAN)
			.put("MarsOnly", ScenarioType.MARS_ONLY)
			.put("SolarSystem", ScenarioType.SOLAR_SYSTEM)
			.build();
	
	public String name;
	public String description;
	public Date startDate;
	public String scenarioType;
	public List<Location> locations;
	public List<Mission> missions = new ArrayList<Mission>();
	public List<ResourceType> resources = new ArrayList<ResourceType>();
	public List<Element> elements = new ArrayList<Element>();
	
	public static Scenario createFrom(edu.mit.spacenet.scenario.Scenario scenario) {
		Scenario s = new Scenario();
		s.name = scenario.getName();
		s.description = scenario.getDescription();
		s.startDate = scenario.getStartDate();
		s.scenarioType = TYPE_MAP.inverse().get(scenario.getScenarioType());
		Context context = new Context();
		s.locations = Location.createFrom(scenario.getNetwork().getLocations(), context);
		s.missions = Mission.createFrom(scenario.getMissionList(), context);
		s.resources = ResourceType.createFrom(scenario.getDataSource().getResourceLibrary(), context);
		s.elements = Element.createFrom(scenario.getElements(), context);
		return s;
	}
	
	public edu.mit.spacenet.scenario.Scenario toSpaceNet() {
		edu.mit.spacenet.scenario.Scenario s = new edu.mit.spacenet.scenario.Scenario();
		s.setName(name);
		s.setDescription(description);
		s.setStartDate(startDate);
		s.setScenarioType(TYPE_MAP.get(scenarioType));
		Context context = new Context();
		// load nodes
		for(Location location : locations) {
			if(location instanceof Node) {
				s.getNetwork().add(location.toSpaceNet(context));
			}
		}
		// load edges (must be after nodes due to runtime dependency)
		for(Location location : locations) {
			if(location instanceof Edge) {
				s.getNetwork().add(location.toSpaceNet(context));
			}
		}
		// load resources
		for(ResourceType r : resources) {
			context.getId(r.id, r.toSpaceNet(context));
		}
		// load elements
		for(Element e : elements) {
			context.getId(e.id, e.toSpaceNet(context));
		}
		s.getMissionList().addAll(Mission.toSpaceNet(missions, s, context));
		return s;
	}
}
