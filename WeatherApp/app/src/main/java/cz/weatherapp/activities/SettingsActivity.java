package cz.weatherapp.activities;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Locale;

import cz.weatherapp.AppStorage;
import cz.weatherapp.R;

public class SettingsActivity extends AppCompatActivity {
		
		private AppStorage appStorage;
		
		// Kódové zkratky jazyků - [en, cs ...]
		private String[] languagesCodes;
		
		// Názvy jazyků v jazyce aplikace
		// en -> [Czech, English]
		// cz -> [Čeština, Angličtina]
		private String[] languages;
		
		// Jazyk aplikace
		private String appLanguage = "";
		
		// Jednotka teploty aplikace
		private String appTemperatureUnit = "";
		
		// Jednotka rychlosti aplikace
		private String appSpeedUnit;
		
		// Režim předpovědi aplikace
		private int appForecastMode;
		
		
		/**
		 * Vytvoření aktivity
		 *
		 * @param savedInstanceState
		 */
		@Override
		protected void onCreate(Bundle savedInstanceState) {
				super.onCreate(savedInstanceState);
				
				setContentView(R.layout.activity_settings);
				
				// Změna jazyka aplikace + uložení nastavení
				appStorage = new AppStorage(SettingsActivity.this);
				appStorage.setAndSaveLocale(Locale.getDefault().getLanguage());
				
				// Nastavení orientace na výšku
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
				
				// Nastavení panelu akcí
				getSupportActionBar().setDisplayHomeAsUpEnabled(true);
				getSupportActionBar().setTitle(getString(R.string.settings));

// Výběr jazyka //////////////////////////////////////////////////////////////////////////////////////////////////////
				
				languagesCodes = getResources().getStringArray(R.array.application_languages_codes);
				languages = new String[languagesCodes.length];
				
				// Vytvoření pole jazyků, v jazyce aplikace
				for (int i = 0; i < languagesCodes.length; i++) {
						
						Locale locale = new Locale(languagesCodes[i]);
						String language = StringUtils.capitalize(locale.getDisplayLanguage());
						languages[i] = language;
				}
				
				TextView textViewLanguageLabel = findViewById(R.id.textViewLanguageLabel);
				textViewLanguageLabel.setText(getString(R.string.language) + " :");
				
				TextView textViewLanguage = findViewById(R.id.textViewLanguage);
				appLanguage = StringUtils.capitalize(Locale.getDefault().getDisplayLanguage());
				textViewLanguage.setText(appLanguage);
				
				Button buttonLanguage = findViewById(R.id.buttonLanguage);
				buttonLanguage.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
								
								// Vyhledání indexu, pro zvíraznění již nastaveného jazyka v dialogu
								int languageIndex = Arrays.asList(languages).indexOf(appLanguage);
								
								// Vytvoření dialogu
								AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
								builder.setTitle(getString(R.string.language_select_dialog) + ":");
								builder.setSingleChoiceItems(languages, languageIndex, new DialogInterface.OnClickListener() {
										
										@Override
										public void onClick(DialogInterface dialog, int i) {
												
												// Změna jazyka aplikace + uložení nastavení
												appStorage.setAndSaveLocale(languagesCodes[i]);
												dialog.dismiss();
												recreate();
										}
								});
								
								// Zobrazení dialogu
								AlertDialog alertDialog = builder.create();
								alertDialog.show();
						}
				});
				
// Výběr jednotky teploty ///////////////////////////////////////////////////////////////////////////////////////////
		
				appTemperatureUnit = appStorage.getTemperatureUnit();
				
				TextView textViewTempUnitLabel = findViewById(R.id.textViewTempUnitLabel);
				textViewTempUnitLabel.setText(getString(R.string.temperature_unit) + " :");
				
				TextView textViewTempUnit = findViewById(R.id.textViewTempUnit);
				textViewTempUnit.setText(appTemperatureUnit);
				
				Button buttonTempUnit = findViewById(R.id.buttonTempUnit);
				buttonTempUnit.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
								
								// Jednotky teploty
								String[] temperatureUnits = getResources().getStringArray(R.array.application_temperature_units);
								
								// Vyhledání indexu, pro zvíraznění již nastavené jednotky teploty v dialogu
								int temperatureUnitIndex = Arrays.asList(temperatureUnits).indexOf(appTemperatureUnit);
										
								// Vytvoření dialogu
								AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
								builder.setTitle(getString(R.string.temperature_unit_select_dialog) + ":");
								builder.setSingleChoiceItems(temperatureUnits, temperatureUnitIndex, new DialogInterface.OnClickListener() {
										
										@Override
										public void onClick(DialogInterface dialog, int i) {
												
												// Uložení jednotky teploty
												appStorage.saveTemperatureUnit(temperatureUnits[i]);
												dialog.dismiss();
												recreate();
										}
								});
								
								// Zobrazení dialogu
								AlertDialog alertDialog = builder.create();
								alertDialog.show();
						}
				});
				
