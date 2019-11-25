package br.beholder.smart.cities.bus.simulator.simulation;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Track {

	@JsonProperty("_id")
	public String id;
	public String name;
	public Long number;
	public List<TrackSchedule> schedules = new ArrayList<>();
}
