package com.example.ajc254.weatherapp.data;

import android.content.Context;

import com.example.ajc254.weatherapp.R;
import com.example.ajc254.weatherapp.fragments.Forecast1;
import com.example.ajc254.weatherapp.fragments.Forecast2;
import com.example.ajc254.weatherapp.fragments.Forecast3;
import com.example.ajc254.weatherapp.fragments.Forecast4;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;



public class RenderData {


    /**
     * Determines a weather icon based on an openweathermap API weather id.
     *
     * @param context Context of the application.
     * @param weatherId The openweathermap API id.
     * @param forecastTime The forecast time for the icon.
     *
     * @return The weather icon as a string.
     */
    public static String getWeatherIcon(Context context, int weatherId, String forecastTime){

        SimpleDateFormat parser = new SimpleDateFormat("HH:mm:ss");
        Date forecast=null, sunUp=null, sunDown=null;

        // Define night and day.
        try {
            sunDown = parser.parse("19:00:00");
            sunUp = parser.parse("06:00:00");
            forecast = parser.parse(forecastTime);
        } catch(ParseException e) {
            System.out.println(e);
        }

        // Weather conditions can be distinguished from the first digit of the weather ID.
        int id = weatherId / 100;
        String icon = "";

        if(weatherId == 800){
            // Check whether to return sun or clear night.
            if(forecast.before(sunDown) && forecast.after(sunUp)) {
                icon = context.getString(R.string.weather_sunny);
            } else {
                icon = context.getString(R.string.weather_clear_night);
            }
        } else {
            switch(id) {
                case 2 : icon = context.getString(R.string.weather_thunder);
                    break;
                case 3 : icon = context.getString(R.string.weather_drizzle);
                    break;
                case 7 : icon = context.getString(R.string.weather_foggy);
                    break;
                case 8 : icon = context.getString(R.string.weather_cloudy);
                    break;
                case 6 : icon = context.getString(R.string.weather_snowy);
                    break;
                case 5 : icon = context.getString(R.string.weather_rainy);
                    break;
            }
        }
        return icon;
    }

    /**
     * Calculate the day of the week from a date String.
     *
     * @param forecastDate The date. (In format yyyy-MM-dd)
     *
     * @return The day of the week.
     */
    public static String calculateDayOfWeek(String forecastDate) {

        DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = format1.parse(forecastDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DateFormat format2 = new SimpleDateFormat("EEEE");

        String day = format2.format(date);

        return day;
    }

    /**
     * Obtain the day of the week of a certain forecast fragment, with the specified
     * number of days ahead.
     *
     * @param daysInAdvance Number of days ahead of the current date.
     *
     * @return String containing the day of the week found, null if none assigned yet.
     */
    public static String getDayOfWeek(int daysInAdvance) {

        switch (daysInAdvance) {

            case 1:
                return Forecast1.DAY_OF_WEEK;

            case 2:
                return Forecast2.DAY_OF_WEEK;

            case 3:
                return Forecast3.DAY_OF_WEEK;

            case 4:
                return Forecast4.DAY_OF_WEEK;
        }
        return null;
    }
}
