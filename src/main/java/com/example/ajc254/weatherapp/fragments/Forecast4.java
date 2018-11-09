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

/**
 * Created by acoss on 24/03/2018.
 */

public class Forecast4 extends Fragment {

    private TextView day;
    private TextView weatherIcon;
    private TextView temperatureField;
    private DatabaseCommands dbCommands;

    public static String DAY_OF_WEEK;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_forecast4, container, false);
        day = view.findViewById(R.id.forecast_4_day);
        weatherIcon = view.findViewById(R.id.forecast_4_icon);
        temperatureField = view.findViewById(R.id.forecast_4_temp);
        // Set custom weather Icon font.
        Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(),  "fonts/weather.ttf");
        weatherIcon.setTypeface(custom_font);
        dbCommands = DatabaseCommands.getInstance();

        return view;
    }

    /**
     * Fetch and render the relevant forecast information from the database, populating the view.
     */
    public void populateWeatherData() {

        // Query database.
        Cursor result = dbCommands.getForecastWeather(getActivity().getApplicationContext(), 4);

        // Ensure there is a row returned.
        if (!result.moveToFirst()) {
            return;
        }
        // Render and set the data returned from the query.
        String temperature = result.getString(result.getColumnIndex(WeatherDatabase.DBContent.TEMP_COL));
        int weatherId = result.getInt(result.getColumnIndex(WeatherDatabase.DBContent.WEATHER_ID_COL));
        String forecastTime = result.getString(result.getColumnIndex(WeatherDatabase.DBContent.TIME_COL));
        String forecastDate = result.getString(result.getColumnIndex(WeatherDatabase.DBContent.DATE_COL));

        temperatureField.setText(temperature + "\u00b0");
        weatherIcon.setText(RenderData.getWeatherIcon(getActivity(), weatherId, forecastTime));
        String dayOfWeek = RenderData.calculateDayOfWeek(forecastDate);
        day.setText(dayOfWeek);
        DAY_OF_WEEK = dayOfWeek;

    }


}
