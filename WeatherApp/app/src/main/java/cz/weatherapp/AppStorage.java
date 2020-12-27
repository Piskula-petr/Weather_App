package cz.weatherapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import java.util.Locale;

public class AppStorage {
		
		private Context context;
		
		
		/**
		 * Konstruktor
		 *
		 * @param context
		 */
		public AppStorage(Context context) {
				
				this.context = context;
		}
		
		
		/**
		 * Změna jazyka aplikace + uložení nastavení
		 *
		 * @param language - jazyk ve dvoumístném tvaru - [en, cs]
		 */
		public void setAndSaveLocale(String language) {
				
				Locale locale = new Locale(language);
				Locale.setDefault(locale);
				
				// Vytvoření nastavení
				Configuration configuration = new Configuration();
				configuration.setLocale(locale);
				context.getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());
				
				// Uložení nastavení
				SharedPreferences.Editor editor = context.getSharedPreferences("settings", Context.MODE_PRIVATE).edit();
				editor.putString("language", language);
				editor.apply();
		}
		
		
		/**
		 * Získání jazyka aplikace
		 *
		 * @return - vrací dvoumístný tvar jazyku - [en, cs]
		 */
		public String getLocale() {
				
				SharedPreferences sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
				String language = sharedPreferences.getString("language", "");
				
				return language;
		}
		
		
		/**
		 * Uložení jednotky teploty
		 *
		 * @param temperatureUnit - jednotka teploty - [°C / °F]
		 */
		public void saveTemperatureUnit(String temperatureUnit) {
		
				SharedPreferences.Editor editor = context.getSharedPreferences("settings", Context.MODE_PRIVATE).edit();
				editor.putString("temperatureUnit", temperatureUnit);
				editor.apply();
		}
		
		
		/**
		 * Získání jednotky teploty
		 *
		 * @return - vrací jednotku teploty - [°C / °F]
		 */
		public String getTemperatureUnit() {
		
				SharedPreferences sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
				String temperatureUnit = sharedPreferences.getString("temperatureUnit", "°C");
				
				return temperatureUnit;
		}
		
		
		/**
		 * Uložení jednotky rychlosti
		 *
		 * @param speedUnit - jednotka rychlosti - [km/h, mph, m/s]
		 */
		public void saveSpeedUnit(String speedUnit) {
				
				SharedPreferences.Editor editor = context.getSharedPreferences("settings", Context.MODE_PRIVATE).edit();
				editor.putString("speedUnit", speedUnit);
				editor.apply();
		}
		
		
		/**
		 * Získání jednotky rychlosti
		 *
		 * @return - vrací jednotku rychlosti - [km/h, mph, m/s]
		 */
		public String getSpeedUnit() {
				
				SharedPreferences sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
				String speedUnit = sharedPreferences.getString("speedUnit", "km/h");
				
				return speedUnit;
		}
		
		
		/**
		 * Uložení režimu předpovědi
		 *
		 * @param forecastMode - režim předpovědi - [6, 12, 18, 24]
		 */
		public void saveForecastMode(int forecastMode) {
		
				SharedPreferences.Editor editor = context.getSharedPreferences("settings", Context.MODE_PRIVATE).edit();
				editor.putInt("forecastMode", forecastMode);
				editor.apply();
		}
		
		
		/**
		 * Získání režimu předpovědi
		 *
		 * @return - vrací režim předpovědi - [6, 12, 18, 24]
		 */
		public int getForecastMode() {
				
				SharedPreferences sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
				int forecastMode = sharedPreferences.getInt("forecastMode", 12);
				
				return forecastMode;
		}
		
}
