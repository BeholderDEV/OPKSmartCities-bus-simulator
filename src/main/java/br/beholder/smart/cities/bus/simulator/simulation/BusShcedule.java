package br.beholder.smart.cities.bus.simulator.simulation;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BusShcedule {

	@JsonProperty("schedule_id")
	public String scheduleId;	
	public String departureTime;
}
