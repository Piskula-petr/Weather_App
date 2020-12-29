package cz.weatherapp.weather_data;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import cz.weatherapp.AppStorage;
import cz.weatherapp.BuildConfig;
import cz.weatherapp.R;

public class WeatherData {
		
		private final String CLIMACELL_API_KEY = BuildConfig.CLIMACELL_API_KEY;
		
		private RequestQueue requestQueue;
		private Context context;
		private AppStorage appStorage;
		
		// Aktuální předpověď počasí
		private WeatherDataCurrent weatherDataCurrent;
		private boolean isCurrentDataSet = false;
		
		// Hodinová předpověď počasí
		private List<WeatherDataHourly> weatherDataHourlyList;
		private boolean isHourlyDataSet = false;
		
		// Denní předpověď počasí
		private List<WeatherDataDaily> weatherDataDailyList;
		private boolean isDailyDataSet = false;
		
		// Zeměpisná šířka
		private double latitude;
		
		// Zeměpisná délka
		private double longitude;
		
		// Systém jednotek
		private String unitSystem;
		
		// Jednotka rychlosti
		private String speedUnit;
		
		// Režim předpovědi
		private int forecastMode;
		
		
		/**
		 * Konstruktor
		 *
		 * @param context
		 * @param latitude - zeměpisná šířka
		 * @param longitude - zeměpisná délka
		 */
		public WeatherData(Context context, double latitude, double longitude) {
				
				this.context = context;
				this.latitude = latitude;
				this.longitude = longitude;
				
				requestQueue = Volley.newRequestQueue(context);
				
				appStorage = new AppStorage(context);
				
				// Nastavení systému jednotek
				switch (appStorage.getTemperatureUnit()) {
						
						case "°C": unitSystem = "si"; break;
						case "°F": unitSystem = "us"; break;
				}
				
				// Nastavení jednotky rychlosti
				speedUnit = appStorage.getSpeedUnit();
				
				if (speedUnit.equals("km/h")) speedUnit = "kph";
				
				// Nastavení režimu předpovědi
				forecastMode = appStorage.getForecastMode();
		}
		
		
		/**
		 * Odeslání requestů na API server <br><br>
		 *
		 *     - aktuální předpověď počasí <br>
		 *     - hodinová předpověď počasí <br>
		 *     - denní předpověď počasí <br>
		 */
		public void sendRequests() {
				currentWeatherDataRequest();
				hourlyWeatherDataRequest();
				dailyWeatherDataRequest();
		}
		
		
		/**
		 * Ověření nastavení dat
		 *
		 * @return - vrací true / false
		 */
		public boolean isWeatherDataSet() {
				
				if (isCurrentDataSet && isHourlyDataSet && isDailyDataSet) {
						
						return true;
						
				} else return false;
		}
		
		
		/**
		 * Odeslání requestu pro aktuální předpověď počasí
		 */
		private void currentWeatherDataRequest() {
				
				String url = "https://api.climacell.co/v3/weather/realtime" +
						"?lat=" + latitude + "" +
						"&lon=" + longitude + "" +
						"&location_id=" + CLIMACELL_API_KEY +
						"&unit_system=" + unitSystem +
						"&fields=weather_code%2Ctemp%2Cfeels_like%2Chumidity%2Cwind_speed%3A" + speedUnit + "%2Cwind_direction%2Csunrise%2Csunset%2Cmoon_phase" +
						"&apikey=" + CLIMACELL_API_KEY;
				
				JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
						new Response.Listener<JSONObject>() {
								
								@Override
								public void onResponse(JSONObject response) {
										
										try {
												
												weatherDataCurrent = new WeatherDataCurrent();
												
												// Předpověď
												JSONObject weatherCode = response.getJSONObject("weather_code");
												weatherDataCurrent.setWeatherCode(weatherCode.getString("value"));
												
												// Teplota
												JSONObject temp = response.getJSONObject("temp");
												weatherDataCurrent.setTemperature((int) Math.round(temp.getDouble("value")));
												
												// Pocitová teplota
												JSONObject feelsLike = response.getJSONObject("feels_like");
												weatherDataCurrent.setFeelsLikeTemp((int) Math.round(feelsLike.getDouble("value")));
												
												// Vlhkost vzduchu
												JSONObject humidity = response.getJSONObject("humidity");
												weatherDataCurrent.setHumidity((int) Math.round(humidity.getDouble("value")));
												
												// Rychlost větru
												JSONObject windSpeed = response.getJSONObject("wind_speed");
												weatherDataCurrent.setWindSpeed((int) Math.round(windSpeed.getDouble("value")));
												
												// Směr větru
												JSONObject windDirection = response.getJSONObject("wind_direction");
												weatherDataCurrent.setWindDirection((int) Math.round(windDirection.getDouble("value")));
												
												SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
												
												// Východ slunce
												JSONObject sunrise = response.getJSONObject("sunrise");
												Calendar sunriseCalendar = Calendar.getInstance();
												sunriseCalendar.setTime(format.parse(sunrise.getString("value").replace("Z", "+0:00")));
												weatherDataCurrent.setSunrise(sunriseCalendar);
												
												// Západ slunce
												JSONObject sunset = response.getJSONObject("sunset");
												Calendar sunsetCalendar = Calendar.getInstance();
												sunsetCalendar.setTime(format.parse(sunset.getString("value").replace("Z", "+0:00")));
												weatherDataCurrent.setSunset(sunsetCalendar);
												
												// Měsíční fáze
												JSONObject moonPhase = response.getJSONObject("moon_phase");
												weatherDataCurrent.setMoonPhase(moonPhase.getString("value"));
												
												isCurrentDataSet = true;
												
										} catch (JSONException | ParseException e) {
												
												Toast.makeText(context, R.string.error_current_weather, Toast.LENGTH_LONG).show();
												e.printStackTrace();
										}
								}
						}, new Response.ErrorListener() {
						
						@Override
						public void onErrorResponse(VolleyError error) {
								
								Toast.makeText(context, R.string.error_server_connection, Toast.LENGTH_LONG).show();
								error.printStackTrace();
						}
				});
				
