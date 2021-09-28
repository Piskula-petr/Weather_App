package cz.weatherapp;

import android.content.Context;

import androidx.core.widget.RichContentReceiverCompat;

import java.util.HashMap;
import java.util.LinkedHashMap;
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
		 * Vytvoření Mapy předpovědí počasí - [kód počasí, ID popisu]
		 *
		 * @return - vrací Mapu předpovědi počasí
		 */
		public Map<Integer, String> getWeatherPredictions() {
				
				Map<Integer, String> predictions = new HashMap<>();
				
				predictions.put(1000, context.getString(R.string.clear));
				predictions.put(1100, context.getString(R.string.mostly_clear));
				predictions.put(1101, context.getString(R.string.partly_cloudy));
				predictions.put(1001, context.getString(R.string.cloudy));
				predictions.put(1102, context.getString(R.string.mostly_cloudy));
				predictions.put(2000, context.getString(R.string.fog));
				predictions.put(2100, context.getString(R.string.fog_light));
				predictions.put(4000, context.getString(R.string.drizzle));
				predictions.put(4200, context.getString(R.string.rain_light));
				predictions.put(4001, context.getString(R.string.rain));
				predictions.put(4201, context.getString(R.string.rain_heavy));
				predictions.put(8000, context.getString(R.string.tstorm));
				predictions.put(5100, context.getString(R.string.snow_light));
				predictions.put(5000, context.getString(R.string.snow));
				predictions.put(5001, context.getString(R.string.flurries));
				predictions.put(5101, context.getString(R.string.snow_heavy));
				predictions.put(7102, context.getString(R.string.ice_pellets_light));
				predictions.put(7000, context.getString(R.string.ice_pellets));
				predictions.put(7101, context.getString(R.string.ice_pellets_heavy));
				predictions.put(6000, context.getString(R.string.freezing_drizzle));
				predictions.put(6200, context.getString(R.string.freezing_rain_light));
				predictions.put(6001, context.getString(R.string.freezing_rain));
				predictions.put(6201, context.getString(R.string.freezing_rain_heavy));
				
				return predictions;
		}
		
		
		/**
		 * Vytvoření Mapy obrázků předpovědi počasí - [kód počasí , ID popisku]
		 *
		 * @param isDay - boolean noční / denní obrázky
		 *
		 * @return - vrací Mapu obrázků předpovědi počasí
		 */
		public Map<Integer, Integer> getWeatherPredictionsImages(boolean isDay) {
				
				Map<Integer, Integer> images = new HashMap<>();
				
				// Denní režim
				if (isDay) {
						images.put(1000, R.drawable.clear);
						images.put(1100, R.drawable.mostly_clear);
						images.put(1101, R.drawable.partly_cloudy);
						
				// Noční režim
				} else {
						images.put(1000, R.drawable.clear_night);
						images.put(1100, R.drawable.mostly_clear_night);
						images.put(1101, R.drawable.partly_cloudy_night);
				}
				
				images.put(1001, R.drawable.cloudy);
				images.put(1102, R.drawable.mostly_cloudy);
				images.put(2000, R.drawable.fog);
				images.put(2100, R.drawable.fog_light);
				images.put(4000, R.drawable.drizzle);
				images.put(4200, R.drawable.rain_light);
				images.put(4001, R.drawable.rain);
				images.put(4201, R.drawable.rain_heavy);
				images.put(8000, R.drawable.tstorm);
				images.put(5001, R.drawable.flurries);
				images.put(5100, R.drawable.snow_light);
				images.put(5000, R.drawable.snow);
				images.put(5101, R.drawable.snow_heavy);
				images.put(7102, R.drawable.ice_pellets_light);
				images.put(7000, R.drawable.ice_pellets);
				images.put(7101, R.drawable.ice_pellets_heavy);
				images.put(6000, R.drawable.freezing_drizzle);
				images.put(6200, R.drawable.freezing_rain_light);
				images.put(6001, R.drawable.freezing_rain);
				images.put(6201, R.drawable.freezing_rain_heavy);
				
				return images;
		}
		
		
		/**
		 * Vytvoření Mapy typů počasí - [ID obrázku, popis obrázku]
		 *
		 * @return - vrací Mapu typů počasí
		 */
		public Map<Integer, String> getWeatherTypes(boolean isDay) {
				
				Map<Integer, String> weatherTypes = new LinkedHashMap<>();
				
				// Denní režim
				if (isDay) {
						weatherTypes.put(R.drawable.clear, context.getString(R.string.clear));
						weatherTypes.put(R.drawable.mostly_clear, context.getString(R.string.mostly_clear));
						weatherTypes.put(R.drawable.partly_cloudy, context.getString(R.string.partly_cloudy));
						
						// Noční režim
				} else {
						weatherTypes.put(R.drawable.clear, context.getString(R.string.clear));
						weatherTypes.put(R.drawable.mostly_clear, context.getString(R.string.mostly_clear));
						weatherTypes.put(R.drawable.partly_cloudy, context.getString(R.string.partly_cloudy));
				}
				
				weatherTypes.put(R.drawable.cloudy, context.getString(R.string.mostly_cloudy) +
						"\n" + context.getString(R.string.cloudy));
				
				weatherTypes.put(R.drawable.fog, context.getString(R.string.fog_light) +
						"\n" + context.getString(R.string.fog));
				
				weatherTypes.put(R.drawable.flurries, context.getString(R.string.flurries));
				weatherTypes.put(R.drawable.drizzle, context.getString(R.string.drizzle));
				weatherTypes.put(R.drawable.rain_light, context.getString(R.string.rain_light));
				
				weatherTypes.put(R.drawable.rain, context.getString(R.string.rain) +
						"\n" + context.getString(R.string.rain_heavy));
				
				weatherTypes.put(R.drawable.tstorm, context.getString(R.string.tstorm));
				
				weatherTypes.put(R.drawable.ice_pellets_light, context.getString(R.string.freezing_drizzle) +
						"\n" + context.getString(R.string.freezing_rain_light) +
						"\n" + context.getString(R.string.ice_pellets_light));
				
				weatherTypes.put(R.drawable.ice_pellets, context.getString(R.string.freezing_rain) +
						"\n" + context.getString(R.string.freezing_rain_heavy) +
						"\n" + context.getString(R.string.ice_pellets) +
						"\n" + context.getString(R.string.ice_pellets_heavy));
				
				weatherTypes.put(R.drawable.snow, context.getString(R.string.snow_light) +
						"\n" + context.getString(R.string.snow));
				
				weatherTypes.put(R.drawable.snow_heavy, context.getString(R.string.snow_heavy));
				
				return weatherTypes;
		}
		
		
		/**
		 * Vytvoření Mapy obrázků měsíčních fází - [kód měsíční fáze, ID obrázku]
		 *
		 * @return - vrací Mapu obrázků měsíčních fází
		 */
		public Map<Integer, Integer> getMoonPhaseImages() {
				
				Map<Integer, Integer> images = new HashMap<>();
				
				images.put(0, R.drawable.moon_phase_new);
				images.put(1, R.drawable.moon_phase_waxing_crescent);
				images.put(2, R.drawable.moon_phase_first_quarter);
				images.put(3, R.drawable.moon_phase_waxing_gibbous);
				images.put(4, R.drawable.moon_phase_full);
				images.put(5, R.drawable.moon_phase_waning_gibbous);
				images.put(6, R.drawable.moon_phase_last_quarter);
				images.put(7, R.drawable.moon_phase_waning_crescent);
				
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
