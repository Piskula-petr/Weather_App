package cz.weatherapp.weather_data;

import java.io.Serializable;
import java.util.Calendar;

public class WeatherDataHourly implements Serializable {
		
		private String weatherCode;
		private int temperature;
		private int precipitation;
		private Calendar observationTime;
		
// Konstruktor //////////////////////////////////////////////////////////////////////////////////////////////
		
		public WeatherDataHourly() {
		
		}
		
// Settery //////////////////////////////////////////////////////////////////////////////////////////////////
		
		public void setWeatherCode(String weatherCode) {
				this.weatherCode = weatherCode;
		}
		
		public void setTemperature(int temperature) {
				this.temperature = temperature;
		}
		
		public void setPrecipitation(int precipitation) {
				this.precipitation = precipitation;
		}
		
		public void setObservationTime(Calendar observationTime) {
				this.observationTime = observationTime;
		}
		
// Gettery //////////////////////////////////////////////////////////////////////////////////////////////////
		
		public String getWeatherCode() {
				return weatherCode;
		}
		
		public int getTemperature() {
				return temperature;
		}
		
		public int getPrecipitation() {
				return precipitation;
		}
		
		public Calendar getObservationTime() {
				return observationTime;
		}
		
}
