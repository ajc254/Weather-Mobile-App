package com.example.ajc254.weatherapp.fragments;

import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ajc254.weatherapp.R;
import com.example.ajc254.weatherapp.data.RenderData;
import com.example.ajc254.weatherapp.database.DatabaseCommands;
import com.example.ajc254.weatherapp.database.WeatherDatabase;


public class TodayFragment extends Fragment {

    private TextView cityField;
    private TextView currentTemperatureField;
    private TextView weatherIcon;
    private TextView weatherDescription;
    private TextView leftDetails;
    private TextView rightDetails;
    private DatabaseCommands dbCommands;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_today, container, false);
        cityField = (TextView)rootView.findViewById(R.id.city_field);
        currentTemperatureField = (TextView)rootView.findViewById(R.id.current_temperature_field);
        weatherIcon = (TextView)rootView.findViewById(R.id.weather_icon);
        weatherDescription = (TextView)rootView.findViewById(R.id.weather_description);
        leftDetails = (TextView)rootView.findViewById(R.id.left_details);
        rightDetails = (TextView)rootView.findViewById(R.id.right_details);
        // Set custom weather Icon font.
        Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(),  "fonts/weather.ttf");
        weatherIcon.setTypeface(custom_font);
        dbCommands = DatabaseCommands.getInstance();

        return rootView;
    }

    /**
     * Fetch and render the relevant forecast information from the database, populating the view.
     */
    public void populateWeatherData() {

        // Query database.
        Cursor result = dbCommands.getDetailedWeather(getActivity().getApplicationContext(), 0);

        // Ensure there is a row returned.
        if (!result.moveToFirst()) {
            cityField.setText("No Information Downloaded");
            return;
        }
        // Render and set the data returned from the query.
        String country = result.getString(result.getColumnIndex(WeatherDatabase.DBContent.COUNTRY_COL));
        String city = result.getString(result.getColumnIndex(WeatherDatabase.DBContent.CITY_COL));
        String temperature = result.getString(result.getColumnIndex(WeatherDatabase.DBContent.TEMP_COL));
        int weatherId = result.getInt(result.getColumnIndex(WeatherDatabase.DBContent.WEATHER_ID_COL));
        String forecastTime = result.getString(result.getColumnIndex(WeatherDatabase.DBContent.TIME_COL));
        String humidity = result.getString(result.getColumnIndex(WeatherDatabase.DBContent.HUMIDITY_COL));
        String windSpeed = result.getString(result.getColumnIndex(WeatherDatabase.DBContent.WIND_SPEED_COL));
        String pressure = result.getString(result.getColumnIndex(WeatherDatabase.DBContent.PRESSURE_COL));
        String cloudCoverage = result.getString(result.getColumnIndex(WeatherDatabase.DBContent.CLOUD_COVERAGE_COL));

        cityField.setText(city.toUpperCase() + "\n" + country + "\n" + forecastTime);
        currentTemperatureField.setText(temperature + "\u00b0");
        weatherDescription.setText(result.getString(result.getColumnIndex(WeatherDatabase.DBContent.WEATHER_DESCRIPTION_COL)));
        rightDetails.setText("Humidity:\n" + humidity + "%\n\n\n\n" + "Pressure:\n" + pressure + "hPa");
        leftDetails.setText("Wind Speed:\n" + windSpeed + "m/s\n\n\n\n" + "Clouds:\n" + cloudCoverage + "%");

        weatherIcon.setText(RenderData.getWeatherIcon(getActivity(), weatherId, forecastTime));
    }
}
