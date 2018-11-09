package com.example.ajc254.weatherapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.ajc254.weatherapp.preferences.CityPreference;


/*
Singleton class to deal with database interaction.
 */
public class DatabaseCommands {

    private static DatabaseCommands instance = null;

    private SQLiteDatabase readableDatabase;
    private SQLiteDatabase writeableDatabase;

    /**
     * utilise lazy instantiate on readable/writeable Database.
     */
    private DatabaseCommands() {
        readableDatabase = null;
        writeableDatabase = null;
    }

    /**
     * Get or create an instance of itself.
     *
     * @return The private instance of itself.
     */
    public static DatabaseCommands getInstance() {

        if (instance == null) {
            instance = new DatabaseCommands();
        }
        return instance;
    }

    /**
     * Get or create an instance of the readable database.
     *
     * @param context Context of the application.
     */
    private void checkReadableCreated(Context context) {

        if (readableDatabase == null) {
            readableDatabase = new WeatherDatabase(context).getReadableDatabase();
        }
    }

    /**
     * Get or create an instance of the writeable database.
     *
     * @param context Context of the application.
     */
    private void checkWriteableCreated(Context context) {

        if (writeableDatabase == null) {
            writeableDatabase = new WeatherDatabase(context).getWritableDatabase();
        }
    }


    /**
     * Query the database for basic data to populate the forecast views.
     *
     * @param context The context of the application.
     * @param daysInAdvance The days in advance the forecast needs to be.
     *
     * @return Cursor with the database result.
     */
    public Cursor getForecastWeather(Context context, int daysInAdvance) {

        checkReadableCreated(context);

        // Get the city preference currently set.
        CityPreference cityPref = new CityPreference(context);

        // Define the columns needed.
        String[] columns = {
                WeatherDatabase.DBContent.TEMP_COL,
                WeatherDatabase.DBContent.TIME_COL,
                WeatherDatabase.DBContent.DATE_COL,
                WeatherDatabase.DBContent.WEATHER_ID_COL};

        // Only retrieve rows with the specified city and number of days in advance.
        String selection =
                WeatherDatabase.DBContent.DAYS_IN_ADVANCE_COL + " = ? AND " +
                        WeatherDatabase.DBContent.CITY_COL + " = ?";

        String[] selectionArgs = { Integer.toString(daysInAdvance), cityPref.getCity() };

        Cursor result = readableDatabase.query(WeatherDatabase.DBContent.TABLE_NAME, columns, selection, selectionArgs, null, null, null);
        return result;
    }


    /**
     * Query the database for detailed data to populate the forecast views.
     *
     * @param context The context of the application.
     * @param daysInAdvance The days in advance the forecast needs to be.
     *
     * @return Cursor with the database result.
     */
    public Cursor getDetailedWeather(Context context, int daysInAdvance) {

        checkReadableCreated(context);

        // Get the city preference currently set.
        CityPreference cityPref = new CityPreference(context);

        // Define the columns needed.
        String[] columns = {
                WeatherDatabase.DBContent.TEMP_COL,
                WeatherDatabase.DBContent.CITY_COL,
                WeatherDatabase.DBContent.TIME_COL,
                WeatherDatabase.DBContent.COUNTRY_COL,
                WeatherDatabase.DBContent.WEATHER_ID_COL,
                WeatherDatabase.DBContent.WEATHER_DESCRIPTION_COL,
                WeatherDatabase.DBContent.WIND_SPEED_COL,
                WeatherDatabase.DBContent.PRESSURE_COL,
                WeatherDatabase.DBContent.HUMIDITY_COL,
                WeatherDatabase.DBContent.CLOUD_COVERAGE_COL};

        String selection =
                WeatherDatabase.DBContent.DAYS_IN_ADVANCE_COL + " = ? AND " +
                        WeatherDatabase.DBContent.CITY_COL + " = ?";

        String[] selectionArgs = { Integer.toString(daysInAdvance), cityPref.getCity() };

        Cursor result = readableDatabase.query(WeatherDatabase.DBContent.TABLE_NAME, columns, selection, selectionArgs, null, null, null);
        return result;
    }

    /**
     * Perform a row insertion into the database.
     *
     * @param context Application context.
     * @param tableName Table name.
     * @param city City name.
     * @param country Country name.
     * @param daysInAdvance Days ahead of the current date.
     * @param date Date of the forecast.
     * @param time Time of the forecast.
     * @param weatherId WeatherID of the forecast.
     * @param weatherDescription Desciption of the weather.
     * @param temperature Forecast temperature.
     * @param humidity Forecast humidity.
     * @param pressure Forecast presure.
     * @param cloudCoverage Forecast cloud coverage.
     * @param windSpeed Forecast wind speed.
     *
     * @return ID of the row if successful insertion.
     */
    public long insertIntoDatabase( Context context,
                             String tableName,
                             String city,
                             String country,
                             int daysInAdvance,
                             String date,
                             String time,
                             int weatherId,
                             String weatherDescription,
                             double temperature,
                             double humidity,
                             double pressure,
                             double cloudCoverage,
                             double windSpeed ) {

        checkWriteableCreated(context);

        // Use ContentValues to structure the insert.
        ContentValues values = new ContentValues();

        values.put(WeatherDatabase.DBContent.CITY_COL, city);
        values.put(WeatherDatabase.DBContent.COUNTRY_COL, country);
        values.put(WeatherDatabase.DBContent.DAYS_IN_ADVANCE_COL, daysInAdvance);
        values.put(WeatherDatabase.DBContent.DATE_COL, date);
        values.put(WeatherDatabase.DBContent.TIME_COL, time);
        values.put(WeatherDatabase.DBContent.WEATHER_ID_COL, weatherId);
        values.put(WeatherDatabase.DBContent.WEATHER_DESCRIPTION_COL, weatherDescription);
        values.put(WeatherDatabase.DBContent.TEMP_COL, temperature);
        values.put(WeatherDatabase.DBContent.HUMIDITY_COL, humidity);
        values.put(WeatherDatabase.DBContent.PRESSURE_COL, pressure);
        values.put(WeatherDatabase.DBContent.CLOUD_COVERAGE_COL, cloudCoverage);
        values.put(WeatherDatabase.DBContent.WIND_SPEED_COL, windSpeed);

        return writeableDatabase.insert(tableName, null, values);
    }

    /**
     * Clear the contents of the forecast information in the database.
     *
     * @param context The application context.
     */
    public void clearWeatherData(Context context) {

        checkWriteableCreated(context);
        writeableDatabase.execSQL("DELETE FROM " + WeatherDatabase.DBContent.TABLE_NAME);
    }
}
