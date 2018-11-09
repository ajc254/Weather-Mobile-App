package com.example.ajc254.weatherapp;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.ajc254.weatherapp.preferences.CityPreference;

public class LocationActivity extends ListActivity {

    private ListView view;
    private CityPreference cityPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Find the resource for the city locations, and create an adapter from it.
        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(this,R.array.locations,
                        R.layout.activity_location);

        view = getListView();

        // Bind the list view to the adapter.
        setListAdapter(adapter);

        // Obtain the cityPreference object, ready to update the preference.
        cityPref = new CityPreference(getApplicationContext());

        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id){
                // Set the new clicked on city as a preference.
                cityPref.setCity((String)view.getItemAtPosition(position));

                // Return to the home activity, without creating a new one.
                Intent returnHome = new Intent(getApplicationContext(), HomeActivity.class);
                returnHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                // Refresh the data as the city has been changed.
                returnHome.putExtra("refresh", true);
                startActivity(returnHome);
            }
        });
    }
}