				requestQueue.add(objectRequest);
		}
		
		
		/**
		 * Získání aktuální předpovědi počasí
		 *
		 * @return - vrací aktuální předpověď počasí
		 */
		public WeatherDataCurrent getWeatherDataCurrent() {
				return weatherDataCurrent;
		}
		
		
		/**
		 * Odeslání requestu pro hodinovou předpověď počasí
		 */
		private void hourlyWeatherDataRequest() {
				
				weatherDataHourlyList = new ArrayList<>();
				
				Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Z"));
				
				// Posun datumu
				// forecastMode - [6, 12, 18, 24]
				calendar.add(Calendar.HOUR_OF_DAY, forecastMode);
				
				int year = calendar.get(Calendar.YEAR);
				int month = calendar.get(Calendar.MONTH) + 1;
				int day = calendar.get(Calendar.DAY_OF_MONTH);
				int hour = calendar.get(Calendar.HOUR_OF_DAY);
				
				// Konečné datum
				String endTime = year + "-" + twoDigitsFormatter(month) + "-" + twoDigitsFormatter(day) + "T" + twoDigitsFormatter(hour) + ":00:00Z";
				
				String url = "https://api.climacell.co/v3/weather/forecast/hourly" +
						"?lat=" + latitude + "" +
						"&lon=" + longitude + "" +
						"&location_id=" + CLIMACELL_API_KEY +
						"&unit_system=" + unitSystem +
						"&start_time=now" +
						"&end_time=" + endTime +
						"&fields=weather_code%2Ctemp%2Cprecipitation_probability" +
						"&apikey=" + CLIMACELL_API_KEY;
				
				JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
						new Response.Listener<JSONArray>() {
								
								@Override
								public void onResponse(JSONArray response) {
										
										for (int i = 0; i < response.length(); i++) {
												
												WeatherDataHourly weatherDataHourly = new WeatherDataHourly();
												
												try {
														JSONObject jsonObject = response.getJSONObject(i);
														
														// Předpověď
														JSONObject weatherCode = jsonObject.getJSONObject("weather_code");
														weatherDataHourly.setWeatherCode(weatherCode.getString("value"));
														
														// Teplota
														JSONObject temp = jsonObject.getJSONObject("temp");
														weatherDataHourly.setTemperature((int) Math.round(temp.getDouble("value")));
														
														// Pravděpodobnost srážek
														JSONObject precipitationProbability = jsonObject.getJSONObject("precipitation_probability");
														weatherDataHourly.setPrecipitation(precipitationProbability.getInt("value"));
														
														SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
														
														// Čas pozorování
														JSONObject observationTime = jsonObject.getJSONObject("observation_time");
														
														Calendar observationTimeCalendar = Calendar.getInstance();
														observationTimeCalendar.setTime(format.parse(observationTime.getString("value").replace("Z", "+0:00")));
														weatherDataHourly.setObservationTime(observationTimeCalendar);
														
														weatherDataHourlyList.add(weatherDataHourly);
														
														isHourlyDataSet = true;
														
												} catch (JSONException | ParseException e) {
														
														Toast.makeText(context, R.string.error_hourly_weather, Toast.LENGTH_LONG).show();
														e.printStackTrace();
												}
										}
								}
						}, new Response.ErrorListener() {
						
						@Override
						public void onErrorResponse(VolleyError error) {
								
								Toast.makeText(context, R.string.error_server_connection, Toast.LENGTH_LONG).show();
								error.printStackTrace();
						}
				});
				
				requestQueue.add(arrayRequest);
		}
		
		
		/**
		 * Získání hodinové předpovědi počasí
		 *
		 * @return - vrací List s hodinovým měřením počasí
		 */
		public List<WeatherDataHourly> getWeatherDataHourlyList() {
				return weatherDataHourlyList;
		}
		
		
		/**
		 * Odeslání requestu pro denní předpověď počasí
		 */
		private void dailyWeatherDataRequest() {
				
				weatherDataDailyList = new ArrayList<>();
				
				Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Z"));
				
				// Zítřejší datum
				calendar.add(Calendar.DAY_OF_MONTH, 1);
				
				int year = calendar.get(Calendar.YEAR);
				int month = calendar.get(Calendar.MONTH) + 1;
				int day = calendar.get(Calendar.DAY_OF_MONTH);
				
				// Počáteční datum
				String startTime = year + "-" + twoDigitsFormatter(month) + "-" + twoDigitsFormatter(day) + "T00:00:00Z";
				
				// Posun o 3 dny
				calendar.add(Calendar.DAY_OF_MONTH, 3);
				
				year = calendar.get(Calendar.YEAR);
				month = calendar.get(Calendar.MONTH) + 1;
				day = calendar.get(Calendar.DAY_OF_MONTH);
				
				// Konečné datum
				String endTime = year + "-" + twoDigitsFormatter(month) + "-" + twoDigitsFormatter(day) + "T00:00:00Z";
				
				String url = "https://api.climacell.co/v3/weather/forecast/daily" +
						"?lat=" + latitude +
						"&lon=" + + longitude +
						"&location_id=" + CLIMACELL_API_KEY +
						"&unit_system=" + unitSystem +
						"&start_time=" + startTime +
						"&end_time=" + endTime +
						"&fields=temp%2Cweather_code%2Cprecipitation_probability" +
						"&apikey=" + CLIMACELL_API_KEY;
				
				JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
						new Response.Listener<JSONArray>() {
								
								@Override
								public void onResponse(JSONArray response) {
										
										for (int i = 0; i < response.length(); i++) {
												
												WeatherDataDaily weatherDataDaily = new WeatherDataDaily();
												
												try {
														
														JSONObject jsonObject = response.getJSONObject(i);
														JSONArray temp = jsonObject.getJSONArray("temp");
														
														// Předpověď
														JSONObject weatherCode = jsonObject.getJSONObject("weather_code");
														weatherDataDaily.setWeatherCode(weatherCode.getString("value"));
														
														// Minimální teplota
														JSONObject tempMin = temp.getJSONObject(0).getJSONObject("min");
														weatherDataDaily.setTemperatureMin((int) Math.round(tempMin.getDouble("value")));
														
														// Maximální teplota
														JSONObject tempMax = temp.getJSONObject(1).getJSONObject("max");
														weatherDataDaily.setTemperatureMax((int) Math.round(tempMax.getDouble("value")));
														
														// Pravděpodobnost srážek
														JSONObject precipitationProbability = jsonObject.getJSONObject("precipitation_probability");
														weatherDataDaily.setPrecipitation(precipitationProbability.getInt("value"));
														
														SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
														
														// Den měření
														JSONObject observationTime = jsonObject.getJSONObject("observation_time");
														
														Calendar observationTimeCalendar = Calendar.getInstance();
														observationTimeCalendar.setTime(format.parse(observationTime.getString("value")));
														weatherDataDaily.setObservationTime(observationTimeCalendar);
														
														weatherDataDailyList.add(weatherDataDaily);
														
														isDailyDataSet = true;
														
												} catch (JSONException | ParseException e) {
														
														Toast.makeText(context, R.string.error_daily_weather, Toast.LENGTH_LONG).show();
														e.printStackTrace();
												}
										}
								}
						}, new Response.ErrorListener() {
						
						@Override
						public void onErrorResponse(VolleyError error) {
								
								Toast.makeText(context, R.string.error_server_connection, Toast.LENGTH_LONG).show();
								error.printStackTrace();
						}
				});
				
				requestQueue.add(arrayRequest);
		}
		
		
		/**
		 * Získání denní předpovědi počasí
		 *
		 * @return - vrací List s denním měřením počasí
		 */
		public List<WeatherDataDaily> getWeatherDataDailyList() {
				return weatherDataDailyList;
		}


		/**
		* Převod čísla na dvouciferný formát [1 -> "01"]
		*
		* @param number - vstupní číslo
		*
		* @return - vrací dvouciferný String
		*/
		private String twoDigitsFormatter(int number) {
		
				String twoDigits = (number < 10
							? twoDigits = "0" + number
							: String.valueOf(number));
				
				return twoDigits;
		}

}
