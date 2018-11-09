package com.example.ajc254.weatherapp.preferences;

import android.content.Context;
import android.content.SharedPreferences;


public class UpdatedPreference {

    SharedPreferences updatedPref;

    /**
     * Constructs a city preference object, to manage the 'last_updated' shared preference.
     *
     * @param context The application context.
     */
    public UpdatedPreference(Context context){
        updatedPref = context.getSharedPreferences("last_updated", context.MODE_PRIVATE);
    }

    /**
     * Gets the current value of the last_updated preference.
     *
     * @return The last updated date time as a string.
     */
    public String getLastUpdate() {
        return updatedPref.getString("last_updated", "Not updated yet");
    }

    /**
     * Update the last_updated preference.
     *
     * @param dateTime The new last_updated date time.
     */
    public void setNewUpdate(String dateTime){
        updatedPref.edit().putString("last_updated", dateTime).commit();
    }
}
