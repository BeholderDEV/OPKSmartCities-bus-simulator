package br.beholder.smart.cities.bus.simulator.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.beholder.smart.cities.bus.simulator.simulation.Simulation;
import br.beholder.smart.cities.bus.simulator.simulation.Simulation.State;
import br.beholder.smart.cities.bus.simulator.simulation.Simulation.Status;

@RestController
@RequestMapping(path = "/simulation", produces = "application/json")
public class SimulationController {

	@Value("${google.maps.api.key}")
	private String mapsApiKey;
	
	private final Simulation simulation = new Simulation(mapsApiKey);

	@RequestMapping(method = RequestMethod.POST, path = "/start")
	public SimulationStatus start() {

		simulation.start();

		return state();
	}

	@RequestMapping(method = RequestMethod.POST, path = "/stop")
	public SimulationStatus stop() {

		simulation.stop();

		return state();
	}

	@RequestMapping(method = RequestMethod.GET, path = "/state")
	public SimulationStatus state() {

		String message = "";
		State state = simulation.getState();

		if (state.status == Status.RUNNING) {

			message = "Simulation started";

		} else if (state.status == Status.STOPPED) {

			message = "Simulation stopped";
		}

		return new SimulationStatus(message, simulation.getState());
	}

	public static class SimulationStatus {

		public String message;
		public State state;

		public SimulationStatus(String message, State state) {
			this.message = message;
			this.state = state;
		}
	}
}
