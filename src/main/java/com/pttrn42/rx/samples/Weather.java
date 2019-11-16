package com.pttrn42.rx.samples;

public class Weather {

	private final double temperature;

	public Weather(double temperature) {
		this.temperature = temperature;
	}

	public double getTemperature() {
		return temperature;
	}

	@Override
	public String toString() {
		return "Weather{temperature=" + temperature + '}';
	}
}
