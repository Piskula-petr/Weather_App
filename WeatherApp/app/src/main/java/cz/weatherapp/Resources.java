package cz.weatherapp;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

public class Resources {
		
		private Context context;
		
		
		/**
		 * Konstruktor
		 *
		 * @param context
		 */
		public Resources(Context context) {
		
				this.context = context;
		}
		
		
		/**
		 * Vytvoření Mapy předpovědí počasí: <br>
		 *
		 * klíč - kód předpovědi z API serveru <br>
		 * hodnota - slovně předpověď počasí
		 *
		 * @return - vrací Mapu předpovědi počasí
		 */
		public Map<String, String> getWeatherPredictions() {
				
				Map<String, String> predictions = new HashMap<>();
				
				predictions.put("clear", context.getString(R.string.clear));
				predictions.put("mostly_clear", context.getString(R.string.mostly_clear));
				predictions.put("partly_cloudy", context.getString(R.string.partly_cloudy));
				predictions.put("cloudy", context.getString(R.string.cloudy));
				predictions.put("mostly_cloudy", context.getString(R.string.mostly_cloudy));
				predictions.put("fog", context.getString(R.string.fog));
				predictions.put("fog_light", context.getString(R.string.fog_light));
				predictions.put("drizzle", context.getString(R.string.drizzle));
				predictions.put("rain_light", context.getString(R.string.rain_light));
				predictions.put("rain", context.getString(R.string.rain));
				predictions.put("rain_heavy", context.getString(R.string.rain_heavy));
				predictions.put("tstorm", context.getString(R.string.tstorm));
				predictions.put("snow_light", context.getString(R.string.snow_light));
				predictions.put("snow", context.getString(R.string.snow));
				predictions.put("flurries", context.getString(R.string.flurries));
				predictions.put("snow_heavy", context.getString(R.string.snow_heavy));
				predictions.put("ice_pellets_light", context.getString(R.string.ice_pellets_light));
				predictions.put("ice_pellets", context.getString(R.string.ice_pellets));
				predictions.put("ice_pellets_heavy", context.getString(R.string.ice_pellets_heavy));
				predictions.put("freezing_drizzle", context.getString(R.string.freezing_drizzle));
				predictions.put("freezing_rain_light", context.getString(R.string.freezing_rain_light));
				predictions.put("freezing_rain", context.getString(R.string.freezing_rain));
				predictions.put("freezing_rain_heavy", context.getString(R.string.freezing_rain_heavy));
				
				return predictions;
		}
		
		
		/**
		 * Vytvoření Mapy obrázků předpovědi počasí: <br>
		 *
		 * klíč - kód předpovědi z API serveru <br>
		 * hodnota - ID obrázku
		 *
		 * @param isDay - boolean noční / denní obrázky
		 *
		 * @return - vrací Mapu obrázků předpovědi počasí
		 */
		public Map<String, Integer> getWeatherPredictionsImages(boolean isDay) {
				
				Map<String, Integer> images = new HashMap<>();
				
				// Denní režim
				if (isDay) {
						images.put("clear", R.drawable.clear);
						images.put("mostly_clear", R.drawable.mostly_clear);
						images.put("partly_cloudy", R.drawable.partly_cloudy);
						
				// Noční režim
				} else {
						images.put("clear", R.drawable.clear_night);
						images.put("mostly_clear", R.drawable.mostly_clear_night);
						images.put("partly_cloudy", R.drawable.partly_cloudy_night);
				}
				
				images.put("cloudy", R.drawable.cloudy);
				images.put("mostly_cloudy", R.drawable.mostly_cloudy);
				images.put("fog", R.drawable.fog);
				images.put("fog_light", R.drawable.fog_light);
				images.put("drizzle", R.drawable.drizzle);
				images.put("rain_light", R.drawable.rain_light);
				images.put("rain", R.drawable.rain);
				images.put("rain_heavy", R.drawable.rain_heavy);
				images.put("tstorm", R.drawable.tstorm);
				images.put("flurries", R.drawable.flurries);
				images.put("snow_light", R.drawable.snow_light);
				images.put("snow", R.drawable.snow);
				images.put("snow_heavy", R.drawable.snow_heavy);
				images.put("ice_pellets_light", R.drawable.ice_pellets_light);
				images.put("ice_pellets", R.drawable.ice_pellets);
				images.put("ice_pellets_heavy", R.drawable.ice_pellets_heavy);
				images.put("freezing_drizzle", R.drawable.freezing_drizzle);
				images.put("freezing_rain_light", R.drawable.freezing_rain_light);
				images.put("freezing_rain", R.drawable.freezing_rain);
				images.put("freezing_rain_heavy", R.drawable.freezing_rain_heavy);
				
				return images;
		}
		
		
		/**
		 * Vytvoření Mapy obrázků měsíčních fází: <br>
		 *
		 * klíč - kód z API serveru <br>
		 * hodnota - ID obrázku
		 *
		 * @return - vrací Mapu obrázků měsíčních fází
		 */
		public Map<String, Integer> getMoonPhaseImages() {
				
				Map<String, Integer> images = new HashMap<>();
				
				images.put("new", R.drawable.moon_phase_new);
				images.put("waxing_crescent", R.drawable.moon_phase_waxing_crescent);
				images.put("first_quarter", R.drawable.moon_phase_first_quarter);
				images.put("waxing_gibbous", R.drawable.moon_phase_waxing_gibbous);
				images.put("full", R.drawable.moon_phase_full);
				images.put("waning_gibbous", R.drawable.moon_phase_waning_gibbous);
				images.put("last_quarter", R.drawable.moon_phase_last_quarter);
				images.put("waning_crescent", R.drawable.moon_phase_waning_crescent);
				
				return  images;
		}
		
		
		/**
		 * Získání slovního směru větru, ze zadaného úhlu
		 *
		 * @param windDegree - úhel větru [0 - 360]
		 *
		 * @return - vrací směr větru
		 */
		public String getWindDirection(int windDegree) {
				
				String windDirection = "";
				
				if ((windDegree > 315 && windDegree <= 360) || (windDegree >= 0 && windDegree <= 45)) {
						
						windDirection = context.getString(R.string.north);
						
				} else if (windDegree > 45 && windDegree <= 135) {
						
						windDirection = context.getString(R.string.east);
						
				} else if (windDegree > 135 && windDegree <= 225) {
						
						windDirection = context.getString(R.string.south);
						
				} else if (windDegree > 225 && windDegree <= 315) {
						
						windDirection = context.getString(R.string.west);
				}
				
				return windDirection;
		}
		
}
