package cz.weatherapp.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cz.weatherapp.AppStorage;
import cz.weatherapp.BuildConfig;
import cz.weatherapp.Connection;
import cz.weatherapp.R;
import cz.weatherapp.Resources;
import cz.weatherapp.weather_data.WeatherData;
import cz.weatherapp.weather_data.WeatherDataCurrent;
import cz.weatherapp.weather_data.WeatherDataDaily;
import cz.weatherapp.weather_data.WeatherDataHourly;

public class MainActivity extends AppCompatActivity {
    
    private final String GOOGLE_API_KEY = BuildConfig.GOOGLE_API_KEY;
    
    // Kód výsledku změny polohy
    private final int LOCATION_REQUEST_CODE = 999;
    
    private RelativeLayout relativeLayoutHourly;
    private WeatherData weatherData;
    private AppStorage appStorage;
    private EditText editTextLocation;
    
    // Data o počasí
    private WeatherDataCurrent weatherDataCurrent = new WeatherDataCurrent();
    private List<WeatherDataHourly> weatherDataHourlyList = new ArrayList<>();
    private List<WeatherDataDaily> weatherDataDailyList = new ArrayList<>();
    
    // Zdroje
    private Map<String, String> predictions;
    private Map<String, Integer> images;
    private Map<String, Integer> moonPhases;
    
    // Poloha [město, stát]
    private String location;
    
    // Denní / Noční režim
    private boolean isDay;
    
    // Jednotka teploty aplikace
    private String appTemperatureUnit;
    
