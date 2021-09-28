package cz.weatherapp.weather_data;

import java.io.Serializable;
import java.util.Calendar;

public class WeatherDataDaily implements Serializable {
		
		private int weatherCode;
		private int temperatureMin;
		private int temperatureMax;
		private int precipitationProbability;
		private Calendar observationTime;
		
		
		/**
		 * Konstruktor
		 */
		public WeatherDataDaily() {
		
		}
		
// Settery ///////////////////////////////////////////////////////////////////////////////////////////////////
		
		public void setWeatherCode(int weatherCode) {
				this.weatherCode = weatherCode;
		}
		
		public void setTemperatureMin(int temperatureMin) {
				this.temperatureMin = temperatureMin;
		}
		
		public void setTemperatureMax(int temperatureMax) {
				this.temperatureMax = temperatureMax;
		}
		
		public void setPrecipitationProbability(int precipitationProbability) {
				this.precipitationProbability = precipitationProbability;
		}
		
		public void setObservationTime(Calendar observationTime) {
				this.observationTime = observationTime;
		}

// Gettery ///////////////////////////////////////////////////////////////////////////////////////////////////
		
		public int getWeatherCode() {
				return weatherCode;
		}
		
		public int getTemperatureMin() {
				return temperatureMin;
		}
		
		public int getTemperatureMax() {
				return temperatureMax;
		}
		
		public int getPrecipitationProbability() {
				return precipitationProbability;
		}
		
		public Calendar getObservationTime() {
				return observationTime;
		}
		
}
