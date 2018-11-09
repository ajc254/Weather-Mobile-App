package com.example.ajc254.weatherapp;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.ajc254.weatherapp.data.RenderData;
import com.example.ajc254.weatherapp.database.DatabaseCommands;
import com.example.ajc254.weatherapp.database.WeatherDatabase;

public class DetailedActivity extends AppCompatActivity {

    private TextView cityField;
    private TextView currentTemperatureField;
    private TextView weatherIcon;
    private TextView weatherDescription;
    private TextView leftDetails;
    private TextView rightDetails;
    private TextView dayOfWeek;
    private DatabaseCommands dbCommands;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);
        cityField = (TextView)findViewById(R.id.city_field);
        currentTemperatureField = (TextView)findViewById(R.id.current_temperature_field);
        weatherIcon = (TextView)findViewById(R.id.weather_icon);
        weatherDescription = (TextView)findViewById(R.id.weather_description);
        leftDetails = (TextView)findViewById(R.id.left_details);
        rightDetails = (TextView)findViewById(R.id.right_details);
        dayOfWeek = (TextView)findViewById(R.id.detailed_day);
        // Set custom weather icons font.
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/weather.ttf");
        weatherIcon.setTypeface(custom_font);
        dbCommands = DatabaseCommands.getInstance();

        // Set on click listener to head back to the home activity via an explicit intent.
        findViewById(R.id.back_home).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent backHome = new Intent(getApplicationContext(), HomeActivity.class);

                // This flag ensures a new activity is not created if a home activity already exists.
                backHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                // A refresh is not needed in the home view.
                backHome.putExtra("refresh", false);
                startActivity(backHome);
            }
        });
        populateWeatherData();
    }

    /**
     * Render and display the correct forecast day that has been clicked on.
     */
    public void populateWeatherData() {

        Intent passedIntent = getIntent();

        // Determine which forecast day the user clicked on.
        int daysInAdvance = passedIntent.getIntExtra("days_in_advance", 0);
        assert daysInAdvance != 0 : "Error, extra intent info not recieved";

        // Query the database.
        Cursor result = dbCommands.getDetailedWeather(getApplicationContext(), daysInAdvance);

        // Ensure a result row was fetched.
        if (!result.moveToFirst()) {
            cityField.setText("No Information Downloaded");
            return;
        }
        // Render and display the data.
        String country = result.getString(result.getColumnIndex(WeatherDatabase.DBContent.COUNTRY_COL));
        String city = result.getString(result.getColumnIndex(WeatherDatabase.DBContent.CITY_COL));
        String temperature = result.getString(result.getColumnIndex(WeatherDatabase.DBContent.TEMP_COL));
        int weatherId = result.getInt(result.getColumnIndex(WeatherDatabase.DBContent.WEATHER_ID_COL));
        String forecastTime = result.getString(result.getColumnIndex(WeatherDatabase.DBContent.TIME_COL));
        String humidity = result.getString(result.getColumnIndex(WeatherDatabase.DBContent.HUMIDITY_COL));
        String windSpeed = result.getString(result.getColumnIndex(WeatherDatabase.DBContent.WIND_SPEED_COL));
        String pressure = result.getString(result.getColumnIndex(WeatherDatabase.DBContent.PRESSURE_COL));
        String cloudCoverage = result.getString(result.getColumnIndex(WeatherDatabase.DBContent.CLOUD_COVERAGE_COL));


        cityField.setText(city.toUpperCase() + "\n" + country);
        currentTemperatureField.setText(temperature + "\u00b0");
        weatherDescription.setText(result.getString(result.getColumnIndex(WeatherDatabase.DBContent.WEATHER_DESCRIPTION_COL)));
        rightDetails.setText("Humidity:\n" + humidity + "%\n\n\n\n" + "Pressure:\n" + pressure + "hPa");
        leftDetails.setText("Wind Speed:\n" + windSpeed + "m/s\n\n\n\n" + "Clouds:\n" + cloudCoverage + "%");
        dayOfWeek.setText(RenderData.getDayOfWeek(daysInAdvance));


        weatherIcon.setText(RenderData.getWeatherIcon(this, weatherId, forecastTime));
    }
}
