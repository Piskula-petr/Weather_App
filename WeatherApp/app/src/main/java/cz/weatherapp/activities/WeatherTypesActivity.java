package cz.weatherapp.activities;

import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams;

import java.util.Locale;
import java.util.Map;

import cz.weatherapp.AppStorage;
import cz.weatherapp.R;
import cz.weatherapp.Resources;

public class WeatherTypesActivity extends AppCompatActivity {

    private AppStorage appStorage;
    private Resources resources;
    private RelativeLayout relativeLayoutWeatherTypes;
    
    // Mapa typů počasí - [ID obrázku, popisek]
    private Map<Integer, String> weatherTypes;
    
    // Denní / Noční režim
    private boolean isDay;
    
    
    /**
     * Vytvoření aktivity
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_weather_types);

        // Změna jazyka aplikace + uložení nastavení
        appStorage = new AppStorage(WeatherTypesActivity.this);
        appStorage.setAndSaveLocale(Locale.getDefault().getLanguage());

        // Nastavení orientace na výšku
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        // Nastavení panelu akcí
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.weather_types));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.primary)));
        
// Typy počasí ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
        isDay = getIntent().getBooleanExtra("isDay", true);
        
        relativeLayoutWeatherTypes = findViewById(R.id.relativeLayoutWeatherTypes);
        relativeLayoutWeatherTypes.setPadding(10, 10, 10, 10);
        
        // Získání typů počasí
        resources = new Resources(getApplicationContext());
        weatherTypes = resources.getWeatherTypes(isDay);
        
        LayoutParams layoutParamsImages = new LayoutParams(250, 450);
        LayoutParams layoutParamsPredictions = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 450);
        
        int index = 0;
        
        for (Map.Entry<Integer, String> entry : weatherTypes.entrySet()) {
            
            // Obrázek předpovědi
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(entry.getKey());
            imageView.setLayoutParams(layoutParamsImages);
            layoutParamsImages.setMargins(100, (280 * index), 0, 0);
            relativeLayoutWeatherTypes.addView(imageView);
    
            // Popis předpovědi
            TextView textView = new TextView(this);
            textView.setText(entry.getValue());
            textView.setTextColor(getResources().getColor(R.color.text));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            textView.setGravity(Gravity.CENTER | Gravity.LEFT);
            
            textView.setLayoutParams(layoutParamsPredictions);
            layoutParamsPredictions.setMargins(400, (280 * index), 0, 0);
            relativeLayoutWeatherTypes.addView(textView);
    
            index++;
        }
    }
    
}