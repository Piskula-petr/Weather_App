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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import cz.weatherapp.AppStorage;
import cz.weatherapp.BuildConfig;
import cz.weatherapp.R;

public class WeatherData {
		
		private final String TOMORROW_API_KEY = BuildConfig.TOMORROW_API_KEY;
		
		private RequestQueue requestQueue;
		private Context context;
		private AppStorage appStorage;
		
		// Aktuální předpověď počasí
		private WeatherDataCurrent weatherDataCurrent;
		private boolean isCurrentDataSet = false;
		private boolean isHourlyDataSourceSet = false;
		private boolean isDailyDataSourceSet = false;
		
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
		
		// Jednotka teploty
		private String temperatureUnit;
		
		// Jednotka rychlosti
		private String speedUnit;
		
		// Režim předpovědi
		private int forecastMode;
		
		// Zdroj dat pro aktuální předpověď počasí
		enum CurrentDataSource {
				HOURLY,
				DAILY
		}
		
		
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
				
				weatherDataCurrent = new WeatherDataCurrent();
				weatherDataHourlyList = new ArrayList<>();
				weatherDataDailyList = new ArrayList<>();
				
				// Nastavení jednoteky teploty
				temperatureUnit = appStorage.getTemperatureUnit();
				
				// Nastavení jednotky rychlosti
				speedUnit = appStorage.getSpeedUnit();
				