    // Jednotka rychlosti aplikace
    private String appSpeedUnit;
    
    
    /**
     * Vytvoření aktivity
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);
    
        // Nastavení orientace na výšku
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        
        // Skrytí panelu akcí
        getSupportActionBar().hide();
    
        isDay = getIntent().getBooleanExtra("isDay", true);
        
        // Přesměrování na typy počasí
        Button buttonWeatherTypes = findViewById(R.id.buttonWeatherTypes);
        buttonWeatherTypes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                
                Intent weatherTypes = new Intent(MainActivity.this, WeatherTypesActivity.class);
                weatherTypes.putExtra("isDay", isDay);
                
                startActivity(weatherTypes);
            }
        });

        // Přesměrování do nastavení aplikace
        Button buttonSettings = findViewById(R.id.buttonSettings);
        buttonSettings.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });
    
        // Nastavení jednotek
        appStorage = new AppStorage(MainActivity.this);
        appTemperatureUnit = appStorage.getTemperatureUnit();
        appSpeedUnit = appStorage.getSpeedUnit();
    
        // Změna jazyka aplikace + uložení nastavení
        appStorage.setAndSaveLocale(Locale.getDefault().getLanguage());
    
        // Nastavení zdrojů
        Resources resources = new Resources(getApplicationContext());
        predictions = resources.getWeatherPredictions();
        images = resources.getWeatherPredictionsImages(isDay);
        moonPhases = resources.getMoonPhaseImages();
        
// Aktuální předpověď //////////////////////////////////////////////////////////////////////////////////////
        
        weatherDataCurrent = (WeatherDataCurrent) getIntent().getSerializableExtra("weatherDataCurrent");
        location = getIntent().getStringExtra("location");
        
        // Poloha
        editTextLocation = findViewById(R.id.editTextLocation);
        editTextLocation.setText(location);
        
        // Předpověď
        TextView textViewPrediction = findViewById(R.id.textViewPrediction);
        textViewPrediction.setText(predictions.get(weatherDataCurrent.getWeatherCode()));
    
        // Teplota
        TextView textViewTemperature = findViewById(R.id.textViewTemperature);
        textViewTemperature.setText(String.valueOf(weatherDataCurrent.getTemperature()));
    
        // Jednotka
        TextView textViewUnit = findViewById(R.id.textViewUnit);
        textViewUnit.setText(appTemperatureUnit);
        
        // Obrázek předpovědi
        ImageView imageViewPrediction = findViewById(R.id.imageViewPrediction);
        imageViewPrediction.setImageResource(images.get(weatherDataCurrent.getWeatherCode()));
        
        // Pocitová teplota
        TextView textViewFeelsLikeLabel = findViewById(R.id.textViewFeelsLikeLabel);
        textViewFeelsLikeLabel.setText(getString(R.string.feels_like) + " :");
        
        TextView textViewFeelsLike = findViewById(R.id.textViewFeelsLike);
        textViewFeelsLike.setText(weatherDataCurrent.getFeelsLikeTemp() + " " + appTemperatureUnit);
        
        // Vlhkost vzduchu
        TextView textViewHumidityLabel = findViewById(R.id.textViewHumidityLabel);
        textViewHumidityLabel.setText(getString(R.string.humidity) + " :");
        
        TextView textViewHumidity = findViewById(R.id.textViewHumidity);
        textViewHumidity.setText(weatherDataCurrent.getHumidity() + " %");
        
        // Rychlost větru
        TextView textViewWindSpeedLabel = findViewById(R.id.textViewWindSpeedLabel);
        textViewWindSpeedLabel.setText(getString(R.string.wind_speed) + " :");
        
        TextView textViewWindSpeed = findViewById(R.id.textViewWindSpeed);
        textViewWindSpeed.setText(weatherDataCurrent.getWindSpeed() + " " + appSpeedUnit);
        
        // Směr větru
        TextView textViewWindDirectionLabel = findViewById(R.id.textViewWindDirectionLabel);
        textViewWindDirectionLabel.setText(getString(R.string.wind_direction) + " :");
        
        ImageView imageViewWindDirection = findViewById(R.id.imageViewWindDirection);
        imageViewWindDirection.setRotation(weatherDataCurrent.getWindDirection());
    
        TextView textViewWindDirection = findViewById(R.id.textViewWindDirection);
        textViewWindDirection.setText(resources.getWindDirection(weatherDataCurrent.getWindDirection()));
        
        // Východ slunce
        TextView textViewSunriseLabel = findViewById(R.id.textViewSunriseLabel);
        textViewSunriseLabel.setText(getString(R.string.sunrise) + " :");
        
        TextView textViewSunrise = findViewById(R.id.textViewSunrise);

        SimpleDateFormat timeFormat = new SimpleDateFormat("H:mm");

        Calendar calendar = Calendar.getInstance();
        calendar = weatherDataCurrent.getSunrise();
        textViewSunrise.setText(timeFormat.format(calendar.getTime()));
        
        // Západ slunce
        TextView textViewSunsetLabel = findViewById( R.id.textViewSunsetLabel);
        textViewSunsetLabel.setText(getString(R.string.sunset) + " :");
        
        TextView textViewSunset = findViewById(R.id.textViewSunset);
        calendar = weatherDataCurrent.getSunset();
        textViewSunset.setText(timeFormat.format(calendar.getTime()));

        // Měsíční fáze
        TextView textViewMoonPhaseLabel = findViewById(R.id.textViewMoonPhaseLabel);
        textViewMoonPhaseLabel.setText(getString(R.string.moon_phase) + " :");
        
        ImageView imageViewMoonPhase = findViewById(R.id.imageViewMoonPhase);
        imageViewMoonPhase.setImageResource(moonPhases.get(weatherDataCurrent.getMoonPhase()));
        
// Hodinová předpověď /////////////////////////////////////////////////////////////////////////////////////

        weatherDataHourlyList = (List<WeatherDataHourly>) getIntent().getSerializableExtra("weatherDataHourlyList");

        // Aktuální den v měsíci
        int today = calendar.get(Calendar.DAY_OF_MONTH);
        
        relativeLayoutHourly = findViewById(R.id.relativeLayoutHourly);
        relativeLayoutHourly.setPadding(10, 0, 10, 40);
    
        LayoutParams layoutParams = new LayoutParams(250, 450);
        
        for (int i = 0; i < weatherDataHourlyList.size(); i++) {
    
            WeatherDataHourly weatherDataHourly = weatherDataHourlyList.get(i);
            
            // Čas měření
            TextView textViewHourlyTime = new TextView(this);
            calendar = weatherDataHourly.getObservationTime();
    
            // Zobrazení datumu, v případě následujícího dne
            textViewHourlyTime.setText((calendar.get(Calendar.DAY_OF_MONTH) == today
                ? calendar.get(Calendar.HOUR_OF_DAY) + ":00"
                : calendar.get(Calendar.HOUR_OF_DAY) + ":00\n" + calendar.get(Calendar.DAY_OF_MONTH) + "." + (calendar.get(Calendar.MONTH) + 1) + "."));
            
            textViewHourlyTime.setTextColor(ContextCompat.getColor(this, R.color.text));
            textViewHourlyTime.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            textViewHourlyTime.setGravity(Gravity.CENTER | Gravity.TOP);
    
            textViewHourlyTime.setLayoutParams(layoutParams);
            layoutParams.setMargins((270 * i), 0, 0, 0);
            relativeLayoutHourly.addView(textViewHourlyTime);

            // Obrázek předpovědi
            ImageView imageViewHourly = new ImageView(this);
            imageViewHourly.setImageResource(images.get(weatherDataHourly.getWeatherCode()));
    
            imageViewHourly.setLayoutParams(layoutParams);
            layoutParams.setMargins((270 * i), 20, 0, 0);
            relativeLayoutHourly.addView(imageViewHourly);

            // Teplota
            TextView textViewHourlyTemperature = new TextView(this);
            textViewHourlyTemperature.setText(weatherDataHourly.getTemperature() + " " + appTemperatureUnit);
            textViewHourlyTemperature.setTextColor(ContextCompat.getColor(this, R.color.text));
            textViewHourlyTemperature.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            textViewHourlyTemperature.setGravity(Gravity.CENTER | Gravity.BOTTOM);
    
            textViewHourlyTemperature.setLayoutParams(layoutParams);
            layoutParams.setMargins((270 * i), 0, 0, 0);
            relativeLayoutHourly.addView(textViewHourlyTemperature);
            
            // Pravděpodobnost srážek
            TextView textViewHourlyPrecipitation = new TextView(this);
    
            // Skrytí hodnoty, v případě nuly
            textViewHourlyPrecipitation.setText((weatherDataHourly.getPrecipitation() != 0
                ? weatherDataHourly.getPrecipitation() + " %"
                : ""));
    
            textViewHourlyPrecipitation.setTextColor(ContextCompat.getColor(this, R.color.text));
            textViewHourlyPrecipitation.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            textViewHourlyPrecipitation.setGravity(Gravity.CENTER | Gravity.BOTTOM);
    
            textViewHourlyPrecipitation.setLayoutParams(layoutParams);
            layoutParams.setMargins((270 * i), 60, 0, 10);
            relativeLayoutHourly.addView(textViewHourlyPrecipitation);
        }
        
// Denní předpověď ////////////////////////////////////////////////////////////////////////////////////////

        weatherDataDailyList = (List<WeatherDataDaily>) getIntent().getSerializableExtra("weatherDataDailyList");
        
        // ID komponent z layoutu
        int[] textViewIDsDate = new int[] {R.id.textViewDailyDatePlusOne, R.id.textViewDailyDatePlusTwo, R.id.textViewDailyDatePlusThree, R.id.textViewDailyDatePlusFour};
        int[] textViewIDsPrecipitation = new int[] {R.id.textViewDailyPrecipitationPlusOne, R.id.textViewDailyPrecipitationPlusTwo, R.id.textViewDailyPrecipitationPlusThree, R.id.textViewDailyPrecipitationPlusFour};
        int[] imageViewIDs = new int[] {R.id.imageViewDailyPlusOne, R.id.imageViewDailyPlusTwo, R.id.imageViewDailyPlusThree, R.id.imageViewDailyPlusFour};
        int[] textViewIDsTempMax = new int[] {R.id.textViewTempMaxPlusOne, R.id.textViewTempMaxPlusTwo, R.id.textViewTempMaxPlusThree, R.id.textViewTempMaxPlusFour};
        int[] textViewIDsTempMin = new int[] {R.id.textViewTempMinPlusOne, R.id.textViewTempMinPlusTwo, R.id.textViewTempMinPlusThree, R.id.textViewTempMinPlusFour};
        
        for (int i = 0; i < weatherDataDailyList.size(); i++) {
            
            WeatherDataDaily weatherDataDaily = weatherDataDailyList.get(i);
            
            // Datum předpovědi
            TextView textViewDailyDate = findViewById(textViewIDsDate[i]);
            calendar = weatherDataDaily.getObservationTime();
    
            SimpleDateFormat dateFormat = new SimpleDateFormat("EE dd.MM.");
            String dayOfWeek = dateFormat.format(calendar.getTime());
            textViewDailyDate.setText(StringUtils.capitalize(dayOfWeek));
    
            // Pravděpodobnost srážek
            TextView textViewDailyPrecipitation = findViewById(textViewIDsPrecipitation[i]);
    
            // Skrytí hodnoty, v případě nuly
            textViewDailyPrecipitation.setText((weatherDataDaily.getPrecipitation() != 0
                ? weatherDataDaily.getPrecipitation() + " %"
                : ""));
    
            // Obrázek předpovědi
            ImageView imageViewDaily = findViewById(imageViewIDs[i]);
            imageViewDaily.setImageResource(images.get(weatherDataDaily.getWeatherCode()));
            
            // Maximální teplota
            TextView textViewDailyTempMax = findViewById(textViewIDsTempMax[i]);
            textViewDailyTempMax.setText(weatherDataDaily.getTemperatureMax() + " " + appTemperatureUnit);
            
            // Minimální teplota
            TextView textViewDailyTempMin = findViewById(textViewIDsTempMin[i]);
            textViewDailyTempMin.setText(weatherDataDaily.getTemperatureMin() + " " + appTemperatureUnit);
        }
        
// Vyhledání nové polohy /////////////////////////////////////////////////////////////////////////////////
    
        Places.initialize(getApplicationContext(), GOOGLE_API_KEY, Locale.getDefault());
        
        editTextLocation = findViewById(R.id.editTextLocation);
        editTextLocation.setFocusable(false);
        editTextLocation.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
        
                List<Place.Field> fields = Arrays.asList(Place.Field.LAT_LNG);
                
                // Zobrazení dialogu vyhledání polohy
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                    .setCountry(Locale.getDefault().getCountry())
                    .build(getApplicationContext());
                
                startActivityForResult(intent, LOCATION_REQUEST_CODE);
            }
        });
    }
    
    
    /**
     * Výsledek aktivity
     *
     * @param requestCode - kód požadavku
     * @param resultCode - kód výsledku
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == LOCATION_REQUEST_CODE && resultCode == RESULT_OK) {
            
            Place place = Autocomplete.getPlaceFromIntent(data);
            
            // Změna polohy
            changeLocation(place.getLatLng().latitude, place.getLatLng().longitude);
            
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
    
            Toast.makeText(getApplicationContext(), R.string.error_new_location, Toast.LENGTH_LONG).show();
            
            Status status = Autocomplete.getStatusFromIntent(data);
            System.out.println(status.getStatusMessage());
        }
    }
    
    
    /**
     * Změna polohy
     *
     * @param latitude - zeměpisná šířka
     * @param longitude - zeměpisná délka
     */
    private void changeLocation(double latitude, double longitude) {
    
        Connection connection = new Connection(getApplicationContext());
        
        // Získání polohy
        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
        
        try {
            
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
    
            // Ochrana před NULL
            String locality = (addresses.get(0).getLocality() != null
                ? addresses.get(0).getLocality()
                : addresses.get(0).getAdminArea());
            
            // Zkrácení lokality
            if (locality.length() > 22) {
                
                locality = locality.substring(0, 22);
                
                // Index poslední mezery
                int lastSpaceIndex = locality.lastIndexOf(" ");
                locality = locality.substring(0, lastSpaceIndex) + ".. ";
            }
            
            // Nastavení polohy
            location = locality + ", " + addresses.get(0).getCountryName();
            
            // Konstrola připojení k internetu
            if (connection.isOnline()) {

                // Odeslání requestů na API server
                weatherData = new WeatherData(getApplicationContext(), latitude, longitude);
                weatherData.sendRequests();

            } else {

                // Zobrazení chybového dialogu
                connection.showNoConnectionDialog(MainActivity.this);
            }

            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                
                @Override
                public void run() {
                    
                    // Znovunačtení hlavní aktivity s novými daty
                    if (weatherData.isWeatherDataSet()) {

                        WeatherDataCurrent weatherDataCurrent = weatherData.getWeatherDataCurrent();
                        List<WeatherDataHourly> weatherDataHourlyList = weatherData.getWeatherDataHourlyList();
                        List<WeatherDataDaily> weatherDataDailyList = weatherData.getWeatherDataDailyList();

                        Intent mainActivity = new Intent(MainActivity.this, MainActivity.class);
                        mainActivity.putExtra("isDay", isDay);
                        mainActivity.putExtra("location", location);
                        mainActivity.putExtra("weatherDataCurrent", weatherDataCurrent);
                        mainActivity.putExtra("weatherDataHourlyList", (Serializable) weatherDataHourlyList);
                        mainActivity.putExtra("weatherDataDailyList", (Serializable) weatherDataDailyList);

                        startActivity(mainActivity);
                        
                    // Opakování smyčky
                    } else handler.postDelayed(this, 500);
                }
            };
            
            runnable.run();
            
        } catch (IOException e) {

            Toast.makeText(getApplicationContext(), R.string.error_location, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
    
}