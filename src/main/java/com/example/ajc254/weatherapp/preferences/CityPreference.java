package com.example.ajc254.weatherapp.preferences;

import android.content.Context;
import android.content.SharedPreferences;


public class CityPreference {

    SharedPreferences cityPrefs;

    /**
     * Constructs a city preference object, to manage the 'city_location' shared preference.
     *
     * @param context The application context.
     */
    public CityPreference(Context context){
        cityPrefs = context.getSharedPreferences("city_location", context.MODE_PRIVATE);
    }

    /**
     * Gets the current value of the city_location preference.
     *
     * @return The city location as a string.
     */
    public String getCity(){

        // London is the default location, if the preference is not yet set.
        return cityPrefs.getString("city", "London");
    }

    /**
     * Update the city_location preference.
     *
     * @param city The new city_location value.
     */
    public void setCity(String city){
        cityPrefs.edit().putString("city", city).commit();
    }
}