				// Nastavení režimu předpovědi
				forecastMode = appStorage.getForecastMode();
		}
		
		
		/**
		 * Odeslání requestů na API server <br><br>
		 *
		 *     - hodinová předpověď počasí <br>
		 *     - denní předpověď počasí <br>
		 */
		public void sendRequests() {
				hourlyWeatherDataRequest();
				dailyWeatherDataRequest();
		}
		
		
		/**
		 * Ověření nastavení dat
		 *
		 * @return - vrací true / false
		 */
		public boolean isWeatherDataSet() {
				
				isCurrentDataSet = (isHourlyDataSourceSet && isDailyDataSourceSet ? true : false);
				
				return isCurrentDataSet && isHourlyDataSet && isDailyDataSet;
		}
		
		
		/**
		 * Nastavení aktuální předpovědi počasí
		 */
		private void setCurrentWeatherData(CurrentDataSource currentDataSource ,JSONObject values) {
				
				try {
						
						switch (currentDataSource) {
								
								case HOURLY:
										
										// Předpověď
										weatherDataCurrent.setWeatherCode(values.getInt("weatherCode"));
										
										// Teplota
										weatherDataCurrent.setTemperature(
												temperatureConversion(values.getInt("temperature"))
										);
										
										// Pocitová teplota
										weatherDataCurrent.setFeelsLike(
												temperatureConversion(values.getInt("temperatureApparent"))
										);
										
										// Vlhkost vzduchu
										weatherDataCurrent.setHumidity(values.getInt("humidity"));
										
										// Rychlost větru
										weatherDataCurrent.setWindSpeed(
												windSpeedConversion(values.getInt("windSpeed"))
										);
										
										// Směr větru
										weatherDataCurrent.setWindDirection(values.getInt("windDirection"));
										
										isHourlyDataSourceSet = true;
										break;
								
								case DAILY:
										
										SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
										
										// Východ slunce
										Calendar sunriseTimeCalendar = Calendar.getInstance();
										sunriseTimeCalendar.setTime(format.parse(values.getString("sunriseTime")));
										weatherDataCurrent.setSunrise(sunriseTimeCalendar);
										
										// Západ slunce
										Calendar sunsetTimeCalendar = Calendar.getInstance();
										sunsetTimeCalendar.setTime(format.parse(values.getString("sunsetTime")));
										weatherDataCurrent.setSunset(sunsetTimeCalendar);
										
										// Měsíční fáze
										weatherDataCurrent.setMoonPhase(values.getInt("moonPhase"));
										
										isDailyDataSourceSet = true;
										break;
						}
						
				} catch (JSONException | ParseException e) {
						
						Toast.makeText(context, R.string.error_current_weather, Toast.LENGTH_LONG).show();
						e.printStackTrace();
				}
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

				String url = "https://api.tomorrow.io/v4/timelines" +
						"?location=" + latitude + "," + longitude +
						"&fields=temperature,temperatureApparent,humidity,windSpeed,windDirection,precipitationProbability,weatherCode" +
						"&units=metric" +
						"&timesteps=1h" +
						"&endTime=" + endTime +
						"&timezone=Europe/Prague" +
						"&apikey=" + TOMORROW_API_KEY;
				
				JsonObjectRequest arrayRequest = new JsonObjectRequest(Request.Method.GET, url, null,
						new Response.Listener<JSONObject>() {
								
								@Override
								public void onResponse(JSONObject response) {
										
										try {
												
												JSONObject data = response.getJSONObject("data");
												JSONObject timelines = data.getJSONArray("timelines").getJSONObject(0);
												JSONArray intervals = timelines.getJSONArray("intervals");
												
												// Aktuální předpověď
												setCurrentWeatherData(CurrentDataSource.HOURLY, intervals.getJSONObject(0).getJSONObject("values"));
												
												// Hodinová předpoveď
												for (int i = 0; i < intervals.length(); i++) {
														
														WeatherDataHourly weatherDataHourly = new WeatherDataHourly();
														
														JSONObject interval = intervals.getJSONObject(i);
														
														SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
														
														// Čas pozorování
														Calendar startTimeCalendar = Calendar.getInstance();
														startTimeCalendar.setTime(format.parse(interval.getString("startTime")));
														weatherDataHourly.setObservationTime(startTimeCalendar);
														
														JSONObject values = interval.getJSONObject("values");
														
														// Předpověď
														weatherDataHourly.setWeatherCode(values.getInt("weatherCode"));
														
														// Teplota
														weatherDataHourly.setTemperature(
																temperatureConversion(values.getInt("temperature"))
														);
														
														// Pravděpodobnost srážek
														weatherDataHourly.setPrecipitationProbability(values.getInt("precipitationProbability"));
														
														weatherDataHourlyList.add(weatherDataHourly);
												}
												
												isHourlyDataSet = true;
												
										} catch (JSONException | ParseException e) {

												Toast.makeText(context, R.string.error_hourly_weather, Toast.LENGTH_LONG).show();
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
				
				Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Z"));
				
				int year = calendar.get(Calendar.YEAR);
				int month = calendar.get(Calendar.MONTH) + 1;
				int day = calendar.get(Calendar.DAY_OF_MONTH);

				// Posun o 4 dny
				calendar.add(Calendar.DAY_OF_MONTH, 4);
				
				year = calendar.get(Calendar.YEAR);
				month = calendar.get(Calendar.MONTH) + 1;
				day = calendar.get(Calendar.DAY_OF_MONTH);
				
				// Konečné datum
				String endTime = year + "-" + twoDigitsFormatter(month) + "-" + twoDigitsFormatter(day) + "T00:00:00Z";
				
				String url = "https://api.tomorrow.io/v4/timelines" +
						"?location=" + latitude + "," + longitude +
						"&fields=temperatureMax,temperatureMin,precipitationProbability,sunriseTime,sunsetTime,moonPhase,weatherCode" +
						"&units=metric" +
						"&timesteps=1d" +
						"&endTime=" + endTime +
						"&timezone=Europe/Prague" +
						"&apikey=" + TOMORROW_API_KEY;
				
				JsonObjectRequest arrayRequest = new JsonObjectRequest(Request.Method.GET, url, null,
						new Response.Listener<JSONObject>() {
								
								@Override
								public void onResponse(JSONObject response) {
										
										try {
												
												JSONObject data = response.getJSONObject("data");
												JSONObject timelines = data.getJSONArray("timelines").getJSONObject(0);
												JSONArray intervals = timelines.getJSONArray("intervals");
												
												// Aktuální předpověď
												setCurrentWeatherData(CurrentDataSource.DAILY, intervals.getJSONObject(0).getJSONObject("values"));

												// Denní předpověď
												for (int i = 0; i < intervals.length(); i++) {
														
														WeatherDataDaily weatherDataDaily = new WeatherDataDaily();
														
														JSONObject interval = intervals.getJSONObject(i);
														
														SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
														
														// Den měření
														Calendar observationTimeCalendar = Calendar.getInstance();
														observationTimeCalendar.setTime(format.parse(interval.getString("startTime")));
														weatherDataDaily.setObservationTime(observationTimeCalendar);

														JSONObject values = interval.getJSONObject("values");
														
														// Předpověď
														weatherDataDaily.setWeatherCode(values.getInt("weatherCode"));
														
														// Maximální teplota
														weatherDataDaily.setTemperatureMax(
																temperatureConversion(values.getInt("temperatureMax"))
														);
														
														// Minimální teplota
														weatherDataDaily.setTemperatureMin(
																temperatureConversion(values.getInt("temperatureMin"))
														);
														
														// Pravděpodobnost srážek
														weatherDataDaily.setPrecipitationProbability(values.getInt("precipitationProbability"));
														
														weatherDataDailyList.add(weatherDataDaily);
												}
												
												isDailyDataSet = true;
												
										} catch (JSONException | ParseException e) {
												
												Toast.makeText(context, R.string.error_daily_weather, Toast.LENGTH_LONG).show();
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
		
		
		/**
		 * Přepočet teploty (výchozí jednotka °C)
		 *
		 * @param temperature - teploty
		 *
		 * @return - vrací upravenou teplotu
		 */
		private int temperatureConversion(int temperature) {

				if (temperatureUnit.equals("°F")){
						
						temperature = (int) (temperature * 1.8) + 32;
				}
				
				return  temperature;
		}
		
		
		/**
		 * Přepočet rychlosti větru (výchotí jednotka m/s)
		 *
		 * @param windSpeed - rychlost větru
		 *
		 * @return - vrací upravenou rychlost větru
		 */
		private int windSpeedConversion(int windSpeed) {
				
				if (speedUnit.equals("km/h")) windSpeed = (int) (windSpeed * 3.6);
				
				else if (speedUnit.equals("mph")) windSpeed = (int) (windSpeed / 0.44704);
				
				return windSpeed;
		}
		
}