// Výběr jednotky rychlosti //////////////////////////////////////////////////////////////////////////////////////////////
				
				appSpeedUnit = appStorage.getSpeedUnit();
				
				TextView textViewSpeedUnitLabel = findViewById(R.id.textViewSpeedUnitLabel);
				textViewSpeedUnitLabel.setText(getString(R.string.speed_unit) + " :");
				
				TextView textViewSpeedUnit = findViewById(R.id.textViewSpeedUnit);
				textViewSpeedUnit.setText(appSpeedUnit);
				
				Button buttonSpeedUnit = findViewById(R.id.buttonSpeedUnit);
				buttonSpeedUnit.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
								
								// Jednotky rychlosti
								String[] speedUnits = getResources().getStringArray(R.array.application_speed_units);
								
								// Vyhledání indexu, pro zvíraznění již nastavené jednotky rychlosti v dialogu
								int speedUnitIndex = Arrays.asList(speedUnits).indexOf(appSpeedUnit);
								
								// Vytvoření dialogu
								AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
								builder.setTitle(getString(R.string.speed_unit_select_dialog) + ":");
								builder.setSingleChoiceItems(speedUnits, speedUnitIndex, new DialogInterface.OnClickListener() {
										
										@Override
										public void onClick(DialogInterface dialog, int i) {
												
												// Uložení jednotky rychlosti
												appStorage.saveSpeedUnit(speedUnits[i]);
												dialog.dismiss();
												recreate();
										}
								});
								
								// Zobrazení dialogu
								AlertDialog alertDialog = builder.create();
								alertDialog.show();
						}
				});
				
// Výběr hodinové předpovědi //////////////////////////////////////////////////////////////////////////////////////////////////////
				
				appForecastMode = appStorage.getForecastMode();
				
				TextView textViewForecastLabel = findViewById(R.id.textViewForecastModeLabel);
				textViewForecastLabel.setText(getString(R.string.forecast_mode) + " :");
				
				TextView textViewForecast = findViewById(R.id.textViewForecastMode);
				textViewForecast.setText(appForecastMode + " " + getString(R.string.hours));
				
				Button buttonForecastMode = findViewById(R.id.buttonForecastMode);
				buttonForecastMode.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
						
								// Režimy předpovědi
								int[] forecastModes = getResources().getIntArray(R.array.application_forecast_modes);
								
								// Režimy předpovědi s jednoutkou - [6 hours, 12 hours, 18 hours, 24 hours]
								String[] forecastModesWithUnit = new String[forecastModes.length];
								
								// Přidání jednotky
								for (int i =0; i < forecastModes.length; i++) {
										forecastModesWithUnit[i] = forecastModes[i] + " " + getString(R.string.hours);
								}
								
								// Vyhledání indexu, pro zvíraznění již nastaveného režimu předpovědi v dialogu
								int forecastIndex = Arrays.asList(forecastModesWithUnit).indexOf(appForecastMode + " " + getString(R.string.hours));
								
								// Vytvoření dialogu
								AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
								builder.setTitle(getString(R.string.forecast_mode_select_dialog) + ":");
								builder.setSingleChoiceItems(forecastModesWithUnit, forecastIndex, new DialogInterface.OnClickListener() {
										
										@Override
										public void onClick(DialogInterface dialog, int i) {
												
												// Uložení režimu předpovědi
												appStorage.saveForecastMode(forecastModes[i]);
												dialog.dismiss();
												recreate();
										}
								});
								
								// Zobrazení dialogu
								AlertDialog alertDialog = builder.create();
								alertDialog.show();
						}
				});
		}
		
}