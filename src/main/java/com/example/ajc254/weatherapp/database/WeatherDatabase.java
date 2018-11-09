package com.example.ajc254.weatherapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;


public class WeatherDatabase extends SQLiteOpenHelper {


    /**
     * Contructor for the database class.
     *
     * @param context Application context.
     */
    public WeatherDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /*
    * Static nested class to define the database column content, for future reference.
    */
    public static class DBContent implements BaseColumns {

        public static final String TABLE_NAME = "forecast";
        public static final String CITY_COL = "city";
        public static final String COUNTRY_COL = "country";
        public static final String DAYS_IN_ADVANCE_COL = "days_in_advance";
        public static final String DATE_COL = "date";
        public static final String TIME_COL = "time";
        public static final String WEATHER_ID_COL = "weather_id";
        public static final String WEATHER_DESCRIPTION_COL = "weather_description";
        public static final String TEMP_COL = "temperature";
        public static final String HUMIDITY_COL = "humidity";
        public static final String PRESSURE_COL = "pressure";
        public static final String CLOUD_COVERAGE_COL = "cloud_coverage";
        public static final String WIND_SPEED_COL = "wind_speed";
    }


    public static final String DATABASE_NAME = "weather_database";
    public static int DATABASE_VERSION = 3;

    // SQL needed to construct the table if it doesn't exist already.
    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + DBContent.TABLE_NAME + " (" +
                    DBContent._ID + " INTEGER PRIMARY KEY NOT NULL," +
                    DBContent.CITY_COL + " TEXT," +
                    DBContent.COUNTRY_COL + " TEXT," +
                    DBContent.DAYS_IN_ADVANCE_COL + " INTEGER," +
                    DBContent.DATE_COL + " TEXT," +
                    DBContent.TIME_COL + " TEXT," +
                    DBContent.WEATHER_ID_COL + " INTEGER," +
                    DBContent.WEATHER_DESCRIPTION_COL + " TEXT," +
                    DBContent.TEMP_COL + " NUMERIC," +
                    DBContent.HUMIDITY_COL + " NUMERIC," +
                    DBContent.PRESSURE_COL + " NUMERIC," +
                    DBContent.CLOUD_COVERAGE_COL + " NUMERIC," +
                    DBContent.WIND_SPEED_COL + " NUMERIC)";

    // SQL to delete the table.
    private static final String SQL_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + DBContent.TABLE_NAME;


    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("FHDJKFHDJKFHDJKHDJFKHDJFHDDJK");
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_TABLE);
        onCreate(db);
        DATABASE_VERSION = newVersion;
    }
}


