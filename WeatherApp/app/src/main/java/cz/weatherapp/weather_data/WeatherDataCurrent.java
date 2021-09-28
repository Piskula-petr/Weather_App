package cz.weatherapp.weather_data;

import java.io.Serializable;
import java.util.Calendar;

public class WeatherDataCurrent implements Serializable {
		
		private int weatherCode;
		private int temperature;
		private int feelsLike;
		private int humidity;
		private int windSpeed;
		private int windDirection;
		private Calendar sunrise;
		private Calendar sunset;
		private int moonPhase;
		
		
		/**
		 * Konstruktor
		 */
		public WeatherDataCurrent() {
		
		}
		
// Settery //////////////////////////////////////////////////////////////////////////////////////////////////
		
		public void setWeatherCode(int weatherCode) {
				this.weatherCode = weatherCode;
		}
		
		public void setTemperature(int temperature) {
				this.temperature = temperature;
		}
		
		public void setFeelsLike(int feelsLike) {
				this.feelsLike = feelsLike;
		}
		
		public void setHumidity(int humidity) {
				this.humidity = humidity;
		}
		
		public void setWindSpeed(int windSpeed) {
				this.windSpeed = windSpeed;
		}
		
		public void setWindDirection(int windDirection) {
				this.windDirection = windDirection;
		}
		
		public void setSunrise(Calendar sunrise) {
				this.sunrise = sunrise;
		}
		
		public void setSunset(Calendar sunset) {
				this.sunset = sunset;
		}
		
		public void setMoonPhase(int moonPhase) {
				this.moonPhase = moonPhase;
		}
		
// Gettery ///////////////////////////////////////////////////////////////////////////////////////////////////
		
		public int getWeatherCode() {
				return weatherCode;
		}
		
		public int getTemperature() {
				return temperature;
		}
		
		public int getFeelsLike() {
				return feelsLike;
		}
		
		public int getHumidity() {
				return humidity;
		}
		
		public int getWindSpeed() {
				return windSpeed;
		}
		
		public int getWindDirection() {
				return windDirection;
		}
		
		public Calendar getSunrise() {
				return sunrise;
		}
		
		public Calendar getSunset() {
				return sunset;
		}
		
		public int getMoonPhase() {
				return moonPhase;
		}
		
}
