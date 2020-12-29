package cz.weatherapp.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cz.weatherapp.AppStorage;
import cz.weatherapp.Connection;
import cz.weatherapp.R;
import cz.weatherapp.weather_data.WeatherData;
import cz.weatherapp.weather_data.WeatherDataCurrent;
import cz.weatherapp.weather_data.WeatherDataDaily;
import cz.weatherapp.weather_data.WeatherDataHourly;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class WelcomeActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    
    // Kód pro oprávnění polohy
    private final int PERMISSION_LOCATION_REQUEST_CODE = 999;
    
    private WeatherData weatherData;
    private ImageSwitcher imageSwitcher;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private AppStorage appStorage;

    // True - aplikace zastavena
    // False - aplikace běží
    private boolean isStop;
    
    // Denní / Noční režim
    private boolean isDay;
    
    // Pole obrázků pro úvodní animaci
    private int[] images;

    // Index zobrazeného obrázku
    private int index = 0;
    
    // Poloha - [město, stát]
    private String location;
    
    // Jazyk aplikace
    private String applicationLanguage;
    

    /**
     * Výsledek požadavku na oprávnění
     *
     * @param requestCode - kód oprávnění
     * @param permissions - oprávnění
     * @param grantResults - výsledek
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    /**
     * Oprávnění uděleno
     *
     * @param requestCode - kód oprávnění
     * @param perms - List oprávnění
     */
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

        // Nastavení polohy zařízení
        setLocation();
    }

    
    /**
     * Oprávnění zamítnuto
     *
     * @param requestCode - kód oprávnění
     * @param perms - List oprávnění
     */
    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

        // Nastavení polohy zařízení
        setLocation();

        // Přesměrování do nastavení aplikace, při zvolení - [ok]
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {

            new AppSettingsDialog.Builder(this)
                .setTitle(R.string.settings_dialog_title)
                .setRationale(R.string.settings_dialog_text)
                .build()
                .show();

        // Ukončení aktivity, při zvolení - [zrušit]
        } else finish();
    }


    /**
     * Vytvoření aktivity
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    
        setContentView(R.layout.activity_welcome);
    
        // Získání jazyka aplikace
        appStorage = new AppStorage(getApplicationContext());
        applicationLanguage = appStorage.getLocale();
        
        // Při prvním spuštění aplikace
        if (applicationLanguage == "") {
        
            // Jazyky aplikace
            String[] applicationLanguages = getResources().getStringArray(R.array.application_languages_codes);
        
            // Vyhledání indexu v podporovaných jazycích aplikace
            int languageIndex = Arrays.asList(applicationLanguages).indexOf(Locale.getDefault().getLanguage());
        
            // Jazyk nenalezen -> výchozí jazyk EN
            if (languageIndex == -1) {
            
                applicationLanguage = "en";
            
            // Jazyk nalezen -> výběr podle indexu
            } else applicationLanguage = applicationLanguages[languageIndex];
        }
    
        // Změna jazyka aplikace + uložení nastavení
        appStorage.setAndSaveLocale(applicationLanguage);

        // Nastavení orientace na výšku
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        // Nastavení režimu celé obrazovky
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        // Denní režim
        if (hour >= 6 && hour <= 18) {
            
            isDay = true;
            images = new int[] {R.drawable.clear, R.drawable.partly_cloudy, R.drawable.cloudy, R.drawable.rain, R.drawable.tstorm, R.drawable.snow_heavy};

        // Noční režim
        } else {
            
            isDay = false;
            images = new int[] {R.drawable.clear_night, R.drawable.partly_cloudy_night, R.drawable.cloudy, R.drawable.rain, R.drawable.tstorm, R.drawable.snow_heavy};
        }

        imageSwitcher = findViewById(R.id.imageSwitcherLogo);
        imageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {

            @Override
            public View makeView() {

                ImageView imageView = new ImageView(getApplicationContext());
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

                return imageView;
            }
        });

        // Přiřazení animace
        Animation animationIn = AnimationUtils.loadAnimation(this, R.anim.welcom_animation);
        imageSwitcher.setInAnimation(animationIn);
        
        // Nastavení polohy zařízení
        setLocation();
    }


    /**
     * Zastavení aktivity
     */
    @Override
    protected void onStop() {
        super.onStop();

        isStop = true;
    }


    /**
     * Spuštění aktivity
     */
    @Override
    protected void onStart() {
        super.onStart();

        isStop = false;

        // Úvodní animace
        welcomeAnimationLoop();
    }


    /**
     * Smyčka úvodní animace
     */
    private void welcomeAnimationLoop() {

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                
                // Změna obrázku
                imageSwitcher.setImageResource(images[index]);
                index++;

                // Smyčka obrázků
                if (index == images.length) index = 0;

                // Přesměrování na hlavní aktivitu
                if (weatherData != null && weatherData.isWeatherDataSet()) {
                    
                    isStop = true;
                    
                    WeatherDataCurrent weatherDataCurrent = weatherData.getWeatherDataCurrent();
                    List<WeatherDataHourly> weatherDataHourlyList = weatherData.getWeatherDataHourlyList();
                    List<WeatherDataDaily> weatherDataDailyList = weatherData.getWeatherDataDailyList();
                    
                    Intent mainActivity = new Intent(WelcomeActivity.this, MainActivity.class);
                    mainActivity.putExtra("isDay", isDay);
                    mainActivity.putExtra("location", location);
                    mainActivity.putExtra("weatherDataCurrent", weatherDataCurrent);
                    mainActivity.putExtra("weatherDataHourlyList", (Serializable) weatherDataHourlyList);
                    mainActivity.putExtra("weatherDataDailyList", (Serializable) weatherDataDailyList);
                    
                    startActivity(mainActivity);
                    
                } else {
                    
                    // Opakování animace
                    if (!isStop) handler.postDelayed(this, 1600);
                }
            }
        };

        runnable.run();
    }
    

    /**
     * Nastavení polohy zařízení
     */
    private void setLocation() {
        
        Connection connection = new Connection(getApplicationContext());
        
        // Potřebaná oprávnění
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};

        // Oprávnění udělena
        if (ContextCompat.checkSelfPermission(this, perms[0]) == PackageManager.PERMISSION_GRANTED) {
            
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
        
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    
                    // Získání polohy
                    Location lastLocation = task.getResult();
                    Geocoder geocoder = new Geocoder(WelcomeActivity.this, Locale.getDefault());

                    try {
                        
                        List<Address> addresses = geocoder.getFromLocation(lastLocation.getLatitude(), lastLocation.getLongitude(), 1);
                        
                        // Ochrana před NULL
                        String locality = (addresses.get(0).getLocality() != null
                            ? addresses.get(0).getLocality()
                            : addresses.get(0).getAdminArea());
    
                        // Nastavení polohy
                        location = locality + ", " + addresses.get(0).getCountryName();
    
                        // Konstrola připojení k internetu
                        if (connection.isOnline()) {
        
                            // Odeslání requestů na API server
                            weatherData = new WeatherData(getApplicationContext(), lastLocation.getLatitude(), lastLocation.getLongitude());
                            weatherData.sendRequests();
        
                        } else {
        
                            // Zobrazení chybového dialogu
                            connection.showNoConnectionDialog(WelcomeActivity.this);
                            isStop = true;
                        }
                        
                    } catch (IOException e) {
                        
                        Toast.makeText(getApplicationContext(), R.string.error_location, Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            });

        // Oprávnění zamítnuta
        } else {

            // Vyvolání požadavku na oprávnění
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.permissions_dialog_text),
                PERMISSION_LOCATION_REQUEST_CODE,
                perms);
        }
    }
    
}