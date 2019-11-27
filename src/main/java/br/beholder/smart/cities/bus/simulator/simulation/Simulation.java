package br.beholder.smart.cities.bus.simulator.simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import br.beholder.smart.cities.bus.simulator.simulation.GoogleMaps.GeolocationData;

public class Simulation {

	private ExecutorService executor = Executors.newCachedThreadPool();

	private Future<Void> simulationTask = null;

	private String mapsApiKey;

	public Simulation(String mapsApiKey) {
		this.mapsApiKey = mapsApiKey;
	}

	public synchronized void start() {

		if (simulationTask != null) {
			return;
		}

		simulationTask = executor.submit(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				try {
					execute();
				} catch (Exception e) {
					simulationTask = null;
					e.printStackTrace(System.err);
					throw e;
				}

				return null;
			}
		});
	}

	public synchronized void stop() {

		if (simulationTask == null) {
			return;
		}

		simulationTask.cancel(true);
		simulationTask = null;

		try {
			SmartCities.resetBuses();
		} catch (Exception e) {

		}
	}

	public State getState() {

		State state = new State();

		state.status = (simulationTask == null) ? Status.STOPPED : Status.RUNNING;

		return state;
	}

	private void execute() throws Exception {

		SmartCities.resetBuses();

		List<Bus> buses = SmartCities.list(Bus.class);
		List<Track> tracks = SmartCities.list(Track.class);
		List<TrackSchedule> availableSchedules = findAvailableSchedulesForToday(tracks);
		List<BusAssignment> assignments = assignSchedulesToBuses(availableSchedules, buses);

		while (true) {
			System.out.println("Simulating...");

			for (BusAssignment assignment : assignments) {
				if (assignment.bus.number.equals(342L) || assignment.bus.number.equals(352L) || assignment.bus.number.equals(371L)) {
					updateBusState(assignment);
				}

			}

			Thread.sleep(1000);
		}
	}

	private void updateBusState(BusAssignment busAssignment) throws Exception {

//		if (System.currentTimeMillis() < busAssignment.nextUpdate) {
//			System.out.println("Skipping, time remaining: " + (busAssignment.nextUpdate - System.currentTimeMillis()));
//			return;
//		}

		int passengerAmount = 20;

		Random random = new Random(System.currentTimeMillis());
		Bus bus = busAssignment.bus;

		Waypoint previousWaypoint = bus.position;

		busAssignment.currentWaypointIndex = (busAssignment.currentWaypointIndex + 1)
				% busAssignment.schedule.waypoints.size();
		bus.position = busAssignment.schedule.waypoints.get(busAssignment.currentWaypointIndex);

		int previousLotation = bus.passengersNum;
		int passengersIn = 0;
		int passengersOut = random.nextInt(Math.min(passengerAmount, bus.passengersNum + 1));

		bus.passengersNum = bus.passengersNum - passengersOut;

		if (bus.passengersNum < bus.totalCapacity) {

			int spaceAvailable = bus.totalCapacity - bus.passengersNum;

			passengersIn = random.nextInt(Math.min(passengerAmount, spaceAvailable + 1));
			bus.passengersNum = bus.passengersNum + passengersIn;
		}

		SmartCities.updateWaypoint(bus);
		SmartCities.addPassengers(bus, passengersIn);
		SmartCities.removePassengers(bus, passengersOut);

//		int nextWayPointIndex = (busAssignment.currentWaypointIndex + 1) % busAssignment.schedule.waypoints.size();
//		Waypoint nextWaypoint = busAssignment.schedule.waypoints.get(nextWayPointIndex);
//
//		try
//		{
//			GeolocationData geolocationData = GoogleMaps.calculateDistance(mapsApiKey, bus.position, nextWaypoint);
//	
//			busAssignment.nextUpdate = System.currentTimeMillis() + (geolocationData.durationInSeconds * 1000L);
////			busAssignment.nextUpdate = busAssignment.nextUpdate + random.nextInt(61000);
//		}
//		catch (Exception e) {
//			busAssignment.nextUpdate = System.currentTimeMillis() + random.nextInt(10000);
//		}

//			System.out.println("Bus #" + bus.number);
//			System.out.println("Passengers out: " + passengersOut);
//			System.out.println("Passengers in: " + passengersIn);
//			System.out.println("Previous lotation: " + previousLotation);
//			System.out.println("Current lotation: " + bus.passengersNum);
//			System.out.println("Previous position: " + previousWaypoint.latitude + "," + previousWaypoint.longitude);
//			System.out.println("Current position: " + bus.position.latitude + "," + bus.position.longitude);
	}

	private List<BusAssignment> assignSchedulesToBuses(List<TrackSchedule> availableSchedules, List<Bus> buses)
			throws Exception {

		Random random = new Random(System.currentTimeMillis());
		List<String> assignedIds = new ArrayList<String>();
		List<BusAssignment> assignments = new ArrayList<>();

		for (Bus bus : buses) {

			if (bus.number.equals(342L) || bus.number.equals(352L) || bus.number.equals(371L)) {
				System.out.println("Assigning buses...");

				while (true) {
					TrackSchedule schedule = availableSchedules.get(random.nextInt(availableSchedules.size()));
					int departureIndex = random.nextInt(schedule.departureTimes.size());

					String departureTime = schedule.departureTimes.get(departureIndex);
					String id = schedule.id + departureTime;

					if (!assignedIds.contains(id)) {

						int waypointIndex = random.nextInt(schedule.waypoints.size());

						BusAssignment assignment = new BusAssignment();

						bus.schedule = new BusShcedule();
						bus.schedule.departureTime = departureTime;
						bus.schedule.scheduleId = schedule.id;
						bus.position = schedule.waypoints.get(waypointIndex);

						assignment.bus = bus;
						assignment.schedule = schedule;
						assignment.currentWaypointIndex = waypointIndex;
						assignment.nextUpdate = 0L;

						assignedIds.add(id);
						assignments.add(assignment);

						SmartCities.update(assignment.bus);

						break;
					}
				}
			}
		}

		return assignments;
	}

	private List<TrackSchedule> findAvailableSchedulesForToday(List<Track> tracks) {

		DayOfWeek dayOfWeek = DayOfWeek.today();
		List<TrackSchedule> schedules = new ArrayList<>();

		for (Track track : tracks) {

			for (TrackSchedule schedule : track.schedules) {

				if (schedule.daysOfWeek.contains(dayOfWeek)) {
					schedule.track = track;
					schedules.add(schedule);
				}
			}
		}

		return schedules;
	}

	public static class State {

		public Status status;

	}

	public enum Status {
		RUNNING, STOPPED
	}

	public static void main(String[] args) throws Exception {

		String mapsKey = System.getenv("MAPS_API_KEY");

		Simulation simulation = new Simulation(mapsKey);
		simulation.execute();
	}
}
