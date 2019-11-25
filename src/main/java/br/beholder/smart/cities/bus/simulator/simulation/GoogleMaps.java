package br.beholder.smart.cities.bus.simulator.simulation;

import java.util.List;

import org.apache.http.client.fluent.Request;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.beholder.smart.cities.bus.simulator.simulation.GoogleMaps.MapsData.Route.Leg;

public class GoogleMaps {

	public static GeolocationData calculateDistance(String apiKey, Waypoint origin, Waypoint destination)
			throws Exception {

		String originParam = "origin=" + origin.latitude + "," + origin.longitude;
		String destinationParam = "destination=" + destination.latitude + "," + destination.longitude;
		String finalUrl = "https://maps.googleapis.com/maps/api/directions/json?" + originParam + "&" + destinationParam
				+ "&key=" + apiKey;

		String result = Request.Get(finalUrl).execute().returnContent().asString();

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		MapsData mapsData = objectMapper.readValue(result, MapsData.class);
		Leg mainLeg = mapsData.routes.get(0).legs.get(0);

		return new GeolocationData(mainLeg.distance.value, mainLeg.duration.value);
	}

	public static class GeolocationData {
		public int distanceInMeters;
		public int durationInSeconds;

		public GeolocationData(int distanceInMeters, int durationInSeconds) {
			this.distanceInMeters = distanceInMeters;
			this.durationInSeconds = durationInSeconds;
		}
	}

	public static class MapsData {
		public List<Route> routes;

		public static class Route {
			public List<Leg> legs;

			public static class Leg {
				public Distance distance;
				public Duration duration;

				public static class Distance {
					public int value;
				}

				public static class Duration {
					public int value;
				}
			}
		}
	}

}
