package com.example.ajc254.weatherapp.data;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.ajc254.weatherapp.HomeActivity;
import com.example.ajc254.weatherapp.database.DatabaseCommands;
import com.example.ajc254.weatherapp.database.WeatherDatabase;
import com.example.ajc254.weatherapp.preferences.UpdatedPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/*
 * Asynchronous task, used for downloading and storing relevant weather data.
 */
public class UpdateStoredData extends AsyncTask<Void, Void, Void> {

    private DatabaseCommands dbCommands;
    private Context context;
    private DateFormat dateFormat;
    private boolean internetIssue;
    private HomeActivity UI;
    private ProgressDialog dialog;
    private UpdatedPreference lastUpdated;

    /**
     * Construct the downloader Asyn task.
     *
     * @param context Context of the application.
     * @param UI The Home Activity.
     */
    public UpdateStoredData(Context context, HomeActivity UI) {

        this.context = context;
        dbCommands = DatabaseCommands.getInstance();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("BST"));
        internetIssue = false;
        this.UI = UI;
        lastUpdated = new UpdatedPreference(context);
        dialog = new ProgressDialog(UI);
    }

    @Override
    protected void onPreExecute() {

        // Start the progress spinner.
        dbCommands.clearWeatherData(context);
        this.dialog.setMessage("Downloading Weather...\nPlease Wait.");
        this.dialog.show();
    }

    @Override
    protected Void doInBackground(Void... params) {

        // Retrieve all the needed data from the API.
        JSONObject exeterData = RetrieveData.getJSON("Exeter");
        if(isCancelled()) {return null;}    // Periodically check if cancelled, due to screen rotate.
        if (exeterData == null) { internetIssue = true; return null; } // Check for internet issues.

        JSONObject bristolData = RetrieveData.getJSON("Bristol");
        if(isCancelled()) {return null;}
        if (bristolData == null) { internetIssue = true; return null; }

        JSONObject londonData = RetrieveData.getJSON("London");
        if(isCancelled()) {return null;}
        if (londonData == null) { internetIssue = true; return null; }

        // If no internet issue, store the relevant data into the database.
        else {
            dbCommands.clearWeatherData(context);
            storeRelevantData(exeterData);

            storeRelevantData(bristolData);

            storeRelevantData(londonData);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {

        if(internetIssue) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            /* Notify the user of an internet issue.
            *NOTE: It takes a short while of attempting to downloads before detecting an internet issue.
            */
            Toast.makeText(context, "Internet Connection Issue", Toast.LENGTH_LONG).show();
            // Use previously downloaded data to populate the views.
            UI.repopulateData();
            UI.refreshUpdatedTime();
        }
        // Make the UI thread render and populate the new data, and modify 'Last updated' time to now.
        else {
            SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yyyy 'at' HH:mm:ss z");
            parser.setTimeZone(TimeZone.getTimeZone("Europe/London"));
            lastUpdated.setNewUpdate(parser.format(new Date()));
            UI.repopulateData();
            UI.refreshUpdatedTime();
        }
        // Remove spinning progress dialog.
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    protected void onCancelled() {

        //Perform no UI computation if cancelled due to screen orientation change.
        return;
    }


    /**
     * Extracts the relevant weather data needed from a JSONObject API response, and stores it
     * into the database.
     *
     *
     * @param data The JSONObject of the parsed API response.
     */
    protected void storeRelevantData(JSONObject data) {

        try {

            JSONObject cityJSON = data.getJSONObject("city");
            String city = cityJSON.getString("name");
            String country = cityJSON.getString("country");

            JSONArray entries = data.getJSONArray("list");

            // Insert current weather today.
            JSONObject today = entries.getJSONObject(0);

            // Extract the most up to date information available today.
            insertIntoDatabase(entries.getJSONObject(0), city, country, 0);

            // Calculate the dates of the days we need.
            int todaysUnixTimestamp = today.getInt("dt");
            Date currentDate = new java.util.Date((long)todaysUnixTimestamp*1000);
            Calendar c = Calendar.getInstance();
            c.setTime(currentDate);
            c.add(Calendar.DATE, 1);
            String oneDayAhead = dateFormat.format(c.getTime());
            c.add(Calendar.DATE, 1);
            String twoDaysAhead = dateFormat.format(c.getTime());
            c.add(Calendar.DATE, 1);
            String threeDaysAhead = dateFormat.format(c.getTime());
            c.add(Calendar.DATE, 1);
            String fourDaysAhead = dateFormat.format(c.getTime());

            for (int i=1; i<entries.length(); i++) {

                JSONObject entry = entries.getJSONObject(i);
                String dateTime = entry.getString("dt_txt");

                // Extract midday forecasts for future days.
                if (extractTime(dateTime).equals("12:00:00") && extractDate(dateTime).equals(oneDayAhead)) {
                    insertIntoDatabase(entry, city, country, 1);
                } else if (extractTime(dateTime).equals("12:00:00") && extractDate(dateTime).equals(twoDaysAhead)) {
                    insertIntoDatabase(entry, city, country, 2);
                } else if (extractTime(dateTime).equals("12:00:00") && extractDate(dateTime).equals(threeDaysAhead)) {
                    insertIntoDatabase(entry, city, country, 3);
                } else if (extractTime(dateTime).equals("12:00:00") && extractDate(dateTime).equals(fourDaysAhead)) {
                    insertIntoDatabase(entry, city, country, 4);
                }
            }
        }
        catch(JSONException e) {
            System.out.println(e);
        }
    }

    /**
     * String manipulation on a date time to get just the Date.
     *
     * @param dateTimeText The date Time to manipulate.
     *
     * @return The date.
     */
    private String extractDate(String dateTimeText) {
        return dateTimeText.substring(0, 10);
    }

    /**
     * String manipulation on a date time to get just the Date.
     *
     * @param dateTimeText The date Time to manipulate.
     *
     * @return The date.
     */
    private String extractTime(String dateTimeText) {
        return dateTimeText.substring(11);
    }

    /**
     * Takes a JSONObject and stores it's weather information in the database.
     *
     * @param weatherInfo The JSON object with the weather information.
     * @param city The city representing the weather.
     * @param country The country representing the weather.
     * @param daysAhead The numeber of days this weather data is ahead of the current date.
     */
    protected void insertIntoDatabase(JSONObject weatherInfo, String city, String country, int daysAhead) {

        try {

            JSONObject weatherDetails = weatherInfo.getJSONArray("weather").getJSONObject(0);
            JSONObject mainDetails = weatherInfo.getJSONObject("main");

            // Use the database singleton class to handle the SQL.
            dbCommands.insertIntoDatabase(
                context,
                WeatherDatabase.DBContent.TABLE_NAME,
                city,
                country,
                daysAhead,
                extractDate(weatherInfo.getString("dt_txt")),
                extractTime(weatherInfo.getString("dt_txt")),
                weatherDetails.getInt("id"),
                weatherDetails.getString("description"),
                mainDetails.getDouble("temp"),
                mainDetails.getDouble("humidity"),
                mainDetails.getDouble("pressure"),
                weatherInfo.getJSONObject("clouds").getDouble("all"),
                weatherInfo.getJSONObject("wind").getDouble("speed")
            );
        }
        catch(JSONException e) {
            System.out.println(e);
        }
    }
}
