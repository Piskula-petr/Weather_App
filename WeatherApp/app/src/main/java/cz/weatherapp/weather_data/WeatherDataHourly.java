package cz.weatherapp.weather_data;

import java.io.Serializable;
import java.util.Calendar;

public class WeatherDataHourly implements Serializable {
		
		private int weatherCode;
		private int temperature;
		private int precipitationProbability;
		private Calendar observationTime;
		
		
		/**
		 * Konstruktor
		 */
		public WeatherDataHourly() {
		
		}
		
// Settery //////////////////////////////////////////////////////////////////////////////////////////////////
		
		public void setWeatherCode(int weatherCode) {
				this.weatherCode = weatherCode;
		}
		
		public void setTemperature(int temperature) {
				this.temperature = temperature;
		}
		
		public void setPrecipitationProbability(int precipitationProbability) {
				this.precipitationProbability = precipitationProbability;
		}
		
		public void setObservationTime(Calendar observationTime) {
				this.observationTime = observationTime;
		}

// Gettery //////////////////////////////////////////////////////////////////////////////////////////////////
		
		public int getWeatherCode() {
				return weatherCode;
		}
		
		public int getTemperature() {
				return temperature;
		}
		
		public int getPrecipitationProbability() {
				return precipitationProbability;
		}
		
		public Calendar getObservationTime() {
				return observationTime;
		}
}
