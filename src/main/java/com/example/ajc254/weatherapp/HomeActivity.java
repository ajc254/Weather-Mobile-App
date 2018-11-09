package com.example.ajc254.weatherapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.ajc254.weatherapp.data.UpdateStoredData;
import com.example.ajc254.weatherapp.fragments.Forecast1;
import com.example.ajc254.weatherapp.fragments.Forecast2;
import com.example.ajc254.weatherapp.fragments.Forecast3;
import com.example.ajc254.weatherapp.fragments.Forecast4;
import com.example.ajc254.weatherapp.fragments.TodayFragment;
import com.example.ajc254.weatherapp.preferences.UpdatedPreference;


public class HomeActivity extends AppCompatActivity {


    private TodayFragment todayFragment;
    private Forecast1 forecast1;
    private Forecast2 forecast2;
    private Forecast3 forecast3;
    private Forecast4 forecast4;
    private UpdatedPreference updatedPref;
    private TextView lastUpdatedField;
    private UpdateStoredData downloader;

    private static boolean sessionFirstUpdate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Only download new data automatically if it has not been done yet this app session.
        if(!sessionFirstUpdate) {
            updateData();
        }

        // Set the button to enable the ability to change the city click listener.
        findViewById(R.id.change_city_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent citySelection = new Intent(getApplicationContext(), LocationActivity.class);
                startActivity(citySelection);
            }
        });

        // Set the manual download refresh button click listener.
        findViewById(R.id.update_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateData();
            }
        });

        // Obtain a reference of all the child fragments.
        todayFragment = (TodayFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_today);
        forecast1 = (Forecast1) getSupportFragmentManager().findFragmentById(R.id.fragment_forecast1);
        forecast2 = (Forecast2) getSupportFragmentManager().findFragmentById(R.id.fragment_forecast2);
        forecast3 = (Forecast3) getSupportFragmentManager().findFragmentById(R.id.fragment_forecast3);
        forecast4 = (Forecast4) getSupportFragmentManager().findFragmentById(R.id.fragment_forecast4);

        // Obtain the current preferences stored.
        lastUpdatedField = findViewById(R.id.last_updated);
        updatedPref = new UpdatedPreference(getApplicationContext());

        // Set the appropriate on click listener, to each forecast fragment, to start a detailed view.
        findViewById(R.id.fragment_forecast1).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent showDetail = new Intent(getApplicationContext(), DetailedActivity.class);

                // This extra intent information distinguishes which forecast to show in the detailed view.
                showDetail.putExtra("days_in_advance", 1);
                startActivity(showDetail);
            }
        });

        findViewById(R.id.fragment_forecast2).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent showDetail = new Intent(getApplicationContext(), DetailedActivity.class);
                showDetail.putExtra("days_in_advance", 2);
                startActivity(showDetail);
            }
        });

        findViewById(R.id.fragment_forecast3).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent showDetail = new Intent(getApplicationContext(), DetailedActivity.class);
                showDetail.putExtra("days_in_advance", 3);
                startActivity(showDetail);
            }
        });

        findViewById(R.id.fragment_forecast4).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent showDetail = new Intent(getApplicationContext(), DetailedActivity.class);
                showDetail.putExtra("days_in_advance", 4);
                startActivity(showDetail);
            }
        });

        // Remove the session first update flag, or repopulate if no download was needed, to set the views.
        if (!sessionFirstUpdate) {
            sessionFirstUpdate = true;
        }
        else {
            repopulateData();
            refreshUpdatedTime();
        }
    }

    /**
     * Download new weather forecast data, by executing an asynchronous thread.
     */
    public void updateData() {

        downloader = new UpdateStoredData(getApplicationContext(), HomeActivity.this);
        downloader.execute();
    }

    /**
     * Retrieve and set the last known update time preference.
     */
    public void refreshUpdatedTime() {

        String time = updatedPref.getLastUpdate();
        lastUpdatedField.setText("Last update: " + time);
    }

    /**
     * Repopulates all the child fragment's views, fetching and rendering data from the database.
     */
    public void repopulateData() {

        todayFragment.populateWeatherData();
        forecast1.populateWeatherData();
        forecast2.populateWeatherData();
        forecast3.populateWeatherData();
        forecast4.populateWeatherData();
    }

    @Override
    public void onResume() {
        super.onResume();

        Intent passedIntent = getIntent();
        boolean refresh = passedIntent.getBooleanExtra("refresh", false);
        if (refresh) {
            // Called when the user returns home after changing the city, repopulate the data.
            repopulateData();
        }
    }

    @Override
    public void onNewIntent(Intent newIntent) {
        super.onNewIntent(newIntent);
        // Allows this currently running activity to receive intents, instead of being recreated.
        setIntent(newIntent);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        // This method is called upon a screen orientation.
        super.onSaveInstanceState(savedInstanceState);

        // If a downloader is active, cancel and wait for a new one to start on the new home activity.
        if(downloader != null && downloader.getStatus() == AsyncTask.Status.RUNNING){
            downloader.cancel(true);
            // Ensure the new activity attempts an automatic download.
            sessionFirstUpdate = false;
        }
    }
}

