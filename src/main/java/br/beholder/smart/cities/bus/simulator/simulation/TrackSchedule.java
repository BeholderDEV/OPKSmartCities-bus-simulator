package br.beholder.smart.cities.bus.simulator.simulation;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TrackSchedule {

	@JsonProperty("_id")
	public String id;
	public String title;
	public List<DayOfWeek> daysOfWeek = new ArrayList<>();
	public boolean hollidays;
	public List<String> departureTimes = new ArrayList<>();
	public List<Waypoint> waypoints = new ArrayList<>();
	public List<HotPlace> hotPlaces = new ArrayList<>();
	
	@JsonIgnore
	public Track track;
}
