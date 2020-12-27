package cz.weatherapp.weather_data;

import java.io.Serializable;
import java.util.Calendar;

public class WeatherDataDaily implements Serializable {
		
		private String weatherCode;
		private int temperatureMin;
		private int temperatureMax;
		private int precipitation;
		private Calendar observationTime;
		
// Konstruktor //////////////////////////////////////////////////////////////////////////////////////////////
		
		public WeatherDataDaily() {
		
		}
		
// Settery ///////////////////////////////////////////////////////////////////////////////////////////////////
		
		public void setWeatherCode(String weatherCode) {
				this.weatherCode = weatherCode;
		}
		
		public void setTemperatureMin(int temperatureMin) {
				this.temperatureMin = temperatureMin;
		}
		
		public void setTemperatureMax(int temperatureMax) {
				this.temperatureMax = temperatureMax;
		}
		
		public void setPrecipitation(int precipitation) {
				this.precipitation = precipitation;
		}
		
		public void setObservationTime(Calendar observationTime) {
				this.observationTime = observationTime;
		}

// Gettery ///////////////////////////////////////////////////////////////////////////////////////////////////
		
		public String getWeatherCode() {
				return weatherCode;
		}
		
		public int getTemperatureMin() {
				return temperatureMin;
		}
		
		public int getTemperatureMax() {
				return temperatureMax;
		}
		
		public int getPrecipitation() {
				return precipitation;
		}
		
		public Calendar getObservationTime() {
				return observationTime;
		}
		
}
