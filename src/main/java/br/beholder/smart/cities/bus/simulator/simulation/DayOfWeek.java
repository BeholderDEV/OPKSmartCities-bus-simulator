package br.beholder.smart.cities.bus.simulator.simulation;

import java.util.Calendar;

public enum DayOfWeek {
	SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY;

	public static DayOfWeek today() {

		int index = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;

		return DayOfWeek.values()[index];
	}

}
