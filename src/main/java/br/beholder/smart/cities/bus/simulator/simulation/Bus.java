package br.beholder.smart.cities.bus.simulator.simulation;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Bus {

	@JsonProperty("_id")
	public String id;
	public Long number;
	public int seats;
	public int totalCapacity;
	public int passengersNum;
	public Waypoint position;
	public String chassi;
	public BusShcedule schedule;

}
