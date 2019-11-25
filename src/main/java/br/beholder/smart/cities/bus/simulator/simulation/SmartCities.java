package br.beholder.smart.cities.bus.simulator.simulation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class SmartCities {

	private static final Map<Class<? extends Object>, String> ENDPOINTS = new HashMap<>();

	private static final String HEROKU_PROXY = "https://cors-anywhere.herokuapp.com";

	private static final String API_BASE = "opksmartbusao.herokuapp.com/api/";

	private static final String APP_NAME = "OPKSmartCities-Bus-Simulator";

	static {

		ENDPOINTS.put(Bus.class, "buses");
		ENDPOINTS.put(Track.class, "tracks");
	}

	private static String mountUrl(String endPoint) throws Exception {

		return HEROKU_PROXY + "/" + API_BASE + "/" + endPoint;
	}

	private static String mountUrl(Class<? extends Object> resourceClass) throws Exception {

		return HEROKU_PROXY + "/" + API_BASE + "/" + ENDPOINTS.get(resourceClass);
	}

	public static void addPassengers(Bus bus, int count) throws Exception {
		Request.Put(mountUrl("buses/addPassenger/" + bus.chassi + "/" + count)).addHeader("X-Requested-With", APP_NAME)
				.execute().discardContent();
	}

	public static void removePassengers(Bus bus, int count) throws Exception {
		Request.Put(mountUrl("buses/removePassenger/" + bus.chassi + "/" + count))
				.addHeader("X-Requested-With", APP_NAME).execute().discardContent();
	}

	public static void updateWaypoint(Bus bus) throws Exception {

		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(bus.position);

		Request.Put(mountUrl("buses/updateWaypoint/" + bus.chassi)).addHeader("X-Requested-With", APP_NAME)
				.bodyString(json, ContentType.APPLICATION_JSON).execute().discardContent();
	}

	public static void resetBuses() throws Exception {
		Request.Put(mountUrl("buses/resetAll")).addHeader("X-Requested-With", APP_NAME).execute().discardContent();
	}

	public static void update(Object resource) throws Exception {

		Field idField = resource.getClass().getField("id");
		String id = (String) idField.get(resource);

		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(resource);

		Request.Put(mountUrl(resource.getClass()) + "/" + id).addHeader("X-Requested-With", APP_NAME)
				.bodyString(json, ContentType.APPLICATION_JSON).execute().discardContent();
	}

	public static <T> List<T> list(Class<? extends T> resourceClass) throws Exception {

		String result = Request.Get(mountUrl(resourceClass)).addHeader("X-Requested-With", APP_NAME).execute()
				.returnContent().asString();

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		return objectMapper.readValue(result,
				TypeFactory.defaultInstance().constructCollectionType(ArrayList.class, resourceClass));
	}
}
